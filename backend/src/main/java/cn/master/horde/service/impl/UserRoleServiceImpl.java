package cn.master.horde.service.impl;

import cn.master.horde.common.constants.InternalUserRole;
import cn.master.horde.common.constants.UserRoleScope;
import cn.master.horde.common.constants.UserRoleType;
import cn.master.horde.common.result.BizException;
import cn.master.horde.common.service.CurrentUserService;
import cn.master.horde.dto.permission.Permission;
import cn.master.horde.dto.permission.PermissionCache;
import cn.master.horde.dto.permission.PermissionDefinitionItem;
import cn.master.horde.dto.request.PermissionSettingUpdateRequest;
import cn.master.horde.dto.request.UserRoleUpdateRequest;
import cn.master.horde.entity.UserRole;
import cn.master.horde.entity.UserRolePermission;
import cn.master.horde.entity.UserRoleRelation;
import cn.master.horde.mapper.UserRoleMapper;
import cn.master.horde.mapper.UserRolePermissionMapper;
import cn.master.horde.mapper.UserRoleRelationMapper;
import cn.master.horde.service.UserRolePermissionService;
import cn.master.horde.service.UserRoleService;
import cn.master.horde.util.JsonHelper;
import cn.master.horde.util.ServiceUtils;
import cn.master.horde.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.horde.common.constants.InternalUserRole.MEMBER;
import static cn.master.horde.common.result.SystemResultCode.*;
import static cn.master.horde.entity.table.UserRolePermissionTableDef.USER_ROLE_PERMISSION;
import static cn.master.horde.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.horde.entity.table.UserRoleTableDef.USER_ROLE;

