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
import cn.master.horde.entity.SystemProject;
import cn.master.horde.service.SystemProjectService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 项目 控制层。
 *
 * @author 11's papa
 * @since 2026-01-15
 */
@RestController
@RequestMapping("/systemProject")
public class SystemProjectController {

    @Autowired
    private SystemProjectService systemProjectService;

    /**
     * 保存项目。
     *
     * @param systemProject 项目
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody SystemProject systemProject) {
        return systemProjectService.save(systemProject);
    }

    /**
     * 根据主键删除项目。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable String id) {
        return systemProjectService.removeById(id);
    }

    /**
     * 根据主键更新项目。
     *
     * @param systemProject 项目
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody SystemProject systemProject) {
        return systemProjectService.updateById(systemProject);
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
    @GetMapping("page")
    public Page<SystemProject> page(Page<SystemProject> page) {
        return systemProjectService.page(page);
    }

}
