package cn.master.horde.service.impl;

import cn.master.horde.common.constants.UserRoleScope;
import cn.master.horde.dto.UserTableResponse;
import cn.master.horde.entity.SystemUser;
import cn.master.horde.entity.UserRole;
import cn.master.horde.entity.UserRoleRelation;
import cn.master.horde.mapper.UserRoleRelationMapper;
import cn.master.horde.service.UserRoleRelationService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户-角色关联表 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
@Service
public class UserRoleRelationServiceImpl extends ServiceImpl<UserRoleRelationMapper, UserRoleRelation> implements UserRoleRelationService {

    @Override
    public List<UserRoleRelation> selectByUserId(String userId) {
        return queryChain().where(UserRoleRelation::getUserId).eq(userId).list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserSystemGlobalRole(SystemUser user, String operator, List<String> roleList) {
        List<String> deleteRoleList = new ArrayList<>();
        List<UserRoleRelation> saveList = new ArrayList<>();
        List<UserRoleRelation> userRoleRelationList = selectGlobalRoleByUserId(user.getId());
        List<String> userSavedRoleIdList = userRoleRelationList.stream().map(UserRoleRelation::getRoleId).toList();
        // 获取要移除的权限
        for (String userSavedRoleId : userSavedRoleIdList) {
            if (!roleList.contains(userSavedRoleId)) {
                deleteRoleList.add(userSavedRoleId);
            }
        }
        // 获取要添加的权限
        for (String roleId : roleList) {
            if (!userSavedRoleIdList.contains(roleId)) {
                UserRoleRelation userRoleRelation = new UserRoleRelation();
                userRoleRelation.setUserId(user.getId());
                userRoleRelation.setRoleId(roleId);
                userRoleRelation.setSourceId(UserRoleScope.SYSTEM);
                userRoleRelation.setCreateUser(operator);
                saveList.add(userRoleRelation);
            }
        }
        if (CollectionUtils.isNotEmpty(deleteRoleList)) {
            List<String> deleteIdList = new ArrayList<>();
            userRoleRelationList.forEach(item -> {
                if (deleteRoleList.contains(item.getRoleId())) {
                    deleteIdList.add(item.getId());
                }
            });
            mapper.deleteBatchByIds(deleteIdList);
        }
        if (CollectionUtils.isNotEmpty(saveList)) {
            saveList.forEach(item -> mapper.insert(item));
        }
    }

    @Override
    public Map<String, UserTableResponse> selectGlobalUserRole(List<String> userIdList) {
        List<UserRoleRelation> userRoleRelationList = queryChain().where(UserRoleRelation::getUserId).in(userIdList).list();
        List<String> userRoleIdList = userRoleRelationList.stream().map(UserRoleRelation::getRoleId).distinct().toList();
        Map<String, UserRole> userRoleMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userRoleIdList)) {
            userRoleMap = QueryChain.of(UserRole.class)
                    .where(UserRole::getId).in(userRoleIdList).and(UserRole::getScopeId).eq(UserRoleScope.GLOBAL)
                    .list().stream()
                    .collect(Collectors.toMap(UserRole::getId, item -> item));
        }
        Map<String, UserTableResponse> returnMap = new HashMap<>();
        for (UserRoleRelation userRoleRelation : userRoleRelationList) {
            UserTableResponse userInfo = returnMap.get(userRoleRelation.getUserId());
            if (userInfo == null) {
                userInfo = new UserTableResponse();
                userInfo.setId(userRoleRelation.getUserId());
                returnMap.put(userRoleRelation.getUserId(), userInfo);
            }
            UserRole userRole = userRoleMap.get(userRoleRelation.getRoleId());
            if (userRole != null && UserRoleScope.SYSTEM.equalsIgnoreCase(userRole.getType())) {
                userInfo.setUserRole(userRole);
            }
        }
        return returnMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByUserIdList(List<String> userIdList) {
        mapper.deleteByQuery(queryChain().where(UserRoleRelation::getUserId).in(userIdList));
    }

    private List<UserRoleRelation> selectGlobalRoleByUserId(String userId) {
        return queryChain().where(UserRoleRelation::getUserId).eq(userId)
                .and(UserRoleRelation::getRoleId).in(
                        QueryChain.of(UserRole.class)
                                .where(UserRole::getType).eq("SYSTEM").and(UserRole::getScopeId).eq(UserRoleScope.GLOBAL)
                                .list().stream().map(UserRole::getId).toList()
                ).list();
    }
}
