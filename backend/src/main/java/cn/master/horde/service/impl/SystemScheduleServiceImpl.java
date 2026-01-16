package cn.master.horde.service.impl;

import cn.master.horde.common.constants.ApplicationNumScope;
import cn.master.horde.common.job.ScheduleManager;
import cn.master.horde.common.result.BizException;
import cn.master.horde.common.result.ResultCode;
import cn.master.horde.common.service.CurrentUserService;
import cn.master.horde.common.service.NumGenerator;
import cn.master.horde.dao.ScheduleConfig;
import cn.master.horde.dao.ScheduleCronRequest;
import cn.master.horde.entity.SystemSchedule;
import cn.master.horde.mapper.SystemScheduleMapper;
import cn.master.horde.service.SystemScheduleService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.master.horde.entity.table.SystemScheduleTableDef.SYSTEM_SCHEDULE;

/**
 * 定时任务 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-15
 */
@Service
@RequiredArgsConstructor
public class SystemScheduleServiceImpl extends ServiceImpl<SystemScheduleMapper, SystemSchedule> implements SystemScheduleService {
    private final ScheduleManager scheduleManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSchedule(SystemSchedule schedule) throws ClassNotFoundException {
        schedule.setNum(NumGenerator.nextNum(schedule.getProjectId(), ApplicationNumScope.TASK));
        schedule.setCreateUser(CurrentUserService.getCurrentUsername());
        mapper.insertSelective(schedule);
        if (BooleanUtils.isTrue(schedule.getEnable())) {
            Class<?> targetClass = Class.forName(schedule.getJob());
            @SuppressWarnings("unchecked")
            Class<? extends Job> jobClass = (Class<? extends Job>) targetClass;
            addOrUpdateCronJob(schedule, new JobKey(schedule.getJobKey(), schedule.getProjectId()),
                    new TriggerKey(schedule.getJobKey(), schedule.getProjectId()), jobClass);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateCronJob(SystemSchedule request, JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> clazz) {
        String cronExpression = request.getValue();
        if (BooleanUtils.isTrue(request.getEnable()) && StringUtils.isNotBlank(cronExpression)) {
            try {
                scheduleManager.addOrUpdateCronJob(jobKey, triggerKey, clazz, cronExpression,
                        scheduleManager.getDefaultJobDataMap(request, cronExpression, request.getCreateUser()));
            } catch (SchedulerException e) {
                throw new RuntimeException("定时任务开启异常: " + e.getMessage());
            }
        } else {
            pauseTask(request.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String scheduleConfig(ScheduleConfig scheduleConfig, JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> clazz, String operator) {
        SystemSchedule schedule;
        QueryChain<SystemSchedule> scheduleQueryChain = queryChain()
                .where(SYSTEM_SCHEDULE.JOB_KEY.eq(scheduleConfig.getKey())
                        .and(SYSTEM_SCHEDULE.JOB.eq(clazz.getName())));
        List<SystemSchedule> scheduleList = scheduleQueryChain.list();
        if (CollectionUtils.isNotEmpty(scheduleList)) {
            schedule = scheduleConfig.genCronSchedule(scheduleList.getFirst());
            schedule.setJob(clazz.getName());
            mapper.updateByQuery(schedule, scheduleQueryChain);
        } else {
            schedule = scheduleConfig.genCronSchedule(null);
            schedule.setJob(clazz.getName());
            schedule.setCreateUser(operator);
            schedule.setNum(NumGenerator.nextNum(scheduleConfig.getProjectId(), ApplicationNumScope.TASK));
            mapper.insertSelective(schedule);
        }
        JobDataMap jobDataMap = scheduleManager.getDefaultJobDataMap(schedule, scheduleConfig.getCron(), schedule.getCreateUser());
        scheduleManager.removeJob(jobKey, triggerKey);
        if (BooleanUtils.isTrue(schedule.getEnable())) {
            scheduleManager.addCronJob(jobKey, triggerKey, clazz, scheduleConfig.getCron(), jobDataMap);
        }
        return schedule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCron(ScheduleCronRequest request) throws ClassNotFoundException {
        SystemSchedule schedule = checkScheduleExit(request.id());
        schedule.setValue(request.cron());
        mapper.update(schedule);
        Class<?> jobClass = Class.forName(schedule.getJob());
        if (Job.class.isAssignableFrom(jobClass)) {
            @SuppressWarnings("unchecked")
            Class<? extends Job> jobClassCast = (Class<? extends Job>) jobClass;
            addOrUpdateCronJob(schedule, new JobKey(schedule.getJobKey(), schedule.getProjectId()),
                    new TriggerKey(schedule.getJobKey(), schedule.getProjectId()), jobClassCast);
        } else {
            throw new BizException("指定的类不是有效的Job类: " + schedule.getJob());
        }
    }

    @Override
    public void removeJob(String key, String job) {
        scheduleManager.removeJob(new JobKey(key, job), new TriggerKey(key, job));
    }

    @Override
    public void deleteTask(String id) {
        SystemSchedule schedule = checkScheduleExit(id);
        mapper.delete(schedule);
        removeJob(schedule.getJobKey(), schedule.getProjectId());
    }

    @Override
    public void executeTask(String id) {
        SystemSchedule schedule = checkScheduleExit(id);
        scheduleManager.triggerJob(new JobKey(schedule.getJobKey(), schedule.getProjectId()));
    }

    @Override
    public void pauseTask(String id) {
        SystemSchedule schedule = checkScheduleExit(id);
        scheduleManager.pauseJob(new JobKey(schedule.getJobKey(), schedule.getProjectId()));
    }

    private SystemSchedule checkScheduleExit(String id) {
        return queryChain().where(SYSTEM_SCHEDULE.ID.eq(id)).oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.VALIDATE_FAILED, "定时任务不存在"));
    }

    @Override
    public void resumeTask(String id) {
        SystemSchedule schedule = checkScheduleExit(id);
        scheduleManager.resumeJob(new JobKey(schedule.getJobKey(), schedule.getProjectId()));
    }

    @Override
    public List<SystemSchedule> getTaskByProjectId(String projectId) {
        return queryChain().where(SYSTEM_SCHEDULE.PROJECT_ID.eq(projectId)).list();
    }
}
