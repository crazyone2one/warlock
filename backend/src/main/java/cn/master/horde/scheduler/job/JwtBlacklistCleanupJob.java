package cn.master.horde.scheduler.job;

import cn.master.horde.service.JwtBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * JWT 黑名单清理定时任务
 *
 * @author Qoder
 * @since 2026/2/2
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtBlacklistCleanupJob {
    private final JwtBlacklistService jwtBlacklistService;

    /**
     * 每天凌晨2点清理过期的JWT黑名单记录
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredRecords() {
        log.info("开始执行JWT黑名单过期记录清理任务");
        try {
            int deletedCount = jwtBlacklistService.cleanupExpired();
            log.info("JWT黑名单过期记录清理完成，共删除 {} 条记录", deletedCount);
        } catch (Exception e) {
            log.error("JWT黑名单过期记录清理失败", e);
        }
    }
}