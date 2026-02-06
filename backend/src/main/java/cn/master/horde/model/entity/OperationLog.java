package cn.master.horde.model.entity;

import cn.master.horde.common.constants.Created;
import cn.master.horde.common.constants.Updated;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作日志表 实体类。
 *
 * @author 11's papa
 * @since 2026-02-05
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

    @Id
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{operation_log.id.not_blank}", groups = {Updated.class})
    private String id;

    /**
     * 链路ID（用于全链路追踪）
     */
    @Schema(description = "链路ID（用于全链路追踪）")
    private String traceId;

    @Schema(description = "操作方法", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{operation_log.method.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 255, message = "{operation_log.method.length_range}", groups = {Created.class, Updated.class})
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

    /**
     * 项目id
     */
    @Schema(description = "项目id")
    private String projectId;

    /**
     * 资源id
     */
    @Schema(description = "资源id")
    private String sourceId;

    @Schema(description =  "操作类型/add/update/delete", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{operation_log.type.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 20, message = "{operation_log.type.length_range}", groups = {Created.class, Updated.class})
    private String type;

    /**
     * 操作模块/api/case/scenario/ui
     */
    @Schema(description = "操作模块/api/case/scenario/ui")
    private String module;

    /**
     * 操作详情
     */
    @Schema(description = "操作详情")
    private String content;

    /**
     * 变更前内容
     */
    @Schema(description = "变更前内容")
    private byte[] originalValue;

    /**
     * 变更后内容
     */
    @Schema(description = "变更后内容")
    private byte[] modifiedValue;

    /**
     * 操作人
     */
    @Schema(description = "操作人")
    private String createUser;

}
