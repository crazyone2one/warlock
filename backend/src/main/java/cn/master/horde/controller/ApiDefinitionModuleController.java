package cn.master.horde.controller;

import cn.master.horde.common.util.SessionUtils;
import cn.master.horde.model.dto.BaseTreeNode;
import cn.master.horde.model.dto.api.request.ApiModuleRequest;
import cn.master.horde.model.dto.api.request.ModuleCreateRequest;
import cn.master.horde.model.dto.api.request.ModuleUpdateRequest;
import cn.master.horde.model.entity.ApiDefinitionModule;
import cn.master.horde.service.ApiDefinitionModuleService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 接口模块 控制层。
 *
 * @author 11's papa
 * @since 2026-02-05
 */
@RestController
@Tag(name = "接口模块接口")
@RequiredArgsConstructor
@RequestMapping("/api/definition/module")
public class ApiDefinitionModuleController {

    private final ApiDefinitionModuleService apiDefinitionModuleService;

    @PostMapping("save")
    @Operation(description = "接口管理-模块-添加模块")
    @PreAuthorize("hasPermission('PROJECT_API_DEFINITION','READ+ADD')")
    public String save(@RequestBody @Parameter(description = "接口模块") ModuleCreateRequest request) {
        return apiDefinitionModuleService.add(request, SessionUtils.getCurrentUsername());
    }

    @GetMapping("remove/{id}")
    @Operation(description = "接口管理-模块-删除模块")
    @PreAuthorize("hasPermission('PROJECT_API_DEFINITION','READ+DELETE')")
    public void remove(@PathVariable @Parameter(description = "接口模块主键") String id) {
        apiDefinitionModuleService.deleteModule(id, SessionUtils.getCurrentUsername());
    }

    @PostMapping("update")
    @Operation(description = "接口管理-模块-修改模块")
    @PreAuthorize("hasPermission('PROJECT_API_DEFINITION','READ+UPDATE')")
    public void update(@RequestBody @Parameter(description = "接口模块主键") @Validated ModuleUpdateRequest request) {
        apiDefinitionModuleService.update(request, SessionUtils.getCurrentUsername());
    }

    /**
     * 查询所有接口模块。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description = "查询所有接口模块")
    public List<ApiDefinitionModule> list() {
        return apiDefinitionModuleService.list();
    }

    /**
     * 根据主键获取接口模块。
     *
     * @param id 接口模块主键
     * @return 接口模块详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取接口模块")
    public ApiDefinitionModule getInfo(@PathVariable @Parameter(description = "接口模块主键") String id) {
        return apiDefinitionModuleService.getById(id);
    }

    /**
     * 分页查询接口模块。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description = "分页查询接口模块")
    public Page<ApiDefinitionModule> page(@Parameter(description = "分页信息") Page<ApiDefinitionModule> page) {
        return apiDefinitionModuleService.page(page);
    }

    @PostMapping("/tree")
    @Operation(summary = "接口测试-接口管理-模块-查找模块")
    @PreAuthorize("hasPermission('PROJECT_API_DEFINITION','READ')")
    public List<BaseTreeNode> getTreeAndRequest(@RequestBody @Validated ApiModuleRequest request) {
        return apiDefinitionModuleService.getTree(request, false, true);
    }

    @PostMapping("/count")
    @Operation(summary = "接口测试-接口管理-模块-统计模块数量")
    @PreAuthorize("hasPermission('PROJECT_API_DEFINITION','READ')")
    public Map<String, Long> moduleCount(@Validated @RequestBody ApiModuleRequest request) {
        return apiDefinitionModuleService.moduleCount(request, false);
    }
}
