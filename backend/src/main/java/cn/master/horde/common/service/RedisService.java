package cn.master.horde.common.service;

import cn.master.horde.common.util.JsonHelper;
import com.mybatisflex.core.row.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    public void storeSensor(String projectNum, String name, List<Row> sensorList, long timeout) {
        redisTemplate.opsForValue().set("sensor:" + projectNum + ":" + name, JsonHelper.objectToType(String.class).apply(sensorList), Duration.ofSeconds(timeout));
    }

    public String getSensor(String projectNum, String name) {
        return redisTemplate.opsForValue().get("sensor:" + projectNum + ":" + name);
    }

    public void deleteSensor(String projectNum, String name) {
        redisTemplate.delete("sensor:" + projectNum + ":" + name);
    }
}
