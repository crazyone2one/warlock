package cn.master.horde.service;

import cn.master.horde.model.dto.LogDTO;
import com.mybatisflex.core.service.IService;
import cn.master.horde.model.entity.OperationLog;

import java.util.List;

/**
 * 操作日志表 服务层。
 *
 * @author 11's papa
 * @since 2026-01-22
 */
public interface OperationLogService extends IService<OperationLog> {
    void add(LogDTO log);

    void batchAdd(List<LogDTO> logs);
}
