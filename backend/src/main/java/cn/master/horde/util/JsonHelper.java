package cn.master.horde.util;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.function.Function;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
public class JsonHelper {
    private static final ObjectMapper objectMapper = JsonMapper.builder().build();

    /**
     * 创建一个将Object类型转换为指定类型的Function
     *
     * @param <T>   目标类型参数
     * @param clazz 要转换的目标类型Class对象
     * @return 返回一个Function，该Function接收Object类型的输入并转换为指定的T类型
     */
    public static <T> Function<Object, T> objectToType(Class<T> clazz) {
        return o -> objectMapper.convertValue(o, clazz);
    }

    public static String objectToString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
