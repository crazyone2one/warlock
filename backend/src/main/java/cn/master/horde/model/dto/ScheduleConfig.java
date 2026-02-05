package cn.master.horde.model.dto;

import cn.master.horde.model.entity.SystemSchedule;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@Data
@Builder
public class ScheduleConfig {
    private String key;

    private String projectId;

    private String name;

    private Boolean enable;

    private String cron;

    private String resourceType;

    private Map<String, Object> config;

    public SystemSchedule genCronSchedule(SystemSchedule schedule) {
        if (schedule == null) {
            schedule = new SystemSchedule();
        }
        schedule.setName(this.getName());
        schedule.setType("CRON");
        schedule.setJobKey(this.getKey());
        schedule.setEnable(this.getEnable());
        schedule.setProjectId(this.getProjectId());
        schedule.setValue(this.getCron());
        schedule.setResourceType(this.getResourceType());
        schedule.setConfig(this.getConfig());
        return schedule;
    }
}
