package cn.master.horde.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.function.Function;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
public class JsonHelper {
    private static final JsonMapper objectMapper = JsonMapper.builder()
            .changeDefaultPropertyInclusion(include ->
                    include.withValueInclusion(JsonInclude.Include.NON_NULL))
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .findAndAddModules()
            .build();

    /**
     * JSON Object → 对象
     *
     * @param <T>   目标类型参数
     * @param clazz 要转换的目标类型Class对象
     * @return 返回一个Function，该Function接收Object类型的输入并转换为指定的T类型
     */
    public static <T> Function<Object, T> objectToType(Class<T> clazz) {
        return o -> objectMapper.convertValue(o, clazz);
    }

    /**
     * JSON 字符串 → 对象
     *
     * @param <T>   目标类型参数
     * @param clazz 要转换的目标类型Class对象
     * @return 返回一个Function，该Function接收Object类型的输入并转换为指定的T类型
     */
    public static <T> Function<String, T> toObject(Class<T> clazz) {
        return o -> objectMapper.readValue(o, clazz);
    }

    /**
     * JSON Object → 对象
     *
     * @param <T>     目标类型参数
     * @param typeRef 要转换的目标类型TypeReference对象
     * @return 返回一个Function，该Function接收Object类型的输入并转换为指定的T类型
     */
    public static <T> Function<Object, T> toObject(TypeReference<T> typeRef) {
        return o -> objectMapper.convertValue(o, typeRef);
    }

    /**
     * JSON Object → 对象
     *
     * @param <T>     目标类型参数
     * @param json    要转换的JSON对象
     * @param typeRef 要转换的目标类型TypeReference对象
     * @return 返回转换后的对象
     */
    public static <T> T toObject(Object json, TypeReference<T> typeRef) {
        return objectMapper.convertValue(json, typeRef);
    }

    /**
     * 对象 → JSON 字符串
     *
     * @param object 要转换的对象
     * @return 返回对象的JSON字符串表示
     */
    public static String objectToString(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    public static <T> List<T> parseArray(String content, Class<T> valueType) {
        return objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(List.class, valueType));
    }
}
