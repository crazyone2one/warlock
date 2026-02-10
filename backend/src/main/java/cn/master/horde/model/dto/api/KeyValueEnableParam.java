package cn.master.horde.model.dto.api;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class KeyValueEnableParam extends KeyValueParam {
    /**
     * 是否启用
     * 默认启用
     */
    private Boolean enable = true;
    /**
     * 描述
     */
    private String description;
}
