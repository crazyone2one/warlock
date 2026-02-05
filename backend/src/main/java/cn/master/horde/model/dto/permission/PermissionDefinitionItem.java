package cn.master.horde.model.dto.permission;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/1/23, 星期五
 **/
@Data
@NoArgsConstructor
@Schema(description = "权限设置菜单项")
public class PermissionDefinitionItem {
    @Schema(description = "菜单项ID")
    private String id;

    @Schema(description = "菜单所属类型 SYSTEM PROJECT")
    private String type;

    @Schema(description = "菜单项名称")
    private String name;

    @Schema(description = "菜单是否全选")
    private Boolean enable = false;

    @Schema(description = "菜单下的权限列表")
    private List<Permission> permissions;

    @Schema(description = "子菜单")
    private List<PermissionDefinitionItem> children;

    @Schema(description = "排序")
    private Integer order;
    
    @JsonCreator
    public PermissionDefinitionItem(
            @JsonProperty("id") String id,
            @JsonProperty("type") String type,
            @JsonProperty("name") String name,
            @JsonProperty("enable") Boolean enable,
            @JsonProperty("permissions") List<Permission> permissions,
            @JsonProperty("children") List<PermissionDefinitionItem> children,
            @JsonProperty("order") Integer order) {
        this.id = id;
        this.type = type != null ? type : "";
        this.name = name != null ? name : "";
        this.enable = enable != null ? enable : false;
        this.permissions = permissions;
        this.children = children;
        this.order = order != null ? order : 0;
    }

    // 添加一个简单的构造函数用于框架反射
    public PermissionDefinitionItem(String id) {
        this.id = id;
    }
}
