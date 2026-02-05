package cn.master.horde.service;

import cn.master.horde.model.dto.request.PermissionSettingUpdateRequest;
import com.mybatisflex.core.service.IService;
import cn.master.horde.model.entity.UserRolePermission;

/**
 * 用户组权限 服务层。
 *
 * @author 11's papa
 * @since 2026-01-23
 */
public interface UserRolePermissionService extends IService<UserRolePermission> {

    void updatePermissionSetting(PermissionSettingUpdateRequest request);
}
