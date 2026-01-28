package cn.master.horde.service.impl;

import cn.master.horde.common.constants.UserRoleType;
import cn.master.horde.dto.UserDTO;
import cn.master.horde.dto.permission.UserRolePermissionDTO;
import cn.master.horde.dto.permission.UserRoleResourceDTO;
import cn.master.horde.entity.*;
import cn.master.horde.mapper.SystemUserMapper;
import cn.master.horde.service.SystemUserService;
import cn.master.horde.service.UserRoleRelationService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.master.horde.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;
import static cn.master.horde.entity.table.UserRolePermissionTableDef.USER_ROLE_PERMISSION;
import static cn.master.horde.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.horde.entity.table.UserRoleTableDef.USER_ROLE;

/**
 * 用户 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {
    private final UserRoleRelationService userRoleRelationService;

    @Override
    public UserDTO getUserInfoById(String id) {
        UserDTO user = getUserDTO(id);
        autoSwitch(user);
        return user;
    }

    private void autoSwitch(UserDTO user) {
        // 判断是否是系统管理员
        if (isSystemAdmin(user)) {
            return;
        }
        // 用户有 last_project_id 权限
        if (hasLastProjectPermission(user)) {
            return;
        }
        // 判断其他权限
        checkNewOrganizationAndProject(user);
    }

    private void checkNewOrganizationAndProject(UserDTO user) {
        List<UserRoleRelation> userRoleRelations = user.getUserRoleRelations();
        List<String> projectRoleIds = user.getUserRoles()
                .stream().filter(ug -> Objects.equals(ug.getType(), UserRoleType.PROJECT.name()))
                .map(UserRole::getCode)
                .toList();
        List<UserRoleRelation> project = userRoleRelations.stream().filter(ug -> projectRoleIds.contains(ug.getRoleId()))
                .toList();
        if (CollectionUtils.isNotEmpty(project)) {
            UserRoleRelation userRoleRelation = project.stream().filter(p -> StringUtils.isNotBlank(p.getSourceId()))
                    .toList().getFirst();
            String projectId = userRoleRelation.getSourceId();
            SystemProject p = QueryChain.of(SystemProject.class).where(SYSTEM_PROJECT.ID.eq(projectId)).one();
            updateChain().set(SystemUser::getId, user.getId()).set(SystemUser::getLastProjectId, p.getId()).update();
        }
    }

    private boolean hasLastProjectPermission(UserDTO user) {
        if (StringUtils.isNotBlank(user.getLastProjectId())) {
            List<UserRoleRelation> userRoleRelations = user.getUserRoleRelations().stream()
                    .filter(ug -> Objects.equals(user.getLastProjectId(), ug.getSourceId()))
                    .toList();
            if (CollectionUtils.isNotEmpty(userRoleRelations)) {
                List<SystemProject> systemProjects = QueryChain.of(SystemProject.class).where(SystemProject::getId).eq(user.getLastProjectId())
                        .and(SystemProject::getEnable).eq(true)
                        .list();
                return CollectionUtils.isNotEmpty(systemProjects);
            }
        }
        return false;
    }

    private boolean isSystemAdmin(UserDTO user) {
        return isSuperUser(user.getId());
    }

    private boolean isSuperUser(String id) {
        return QueryChain.of(UserRoleRelation.class)
                .innerJoin(UserRole.class).on(USER_ROLE_RELATION.ROLE_ID.eq(USER_ROLE.ID)
                        .and(USER_ROLE.CODE.eq("admin")))
                .where(USER_ROLE_RELATION.USER_ID.eq(id))
                .exists();
    }

    private UserDTO getUserDTO(String id) {
        UserDTO user = mapper.selectOneWithRelationsByIdAs(id, UserDTO.class);
        UserRolePermissionDTO dto = getUserRolePermission(id);
        user.setUserRoleRelations(dto.getUserRoleRelations());
        user.setUserRoles(dto.getUserRoles());
        user.setUserRolePermissions(dto.getList());
        return user;
    }

    private UserRolePermissionDTO getUserRolePermission(String userId) {
        UserRolePermissionDTO permissionDTO = new UserRolePermissionDTO();
        List<UserRoleResourceDTO> list = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = userRoleRelationService.selectByUserId(userId);
        if (CollectionUtils.isEmpty(userRoleRelations)) {
            return permissionDTO;
        }
        permissionDTO.setUserRoleRelations(userRoleRelations);
        List<String> roleList = userRoleRelations.stream().map(UserRoleRelation::getRoleId).toList();
        List<UserRole> userRoles = QueryChain.of(UserRole.class).where(UserRole::getId).in(roleList).list();
        permissionDTO.setUserRoles(userRoles);
        for (UserRole gp : userRoles) {
            UserRoleResourceDTO dto = new UserRoleResourceDTO();
            dto.setUserRole(gp);
            List<UserRolePermission> userRolePermissions = QueryChain.of(UserRolePermission.class)
                    .where(USER_ROLE_PERMISSION.ROLE_ID.eq(gp.getId()))
                    .list();
            dto.setUserRolePermissions(userRolePermissions);
            list.add(dto);
        }
        permissionDTO.setList(list);
        return permissionDTO;
    }
}
