package cn.master.horde.service.impl;

import cn.master.horde.common.constants.CustomFieldType;
import cn.master.horde.common.constants.TemplateRequiredCustomField;
import cn.master.horde.common.constants.TemplateScopeType;
import cn.master.horde.common.result.BizException;
import cn.master.horde.common.util.ServiceUtils;
import cn.master.horde.common.util.Translator;
import cn.master.horde.model.dto.BasePageRequest;
import cn.master.horde.model.dto.CustomFieldDTO;
import cn.master.horde.model.dto.request.CustomFieldOptionRequest;
import cn.master.horde.model.entity.CustomField;
import cn.master.horde.model.entity.CustomFieldOption;
import cn.master.horde.model.mapper.CustomFieldMapper;
import cn.master.horde.model.mapper.SystemProjectMapper;
import cn.master.horde.service.CustomFieldOptionService;
import cn.master.horde.service.CustomFieldService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.horde.common.result.SystemResultCode.CUSTOM_FIELD_EXIST;
import static cn.master.horde.common.result.SystemResultCode.INTERNAL_CUSTOM_FIELD_PERMISSION;
import static cn.master.horde.model.entity.table.CustomFieldTableDef.CUSTOM_FIELD;

/**
 * 自定义字段 服务层实现。
 *
 * @author 11's papa
 * @since 2026-02-05
 */
