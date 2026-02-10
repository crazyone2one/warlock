package cn.master.horde.model.dto.api.request.http.body;

import cn.master.horde.model.dto.api.schema.JsonSchemaItem;
import jakarta.validation.Valid;
import lombok.Data;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Data
public class JsonBody {
    /**
     * 是否 json-schema
     * 默认false
     */
    private Boolean enableJsonSchema = false;
    /**
     * json 参数值
     */
    private String jsonValue;
    /**
     * json-schema 定义
     */
    @Valid
    private JsonSchemaItem jsonSchema;
}
