package cn.master.horde.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/1/28, 星期三
 **/
@Data
public class UserBatchCreateResponse {
    @Schema(description = "成功创建的数据")
    List<UserCreateInfo> successList;
    @Schema(description = "邮箱异常数据")
    Map<String, String> errorEmails;
}

