package cn.master.horde.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.core.handler.JacksonTypeHandler;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/1/16, 星期五
 **/
@Data
public class ScheduleDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String jobKey;
    private String projectName;

    private String projectId;

    private String id;

    private String name;

    private Long num;

    private String resourceType;

    private String value;

    private Long lastTime;

    private String triggerStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public LocalDateTime getLastTimeAsLocalDateTime() {
        if (lastTime != null && lastTime > 0) {
            return Instant.ofEpochMilli(lastTime).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return null;
    }


    private Long nextTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public LocalDateTime getNextTimeAsLocalDateTime() {
        if (nextTime != null && nextTime > 0) {
            return Instant.ofEpochMilli(nextTime).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return null;
    }


    private boolean enable;


    private String createUserId;


    private String createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Column(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> runConfig;
}
