package cn.master.horde.dto.permission;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : 11's papa
 * @since : 2026/1/23, 星期五
 **/
@Data
@NoArgsConstructor
@Schema(description = "权限信息")
public class Permission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "权限ID")
    private String id;

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "是否启用该权限")
    private Boolean enable = false;

    @JsonCreator
    public Permission(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("enable") Boolean enable) {
        this.id = id;
        this.name = name != null ? name : "";
        this.enable = enable != null ? enable : false;
    }
}
