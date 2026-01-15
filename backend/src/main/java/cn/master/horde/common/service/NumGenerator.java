package cn.master.horde.common.service;

import cn.master.horde.common.constants.ApplicationNumScope;
import jakarta.annotation.Resource;
import org.redisson.Redisson;
import org.redisson.api.RIdGenerator;
import org.springframework.stereotype.Component;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@Component
public class NumGenerator {
    private static final long INIT = 101L; // 代表从101开始，各种domain的 num
    private static final long LIMIT = 1;
    private static Redisson redisson;

    @Resource
    public void setRedisson(Redisson redisson) {
        NumGenerator.redisson = redisson;
    }

    public static long nextNum(String prefix, ApplicationNumScope scope) {
        RIdGenerator idGenerator = redisson.getIdGenerator(prefix + "_" + scope.name());
        // 每次都尝试初始化，容量为1，只有一个线程可以初始化成功
        if (!idGenerator.isExists()) {
            idGenerator.tryInit(INIT, LIMIT);
        }
        return idGenerator.nextId();
    }
}
