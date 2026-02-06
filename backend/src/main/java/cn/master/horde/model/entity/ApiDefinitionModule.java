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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接口模块 实体类。
 *
 * @author 11's papa
 * @since 2026-02-05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "接口模块")
@Table("api_definition_module")
public class ApiDefinitionModule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "接口模块pk", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{api_definition_module.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{api_definition_module.id.length_range}", groups = {Created.class, Updated.class})
    private String id;

    @Schema(description = "模块名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{api_definition_module.name.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 255, message = "{api_definition_module.name.length_range}", groups = {Created.class, Updated.class})
    private String name;

    @Schema(description = "父级fk", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{api_definition_module.parent_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{api_definition_module.parent_id.length_range}", groups = {Created.class, Updated.class})
    private String parentId;

    @Schema(description = "项目fk", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{api_definition_module.project_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{api_definition_module.project_id.length_range}", groups = {Created.class, Updated.class})
    private String projectId;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{api_definition_module.pos.not_blank}", groups = {Created.class})
    private Long pos;

    /**
     * 创建时间
     */
    @Column(onInsertValue = "now()")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updateUser;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createUser;

}
