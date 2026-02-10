package cn.master.horde.model.dto.api.request.http.body;

import cn.master.horde.model.dto.api.KeyValueEnableParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class WWWFormKV extends KeyValueEnableParam {
    /**
     * 参数类型
     * 取值参考 {@link BodyParamType} 中的 value 属性
     */
    private String paramType = BodyParamType.STRING.getValue();
    /**
     * 是否必填
     * 默认为 false
     */
    private Boolean required = false;
    /**
     * 最小长度
     */
    private Integer minLength;
    /**
     * 最大长度
     */
    private Integer maxLength;
}
