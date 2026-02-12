package cn.master.horde.service.impl;

import cn.master.horde.model.entity.QuartzJobExecutionLog;
import cn.master.horde.model.mapper.QuartzJobExecutionLogMapper;
import cn.master.horde.service.QuartzJobExecutionLogService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 2026-02-12
 */
@Service
public class QuartzJobExecutionLogServiceImpl extends ServiceImpl<QuartzJobExecutionLogMapper, QuartzJobExecutionLog> implements QuartzJobExecutionLogService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLog(QuartzJobExecutionLog log) {
        log.setServerHost(getLocalHostName());
        save(log);
    }

    private String getLocalHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
