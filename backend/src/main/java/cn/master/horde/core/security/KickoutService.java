package cn.master.horde.core.security;

import cn.master.horde.service.JwtBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/1/30, 星期五
 **/
@Service
@RequiredArgsConstructor
public class KickoutService {
    private final SessionRegistry sessionRegistry;
    private final JwtBlacklistService jwtBlacklistService;
    private final JwtTokenManager jwtTokenManager;

    public void kickUserByUsername(String username) {
        // 使会话立即过期（如果有的话）
        sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> principal instanceof CustomUserDetails)
                .map(principal -> (CustomUserDetails) principal)
                .filter(userDetails -> userDetails.getUsername().equals(username))
                .forEach(userDetails -> {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails, false);
                    for (SessionInformation session : sessions) {
                        session.expireNow(); // 使会话立即过期
                    }
                });

        // 踢出用户的JWT令牌
        jwtTokenManager.kickOutUserTokens(username, "Admin kickout");
        jwtBlacklistService.removeByUsername(username);
    }
}
