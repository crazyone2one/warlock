package cn.master.horde.model.dto.api.request.http.body;

import cn.master.horde.common.constants.ValueEnum;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
public enum BodyParamType implements ValueEnum {
    /**
     * 字符串类型
     * 默认 application/text
     */
    STRING("string"),
    /**
     * 整型
     * 默认 application/text
     */
    INTEGER("integer"),
    /**
     * 数值型
     * 默认 application/text
     */
    NUMBER("number"),
    BOOLEAN("boolean"),
    /**
     * 数组
     * 默认 application/text
     */
    ARRAY("array"),
    /**
     * 文件类型
     * 默认 application/octet-stream
     */
    FILE("file"),
    /**
     * json 类型
     * 默认 application/json
     */
    JSON("json");

    private final String value;

    BodyParamType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
