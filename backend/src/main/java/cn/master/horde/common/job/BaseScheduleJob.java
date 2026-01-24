package cn.master.horde.common.job;

import cn.master.horde.common.service.SensorService;
import cn.master.horde.dto.ScheduleConfigParameter;
import cn.master.horde.dto.SlaveParameter;
import cn.master.horde.entity.ProjectParameter;
import cn.master.horde.util.FileHelper;
import cn.master.horde.util.JsonHelper;
import com.github.benmanes.caffeine.cache.Cache;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.row.Row;
import org.apache.commons.lang3.ObjectUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.List;

import static cn.master.horde.entity.table.ProjectParameterTableDef.PROJECT_PARAMETER;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
public abstract class BaseScheduleJob implements Job {
    protected final SensorService sensorService;
    protected final FileHelper fileHelper;
    protected final Cache<String, SlaveParameter> slaveCache;

    protected BaseScheduleJob(SensorService sensorService, FileHelper fileHelper, Cache<String, SlaveParameter> slaveCache) {
        this.sensorService = sensorService;
        this.fileHelper = fileHelper;
        this.slaveCache = slaveCache;
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

    /**
     * 获取从站配置
     *
     * @return 从站配置
     */
    protected SlaveParameter sshSlaveConfig() {
        SlaveParameter slaveParameter = slaveCache.getIfPresent(projectId);
        if (ObjectUtils.isNotEmpty(slaveParameter)) {
            return slaveParameter;
        }
        SlaveParameter parameter = QueryChain.of(ProjectParameter.class).where(PROJECT_PARAMETER.PROJECT_ID.eq(projectId))
                .and(PROJECT_PARAMETER.PARAMETER_TYPE.eq("ssh")).oneOpt()
                .map(ssh -> JsonHelper.objectToType(SlaveParameter.class).apply(ssh.getParameters()))
                .orElseThrow(() -> new RuntimeException("Slave parameter not found"));
        slaveCache.put(projectId, parameter);
        return parameter;
    }
}
