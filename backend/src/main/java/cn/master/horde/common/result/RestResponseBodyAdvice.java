package cn.master.horde.common.result;

import cn.master.horde.util.JsonHelper;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@RestControllerAdvice
public class RestResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return StringHttpMessageConverter.class.isAssignableFrom(converterType) ||
                JacksonJsonHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public @Nullable Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return JsonHelper.objectToType(String.class).apply(ResultHolder.success(null));
        }
        if ("/v3/api-docs/swagger-config".equals(request.getURI().getPath())) {
            return body;
        }
        if (!(body instanceof ResultHolder)) {
            if (body instanceof String) {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return JsonHelper.objectToType(String.class).apply(ResultHolder.success(body));
            }
            return ResultHolder.success(body);
        }
        return body;
    }
}
