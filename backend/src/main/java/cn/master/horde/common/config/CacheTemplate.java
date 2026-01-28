package cn.master.horde.common.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * @author : 11's papa
 * @since : 2026/1/27, 星期二
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheTemplate<K, V> {
    private final RedisTemplate<K, V> redisTemplate;
    private final Cache<K, V> local = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(Duration.ofHours(1))
            .recordStats()
            .build();

    public V get(K key, Supplier<V> supplier) {
        // l1 缓存
        V v = local.getIfPresent(key);
        if (v != null) {
            return v;
        }
        // l2 缓存
        v = redisTemplate.opsForValue().get(key);
        if (v != null) {
            local.put(key, v);
            return v;
        }
        // l3
        v = supplier.get();
        if (v != null) {
            set(key, v);
        }
        return v;
    }

    public void set(K key, V value) {
        local.put(key, value);
        redisTemplate.opsForValue().set(key, value, Duration.ofHours(12));
    }

    public void delete(K key) {
        local.invalidate(key);
        redisTemplate.delete(key);
    }
}
