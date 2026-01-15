package cn.master.horde.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.horde.entity.SystemUser;
import cn.master.horde.mapper.SystemUserMapper;
import cn.master.horde.service.SystemUserService;
import org.springframework.stereotype.Service;

/**
 * 用户 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser>  implements SystemUserService{

}
