package cn.master.horde.common.util;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * @author : 11's papa
 * @since : 2026/1/22, 星期四
 **/
public class TraceUtil {
    private static final String TRACE_ID_KEY = "traceId";

    public static void setTraceIdIfAbsent() {
        if (MDC.get(TRACE_ID_KEY) == null) {
            MDC.put(TRACE_ID_KEY, generateTraceId());
        }
    }

    private static String generateTraceId() {
        return "TRACE-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }
}
