package cn.master.horde.scheduler.job;

import cn.master.horde.scheduler.BaseScheduleJob;
import cn.master.horde.common.service.SensorService;
import cn.master.horde.model.dto.SlaveParameter;
import cn.master.horde.common.util.FileHelper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@Slf4j
public class DemoJob extends BaseScheduleJob {
    protected DemoJob(SensorService sensorService, FileHelper fileHelper, Cache<String, SlaveParameter> slaveCache) {
        super(sensorService, fileHelper, slaveCache);
    }

    @Override
    protected void businessExecute(JobExecutionContext context) {
        SlaveParameter slaveParameter = sshSlaveConfig();
        log.info(slaveParameter.getHost());
        log.info(scheduleConfigParameter.getField("k1", String.class));
    }

    public static JobKey getJobKey(String projectId, String jobKey) {
        return new JobKey(jobKey, projectId);
    }

    public static TriggerKey getTriggerKey(String projectId, String jobKey) {
        return new TriggerKey(jobKey, projectId);
    }
}
