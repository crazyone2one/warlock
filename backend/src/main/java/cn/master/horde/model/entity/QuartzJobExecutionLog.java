package cn.master.horde.model.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体类。
 *
 * @author 11's papa
 * @since 2026-02-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "")
@Table("quartz_job_execution_log")
public class QuartzJobExecutionLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "ID")
    private String id;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "任务组")
    private String jobGroup;

    @Schema(description = "bean名称")
    private String beanName;

    @Schema(description = "方法名称")
    private String methodName;

    @Schema(description = "参数")
    private String paramsJson;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "耗时（毫秒）")
    private Long durationMs;

    @Schema(description = "status")
    private Status status;

    @Schema(description = "失败时的异常消息（非完整堆栈）")
    private String errorMessage;

    @Schema(description = "完整堆栈（可选，占用空间大）")
    private String stackTrace;

    @Schema(description = "SCHEDULED / MANUAL")
    private String triggerType;

    @Schema(description = "执行服务器（集群环境有用）")
    private String serverHost;

    public enum Status {
        SUCCESS, FAILED
    }
}
