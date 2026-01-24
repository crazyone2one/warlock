package cn.master.horde.common.aspect;

import cn.master.horde.entity.OperationLog;
import cn.master.horde.service.OperationLogService;
import cn.master.horde.util.JsonHelper;
import cn.master.horde.util.TraceUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : 11's papa
 * @since : 2026/1/22, 星期四
 **/
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    private final OperationLogService operationLogService;

    @Pointcut("@annotation(cn.master.horde.common.annotation.Loggable)")
    public void loggableMethod() {
    }

    @Around("loggableMethod()")
    public Object logToDatabase(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        // 获取 Web 信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String uri = attributes != null ? attributes.getRequest().getRequestURI() : "N/A";
        String method = attributes != null ? attributes.getRequest().getMethod() : "N/A";
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().toShortString();
        // 在记录 args 前过滤敏感字段
        String safeArgs = Arrays.stream(args)
                .map(this::sanitizeArgument)
                .collect(Collectors.joining(", "));
        OperationLog opLog = new OperationLog();
        opLog.setTraceId(TraceUtil.getTraceId()); // 从 MDC 获取 TraceID
        opLog.setMethodName(methodName);
        opLog.setUri(uri);
        opLog.setHttpMethod(method);
        opLog.setArgs(JsonHelper.objectToString(safeArgs));
        long duration = System.currentTimeMillis() - start;
        try {
            Object result = joinPoint.proceed();
            opLog.setResult(safeToString(result));
            opLog.setDurationMs(duration);
            log.info("✅ [{}] | URI: {} | Method: {} | Args: {} | Duration: {}ms", methodName, uri, method, safeArgs, duration);
            operationLogService.save(opLog); // 异步保存
            return result;
        } catch (Exception e) {
            opLog.setException(e.getMessage());
            opLog.setDurationMs(duration);
            log.error("❌ [{}] | URI: {} | Method: {} | Args: {} | Exception: {} | Duration: {}ms", methodName, uri, method, safeArgs, e.getMessage(), duration);
            operationLogService.save(opLog);
            throw e;
        }
    }

    private String safeToString(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return (String) obj;
        return JsonHelper.objectToString(obj);
    }

    /**
     * 对参数进行脱敏处理，过滤敏感字段
     *
     * @param arg 待处理的参数
     * @return 脱敏后的字符串
     */
    private String sanitizeArgument(Object arg) {
        switch (arg) {
            case null -> {
                return "null";
            }

            // 处理字符串类型，检查是否包含敏感关键词
            case String str -> {
                if (containsSensitiveField(str)) {
                    return "******";
                }
                return str;
            }

            // 处理集合类型
            case Collection<?> objects -> {
                return "[Collection with " + objects.size() + " items]";
            }

            // 处理Map类型
            case Map<?, ?> map -> {
                Map<Object, Object> sanitizedMap = new java.util.HashMap<>();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    if (isSensitiveField(key.toString())) {
                        sanitizedMap.put(key, "******");
                    } else {
                        sanitizedMap.put(key, value);
                    }
                }
                return sanitizedMap.toString();
            }
            default -> {}
        }

        // 处理数组类型
        if (arg.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(arg);
            return "[Array with " + length + " items]";
        }

        // 处理对象类型，递归检查其字段
        return sanitizeObjectFields(arg);
    }

    /**
     * 检查字符串是否包含敏感字段
     *
     * @param str 待检查的字符串
     * @return 是否包含敏感字段
     */
    private boolean containsSensitiveField(String str) {
        return str.toLowerCase().contains("password") ||
                str.toLowerCase().contains("pwd") ||
                str.toLowerCase().contains("secret") ||
                str.toLowerCase().contains("token") ||
                str.toLowerCase().contains("key");
    }

    /**
     * 判断字段名是否为敏感字段
     *
     * @param fieldName 字段名
     * @return 是否为敏感字段
     */
    private boolean isSensitiveField(String fieldName) {
        String lowerFieldName = fieldName.toLowerCase();
        return lowerFieldName.contains("password") ||
                lowerFieldName.contains("pwd") ||
                lowerFieldName.contains("secret") ||
                lowerFieldName.contains("token") ||
                lowerFieldName.contains("key") ||
                lowerFieldName.contains("credential") ||
                lowerFieldName.contains("authorization") ||
                lowerFieldName.contains("auth");
    }

    /**
     * 对对象的字段进行脱敏处理
     *
     * @param obj 待处理的对象
     * @return 脱敏后的字符串表示
     */
    private String sanitizeObjectFields(Object obj) {
        Class<?> clazz = obj.getClass();

        // 特定类型处理，如AuthenticationRequest等
        if (clazz.getName().contains("AuthenticationRequest") ||
            clazz.getSimpleName().toLowerCase().contains("login") ||
            clazz.getSimpleName().toLowerCase().contains("auth")) {
            // 使用反射获取字段值并脱敏
            StringBuilder sb = new StringBuilder();
            sb.append(clazz.getSimpleName()).append("{");
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    if (isSensitiveField(field.getName())) {
                        sb.append(field.getName()).append("=******");
                    } else {
                        sb.append(field.getName()).append("=").append(value);
                    }
                } catch (IllegalAccessException e) {
                    sb.append(field.getName()).append("=<access denied>");
                }
                if (i < fields.length - 1) sb.append(", ");
            }
            sb.append("}");
            return sb.toString();
        }

        // 默认处理：返回对象的toString()结果
        return obj.toString();
    }
}
