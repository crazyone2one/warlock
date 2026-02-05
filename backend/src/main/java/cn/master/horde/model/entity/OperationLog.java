package cn.master.horde.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作日志表 实体类。
 *
 * @author 11's papa
 * @since 2026-01-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "操作日志表")
@Table("operation_log")
public class OperationLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @Schema(description = "主键")
    private String id;

    /**
     * 链路ID（用于全链路追踪）
     */
    @Schema(description = "链路ID（用于全链路追踪）")
    private String traceId;

    /**
     * 方法签名
     */
    @Schema(description = "方法签名")
    private String methodName;

    /**
     * 请求URI
     */
    @Schema(description = "请求URI")
    private String uri;

    /**
     * HTTP方法（GET/POST等）
     */
    @Schema(description = "HTTP方法（GET/POST等）")
    private String httpMethod;

    /**
     * 方法参数（JSON格式）
     */
    @Schema(description = "方法参数（JSON格式）")
    private String args;

    /**
     * 返回结果（JSON格式）
     */
    @Schema(description = "返回结果（JSON格式）")
    private String result;

    /**
     * 异常信息
     */
    @Schema(description = "异常信息")
    private String exception;

    /**
     * 执行耗时（毫秒）
     */
    @Schema(description = "执行耗时（毫秒）")
    private Long durationMs;

    /**
     * 创建时间（精确到毫秒）
     */
    @Column(onInsertValue = "now()")
    @Schema(description = "创建时间（精确到毫秒）")
    private LocalDateTime createTime;

}
