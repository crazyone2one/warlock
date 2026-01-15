package cn.master.horde.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.util.JsonRecyclerPools;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@Configuration
public class JacksonConfig {
    @Bean
    public JsonMapper jsonMapper() {
        JsonFactory jsonFactory = JsonFactory.builder().recyclerPool(JsonRecyclerPools.threadLocalPool())
                .build();
        return JsonMapper.builder(jsonFactory)
                .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();
    }
}
