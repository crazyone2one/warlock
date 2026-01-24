package cn.master.horde.core.security;

import cn.master.horde.entity.SystemUser;
import cn.master.horde.entity.UserRolePermission;
import cn.master.horde.entity.UserRoleRelation;
import com.mybatisflex.core.query.QueryChain;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author : 11's papa
 * @since : 2026/1/14, 星期三
 **/
@NullMarked
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return QueryChain.of(SystemUser.class).where(SystemUser::getUserName).eq(username).oneOpt()
                .map(user -> {
                    // 查角色 ID 列表
                    List<String> roleIds = QueryChain.of(UserRoleRelation.class)
                            .where(UserRoleRelation::getUserId).eq(user.getId()).list()
                            .stream().map(UserRoleRelation::getRoleId).toList();
                    Set<String> flattenedPermissions = Collections.emptySet();
                    // 查原始权限字符串（如 "SYSTEM_USER:READ+UPDATE"）
                    List<String> rawPermissions = QueryChain.of(UserRolePermission.class).where(UserRolePermission::getRoleId).in(roleIds).list().stream()
                            .map(UserRolePermission::getPermissionId).toList();
                    flattenedPermissions = flattenPermissions(rawPermissions);
                    return new CustomUserDetails(user, Collections.emptyList(), flattenedPermissions);
                })
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    }

    private Set<String> flattenPermissions(List<String> rawPermissions) {
        Set<String> result = new HashSet<>();

        for (String perm : rawPermissions) {
            if (perm.isBlank()) continue;
            String[] parts = perm.split(":", 2); // 最多分两段
            if (parts.length != 2) continue;

            String resource = parts[0].trim();
            String opsPart = parts[1].trim();
            // String[] operations = opsPart.split("\\+");
            //
            // for (String op : operations) {
            //     op = op.trim();
            //     if (!op.isEmpty()) {
            //         result.add(resource + ":" + op);
            //     }
            // }
            result.add(resource + ":" + opsPart);
        }
        return result;
    }
}
