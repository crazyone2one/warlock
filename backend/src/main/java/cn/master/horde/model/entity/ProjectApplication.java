package cn.master.horde.model.entity;

import cn.master.horde.common.constants.Created;
import cn.master.horde.common.constants.Updated;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import java.io.Serial;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 项目应用 实体类。
 *
 * @author 11's papa
 * @since 2026-02-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "项目应用")
@Table("project_application")
public class ProjectApplication implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description =  "项目应用ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{project_version.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{project_version.id.length_range}", groups = {Created.class, Updated.class})
    private String id;

    @Schema(description =  "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{project_application.project_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{project_application.project_id.length_range}", groups = {Created.class, Updated.class})
    private String projectId;

    /**
     * 配置项
     */
    @Id
    @Schema(description = "配置项")
    private String type;

    /**
     * 配置值
     */
    @Schema(description = "配置值")
    private String typeValue;

}