/**
 * 用户角色表 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
    private final UserRolePermissionMapper userRolePermissionMapper;
    private final UserRoleRelationMapper userRoleRelationMapper;
    private final PermissionCache permissionCache;
    private final UserRolePermissionService userRolePermissionService;

    @Override
    public List<UserRole> list() {
        List<UserRole> userRoles = queryChain().where(USER_ROLE.SCOPE_ID.eq(UserRoleScope.GLOBAL)).list();
        userRoles.sort(Comparator.comparingInt(this::getTypeOrder)
                .thenComparingInt(item -> getInternal(item.getInternal()))
                .thenComparing(UserRole::getCreateTime));
        return userRoles;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(UserRoleUpdateRequest request) {
        UserRole userRole = new UserRole();
        BeanUtils.copyProperties(request, userRole);
        userRole.setCreateUser(CurrentUserService.getCurrentUserId());
        userRole.setInternal(false);
        userRole.setScopeId(UserRoleScope.GLOBAL);
        checkExist(userRole);
        mapper.insertSelective(userRole);
        if (Strings.CS.equals(userRole.getType(), UserRoleType.PROJECT.name())) {
            // 项目级别用户组, 初始化基本信息权限
            UserRolePermission initPermission = new UserRolePermission();
            initPermission.setRoleId(userRole.getId());
            initPermission.setPermissionId("PROJECT_BASE_INFO:READ");
            userRolePermissionService.save(initPermission);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRole(UserRoleUpdateRequest request) {
        UserRole userRole = new UserRole();
        BeanUtils.copyProperties(request, userRole);
        UserRole originUserRole = getWithCheck(userRole.getId());
        checkGlobalUserRole(originUserRole);
        checkInternalUserRole(originUserRole);
        userRole.setInternal(false);
        checkExist(userRole);
        mapper.update(userRole);
    }

    @Override
    public void checkGlobalUserRole(UserRole userRole) {
        if (!Strings.CS.equals(userRole.getScopeId(), UserRoleScope.GLOBAL)) {
            throw new BizException(GLOBAL_USER_ROLE_PERMISSION);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        UserRole userRole = getWithCheck(id);
        checkGlobalUserRole(userRole);
        checkInternalUserRole(userRole);
        QueryChain<UserRolePermission> where = QueryChain.of(UserRolePermission.class).where(USER_ROLE_PERMISSION.ROLE_ID.eq(id));
        userRolePermissionMapper.deleteByQuery(where);
        mapper.deleteById(id);
        // 检查是否只有一个用户组，如果是则添加系统成员等默认用户组
        checkOneLimitRole(id, MEMBER.getValue(), CurrentUserService.getCurrentUserId(), UserRoleScope.SYSTEM);
        QueryChain<UserRoleRelation> relationWhere = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.ROLE_ID.eq(id));
        userRoleRelationMapper.deleteByQuery(relationWhere);

    }

    @Override
    public List<PermissionDefinitionItem> getPermissionSetting(String id) {
        UserRole userRole = getWithCheck(id);
        checkGlobalUserRole(userRole);
        // 获取该用户组拥有的权限
        Set<String> permissionIds = QueryChain.of(UserRolePermission.class).where(USER_ROLE_PERMISSION.ROLE_ID.eq(id)).list()
                .stream().map(UserRolePermission::getPermissionId).collect(Collectors.toSet());
        // 获取所有的权限
        List<PermissionDefinitionItem> permissionDefinition = permissionCache.getPermissionDefinition();
        permissionDefinition = JsonHelper.parseArray(JsonHelper.objectToString(permissionDefinition), PermissionDefinitionItem.class);
        // 过滤该用户组级别的菜单，例如系统级别 (管理员返回所有权限位)
        permissionDefinition = permissionDefinition.stream()
                .filter(item -> Strings.CS.equals(item.getType(), userRole.getType()) || Strings.CS.equals(userRole.getCode(), InternalUserRole.ADMIN.getValue()))
                .sorted(Comparator.comparing(PermissionDefinitionItem::getOrder))
                .collect(Collectors.toList());
        // 设置勾选项
        for (PermissionDefinitionItem firstLevel : permissionDefinition) {
            List<PermissionDefinitionItem> children = firstLevel.getChildren();
            boolean allCheck = true;
            firstLevel.setName(Translator.get(firstLevel.getName()));
            for (PermissionDefinitionItem secondLevel : children) {
                List<Permission> permissions = secondLevel.getPermissions();
                secondLevel.setName(Translator.get(secondLevel.getName()));
                if (CollectionUtils.isEmpty(permissions)) {
                    continue;
                }
                boolean secondAllCheck = true;
                for (Permission p : permissions) {
                    if (StringUtils.isNotBlank(p.getName())) {
                        // 有 name 字段翻译 name 字段
                        p.setName(Translator.get(p.getName()));
                    } else {
                        p.setName(translateDefaultPermissionName(p));
                    }
                    // 管理员默认勾选全部二级权限位
                    if (permissionIds.contains(p.getId()) || Strings.CS.equals(userRole.getCode(), InternalUserRole.ADMIN.getValue())) {
                        p.setEnable(true);
                    } else {
                        // 如果权限有未勾选，则二级菜单设置为未勾选
                        p.setEnable(false);
                        secondAllCheck = false;
                    }
                }
                secondLevel.setEnable(secondAllCheck);
                if (!secondAllCheck) {
                    // 如果二级菜单有未勾选，则一级菜单设置为未勾选
                    allCheck = false;
                }
            }
            firstLevel.setEnable(allCheck);
        }
        return permissionDefinition;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermissionSetting(PermissionSettingUpdateRequest request) {
        UserRole userRole = getWithCheck(request.getUserRoleId());
        checkGlobalUserRole(userRole);
        // 内置管理员级别用户组无法更改权限
        checkAdminUserRole(userRole);
        userRolePermissionService.updatePermissionSetting(request);
    }

    private void checkAdminUserRole(UserRole userRole) {
        if (Strings.CS.equalsAny(userRole.getCode(), InternalUserRole.ADMIN.getValue(),
                InternalUserRole.ORG_ADMIN.getValue(), InternalUserRole.PROJECT_ADMIN.getValue())) {
            throw new BizException(ADMIN_USER_ROLE_PERMISSION);
        }
    }

    private String translateDefaultPermissionName(Permission p) {
        String[] idSplit = p.getId().split(":");
        String permissionKey = idSplit[idSplit.length - 1];
        Map<String, String> translationMap = new HashMap<>() {{
            put("READ", "permission.read");
            put("READ+ADD", "permission.add");
            put("READ+UPDATE", "permission.edit");
            put("READ+DELETE", "permission.delete");
            put("READ+IMPORT", "permission.import");
            put("READ+RECOVER", "permission.recover");
            put("READ+EXPORT", "permission.export");
            put("READ+EXECUTE", "permission.execute");
            put("READ+DEBUG", "permission.debug");
        }};
        return Translator.get(translationMap.get(permissionKey));
    }

    private void checkOneLimitRole(String roleId, String defaultRoleId, String currentUserId, String system) {
        // 查询要删除的用户组关联的用户ID
        List<String> userIds = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.ROLE_ID.eq(roleId)).list()
                .stream().map(UserRoleRelation::getUserId).toList();
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        // 查询用户列表与所有用户组的关联关系，并分组（UserRoleRelation 中只有 userId 和 sourceId）
        Map<String, List<UserRoleRelation>> userRoleRelationMap = QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.USER_ID.in(userIds)).list()
                .stream()
                .collect(Collectors.groupingBy(i -> i.getUserId() + i.getSourceId()));
        List<UserRoleRelation> addRelations = new ArrayList<>();
        userRoleRelationMap.forEach((_, relations) -> {
            // 如果当前用户组只有一个用户，并且就是要删除的用户组，则添加组织成员等默认用户组
            if (relations.size() == 1 && Strings.CS.equals(relations.getFirst().getRoleId(), roleId)) {
                UserRoleRelation relation = new UserRoleRelation();
                relation.setUserId(relations.getFirst().getUserId());
                relation.setSourceId(relations.getFirst().getSourceId());
                relation.setRoleId(defaultRoleId);
                relation.setCreateUser(currentUserId);
                addRelations.add(relation);
            }
        });
        userRoleRelationMapper.insertBatch(addRelations);
    }

    public void checkInternalUserRole(UserRole userRole) {
        if (BooleanUtils.isTrue(userRole.getInternal())) {
            throw new BizException(GLOBAL_USER_ROLE_PERMISSION);
        }
    }

    public UserRole getWithCheck(String id) {
        return checkResourceExist(mapper.selectOneById(id));
    }

    public UserRole checkResourceExist(UserRole userRole) {
        return ServiceUtils.checkResourceExist(userRole, "permission.system_user_role.name");
    }

    private void checkExist(UserRole userRole) {
        if (StringUtils.isBlank(userRole.getName())) {
            return;
        }
        if (queryChain().where(USER_ROLE.NAME.eq(userRole.getName())
                .and(USER_ROLE.SCOPE_ID.eq(UserRoleScope.GLOBAL))
                .and(USER_ROLE.ID.ne(userRole.getId()))).count() > 0) {
            throw new BizException(GLOBAL_USER_ROLE_EXIST);
        }
    }

    private int getTypeOrder(UserRole userRole) {
        Map<String, Integer> typeOrderMap = new HashMap<>(3) {{
            put(UserRoleType.SYSTEM.name(), 1);
            put(UserRoleType.PROJECT.name(), 2);
        }};
        return typeOrderMap.getOrDefault(userRole.getType(), 0);
    }

    private int getInternal(Boolean internal) {
        return BooleanUtils.isTrue(internal) ? 0 : 1;
    }
}
