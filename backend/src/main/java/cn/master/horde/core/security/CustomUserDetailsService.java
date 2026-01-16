package cn.master.horde.core.security;

import cn.master.horde.entity.SystemUser;
import cn.master.horde.entity.UserRoleRelation;
import com.mybatisflex.core.query.QueryChain;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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
                    List<String> list = QueryChain.of(UserRoleRelation.class)
                            .where(UserRoleRelation::getUserId).eq(user.getId()).list()
                            .stream().map(UserRoleRelation::getRoleId).toList();
                    return new CustomUserDetails(user, list);
                })
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    }
}
