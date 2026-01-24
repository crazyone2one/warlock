package cn.master.horde.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.json.JsonFactory;
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
                .changeDefaultPropertyInclusion(include ->
                        include.withValueInclusion(JsonInclude.Include.NON_NULL))
                // 允许对象为空时不报错
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                // 忽略未知字段（反序列化时）
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .findAndAddModules()
                .build();
    }
}
