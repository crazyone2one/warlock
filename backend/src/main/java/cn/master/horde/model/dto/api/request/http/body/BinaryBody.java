package cn.master.horde.model.dto.api.request.http.body;

import cn.master.horde.model.dto.api.ApiFile;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
public record BinaryBody(String description, ApiFile file) {
}
