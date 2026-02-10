package cn.master.horde.controller;

import cn.master.horde.common.constants.OperationLogType;
import cn.master.horde.common.log.annotation.Loggable;
import cn.master.horde.common.util.SessionUtils;
import cn.master.horde.model.dto.api.request.ApiDefinitionAddRequest;
import cn.master.horde.model.entity.ApiDefinition;
import cn.master.horde.service.ApiDefinitionService;
import cn.master.horde.service.log.ApiDefinitionLogService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 接口定义 控制层。
 *
 * @author 11's papa
 * @since 2026-02-09
 */
@RestController
@Tag(name = "接口定义接口")
@RequiredArgsConstructor
@RequestMapping("/api/definition")
public class ApiDefinitionController {

    private final ApiDefinitionService apiDefinitionService;

    @PostMapping("save")
    @Operation(description = "保存接口定义")
    @Loggable(type = OperationLogType.ADD, expression = "#wClass.addLog(#request)", wClass = ApiDefinitionLogService.class)
    public ApiDefinition save(@RequestBody @Parameter(description = "接口定义") @Validated ApiDefinitionAddRequest request) {
        return apiDefinitionService.create(request, SessionUtils.getCurrentUsername());
    }

    /**
     * 根据主键删除接口定义。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description = "根据主键删除接口定义")
    public boolean remove(@PathVariable @Parameter(description = "接口定义主键") String id) {
        return apiDefinitionService.removeById(id);
    }

    /**
     * 根据主键更新接口定义。
     *
     * @param apiDefinition 接口定义
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description = "根据主键更新接口定义")
    public boolean update(@RequestBody @Parameter(description = "接口定义主键") ApiDefinition apiDefinition) {
        return apiDefinitionService.updateById(apiDefinition);
    }

    /**
     * 查询所有接口定义。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description = "查询所有接口定义")
    public List<ApiDefinition> list() {
        return apiDefinitionService.list();
    }

    /**
     * 根据主键获取接口定义。
     *
     * @param id 接口定义主键
     * @return 接口定义详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取接口定义")
    public ApiDefinition getInfo(@PathVariable @Parameter(description = "接口定义主键") String id) {
        return apiDefinitionService.getById(id);
    }

    /**
     * 分页查询接口定义。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description = "分页查询接口定义")
    public Page<ApiDefinition> page(@Parameter(description = "分页信息") Page<ApiDefinition> page) {
        return apiDefinitionService.page(page);
    }

}
