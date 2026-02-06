package cn.master.horde.service.impl;

import cn.master.horde.model.dto.LogDTO;
import cn.master.horde.model.entity.OperationHistory;
import cn.master.horde.model.entity.OperationLog;
import cn.master.horde.model.mapper.OperationHistoryMapper;
import cn.master.horde.model.mapper.OperationLogMapper;
import cn.master.horde.service.OperationLogService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 操作日志表 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-22
 */
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    private final OperationHistoryMapper operationHistoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(LogDTO log) {
        if (StringUtils.isBlank(log.getProjectId())) {
            log.setProjectId("none");
        }
        if (StringUtils.isBlank(log.getCreateUser())) {
            log.setCreateUser("admin");
        }
        log.setContent(subStrContent(log.getContent()));
        mapper.insert(log);
        if (log.getHistory()) {
            operationHistoryMapper.insert(getHistory(log));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAdd(List<LogDTO> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }
        logs.forEach(item -> {
            item.setContent(subStrContent(item.getContent()));
            // 限制长度
            mapper.insert(item);
            if (item.getHistory()) {
                operationHistoryMapper.insert(getHistory(item));
            }
        });
    }

    private static OperationHistory getHistory(LogDTO log) {
        OperationHistory history = new OperationHistory();
        BeanUtils.copyProperties(log, history);
        return history;
    }

    private String subStrContent(String content) {
        if (StringUtils.isNotBlank(content) && content.length() > 500) {
            return content.substring(0, 499);
        }
        return content;
    }
}
