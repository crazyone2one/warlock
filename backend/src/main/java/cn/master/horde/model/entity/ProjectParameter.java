package cn.master.horde.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 项目级参数 实体类。
 *
 * @author 11's papa
 * @since 2026-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "项目级参数")
@Table("project_parameter")
public class ProjectParameter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID（UUID）
     */
    @Id
    @Schema(description = "ID（UUID）")
    private String id;

    /**
     * 项目ID（UUID）
     */
    @Schema(description = "项目ID（UUID）")
    private String projectId;

    /**
     * 参数类型
     */
    @Schema(description = "参数类型")
    private String parameterType;

    /**
     * 参数配置（JSON格式）
     */
    @Schema(description = "参数配置（JSON格式）")
    @Column(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> parameters;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createUser;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updateUser;

    /**
     * 创建时间
     */
    @Column(onInsertValue = "now()")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0-未删，1-已删）
     */
    @Schema(description = "逻辑删除（0-未删，1-已删）")
    private Boolean deleted;

}
