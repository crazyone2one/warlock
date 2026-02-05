package cn.master.horde.service.impl;

import cn.master.horde.common.constants.ApplicationNumScope;
import cn.master.horde.scheduler.ScheduleManager;
import cn.master.horde.common.result.BizException;
import cn.master.horde.common.result.ResultCode;
import cn.master.horde.common.util.SessionUtils;
import cn.master.horde.common.service.NumGenerator;
import cn.master.horde.model.dto.ScheduleConfig;
import cn.master.horde.model.dto.ScheduleCronRequest;
import cn.master.horde.model.dto.ScheduleDTO;
import cn.master.horde.model.dto.SchedulePageRequest;
import cn.master.horde.model.entity.SystemSchedule;
import cn.master.horde.model.mapper.SystemScheduleMapper;
import cn.master.horde.service.SystemScheduleService;
import com.mybatisflex.core.paginate.Page;
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

import static cn.master.horde.model.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;
import static cn.master.horde.model.entity.table.SystemScheduleTableDef.SYSTEM_SCHEDULE;


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
    public void addSchedule(SystemSchedule schedule) {
        schedule.setNum(NumGenerator.nextNum(schedule.getProjectId(), ApplicationNumScope.TASK));
        String currentUserId = SessionUtils.getCurrentUserId();
        schedule.setCreateUser(currentUserId);
        schedule.setUpdateUser(currentUserId);
        mapper.insertSelective(schedule);
        if (BooleanUtils.isTrue(schedule.getEnable())) {
            try {
                addOrUpdateJob(schedule);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
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
    public void updateCron(ScheduleCronRequest request) {
        SystemSchedule schedule = checkScheduleExit(request.id());
        schedule.setValue(request.cron());
        mapper.update(schedule);
        try {
            addOrUpdateJob(schedule);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void addOrUpdateJob(SystemSchedule schedule) throws ClassNotFoundException {
        Class<?> jobClass = Class.forName(schedule.getJob());
        if (Job.class.isAssignableFrom(jobClass)) {
            @SuppressWarnings("unchecked")
            Class<? extends Job> jobClassCast = (Class<? extends Job>) jobClass;
            addOrUpdateCronJob(schedule, new JobKey(schedule.getJobKey(), schedule.getProjectId()),
                    new TriggerKey(schedule.getJobKey(), schedule.getProjectId()), jobClassCast);
        } else {
            throw new BizException(ResultCode.VALIDATE_FAILED, "指定的类不是有效的Job类: " + schedule.getJob());
        }
    }

    @Override
    public void removeJob(String key, String job) {
        scheduleManager.removeJob(new JobKey(key, job), new TriggerKey(key, job));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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

    @Override
    public Page<ScheduleDTO> page(SchedulePageRequest request) {
        QueryChain<SystemSchedule> systemScheduleQueryChain = queryChain()
                .select(SYSTEM_SCHEDULE.ID, SYSTEM_SCHEDULE.NAME, SYSTEM_SCHEDULE.ENABLE, SYSTEM_SCHEDULE.VALUE)
                .select(SYSTEM_SCHEDULE.CREATE_USER, SYSTEM_SCHEDULE.CREATE_TIME, SYSTEM_SCHEDULE.NUM, SYSTEM_SCHEDULE.PROJECT_ID)
                .select(SYSTEM_SCHEDULE.CONFIG.as("runConfig"))
                .select(SYSTEM_SCHEDULE.RESOURCE_TYPE, SYSTEM_SCHEDULE.JOB_KEY)
                .select(SYSTEM_PROJECT.NAME.as("projectName"))
                .select("QRTZ_TRIGGERS.PREV_FIRE_TIME AS last_time")
                .select("QRTZ_TRIGGERS.NEXT_FIRE_TIME AS nextTime")
                .select("QRTZ_TRIGGERS.TRIGGER_STATE AS triggerStatus")
                .from(SYSTEM_SCHEDULE)
                .leftJoin(SYSTEM_PROJECT).on(SYSTEM_PROJECT.ID.eq(SYSTEM_SCHEDULE.PROJECT_ID))
                .leftJoin("QRTZ_TRIGGERS").on("QRTZ_TRIGGERS.TRIGGER_NAME = system_schedule.job_key");
        return systemScheduleQueryChain
                .where(SYSTEM_SCHEDULE.NAME.like(request.getKeyword()).or(SYSTEM_SCHEDULE.NUM.like(request.getKeyword())))
                .and(SYSTEM_SCHEDULE.PROJECT_ID.eq(request.getProjectId()))
                .and(SYSTEM_SCHEDULE.RESOURCE_TYPE.eq(request.getResourceType()))
                .orderBy(SYSTEM_SCHEDULE.ENABLE.desc(), SYSTEM_SCHEDULE.NUM.desc())
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), ScheduleDTO.class);
    }

    @Override
    public SystemSchedule getByJobKey(String jobKey) {
        return queryChain().where(SYSTEM_SCHEDULE.JOB_KEY.eq(jobKey)).oneOpt().orElseThrow(() -> new BizException(ResultCode.VALIDATE_FAILED, "定时任务不存在"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(String id) {
        SystemSchedule schedule = checkScheduleExit(id);
        schedule.setEnable(!schedule.getEnable());
        mapper.update(schedule);
        try {
            addOrUpdateJob(schedule);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
