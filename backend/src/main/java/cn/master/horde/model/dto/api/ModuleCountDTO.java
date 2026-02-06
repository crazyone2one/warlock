package cn.master.horde.model.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 11's papa
 * @since : 2026/2/5, 星期四
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleCountDTO {
    private String moduleId;
    private int dataCount;
}
