package cn.master.horde.service;

import cn.master.horde.dto.UserTableResponse;
import cn.master.horde.entity.SystemUser;
import cn.master.horde.entity.UserRoleRelation;
import com.mybatisflex.core.service.IService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

/**
 * 用户-角色关联表 服务层。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
public interface UserRoleRelationService extends IService<UserRoleRelation> {

    List<UserRoleRelation> selectByUserId(String userId);

    void updateUserSystemGlobalRole(@Valid SystemUser user, @Valid @NotEmpty String operator, @Valid @NotEmpty List<String> roleList);

    Map<String, UserTableResponse> selectGlobalUserRole(List<String> userIdList);

    void deleteByUserIdList(List<String> userIdList);
}
