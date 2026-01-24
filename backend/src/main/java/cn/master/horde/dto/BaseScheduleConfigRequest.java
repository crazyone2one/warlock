package cn.master.horde.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/1/19, 星期一
 **/
@Data
public class BaseScheduleConfigRequest {
    @NotBlank(message = "{api_scenario.id.not_blank}")
    @Schema(description = "定时任务资源ID")
    @Size(min = 1, max = 50, message = "{api_scenario.id.length_range}")
    private String jobKey;

    @Schema(description = "启用/禁用")
    private boolean enable;

    @Schema(description = "Cron表达式")
    @NotBlank
    @Size(max = 255, message = "{length.too.large}")
    private String cron;

    @Schema(description = "运行配置")
    private Map<String, Object> runConfig = new HashMap<>();
}
