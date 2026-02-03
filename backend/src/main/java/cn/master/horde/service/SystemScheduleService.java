package cn.master.horde.service;

import cn.master.horde.dto.ScheduleConfig;
import cn.master.horde.dto.ScheduleCronRequest;
import cn.master.horde.dto.ScheduleDTO;
import cn.master.horde.dto.SchedulePageRequest;
import cn.master.horde.entity.SystemSchedule;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
}
