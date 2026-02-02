package cn.master.horde.core.handler;

import cn.master.horde.core.security.JwtTokenManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * @author : 11's papa
 * @since : 2026/1/29, 星期四
 **/
@Slf4j
@NullMarked
@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private final JwtTokenManager jwtTokenManager;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, @Nullable Authentication authentication) {
        assert authentication != null;
        log.info("{} Logout successful", authentication.getName());

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
