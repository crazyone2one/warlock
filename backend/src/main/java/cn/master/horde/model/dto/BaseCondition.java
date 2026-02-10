package cn.master.horde.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@Data
public class BaseCondition {
    @Schema(description =  "关键字")
    private String keyword;
    private String projectId;
    @Schema(description =  "过滤字段")
    private Map<String, List<String>> filter;
}
