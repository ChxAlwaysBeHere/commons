package org.chx.commons.web.advice;

import org.chx.commons.web.response.HttpResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * wrap json response data
 *
 * @author chenxi
 * @date 2020-01-17
 */
@ControllerAdvice
public class HttpJsonResponseBodyAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        Class<?> clazz = returnType.getMethod().getReturnType();
        if (clazz == Void.class || HttpResponse.class.isAssignableFrom(clazz)) {
            return body;
        }

        return HttpResponse.success(body);
    }
}
