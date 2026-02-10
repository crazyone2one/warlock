package cn.master.horde.security.security;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;

/**
 * @author : 11's papa
 * @since : 2026/1/24, 星期六
 **/
@NullMarked
@Component
public class OptimizedPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (!authentication.isAuthenticated()) {
            return false;
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails userDetails)) {
            return false;
        }
        if (userDetails.getUsername().equals("admin")) {
            return true;
        }
        Set<String> userPermissions = userDetails.getPermissions();
        String resource = (String) targetDomainObject;
        String requiredOp = (String) permission;
        String fullPermission = resource + ":" + requiredOp;
        return userPermissions.contains(fullPermission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
