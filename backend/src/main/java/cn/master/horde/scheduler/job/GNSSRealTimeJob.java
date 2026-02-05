package cn.master.horde.scheduler.job;

import cn.master.horde.common.constants.WkkSensorEnum;
import cn.master.horde.scheduler.BaseScheduleJob;
import cn.master.horde.common.service.SensorService;
import cn.master.horde.model.dto.SlaveParameter;
import cn.master.horde.common.util.DateFormatUtils;
import cn.master.horde.common.util.FileHelper;
import cn.master.horde.common.util.JsonHelper;
import cn.master.horde.common.util.RandomUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.mybatisflex.core.row.Row;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.jspecify.annotations.NullMarked;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author : 11's papa
 * @since : 2026/1/21, 星期三
 **/
public class GNSSRealTimeJob extends BaseScheduleJob {
    @NullMarked
    protected GNSSRealTimeJob(SensorService sensorService, FileHelper fileHelper, Cache<String, SlaveParameter> slaveCache) {
        super(sensorService, fileHelper, slaveCache);
    }

    @Override
    protected void businessExecute(JobExecutionContext context) {
        final LocalDateTime now = LocalDateTime.now(ZoneOffset.of("+8"));
        List<Row> rows = sourceRows(WkkSensorEnum.GNSS_REAL_TIME.getCacheKey(), WkkSensorEnum.GNSS_REAL_TIME.getTableName());
        List<Row> effectiveSensor = rows.stream()
                .filter(s -> BooleanUtils.isFalse(s.getBoolean("deleted")))
                .filter(s -> s.getInt("in_use") == 1)
                .filter(s -> s.getInt("breakdown") == 0)
                .toList();
        if (CollectionUtils.isEmpty(effectiveSensor)) {
            return;
        }
        String fileName = projectNum + "_" + WkkSensorEnum.GNSS_REAL_TIME.getFileCode() + "_"
                + DateFormatUtils.localDateTimeToString(now) + "_"
                + RandomUtils.generateRandomIntegerByLength(4) + ".json";
        Map<String, Object> content = new LinkedHashMap<>();
        content.put("send_time", DateFormatUtils.toTimePatternString(now));
        content.put("open_pit_no", projectNum);
        content.put("data", contentData(effectiveSensor, now));
        SlaveParameter slaveParameter = sshSlaveConfig();
        String filePath = fileHelper.filePath(slaveParameter.getLocalPath(), projectNum, WkkSensorEnum.GNSS_REAL_TIME.getCacheKey(), fileName);
        fileHelper.generateFile(filePath, JsonHelper.objectToType(String.class).apply(content));
        fileHelper.uploadFile(slaveParameter, filePath, slaveParameter.getRemotePath() + File.separator + WkkSensorEnum.GNSS_REAL_TIME.getCacheKey());
    }

    private List<Map<String, Object>> contentData(List<Row> sensorInRedis, LocalDateTime now) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sensorCode = scheduleConfigParameter.getField("sensorCode", String.class);

        for (Row s : sensorInRedis) {
            String equipNo = s.getString("equip_no");
            AtomicReference<String> disp = new AtomicReference<>("1.0");
            if (equipNo.equals(sensorCode)) {
                disp.set(Optional.ofNullable(scheduleConfigParameter.getField("disp", String.class)).orElse("1.0"));
            }
            Map<String, Object> content = new LinkedHashMap<>();
            content.put("equip_no", equipNo);
            content.put("monitor_time", DateFormatUtils.toTimePatternString(now));
            content.put("longitude", s.getString("longitude"));
            content.put("latitude", s.getString("latitude"));
            content.put("altitude", s.getString("altitude"));
            content.put("x_disp", disp.get());
            content.put("y_disp", disp.get());
            content.put("z_disp", disp.get());
            content.put("x_speed", "1.4");
            content.put("y_speed", "1.5");
            content.put("z_speed", "1.6");
            content.put("x_acc_speed", "1.7");
            content.put("y_acc_speed", "1.8");
            content.put("z_acc_speed", "1.9");
            result.add(content);
        }

        return result;
    }

    public static JobKey getJobKey(String projectId, String jobKey) {
        return new JobKey(jobKey, projectId);
    }

    public static TriggerKey getTriggerKey(String projectId, String jobKey) {
        return new TriggerKey(jobKey, projectId);
    }
}
