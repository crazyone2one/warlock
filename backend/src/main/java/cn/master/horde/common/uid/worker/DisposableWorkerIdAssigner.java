package cn.master.horde.common.uid.worker;

import org.springframework.stereotype.Service;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Service
public class DisposableWorkerIdAssigner implements WorkerIdAssigner{
    @Override
    public long assignWorkerId() {
        return 1;
    }
}
