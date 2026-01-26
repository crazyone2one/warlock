package cn.master.horde.service;

import com.mybatisflex.core.service.IService;
import cn.master.horde.entity.UserRoleRelation;

import java.util.List;

/**
 * 用户-角色关联表 服务层。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
public interface UserRoleRelationService extends IService<UserRoleRelation> {

    List<UserRoleRelation> selectByUserId(String userId);
}
