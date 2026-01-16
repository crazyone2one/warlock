package cn.master.horde.controller;

import cn.master.horde.common.service.CurrentUserService;
import cn.master.horde.dao.BasePageRequest;
import cn.master.horde.entity.SystemProject;
import cn.master.horde.service.SystemProjectService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目 控制层。
 *
 * @author 11's papa
 * @since 2026-01-15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/system-project")
public class SystemProjectController {

    private final SystemProjectService systemProjectService;

    /**
     * 保存项目。
     *
     * @param systemProject 项目
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody SystemProject systemProject) {
        return systemProjectService.saveProject(systemProject);
    }

    /**
     * 根据主键删除项目。
     *
     * @param id 主键
     */
    @DeleteMapping("remove/{id}")
    public void remove(@PathVariable String id) {
        systemProjectService.removeProject(id);
    }

    /**
     * 根据主键更新项目。
     *
     * @param systemProject 项目
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    public boolean update(@RequestBody SystemProject systemProject) {
        return systemProjectService.updateProject(systemProject);
    }

    /**
     * 查询所有项目。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<SystemProject> list() {
        return systemProjectService.list();
    }

    /**
     * 根据主键获取项目。
     *
     * @param id 项目主键
     * @return 项目详情
     */
    @GetMapping("getInfo/{id}")
    public SystemProject getInfo(@PathVariable String id) {
        return systemProjectService.getById(id);
    }

    /**
     * 分页查询项目。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    public Page<SystemProject> page(@Validated @RequestBody BasePageRequest page) {
        return systemProjectService.getProjectPage(page);
    }

    @GetMapping("/enable/{id}")
    public void enable(@PathVariable String id) {
        systemProjectService.enable(id, CurrentUserService.getCurrentUsername());
    }

    @GetMapping("/disable/{id}")
    public void disable(@PathVariable String id) {
        systemProjectService.disable(id, CurrentUserService.getCurrentUsername());
    }
}
