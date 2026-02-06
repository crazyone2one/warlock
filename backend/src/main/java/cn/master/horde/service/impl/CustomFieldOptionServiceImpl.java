package cn.master.horde.service.impl;

import cn.master.horde.model.dto.request.CustomFieldOptionRequest;
import cn.master.horde.model.entity.CustomFieldOption;
import cn.master.horde.model.mapper.CustomFieldOptionMapper;
import cn.master.horde.service.CustomFieldOptionService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 自定义字段选项 服务层实现。
 *
 * @author 11's papa
 * @since 2026-02-05
 */
@Service
public class CustomFieldOptionServiceImpl extends ServiceImpl<CustomFieldOptionMapper, CustomFieldOption> implements CustomFieldOptionService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addByFieldId(String fieldId, List<CustomFieldOption> customFieldOptions) {
        if (CollectionUtils.isEmpty(customFieldOptions)) {
            return;
        }
        customFieldOptions.forEach(item -> {
            item.setFieldId(fieldId);
            item.setInternal(BooleanUtils.isTrue(item.getInternal()));
        });
        saveBatch(customFieldOptions);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByFieldId(String fieldId, List<CustomFieldOptionRequest> customFieldOptionRequests) {
        List<CustomFieldOption> originOptions = getByFieldId(fieldId);
        // 查询原有选项
        Map<String, CustomFieldOption> optionMap =
                originOptions.stream().collect(Collectors.toMap(CustomFieldOption::getValue, i -> i));
        // 先删除选项，再添加
        deleteByFieldId(fieldId);
        List<CustomFieldOption> customFieldOptions = customFieldOptionRequests.stream().map(item -> {
            CustomFieldOption customFieldOption = new CustomFieldOption();
            BeanUtils.copyProperties(item, customFieldOption);
            if (optionMap.get(item.getValue()) != null) {
                // 保留选项是否是内置的选项
                customFieldOption.setInternal(optionMap.get(item.getValue()).getInternal());
            } else {
                customFieldOption.setInternal(false);
            }
            customFieldOption.setFieldId(fieldId);
            return customFieldOption;
        }).toList();
        if (CollectionUtils.isNotEmpty(customFieldOptions)) {
            saveBatch(customFieldOptions);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(String fieldId) {
        mapper.deleteByQuery(queryChain().where(CustomFieldOption::getFieldId).eq(fieldId));
    }

    @Override
    public List<CustomFieldOption> getByFieldId(String fieldId) {
        List<CustomFieldOption> list = queryChain().where(CustomFieldOption::getFieldId).eq(fieldId).list();
        if (CollectionUtils.isNotEmpty(list)) {
            list.sort(Comparator.comparing(CustomFieldOption::getPos));
        }
        return list;
    }

    @Override
    public List<CustomFieldOption> getByFieldIds(List<String> fieldIds) {
        if (CollectionUtils.isEmpty(fieldIds)) {
            return List.of();
        }
        List<CustomFieldOption> list = queryChain().where(CustomFieldOption::getFieldId).in(fieldIds).list();
        if (CollectionUtils.isNotEmpty(list)) {
            list.sort(Comparator.comparing(CustomFieldOption::getPos));
        }
        return list;
    }
}
