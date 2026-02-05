package cn.master.horde.service;

import cn.master.horde.model.entity.JwtBlacklist;
import com.mybatisflex.core.service.IService;

import java.time.LocalDateTime;

/**
 * JWT 黑名单服务接口
 *
 * @author Qoder
 * @since 2026/2/2
 */
public interface JwtBlacklistService extends IService<JwtBlacklist> {

    /**
     * 将JWT令牌加入黑名单
     *
     * @param token      JWT令牌
     * @param expireTime 过期时间
     * @param reason     踢出原因
     * @param userName   用户名
     */
    void addToBlacklist(String token, LocalDateTime expireTime, String reason, String userName);

    /**
     * 检查JWT令牌是否在黑名单中
     *
     * @param token JWT令牌
     * @return 是否在黑名单中
     */
    boolean isInBlacklist(String token);

    /**
     * 清理已过期的黑名单记录
     *
     * @return 清理的记录数
     */
    int cleanupExpired();

    void removeByUsername(String username);
}