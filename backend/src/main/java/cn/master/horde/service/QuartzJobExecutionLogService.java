package cn.master.horde.service;

import com.mybatisflex.core.service.IService;
import cn.master.horde.model.entity.QuartzJobExecutionLog;

/**
 *  服务层。
 *
 * @author 11's papa
 * @since 2026-02-12
 */
public interface QuartzJobExecutionLogService extends IService<QuartzJobExecutionLog> {
    void saveLog(QuartzJobExecutionLog log);
}
