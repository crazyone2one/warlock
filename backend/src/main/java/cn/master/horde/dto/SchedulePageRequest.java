package cn.master.horde.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : 11's papa
 * @since : 2026/1/16, 星期五
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class SchedulePageRequest extends BasePageRequest {
    private String resourceType;
}
