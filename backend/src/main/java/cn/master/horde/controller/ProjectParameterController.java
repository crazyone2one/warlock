package cn.master.horde.controller;

import cn.master.horde.entity.ProjectParameter;
import cn.master.horde.service.ProjectParameterService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目级参数 控制层。
 *
 * @author 11's papa
 * @since 2026-01-19
 */
@RestController
@Tag(name = "项目级参数接口")
@RequiredArgsConstructor
@RequestMapping("/project-parameter")
public class ProjectParameterController {

    private final ProjectParameterService projectParameterService;

    /**
     * 保存项目级参数。
     *
     * @param projectParameter 项目级参数
     */
    @PostMapping("save")
    @Operation(description = "保存项目级参数")
    public void save(@RequestBody @Parameter(description = "项目级参数") ProjectParameter projectParameter) {
        projectParameterService.saveParameter(projectParameter);
    }

    /**
     * 根据主键删除项目级参数。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description = "根据主键删除项目级参数")
    public boolean remove(@PathVariable @Parameter(description = "项目级参数主键") String id) {
        return projectParameterService.removeById(id);
    }

    /**
     * 根据主键更新项目级参数。
     *
     * @param projectParameter 项目级参数
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description = "根据主键更新项目级参数")
    public boolean update(@RequestBody @Parameter(description = "项目级参数主键") ProjectParameter projectParameter) {
        return projectParameterService.updateById(projectParameter);
    }

    /**
     * 查询所有项目级参数。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description = "查询所有项目级参数")
    public List<ProjectParameter> list() {
        return projectParameterService.list();
    }

    /**
     * 根据主键获取项目级参数。
     *
     * @param projectId 项目级参数主键
     * @return 项目级参数详情
     */
    @GetMapping("getInfo/{projectId}/{type}")
    @Operation(description = "根据项目id/类型获取项目")
    public ProjectParameter getInfo(@PathVariable @Parameter(description = "项目id") String projectId,
                                    @PathVariable @Parameter(description = "参数类型") String type) {
        return projectParameterService.getParameterByProjectIdAndType(projectId, type);
    }

    /**
     * 分页查询项目级参数。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description = "分页查询项目级参数")
    public Page<ProjectParameter> page(@Parameter(description = "分页信息") Page<ProjectParameter> page) {
        return projectParameterService.page(page);
    }

}
