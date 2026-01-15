package cn.master.horde.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.horde.entity.UserRole;
import cn.master.horde.mapper.UserRoleMapper;
import cn.master.horde.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户角色表 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole>  implements UserRoleService{

}
