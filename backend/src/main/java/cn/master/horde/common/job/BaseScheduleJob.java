package cn.master.horde.common.job;

import cn.master.horde.common.config.FileTransferConfiguration;
import cn.master.horde.common.service.SensorService;
import cn.master.horde.dao.ScheduleConfigParameter;
import cn.master.horde.dao.SlaveParameter;
import cn.master.horde.util.FileHelper;
import cn.master.horde.util.JsonHelper;
import com.mybatisflex.core.row.Row;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
public abstract class BaseScheduleJob implements Job {
    protected final SensorService sensorService;
    protected final FileHelper fileHelper;
    protected final FileTransferConfiguration fileTransferConfiguration;

    protected BaseScheduleJob(SensorService sensorService, FileHelper fileHelper, FileTransferConfiguration fileTransferConfiguration) {
        this.sensorService = sensorService;
        this.fileHelper = fileHelper;
        this.fileTransferConfiguration = fileTransferConfiguration;
    }

    protected String projectId;
    protected String projectNum;
    protected String projectName;
    protected ScheduleConfigParameter scheduleConfigParameter;

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        this.projectId = jobDataMap.getString("projectId");
        this.projectNum = jobDataMap.getString("projectNum");
        this.projectName = jobDataMap.getString("projectName");
        this.scheduleConfigParameter = JsonHelper.objectToType(ScheduleConfigParameter.class).apply(jobDataMap.get("config"));
        businessExecute(context);
    }


    protected abstract void businessExecute(JobExecutionContext context);

    protected List<Row> sourceRows(String key, String tableName) {
        return sensorService.getSensorFromRedis(projectNum, key, tableName);
    }

    protected SlaveParameter slaveConfig() {
        return fileTransferConfiguration.getSlaveConfigByResourceId(projectNum);
    }
}
