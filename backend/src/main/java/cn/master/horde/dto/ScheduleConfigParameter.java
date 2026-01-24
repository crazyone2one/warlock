package cn.master.horde.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleConfigParameter {
    private String sensorId;
    // 用于存放动态字段
    private Map<String, Object> additionalFields = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }

    @JsonAnySetter
    public void addField(String key, Object value) {
        additionalFields.put(key, value);
    }

    public <T> T getField(String name, Class<T> type) {
        Object value = additionalFields.get(name);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        // 尝试类型转换
        return convertValue(value, type);
    }

    @SuppressWarnings("unchecked")
    private <T> T convertValue(Object value, Class<T> type) {
        if (value == null) {
            return null;
        }

        if (type == String.class) {
            return (T) value.toString();
        } else if (type == Integer.class || type == int.class) {
            if (value instanceof Number) {
                return (T) Integer.valueOf(((Number) value).intValue());
            }
            return (T) Integer.valueOf(value.toString());
        } else if (type == Long.class || type == long.class) {
            if (value instanceof Number) {
                return (T) Long.valueOf(((Number) value).longValue());
            }
            return (T) Long.valueOf(value.toString());
        } else if (type == Boolean.class || type == boolean.class) {
            if (value instanceof Boolean) {
                return (T) value;
            }
            return (T) Boolean.valueOf(value.toString());
        }

        throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to " + type);
    }
}
