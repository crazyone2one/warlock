package cn.master.horde.service.impl;

import cn.master.horde.common.constants.UserRoleScope;
import cn.master.horde.common.result.BizException;
import cn.master.horde.model.dto.UserExcludeOptionDTO;
import cn.master.horde.model.dto.UserRoleRelationUserDTO;
import cn.master.horde.model.dto.UserTableResponse;
import cn.master.horde.model.dto.request.UserRoleRelationQueryRequest;
import cn.master.horde.model.dto.request.UserRoleRelationUpdateRequest;
import cn.master.horde.model.entity.SystemUser;
import cn.master.horde.model.entity.UserRole;
import cn.master.horde.model.entity.UserRoleRelation;
import cn.master.horde.model.mapper.UserRoleRelationMapper;
import cn.master.horde.service.UserRoleRelationService;
import cn.master.horde.service.UserRoleService;
import cn.master.horde.common.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.horde.common.result.SystemResultCode.GLOBAL_USER_ROLE_LIMIT;
import static cn.master.horde.common.result.SystemResultCode.USER_ROLE_RELATION_EXIST;
import static cn.master.horde.model.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.horde.model.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;

/**
 * 用户-角色关联表 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
@Service
@RequiredArgsConstructor
public class UserRoleRelationServiceImpl extends ServiceImpl<UserRoleRelationMapper, UserRoleRelation> implements UserRoleRelationService {
    private final UserRoleService userRoleService;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(UserRoleRelationUpdateRequest request) {
        checkGlobalSystemUserRoleLegality(Collections.singletonList(request.getRoleId()));
        checkUserLegality(request.getUserIds());
        List<UserRoleRelation> userRoleRelations = new ArrayList<>();
        request.getUserIds().forEach(userId -> {
            UserRoleRelation userRoleRelation = new UserRoleRelation();
            BeanUtils.copyProperties(request, userRoleRelation);
            userRoleRelation.setUserId(userId);
            userRoleRelation.setSourceId(UserRoleScope.SYSTEM);
            checkExist(userRoleRelation);
            userRoleRelations.add(userRoleRelation);
        });
        mapper.insertBatch(userRoleRelations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        UserRoleRelation userRoleRelation = mapper.selectOneById(id);
        UserRole userRole = userRoleService.getById(userRoleRelation.getRoleId());
        userRoleService.checkResourceExist(userRole);
        userRoleService.checkSystemUserGroup(userRole);
        userRoleService.checkGlobalUserRole(userRole);
        mapper.deleteById(id);
        long count = queryChain().where(UserRoleRelation::getUserId).eq(userRoleRelation.getUserId())
                .and(UserRoleRelation::getSourceId).eq(UserRoleScope.SYSTEM)
                .count();
        if (count == 0) {
            throw new BizException(GLOBAL_USER_ROLE_LIMIT);
        }
    }

    @Override
    public Page<UserRoleRelationUserDTO> pageDTO(UserRoleRelationQueryRequest request) {
        Page<UserRoleRelationUserDTO> page = queryChain().select(USER_ROLE_RELATION.ID)
                .select(SYSTEM_USER.ID.as("userId"), SYSTEM_USER.NAME.as("name"), SYSTEM_USER.EMAIL, SYSTEM_USER.PHONE)
                .from(USER_ROLE_RELATION)
                .innerJoin(SYSTEM_USER).on(USER_ROLE_RELATION.USER_ID.eq(SYSTEM_USER.ID)
                        .and(USER_ROLE_RELATION.ROLE_ID.eq(request.getRoleId())))
                .orderBy(USER_ROLE_RELATION.CREATE_TIME.desc())
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), UserRoleRelationUserDTO.class);
        UserRole userRole = userRoleService.getById(request.getRoleId());
        userRoleService.checkSystemUserGroup(userRole);
        userRoleService.checkGlobalUserRole(userRole);
        return page;
    }

    @Override
    public List<UserExcludeOptionDTO> getExcludeSelectOption(String roleId, String keyword) {
        userRoleService.checkResourceExist(userRoleService.getById(roleId));
        // 查询所有用户选项
        List<UserExcludeOptionDTO> selectOptions = getExcludeSelectOptionWithLimit(keyword);
        // 查询已经关联的用户ID
        List<String> excludeUserIds = queryChain().where(UserRoleRelation::getRoleId).eq(roleId).list().stream()
                .map(UserRoleRelation::getUserId).toList();
        selectOptions.forEach((excludeOption) -> {
            if (excludeUserIds.contains(excludeOption.getId())) {
                excludeOption.setExclude(true);
                excludeOption.setDisabled(true);
            }
        });
        return selectOptions;
    }

    private List<UserExcludeOptionDTO> getExcludeSelectOptionWithLimit(String keyword) {
        return QueryChain.of(SystemUser.class)
                .select(SYSTEM_USER.ID, SYSTEM_USER.NAME, SYSTEM_USER.EMAIL)
                .from(SYSTEM_USER)
                .where(SYSTEM_USER.NAME.like(keyword).or(SYSTEM_USER.EMAIL.like(keyword)))
                .limit(100)
                .listAs(UserExcludeOptionDTO.class);
    }

    private void checkGlobalSystemUserRoleLegality(List<String> checkIdList) {
        List<UserRole> userRoleList = QueryChain.of(UserRole.class).where(UserRole::getId).in(checkIdList).list();
        if (userRoleList.size() != checkIdList.size()) {
            throw new BizException(Translator.get("user_role_not_exist"));
        }
        userRoleList.forEach(userRole -> {
            userRoleService.checkSystemUserGroup(userRole);
            userRoleService.checkGlobalUserRole(userRole);
        });
    }

    private void checkUserLegality(List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            throw new BizException(Translator.get("user.not.exist"));
        }
        long count = QueryChain.of(SystemUser.class).where(SystemUser::getId).in(userIds).count();
        if (count != userIds.size()) {
            throw new BizException(Translator.get("user.id.not.exist"));
        }
    }

    private void checkExist(UserRoleRelation userRoleRelation) {
        if (queryChain().where(UserRoleRelation::getUserId).eq(userRoleRelation.getUserId())
                .and(UserRoleRelation::getRoleId).eq(userRoleRelation.getRoleId()).count() > 0) {
            throw new BizException(USER_ROLE_RELATION_EXIST);
        }
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
