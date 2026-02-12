package cn.master.horde.scheduler.job;

import cn.master.horde.model.dto.ScheduleConfigParameter;
import cn.master.horde.scheduler.annotation.QuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : 11's papa
 * @since : 2026/2/12, 星期四
 **/
@Slf4j
@Component
public class MyBusinessJob {
    @QuartzJob(cron = "0/15 * * * * ?", group = "REPORT_GROUP", paramsJson = "{\"fileName\":\"report.pdf\",\"pageCount\":10}")
    public void generateReport(String fileName, int pageCount) {
        log.info("\uD83D\uDCCA 生成报表任务执行中... {} 文件名：{} 页数：{}", System.currentTimeMillis(), fileName, pageCount);
    }

    @QuartzJob(cron = "0 0 2 * * ?", group = "CLEANUP_GROUP") // 每天凌晨2点
    public void cleanTempFiles() {
        log.info("🧹 清理临时文件...");
    }

    @QuartzJob(cron = "0 0/1 * * * ?", group = "NOTIFICATION_GROUP")
    public void sendNotification(ScheduleConfigParameter configParameter) {
        Long userId = configParameter.getField("userId", Long.class);
        String title = configParameter.getField("title", String.class);
        String content = configParameter.getField("content", String.class);
        log.info("📢 发送通知给用户 {}：{} {}", userId, title, content);
    }
}
