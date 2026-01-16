package cn.master.horde.service;

import cn.master.horde.dao.ScheduleConfig;
import cn.master.horde.dao.ScheduleCronRequest;
import cn.master.horde.entity.SystemSchedule;
import com.mybatisflex.core.service.IService;
import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

import java.util.List;

/**
 * 定时任务 服务层。
 *
 * @author 11's papa
 * @since 2026-01-15
 */
public interface SystemScheduleService extends IService<SystemSchedule> {
    void addSchedule(SystemSchedule schedule) throws ClassNotFoundException;

    void addOrUpdateCronJob(SystemSchedule request, JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> clazz);

    String scheduleConfig(ScheduleConfig scheduleConfig, JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> clazz, String operator);

    void updateCron(ScheduleCronRequest request) throws ClassNotFoundException;

    void removeJob(String key, String job);

    void deleteTask(String id);

    void executeTask(String id);

    void pauseTask(String id);

    void resumeTask(String id);

    List<SystemSchedule> getTaskByProjectId(String projectId);
}
