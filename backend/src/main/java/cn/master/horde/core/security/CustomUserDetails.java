package cn.master.horde.core.security;

import cn.master.horde.entity.SystemUser;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 11's papa
 * @since : 2026/1/14, 星期三
 **/
@NullMarked
public record CustomUserDetails(SystemUser user, List<String> roles) implements UserDetails {
    public String getUserId() {
        return this.user.getId();
    }

    public String getUserProjectId() {
        return this.user.getLastProjectId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }
}
