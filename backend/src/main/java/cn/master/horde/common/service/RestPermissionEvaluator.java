package cn.master.horde.common.service;

import cn.master.horde.entity.UserRole;
import cn.master.horde.entity.UserRolePermission;
import com.mybatisflex.core.query.QueryChain;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author : 11's papa
 * @since : 2026/1/23, 星期五
 **/
@Service("rpe")
public class RestPermissionEvaluator {
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    public boolean hasPermission(String permission) {
        if (StringUtils.isEmpty(permission)) {
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).toList();
        if (roles.contains(ROLE_ADMIN)) {
            return true;
        }
        List<String> roleIds = QueryChain.of(UserRole.class).where(UserRole::getCode).in(roles).list()
                .stream().map(UserRole::getId).toList();
        List<String> permissions = QueryChain.of(UserRolePermission.class)
                .where(UserRolePermission::getRoleId).in(roleIds).list()
                .stream()
                .map(UserRolePermission::getPermissionId).toList();
        return permissions.stream().anyMatch(permission::contains);
    }

    public boolean hasAnyPermission(String[] permissions) {
        for (String permission : permissions) {
            if (hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }
    public boolean hasAllPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }
}
