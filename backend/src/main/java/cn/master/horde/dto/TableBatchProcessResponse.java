package cn.master.horde.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 11's papa
 * @since : 2026/1/28, 星期三
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableBatchProcessResponse {
    @Schema(description = "全部数量")
    private long totalCount;
    @Schema(description = "成功数量")
    private long successCount;
}
