package cn.master.horde.controller;

import cn.master.horde.dao.*;
import cn.master.horde.entity.SystemSchedule;
import cn.master.horde.service.SystemScheduleService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 定时任务 控制层。
 *
 * @author 11's papa
 * @since 2026-01-15
 */
@RestController
@Tag(name = "定时任务接口")
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
    @Operation(description = "保存定时任务")
    public void save(@RequestBody @Parameter(description = "定时任务") SystemSchedule systemSchedule) throws ClassNotFoundException {
        systemScheduleService.addSchedule(systemSchedule);
    }

    /**
     * 根据主键删除定时任务。
     *
     * @param id 主键
     */
    @DeleteMapping("remove/{id}")
    public void remove(@PathVariable String id) {
        systemScheduleService.deleteTask(id);
    }

    /**
     * 根据主键更新定时任务。
     *
     * @param systemSchedule 定时任务
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    public boolean update(@RequestBody SystemSchedule systemSchedule) {
        return systemScheduleService.updateById(systemSchedule);
    }

    @PostMapping("/update-cron")
    @Operation(summary = "系统-任务中心-后台任务更新cron表达式")
    public void updateValue(@Validated @RequestBody ScheduleCronRequest request) throws ClassNotFoundException {
        systemScheduleService.updateCron(request);
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
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    public Page<ScheduleDTO> page(@Validated @RequestBody SchedulePageRequest request) {
        return systemScheduleService.page(request);
    }

    @PostMapping(value = "/schedule-config")
    @Operation(summary = "定时任务配置")
    public void scheduleConfig(@Validated @RequestBody BaseScheduleConfigRequest request) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SystemSchedule schedule = systemScheduleService.getByJobKey(request.getJobKey());
        ScheduleConfig scheduleConfig = ScheduleConfig.builder()
                .key(schedule.getJobKey())
                .projectId(schedule.getProjectId())
                .name(schedule.getName())
                .enable(request.isEnable())
                .cron(request.getCron())
                .config(request.getRunConfig())
                .build();
        Class<?> targetClass = Class.forName(schedule.getJob());
        Method getJobKey = targetClass.getMethod("getJobKey", String.class, String.class);
        Method getTriggerKey = targetClass.getMethod("getTriggerKey", String.class, String.class);
        @SuppressWarnings("unchecked")
        Class<? extends Job> jobClass = (Class<? extends Job>) targetClass;
        systemScheduleService.scheduleConfig(scheduleConfig,
                (JobKey) getJobKey.invoke(null, schedule.getProjectId(), schedule.getJobKey()),
                (TriggerKey) getTriggerKey.invoke(null, schedule.getProjectId(), schedule.getJobKey()),
                jobClass,
                "admin");
    }
}
