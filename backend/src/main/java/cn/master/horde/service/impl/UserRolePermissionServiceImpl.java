package cn.master.horde.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.horde.entity.UserRolePermission;
import cn.master.horde.mapper.UserRolePermissionMapper;
import cn.master.horde.service.UserRolePermissionService;
import org.springframework.stereotype.Service;

/**
 * 用户组权限 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-23
 */
@Service
public class UserRolePermissionServiceImpl extends ServiceImpl<UserRolePermissionMapper, UserRolePermission>  implements UserRolePermissionService{

}
