package cn.master.horde.model.dto;

import cn.master.horde.model.entity.CustomField;
import cn.master.horde.model.entity.CustomFieldOption;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/2/6, 星期五
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomFieldDTO extends CustomField {
    private List<CustomFieldOption> options;
    /**
     * 是否被模板使用
     */
    private Boolean used = false;
    /**
     * 模板中该字段是否必选
     */
    private Boolean templateRequired = false;
    /**
     * 内置字段的 key
     */
    private String internalFieldKey;
}
