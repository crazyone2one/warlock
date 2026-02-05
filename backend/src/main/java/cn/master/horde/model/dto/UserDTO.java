package cn.master.horde.model.dto;

import cn.master.horde.model.dto.permission.UserRoleResourceDTO;
import cn.master.horde.model.entity.SystemUser;
import cn.master.horde.model.entity.UserRole;
import cn.master.horde.model.entity.UserRoleRelation;
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
