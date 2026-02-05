package cn.master.horde.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.horde.model.entity.OperationLog;
import cn.master.horde.model.mapper.OperationLogMapper;
import cn.master.horde.service.OperationLogService;
import org.springframework.stereotype.Service;

/**
 * 操作日志表 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-22
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog>  implements OperationLogService{

}
