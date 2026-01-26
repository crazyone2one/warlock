package cn.master.horde.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.horde.entity.UserRoleRelation;
import cn.master.horde.mapper.UserRoleRelationMapper;
import cn.master.horde.service.UserRoleRelationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户-角色关联表 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
@Service
public class UserRoleRelationServiceImpl extends ServiceImpl<UserRoleRelationMapper, UserRoleRelation>  implements UserRoleRelationService{

    @Override
    public List<UserRoleRelation> selectByUserId(String userId) {
        return queryChain().where(UserRoleRelation::getUserId).eq(userId).list();
    }
}
