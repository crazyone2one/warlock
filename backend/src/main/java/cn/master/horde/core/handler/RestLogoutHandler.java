package cn.master.horde.core.handler;

import cn.master.horde.core.security.JwtTokenManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/**
 * @author : 11's papa
 * @since : 2026/2/2, 星期一
 **/
@NullMarked
@Component
@RequiredArgsConstructor
public class RestLogoutHandler implements LogoutHandler {
    private final JwtTokenManager jwtTokenManager;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, @Nullable Authentication authentication) {
        assert authentication != null;
        // 清理用户JWT令牌记录
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            jwtTokenManager.clearUserTokens(username);
        } else if (principal instanceof String) {
            jwtTokenManager.clearUserTokens((String) principal);
        }
    }
}
