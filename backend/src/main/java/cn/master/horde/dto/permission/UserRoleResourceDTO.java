package cn.master.horde.dto.permission;

import cn.master.horde.entity.UserRole;
import cn.master.horde.entity.UserRolePermission;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/1/26, 星期一
 **/
@Data
public class UserRoleResourceDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UserRoleResource resource;
    private List<UserRolePermission> permissions;
    private String type;

    private UserRole userRole;
    private List<UserRolePermission> userRolePermissions;
}
