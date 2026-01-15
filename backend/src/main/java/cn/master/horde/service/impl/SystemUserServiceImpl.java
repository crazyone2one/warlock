package cn.master.horde.service.impl;

import cn.master.horde.entity.SystemUser;
import cn.master.horde.mapper.SystemUserMapper;
import cn.master.horde.service.SystemUserService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import static cn.master.horde.entity.table.SystemUserTableDef.SYSTEM_USER;

/**
 * 用户 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {

    @Override
    public SystemUser getUserInfoById(String id) {
        return queryChain().select(SYSTEM_USER.ID, SYSTEM_USER.USER_NAME, SYSTEM_USER.NICK_NAME,
                        SYSTEM_USER.EMAIL, SYSTEM_USER.PHONE, SYSTEM_USER.LAST_PROJECT_ID)
                .where(SystemUser::getId).eq(id).one();
    }
}
