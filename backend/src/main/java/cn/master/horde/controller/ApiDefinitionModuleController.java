package cn.master.horde.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.horde.model.entity.ApiDefinitionModule;
import cn.master.horde.service.ApiDefinitionModuleService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 接口模块 控制层。
 *
 * @author 11's papa
 * @since 2026-02-05
 */
@RestController
@Tag(name = "接口模块接口")
@RequestMapping("/apiDefinitionModule")
public class ApiDefinitionModuleController {

    @Autowired
    private ApiDefinitionModuleService apiDefinitionModuleService;

    /**
     * 保存接口模块。
     *
     * @param apiDefinitionModule 接口模块
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description="保存接口模块")
    public boolean save(@RequestBody @Parameter(description="接口模块")ApiDefinitionModule apiDefinitionModule) {
        return apiDefinitionModuleService.save(apiDefinitionModule);
    }

    /**
     * 根据主键删除接口模块。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键删除接口模块")
    public boolean remove(@PathVariable @Parameter(description="接口模块主键") String id) {
        return apiDefinitionModuleService.removeById(id);
    }

    /**
     * 根据主键更新接口模块。
     *
     * @param apiDefinitionModule 接口模块
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新接口模块")
    public boolean update(@RequestBody @Parameter(description="接口模块主键") ApiDefinitionModule apiDefinitionModule) {
        return apiDefinitionModuleService.updateById(apiDefinitionModule);
    }

    /**
     * 查询所有接口模块。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有接口模块")
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
    @Operation(description="根据主键获取接口模块")
    public ApiDefinitionModule getInfo(@PathVariable @Parameter(description="接口模块主键") String id) {
        return apiDefinitionModuleService.getById(id);
    }

    /**
     * 分页查询接口模块。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询接口模块")
    public Page<ApiDefinitionModule> page(@Parameter(description="分页信息") Page<ApiDefinitionModule> page) {
        return apiDefinitionModuleService.page(page);
    }

}
