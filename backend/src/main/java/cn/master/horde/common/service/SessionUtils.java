package cn.master.horde.common.service;

import cn.master.horde.core.security.CustomUserDetails;
import cn.master.horde.entity.SystemUser;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 获取当前登录用户信息的工具类
 *
 * @author Qoder
 * @since 2026/1/15
 */
public class SessionUtils {

    /**
     * 获取当前登录用户的用户名
     *
     * @return 用户名，如果未登录则返回null
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof String) {
                return (String) principal;
            } else if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
        }
        return null;
    }

    /**
     * 获取当前登录用户的详细信息
     *
     * @return CustomUserDetails对象，如果未登录则返回null
     */
    public static CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return (CustomUserDetails) principal;
            }
        }
        return null;
    }

    /**
     * 获取当前登录的SystemUser实体
     *
     * @return SystemUser对象，如果未登录则返回null
     */
    public static SystemUser getCurrentSystemUser() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        if (userDetails != null) {
            return QueryChain.of(SystemUser.class).where(SystemUser::getId).eq(userDetails.getUserId())
                    .one();
        }
        return null;
    }

    /**
     * 获取当前登录用户的ID
     *
     * @return 用户ID，如果未登录则返回null
     */
    public static String getCurrentUserId() {
        SystemUser systemUser = getCurrentSystemUser();
        if (systemUser != null) {
            return systemUser.getId();
        }
        return null;
    }

    /**
     * 检查用户是否已登录
     *
     * @return 如果用户已登录返回true，否则返回false
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal());
    }
}