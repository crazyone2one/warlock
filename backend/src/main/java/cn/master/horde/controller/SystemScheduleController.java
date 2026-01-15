package cn.master.horde.controller;

import cn.master.horde.entity.SystemSchedule;
import cn.master.horde.service.SystemScheduleService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 定时任务 控制层。
 *
 * @author 11's papa
 * @since 2026-01-15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/system-schedule")
public class SystemScheduleController {
    private final SystemScheduleService systemScheduleService;

    /**
     * 保存定时任务。
     *
     * @param systemSchedule 定时任务
     */
    @PostMapping("save")
    public void save(@RequestBody SystemSchedule systemSchedule) throws ClassNotFoundException {
        systemScheduleService.addSchedule(systemSchedule);
    }

    /**
     * 根据主键删除定时任务。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable String id) {
        return systemScheduleService.removeById(id);
    }

    /**
     * 根据主键更新定时任务。
     *
     * @param systemSchedule 定时任务
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody SystemSchedule systemSchedule) {
        return systemScheduleService.updateById(systemSchedule);
    }

    /**
     * 查询所有定时任务。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<SystemSchedule> list() {
        return systemScheduleService.list();
    }

    /**
     * 根据主键获取定时任务。
     *
     * @param id 定时任务主键
     * @return 定时任务详情
     */
    @GetMapping("getInfo/{id}")
    public SystemSchedule getInfo(@PathVariable String id) {
        return systemScheduleService.getById(id);
    }

    /**
     * 分页查询定时任务。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<SystemSchedule> page(Page<SystemSchedule> page) {
        return systemScheduleService.page(page);
    }

}
