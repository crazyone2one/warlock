package cn.master.horde.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : 11's papa
 * @since : 2026/1/28, 星期三
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserExcelRowDTO extends UserExcel {
    public int dataIndex;
    public String errorMessage;
    public String userRoleId;
}
