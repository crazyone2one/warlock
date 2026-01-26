package cn.master.horde.dto;

import cn.master.horde.dto.permission.UserRoleResourceDTO;
import cn.master.horde.entity.SystemUser;
import cn.master.horde.entity.UserRole;
import cn.master.horde.entity.UserRoleRelation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/1/26, 星期一
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserDTO extends SystemUser {
    private List<UserRole> userRoles = new ArrayList<>();
    private List<UserRoleRelation> userRoleRelations = new ArrayList<>();
    private List<UserRoleResourceDTO> userRolePermissions = new ArrayList<>();
}
