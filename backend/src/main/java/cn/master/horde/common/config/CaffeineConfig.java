package cn.master.horde.common.config;

import cn.master.horde.dao.SlaveParameter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : 11's papa
 * @since : 2026/1/19, 星期一
 **/
@Configuration
public class CaffeineConfig {
    @Bean
    public Cache<String, SlaveParameter> slaveCache() {
        return Caffeine.newBuilder()
                .initialCapacity(10)
                .maximumSize(100)
                .build();
    }
}
