package cn.master.horde.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author : 11's papa
 * @since : 2026/1/28, 星期三
 **/
@Data
public class UserSelectOption {
    @Schema(description = "节点唯一ID")
    private String id;
    @Schema(description = "节点名称")
    private String name;
    @Schema(description = "是否选中")
    private boolean disabled = false;
    @Schema(description = "是否允许关闭")
    private boolean closeable = true;
}
