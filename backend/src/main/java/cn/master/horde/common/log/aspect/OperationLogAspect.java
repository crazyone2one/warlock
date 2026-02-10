package cn.master.horde.common.log.aspect;

import cn.master.horde.common.constants.OperationLogType;
import cn.master.horde.common.log.annotation.Loggable;
import cn.master.horde.common.util.JsonHelper;
import cn.master.horde.common.util.LogUtils;
import cn.master.horde.common.util.SessionUtils;
import cn.master.horde.model.dto.LogDTO;
import cn.master.horde.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : 11's papa
 * @since : 2026/1/22, 星期四
 **/
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {
    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);
    private final OperationLogService operationLogService;
    private final ApplicationContext applicationContext;
    private final static String ID = "id";
    /**
     * 解析spell表达式
     */
    ExpressionParser parser = new SpelExpressionParser();
    /**
     * 将方法参数纳入Spring管理
     */
    private final StandardReflectionParameterNameDiscoverer
            discoverer = new StandardReflectionParameterNameDiscoverer();
    // 批量变更前后内容
    private final ThreadLocal<List<LogDTO>> beforeValues = new ThreadLocal<>();

    private final ThreadLocal<String> localUser = new ThreadLocal<>();

    private final ThreadLocal<String> localOrganizationId = new ThreadLocal<>();

    private final ThreadLocal<String> localProjectId = new ThreadLocal<>();

    // 此方法随时补充类型，需要在内容变更前执行的类型都可以加入
    private final OperationLogType[] beforeMethodNames = new OperationLogType[]{OperationLogType.UPDATE, OperationLogType.DELETE, OperationLogType.COPY
            , OperationLogType.RECOVER, OperationLogType.DISASSOCIATE, OperationLogType.ASSOCIATE, OperationLogType.ARCHIVED};
    // 需要后置执行合并内容的
    private final OperationLogType[] postMethodNames = new OperationLogType[]{OperationLogType.ADD, OperationLogType.UPDATE, OperationLogType.RERUN};

    @Pointcut("@annotation(cn.master.horde.common.log.annotation.Loggable)")
    public void logPointCut() {
    }

    @AfterThrowing(pointcut = "logPointCut()", throwing = "ex")
    public void handleException(Exception ex) {
        localUser.remove();
        beforeValues.remove();
        localOrganizationId.remove();
        localProjectId.remove();
        LogUtils.error(ex);
    }

    @Before("logPointCut()")
    public void logToDatabase(JoinPoint joinPoint) throws Throwable {
        try {
            localUser.set(SessionUtils.getCurrentUserId());
            localProjectId.set(SessionUtils.getCurrentProjectId());
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            Loggable methodAnnotation = method.getAnnotation(Loggable.class);
            if (methodAnnotation != null && isMatch(methodAnnotation.type())) {
                // 获取参数对象数组
                Object[] args = joinPoint.getArgs();
                // 获取方法参数名
                String[] params = discoverer.getParameterNames(method);
                // 将参数纳入Spring管理
                EvaluationContext context = new StandardEvaluationContext();
                for (int len = 0; len < Objects.requireNonNull(params).length; len++) {
                    context.setVariable(params[len], args[len]);
                }
                boolean isNext = false;
                for (Class<?> clazz : methodAnnotation.wClass()) {
                    context.setVariable("wClass", applicationContext.getBean(clazz));
                    isNext = true;
                }
                if (!isNext) {
                    return;
                }
                // 初始化details内容
                initBeforeDetails(methodAnnotation, context);
            }
        } catch (Exception e) {
            LogUtils.error("操作日志写入异常：" + joinPoint.getSignature());
        }
    }

    @AfterReturning(value = "logPointCut()", returning = "result")
    public void saveLog(JoinPoint joinPoint, Object result) {
        try {
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            // 获取操作
            Loggable methodAnnotation = method.getAnnotation(Loggable.class);
            if (methodAnnotation != null) {
                // 获取参数对象数组
                Object[] args = joinPoint.getArgs();
                // 获取方法参数名
                String[] params = discoverer.getParameterNames(method);
                // 将参数纳入Spring管理
                EvaluationContext context = new StandardEvaluationContext();
                for (int len = 0; len < Objects.requireNonNull(params).length; len++) {
                    context.setVariable(params[len], args[len]);
                }
                for (Class<?> clazz : methodAnnotation.wClass()) {
                    context.setVariable("wClass", applicationContext.getBean(clazz));
                }
                // 需要后置再次执行的方法
                if (Arrays.stream(postMethodNames).anyMatch(input -> input.contains(methodAnnotation.type()))) {
                    initPostDetails(methodAnnotation, context);
                }
                // 存储日志结果
                save(result);
            }
        } catch (Exception e) {
            LogUtils.error("操作日志写入异常：{}", e);
        } finally {
            localUser.remove();
            beforeValues.remove();
        }
    }

    private void save(Object result) {
        List<LogDTO> logDTOList = beforeValues.get();
        if (CollectionUtils.isEmpty(logDTOList)) {
            return;
        }
        logDTOList.forEach(logDTO -> {
            logDTO.setSourceId(StringUtils.defaultIfBlank(logDTO.getSourceId(), getId(result)));
            logDTO.setCreateUser(StringUtils.defaultIfBlank(logDTO.getCreateUser(), localUser.get()));
            logDTO.setProjectId(StringUtils.defaultIfBlank(logDTO.getProjectId(), localProjectId.get()));
            logDTO.setMethod(getMethod());
            logDTO.setPath(getPath());
        });

        // 单条存储
        if (logDTOList.size() == 1) {
            operationLogService.add(logDTOList.getFirst());
        } else {
            operationLogService.batchAdd(logDTOList);
        }
    }

    private String getId(Object result) {
        try {
            if (result != null) {
                String resultStr = JsonHelper.objectToString(result);
                Map object = JsonHelper.objectToMap(resultStr);
                if (MapUtils.isNotEmpty(object) && object.containsKey(ID)) {
                    Object nameValue = object.get(ID);
                    if (ObjectUtils.isNotEmpty(nameValue)) {
                        return nameValue.toString();
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.error("未获取到响应资源Id");
        }
        return null;
    }

    private String getMethod() {
        HttpServletRequest httpRequest = getHttpRequest();
        return httpRequest == null ? StringUtils.EMPTY : httpRequest.getMethod();
    }

    private String getPath() {
        HttpServletRequest httpRequest = getHttpRequest();
        String path = StringUtils.EMPTY;
        if (httpRequest != null) {
            path = httpRequest.getRequestURI();
            if (StringUtils.isNotBlank(httpRequest.getQueryString())) {
                path += "?" + httpRequest.getQueryString();
            }
        }
        return path.length() > 255 ? path.substring(0, 255) : path;
    }

    private HttpServletRequest getHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    private void initPostDetails(Loggable methodAnnotation, EvaluationContext context) {
        try {
            if (StringUtils.isBlank(methodAnnotation.expression())) {
                return;
            }
            // 批量内容处理
            Expression expression = parser.parseExpression(methodAnnotation.expression());
            Object obj = expression.getValue(context);
            if (obj == null) {
                return;
            }

            if (obj instanceof List<?>) {
                mergeLists(beforeValues.get(), (List<LogDTO>) obj);
            } else if (obj instanceof LogDTO log) {
                if (CollectionUtils.isNotEmpty(beforeValues.get())) {
                    beforeValues.get().getFirst().setModifiedValue(log.getOriginalValue());
                } else {
                    beforeValues.set(new ArrayList<>() {{
                        this.add(log);
                    }});
                }
            }

        } catch (Exception e) {
            LogUtils.error("未获取到details内容", e);
        }
    }

    private void mergeLists(List<LogDTO> beforeLogs, List<LogDTO> postLogs) {
        if (CollectionUtils.isEmpty(beforeLogs) && CollectionUtils.isNotEmpty(postLogs)) {
            beforeValues.set(postLogs);
            return;
        }
        if (CollectionUtils.isEmpty(beforeLogs) && CollectionUtils.isEmpty(postLogs)) {
            return;
        }
        Map<String, LogDTO> postDto = postLogs.stream().collect(Collectors.toMap(LogDTO::getSourceId, item -> item));
        beforeLogs.forEach(item -> {
            LogDTO post = postDto.get(item.getSourceId());
            if (post != null) {
                item.setModifiedValue(post.getOriginalValue());
            }
        });
    }

    private void initBeforeDetails(Loggable methodAnnotation, EvaluationContext context) {
        try {
            // 批量内容处理
            Expression expression = parser.parseExpression(methodAnnotation.expression());
            Object obj = expression.getValue(context);
            if (obj == null) {
                return;
            }
            if (obj instanceof List<?>) {
                beforeValues.set((List<LogDTO>) obj);
            } else {
                List<LogDTO> LogDTOs = new ArrayList<>();
                LogDTOs.add((LogDTO) obj);
                beforeValues.set(LogDTOs);
            }

        } catch (Exception e) {
            LogUtils.error("未获取到details内容", e);
        }
    }

    private boolean isMatch(OperationLogType type) {
        return Arrays.stream(beforeMethodNames).anyMatch(input -> input.contains(type));
    }
// long start = System.currentTimeMillis();
    // // 获取 Web 信息
    // ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    // String uri = attributes != null ? attributes.getRequest().getRequestURI() : "N/A";
    // String method = attributes != null ? attributes.getRequest().getMethod() : "N/A";
    // // 获取方法参数
    // Object[] args = joinPoint.getArgs();
    // String methodName = joinPoint.getSignature().toShortString();
    // // 在记录 args 前过滤敏感字段
    // String safeArgs = Arrays.stream(args)
    //         .map(this::sanitizeArgument)
    //         .collect(Collectors.joining(", "));
    // OperationLog opLog = new OperationLog();
    // opLog.setTraceId(TraceUtil.getTraceId()); // 从 MDC 获取 TraceID
    // opLog.setMethod(methodName);
    // opLog.setPath(uri);
    // opLog.setHttpMethod(method);
    // opLog.setArgs(JsonHelper.objectToString(safeArgs));
    // long duration = System.currentTimeMillis() - start;
    // try {
    //     Object result = joinPoint.proceed();
    //     opLog.setResult(safeToString(result));
    //     opLog.setDurationMs(duration);
    //     log.info("✅ [{}] | URI: {} | Method: {} | Args: {} | Duration: {}ms", methodName, uri, method, safeArgs, duration);
    //     operationLogService.save(opLog); // 异步保存
    //     return result;
    // } catch (Exception e) {
    //     opLog.setException(e.getMessage());
    //     opLog.setDurationMs(duration);
    //     log.error("❌ [{}] | URI: {} | Method: {} | Args: {} | Exception: {} | Duration: {}ms", methodName, uri, method, safeArgs, e.getMessage(), duration);
    //     operationLogService.save(opLog);
    //     throw e;
    // }

}
