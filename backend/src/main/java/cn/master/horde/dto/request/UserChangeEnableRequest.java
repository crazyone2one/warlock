package cn.master.horde.dto.request;

import cn.master.horde.dto.TableBatchProcessDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : 11's papa
 * @since : 2026/1/28, 星期三
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserChangeEnableRequest extends TableBatchProcessDTO {
    @Schema(description = "禁用/启用", requiredMode = Schema.RequiredMode.REQUIRED)
    boolean enable;
}
