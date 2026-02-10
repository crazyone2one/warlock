package cn.master.horde.model.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Data
public class KeyValueParam {
    /**
     * 键
     */
    private String key;
    /**
     * 值
     */
    private String value;

    @JsonIgnore
    public boolean isValid() {
        return StringUtils.isNotBlank(key);
    }

    @JsonIgnore
    public boolean isNotBlankValue() {
        return StringUtils.isNotBlank(value);
    }
}
