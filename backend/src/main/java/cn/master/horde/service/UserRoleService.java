package cn.master.horde.service;

import cn.master.horde.dto.permission.PermissionDefinitionItem;
import cn.master.horde.dto.request.PermissionSettingUpdateRequest;
import cn.master.horde.dto.request.UserRoleUpdateRequest;
import cn.master.horde.entity.UserRole;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 用户角色表 服务层。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
public interface UserRoleService extends IService<UserRole> {

    List<UserRole> list();

    void add(UserRoleUpdateRequest request);

    void updateUserRole(UserRoleUpdateRequest request);

    void checkGlobalUserRole(UserRole userRole);

    void delete(String id);

    List<PermissionDefinitionItem> getPermissionSetting(String id);

    void updatePermissionSetting(PermissionSettingUpdateRequest request);
}
