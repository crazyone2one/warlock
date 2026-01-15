package cn.master.horde.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 定时任务 实体类。
 *
 * @author 11's papa
 * @since 2026-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("system_schedule")
public class SystemSchedule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 业务ID
     */
    private Long num;

    /**
     * Quartz任务UUID
     */
    private String jobKey;

    /**
     * 执行类型（如：cron）
     */
    private String type;

    /**
     * Cron表达式
     */
    private String value;

    /**
     * Schedule Job类名
     */
    private String job;

    /**
     * 项目ID
     */
    private String projectId;

    /**
     * 是否开启（0-否，1-是）
     */
    private Boolean enable;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 创建时间（毫秒精度）
     */
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    /**
     * 更新时间（毫秒精度）
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private LocalDateTime updateTime;

    /**
     * 配置（JSON格式）
     */
    @Column(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> config;

    /**
     * 逻辑删除（0-未删，1-已删）
     */
    private Boolean deleted;

}
