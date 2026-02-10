package cn.master.horde.model.dto.api.request;

import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/2/6, 星期五
 **/
public record DslExecuteRequest(String url, String method, Map<String, String> headers, String body) {
}