@Service
@RequiredArgsConstructor
public class CustomFieldServiceImpl extends ServiceImpl<CustomFieldMapper, CustomField> implements CustomFieldService {
    private final SystemProjectMapper systemProjectMapper;
    private final CustomFieldOptionService customFieldOptionService;
    private static final String CREATE_USER = "CREATE_USER";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomField add(CustomField customField, List<CustomFieldOptionRequest> options) {
        ServiceUtils.checkResourceExist(systemProjectMapper.selectOneById(customField.getScopeId()), "permission.project.name");
        // checkProjectTemplateEnable(systemProject.getOrganizationId(), customField.getScene());
        customField.setScopeType(TemplateScopeType.PROJECT.name());
        customField.setInternal(false);
        List<CustomFieldOption> customFieldOptions = parseCustomFieldOptionRequest2Option(options);
        checkAddExist(customField);
        customField.setEnableOptionKey(BooleanUtils.isTrue(customField.getEnableOptionKey()));
        save(customField);
        customFieldOptionService.addByFieldId(customField.getId(), customFieldOptions);
        return customField;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomField update(CustomField customField, List<CustomFieldOptionRequest> options) {
        CustomField originCustomField = getWithCheck(customField.getId());
        if (originCustomField.getInternal()) {
            // 内置字段不能修改名字
            customField.setName(null);
        }
        customField.setScopeId(originCustomField.getScopeId());
        customField.setScene(originCustomField.getScene());
        ServiceUtils.checkResourceExist(systemProjectMapper.selectOneById(customField.getScopeId()), "permission.project.name");
        checkUpdateExist(customField);
        checkResourceExist(customField.getId());
        mapper.insertOrUpdateSelective(customField);
        if (Objects.nonNull(options)) {
            customFieldOptionService.updateByFieldId(customField.getId(), options);
        }
        return customField;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        CustomField customField = getWithCheck(id);
        checkInternal(customField);
        ServiceUtils.checkResourceExist(systemProjectMapper.selectOneById(customField.getScopeId()), "permission.project.name");
        mapper.deleteById(id);
        customFieldOptionService.deleteByFieldId(id);
    }

    @Override
    public Page<CustomFieldDTO> getPage(BasePageRequest page) {
        ServiceUtils.checkResourceExist(systemProjectMapper.selectOneById(page.getProjectId()), "permission.project.name");
        Page<CustomFieldDTO> customFieldDTOPage = queryChain()
                .where(CUSTOM_FIELD.SCOPE_ID.eq(page.getProjectId())
                        .and(CUSTOM_FIELD.NAME.like(page.getKeyword())))
                .pageAs(new Page<>(page.getPage(), page.getPageSize()), CustomFieldDTO.class);
        List<CustomFieldOption> customFieldOptions = customFieldOptionService.getByFieldIds(customFieldDTOPage.getRecords().stream().map(CustomFieldDTO::getId).toList());
        Map<String, List<CustomFieldOption>> optionMap = customFieldOptions.stream().collect(Collectors.groupingBy(CustomFieldOption::getFieldId));
        customFieldDTOPage.getRecords().stream().map(item -> {
            CustomFieldDTO customFieldDTO = new CustomFieldDTO();
            BeanUtils.copyProperties(item, customFieldDTO);
            customFieldDTO.setOptions(optionMap.get(item.getId()));
            if (CustomFieldType.getHasOptionValueSet().contains(customFieldDTO.getType()) && customFieldDTO.getOptions() == null) {
                customFieldDTO.setOptions(List.of());
            }
            if (Strings.CS.equalsAny(item.getType(), CustomFieldType.MEMBER.name(), CustomFieldType.MULTIPLE_MEMBER.name())) {
                // 成员选项添加默认的选项
                CustomFieldOption createUserOption = new CustomFieldOption();
                createUserOption.setFieldId(item.getId());
                createUserOption.setText(Translator.get("message.domain.createUser"));
                createUserOption.setValue(CREATE_USER);
                createUserOption.setInternal(false);
                customFieldDTO.setOptions(List.of(createUserOption));
            }
            if (BooleanUtils.isTrue(item.getInternal())) {
                // 设置哪些内置字段是模板里必选的
                Set<String> templateRequiredCustomFieldSet = Arrays.stream(TemplateRequiredCustomField.values())
                        .map(TemplateRequiredCustomField::getName)
                        .collect(Collectors.toSet());
                customFieldDTO.setTemplateRequired(templateRequiredCustomFieldSet.contains(item.getName()));
                customFieldDTO.setInternalFieldKey(item.getName());
                // 翻译内置字段名称
                customFieldDTO.setName(Translator.get("custom_field." + item.getName()));
            }
            return customFieldDTO;
        });
        return customFieldDTOPage;
    }

    private void checkInternal(CustomField customField) {
        if (customField.getInternal()) {
            throw new BizException(INTERNAL_CUSTOM_FIELD_PERMISSION);
        }
    }

    private void checkUpdateExist(CustomField customField) {
        if (StringUtils.isBlank(customField.getName())) {
            return;
        }
        if (queryChain().where(CUSTOM_FIELD.NAME.eq(customField.getName())
                .and(CUSTOM_FIELD.SCOPE_ID.eq(customField.getScopeId()))
                .and(CUSTOM_FIELD.SCENE.eq(customField.getScene()))
                .and(CUSTOM_FIELD.ID.ne(customField.getId()))).exists()) {
            throw new BizException(CUSTOM_FIELD_EXIST);
        }
    }

    private CustomField getWithCheck(String id) {
        checkResourceExist(id);
        return mapper.selectOneById(id);
    }

    private void checkResourceExist(String id) {
        ServiceUtils.checkResourceExist(mapper.selectOneById(id), "permission.organization_custom_field.name");
    }

    private void checkAddExist(CustomField customField) {
        boolean exists = queryChain().where(CUSTOM_FIELD.NAME.eq(customField.getName())
                .and(CUSTOM_FIELD.SCOPE_ID.eq(customField.getScopeId()))
                .and(CUSTOM_FIELD.SCENE.eq(customField.getScene()))).exists();
        if (exists) {
            throw new BizException(CUSTOM_FIELD_EXIST);
        }
    }

    private List<CustomFieldOption> parseCustomFieldOptionRequest2Option(List<CustomFieldOptionRequest> options) {
        return options.stream().map(option -> CustomFieldOption.builder()
                .value(option.getValue())
                .text(option.getText())
                .pos(option.getPos())
                .build()).toList();
    }
}
