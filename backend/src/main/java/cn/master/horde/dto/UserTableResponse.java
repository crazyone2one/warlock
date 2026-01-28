package cn.master.horde.dto;

import cn.master.horde.entity.SystemUser;
import cn.master.horde.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/1/28, 星期三
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserTableResponse extends SystemUser {
    @Schema(description =  "用户所属用户组")
    private List<UserRole> userRoleList = new ArrayList<>();
    public void setUserRole(UserRole userRole) {
        if (!userRoleList.contains(userRole)) {
            userRoleList.add(userRole);
        }
    }
}
