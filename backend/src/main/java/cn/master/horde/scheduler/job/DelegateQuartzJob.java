package cn.master.horde.scheduler.job;

import cn.master.horde.common.util.LogUtils;
import cn.master.horde.model.dto.ScheduleConfigParameter;
import cn.master.horde.model.entity.QuartzJobExecutionLog;
import cn.master.horde.service.QuartzJobExecutionLogService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @author : 11's papa
 * @since : 2026/2/12, 星期四
 **/
@Component("delegateQuartzJob")
@RequiredArgsConstructor
public class DelegateQuartzJob implements Job {
    private final ApplicationContext applicationContext;
    private final QuartzJobExecutionLogService jobLogService;
    private static final JsonMapper jsonMapper = new JsonMapper();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        String beanName = dataMap.getString("beanName");
        String methodName = dataMap.getString("methodName");
        Object paramsJson = dataMap.get("paramsJson");

        JobKey jobKey = context.getJobDetail().getKey();
        QuartzJobExecutionLog logEntry = new QuartzJobExecutionLog();
        logEntry.setJobName(jobKey.getName());
        logEntry.setJobGroup(jobKey.getGroup());
        logEntry.setBeanName(beanName);
        logEntry.setMethodName(methodName);
        logEntry.setParamsJson(jsonMapper.writeValueAsString(paramsJson));
        logEntry.setStartTime(LocalDateTime.now());
        logEntry.setTriggerType(isManualTrigger(context) ? "MANUAL" : "SCHEDULED");

        long start = System.currentTimeMillis();
        LogUtils.info("▶️ [Quartz] 任务开始 | jobName={}", logEntry.getJobName());

        try {
            Object bean = applicationContext.getBean(beanName);
            Class<?> clazz = bean.getClass();
            Method targetMethod = null;
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    targetMethod = method;
                    break; // 简化：取第一个匹配的方法（建议避免重载）
                }
            }
            if (targetMethod == null) {
                throw new RuntimeException("未找到方法: " + methodName);
            }
            ScheduleConfigParameter args;
            if (paramsJson != null) {
                // 反序列化为 Object[]（假设参数是按顺序的 JSON 数组）
                args = jsonMapper.convertValue(paramsJson, ScheduleConfigParameter.class);
            } else {
                args = new ScheduleConfigParameter();
            }
            // Method method = bean.getClass().getMethod(methodName);
            targetMethod.setAccessible(true);
            targetMethod.invoke(bean, args);

            logEntry.setStatus(QuartzJobExecutionLog.Status.SUCCESS);
            LogUtils.info("✅ [Quartz] 任务成功 | jobName={} | 耗时={}ms", logEntry.getJobName(), System.currentTimeMillis() - start);
        } catch (Exception e) {
            logEntry.setStatus(QuartzJobExecutionLog.Status.FAILED);
            logEntry.setErrorMessage(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            logEntry.setStackTrace(ExceptionUtils.getStackTrace(e)); // 需 Apache Commons Lang

            LogUtils.error("❌ [Quartz] 任务失败 | jobName={}", logEntry.getJobName(), e);
            throw new JobExecutionException(e, false);
        } finally {
            logEntry.setEndTime(LocalDateTime.now());
            logEntry.setDurationMs(System.currentTimeMillis() - start);
            // 异步入库
            jobLogService.saveLog(logEntry);
        }
    }

    /**
     * 判断是否为手动触发（临时 JobDataMap 有特殊标记）
     *
     * @param context JobExecutionContext
     * @return 是否手动触发
     */
    private boolean isManualTrigger(JobExecutionContext context) {
        return context.getJobDetail().getJobDataMap().containsKey("manualTrigger");
    }
}
