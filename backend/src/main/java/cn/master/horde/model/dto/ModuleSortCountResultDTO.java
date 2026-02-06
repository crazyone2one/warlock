package cn.master.horde.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author : 11's papa
 * @since : 2026/2/5, 星期四
 **/
@Data
@AllArgsConstructor
public class ModuleSortCountResultDTO {
    private boolean isRefreshPos;
    private long pos;
}
