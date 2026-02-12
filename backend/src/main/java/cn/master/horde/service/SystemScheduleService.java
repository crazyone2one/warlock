package cn.master.horde.service;

import cn.master.horde.model.dto.*;
import cn.master.horde.model.entity.SystemSchedule;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.quartz.*;

import java.util.List;
import java.util.Map;

/**
 * 定时任务 服务层。
 *
 * @author 11's papa
 * @since 2026-01-15
 */
public interface SystemScheduleService extends IService<SystemSchedule> {
    void addSchedule(SystemSchedule schedule);

    void addOrUpdateCronJob(SystemSchedule request, JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> clazz);

    String scheduleConfig(ScheduleConfig scheduleConfig, JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> clazz, String operator);

    void updateCron(ScheduleCronRequest request);

    void removeJob(String key, String job);

    void deleteTask(String id);

    void executeTask(String id);

    void pauseTask(String id);

    void resumeTask(String id);

    List<SystemSchedule> getTaskByProjectId(String projectId);

    Page<ScheduleDTO> page(SchedulePageRequest request);

    SystemSchedule getByJobKey(@NotBlank(message = "{api_scenario.id.not_blank}") @Size(min = 1, max = 50, message = "{api_scenario.id.length_range}") String jobKey);

    void enable(String id);

    List<JobInfo> getAllJobs() throws SchedulerException;

    void deleteJob(String jobName, String group) throws SchedulerException;

    void resumeJob(String jobName, String group) throws SchedulerException;

    void triggerJob(JobKey jobKey, Map<String, ScheduleConfigParameter> payload) throws SchedulerException;
}
