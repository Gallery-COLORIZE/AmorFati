package com.colorize.amorfati.global.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.databind.ObjectMapper;

/**
 * REST API 컨트롤러의 반환값을 자동으로 ApiResponse<T> 공통 응답 포맷으로 감싸는 ResponseBodyAdvice.
 * 컨트롤러에서 ApiResponse<T> 대신 순수 DTO(data)만 반환해도 자동으로 래핑됩니다.
 */
@Slf4j
@RestControllerAdvice(annotations = RestController.class)
@RequiredArgsConstructor
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 이미 ApiResponse 타입이거나, @RestController/@ResponseBody가 아닌 경우 래핑 제외
        if (ApiResponse.class.isAssignableFrom(returnType.getParameterType())) {
            return false;
        }

        return returnType.getContainingClass().isAnnotationPresent(RestController.class)
                || returnType.hasMethodAnnotation(ResponseBody.class);
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        // 이미 ApiResponse 형태로 감싸진 경우 그대로 반환
        if (body instanceof ApiResponse<?>) {
            return body;
        }

        // 반환 타입이 String인 경우 StringHttpMessageConverter 충돌 방지를 위해 JSON 직렬화 후 문자열로 반환
        if (body instanceof String || StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            try {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return objectMapper.writeValueAsString(ApiResponse.success(body));
            } catch (Exception e) {
                log.error("String response body serialization error", e);
                return body;
            }
        }

        // null 및 일반 객체(DTO, Collection 등)는 ApiResponse.success()로 감싸서 반환
        return ApiResponse.success(body);
    }
}
