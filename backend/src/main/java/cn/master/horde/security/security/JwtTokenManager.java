package cn.master.horde.security.security;

import cn.master.horde.service.JwtBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT令牌管理器
 * 用于跟踪用户与其JWT令牌的映射关系
 *
 * @author Qoder
 * @since 2026/2/2
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenManager {
    private final JwtBlacklistService jwtBlacklistService;

    // 存储用户名到其活跃JWT令牌集合的映射
    private final Map<String, Set<String>> userTokensMap = new ConcurrentHashMap<>();

    /**
     * 记录用户JWT令牌
     *
     * @param username 用户名
     * @param token    JWT令牌
     */
    public void recordUserToken(String username, String token) {
        userTokensMap.computeIfAbsent(username, k -> ConcurrentHashMap.newKeySet()).add(token);
        log.debug("记录用户 {} 的JWT令牌: {}", username, token);
    }

    /**
     * 移除用户JWT令牌记录
     *
     * @param username 用户名
     * @param token    JWT令牌
     */
    public void removeUserToken(String username, String token) {
        Set<String> tokens = userTokensMap.get(username);
        if (tokens != null) {
            tokens.remove(token);
            if (tokens.isEmpty()) {
                userTokensMap.remove(username);
            }
            log.debug("移除用户 {} 的JWT令牌: {}", username, token);
        }
    }

    /**
     * 踢出指定用户的全部JWT令牌
     *
     * @param username 用户名
     * @param reason   踢出原因
     */
    public void kickOutUserTokens(String username, String reason) {
        Set<String> tokens = userTokensMap.get(username);
        if (tokens != null) {
            for (String token : tokens) {
                // 添加到黑名单
                addToBlacklist(username, token, reason);
            }

            // 清空内存中的记录
            userTokensMap.remove(username);
            log.info("踢出用户 {} 的 {} 个JWT令牌", username, tokens.size());
        }
    }

    /**
     * 将JWT令牌添加到黑名单
     *
     * @param token  JWT令牌
     * @param reason 踢出原因
     */
    private void addToBlacklist(String username, String token, String reason) {
        try {
            // 假设JWT有效期为12小时（根据application.yml配置）
            LocalDateTime expireTime = LocalDateTime.now().plusHours(12);
            jwtBlacklistService.addToBlacklist(token, expireTime, reason, username);
            // jwtBlacklistService.removeByUsername(username);
            log.debug("JWT令牌已加入黑名单: {}", token);
        } catch (Exception e) {
            log.error("添加JWT令牌到黑名单失败: {}", token, e);
        }
    }

    /**
     * 清理指定用户的令牌记录（例如用户登出时）
     *
     * @param username 用户名
     */
    public void clearUserTokens(String username) {
        userTokensMap.remove(username);
    }
}