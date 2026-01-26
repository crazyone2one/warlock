package cn.master.horde.dto.permission;

import cn.master.horde.entity.UserRole;
import cn.master.horde.entity.UserRoleRelation;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/1/26, 星期一
 **/
@Data
public class UserRolePermissionDTO {
    List<UserRoleResourceDTO> list = new ArrayList<>();
    List<UserRole> userRoles = new ArrayList<>();
    List<UserRoleRelation> userRoleRelations = new ArrayList<>();
}
