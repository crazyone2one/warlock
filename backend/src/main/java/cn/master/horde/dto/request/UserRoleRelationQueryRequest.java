package cn.master.horde.dto.request;

import cn.master.horde.dto.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : 11's papa
 * @since : 2026/2/3, 星期二
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserRoleRelationQueryRequest extends BasePageRequest {
    @NotBlank
    @Schema(description =  "用户组ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleId;
}
