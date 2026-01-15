package cn.master.horde.job;

import cn.master.horde.common.config.FileTransferConfiguration;
import cn.master.horde.common.job.BaseScheduleJob;
import cn.master.horde.common.service.SensorService;
import cn.master.horde.util.FileHelper;
import org.quartz.JobExecutionContext;

import java.time.LocalDateTime;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
public class DemoJob extends BaseScheduleJob {
    protected DemoJob(SensorService sensorService, FileHelper fileHelper, FileTransferConfiguration fileTransferConfiguration) {
        super(sensorService, fileHelper, fileTransferConfiguration);
    }

    @Override
    protected void businessExecute(JobExecutionContext context) {
        System.out.println("DemoJob execute" + LocalDateTime.now());
    }
}
