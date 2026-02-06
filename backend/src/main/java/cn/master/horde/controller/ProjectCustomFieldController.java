package cn.master.horde.controller;

import cn.master.horde.common.constants.Created;
import cn.master.horde.common.constants.Updated;
import cn.master.horde.common.util.SessionUtils;
import cn.master.horde.model.dto.BasePageRequest;
import cn.master.horde.model.dto.CustomFieldDTO;
import cn.master.horde.model.dto.request.CustomFieldUpdateRequest;
import cn.master.horde.model.entity.CustomField;
import cn.master.horde.service.CustomFieldService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 自定义字段 控制层。
 *
 * @author 11's papa
 * @since 2026-02-05
 */
@RestController
@Tag(name = "自定义字段接口")
@RequiredArgsConstructor
@RequestMapping("/project/custom/field")
public class ProjectCustomFieldController {
    private final CustomFieldService customFieldService;

    @PostMapping("save")
    @Operation(description = "保存自定义字段")
    @PreAuthorize("hasPermission('PROJECT_TEMPLATE','READ+ADD')")
    public CustomField save(@Parameter(description = "自定义字段") @Validated({Created.class}) @RequestBody CustomFieldUpdateRequest request) {
        CustomField customField = new CustomField();
        BeanUtils.copyProperties(request, customField);
        customField.setCreateUser(SessionUtils.getCurrentUsername());
        return customFieldService.add(customField, request.getOptions());
    }

    @GetMapping("remove/{id}")
    @Operation(description = "根据主键删除自定义字段")
    @PreAuthorize("hasPermission('PROJECT_TEMPLATE','READ+DELETE')")
    public void remove(@PathVariable @Parameter(description = "自定义字段主键") String id) {
        customFieldService.delete(id);
    }

    @PutMapping("update")
    @Operation(description = "更新自定义字段")
    @PreAuthorize("hasPermission('PROJECT_TEMPLATE','READ+UPDATE')")
    public CustomField update(@Parameter(description = "自定义字段") @Validated({Updated.class}) @RequestBody CustomFieldUpdateRequest request) {
        CustomField customField = new CustomField();
        BeanUtils.copyProperties(request, customField);
        return customFieldService.update(customField, request.getOptions());
    }

    /**
     * 查询所有自定义字段。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description = "查询所有自定义字段")
    public List<CustomField> list() {
        return customFieldService.list();
    }

    @GetMapping("getInfo/{id}")
    @Operation(description = "获取自定义字段详情")
    @PreAuthorize("hasPermission('PROJECT_TEMPLATE','READ')")
    public CustomField getInfo(@PathVariable @Parameter(description = "自定义字段主键") String id) {
        return customFieldService.getById(id);
    }

    @PostMapping("page")
    @Operation(description = "分页查询自定义字段")
    @PreAuthorize("hasPermission('PROJECT_TEMPLATE','READ')")
    public Page<CustomFieldDTO> page(@Validated @RequestBody BasePageRequest page) {
        return customFieldService.getPage(page);
    }

}
