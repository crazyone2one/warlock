package cn.master.horde.service.impl;

import cn.master.horde.dto.request.PermissionSettingUpdateRequest;
import cn.master.horde.entity.UserRolePermission;
import cn.master.horde.mapper.UserRolePermissionMapper;
import cn.master.horde.service.UserRolePermissionService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.master.horde.entity.table.UserRolePermissionTableDef.USER_ROLE_PERMISSION;

/**
 * 用户组权限 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-23
 */
@Service
public class UserRolePermissionServiceImpl extends ServiceImpl<UserRolePermissionMapper, UserRolePermission> implements UserRolePermissionService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermissionSetting(PermissionSettingUpdateRequest request) {
        List<PermissionSettingUpdateRequest.PermissionUpdateRequest> permissions = request.getPermissions();
        // 先删除 (排除内置基本信息用户组)
        QueryChain<UserRolePermission> chain = queryChain()
                .where(USER_ROLE_PERMISSION.ROLE_ID.eq(request.getUserRoleId())
                        .and(USER_ROLE_PERMISSION.PERMISSION_ID.ne("PROJECT_BASE_INFO:READ")));
        mapper.deleteByQuery(chain);
        // 再新增
        String groupId = request.getUserRoleId();
        permissions.forEach(permission -> {
            if (BooleanUtils.isTrue(permission.getEnable())) {
                String permissionId = permission.getId();
                UserRolePermission groupPermission = new UserRolePermission();
                groupPermission.setRoleId(groupId);
                groupPermission.setPermissionId(permissionId);
                mapper.insert(groupPermission);
            }
        });
    }
}
