package cn.master.horde.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author : 11's papa
 * @since : 2026/2/3, 星期二
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcludeOptionDTO extends OptionDTO {
    @Schema(description =  "是否已经关联过")
    private Boolean exclude = false;
    private Boolean disabled = false;
}