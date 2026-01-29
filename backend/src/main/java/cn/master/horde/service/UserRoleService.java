package cn.master.horde.service;

import cn.master.horde.dto.UserSelectOption;
import cn.master.horde.dto.permission.PermissionDefinitionItem;
import cn.master.horde.dto.request.PermissionSettingUpdateRequest;
import cn.master.horde.dto.request.UserRoleUpdateRequest;
import cn.master.horde.entity.UserRole;
import cn.master.horde.entity.UserRoleRelation;
import com.mybatisflex.core.service.IService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 用户角色表 服务层。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
public interface UserRoleService extends IService<UserRole> {

    List<UserRole> list();

    UserRole add(UserRoleUpdateRequest request);

    UserRole updateUserRole(UserRoleUpdateRequest request);

    void checkGlobalUserRole(UserRole userRole);

    void delete(String id);

    List<PermissionDefinitionItem> getPermissionSetting(String id);

    void updatePermissionSetting(PermissionSettingUpdateRequest request);

    void checkRoleIsGlobalAndHaveMember(@Valid @NotEmpty List<String> roleIdList, boolean isSystem);

    List<UserRole> selectByUserRoleRelations(List<UserRoleRelation> userRoleRelations);

    List<UserSelectOption> getSystemRoleList();
}
