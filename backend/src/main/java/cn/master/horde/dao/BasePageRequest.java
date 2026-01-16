package cn.master.horde.dao;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class BasePageRequest extends BaseCondition {
    @Min(value = 1, message = "当前页码必须大于0")
    private int page;

    @Min(value = 5, message = "每页显示条数必须不小于5")
    @Max(value = 500, message = "每页显示条数不能大于500")
    private int pageSize;
}
