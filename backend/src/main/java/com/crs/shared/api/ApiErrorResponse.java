package com.crs.shared.api;

import com.crs.util.TraceContext;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 统一 API 错误响应。
 *
 * <p>保留 {@code success/message/error} 字段兼容现有前端，同时增加稳定错误码、
 * traceId、时间和请求路径，供后续业务域渐进迁移。</p>
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiErrorResponse(
        boolean success,
        String code,
        String message,
        String error,
        String traceId,
        String timestamp,
        String path,
        Map<String, String> details) {

    public static ApiErrorResponse of(String code, String message, String path) {
        return of(code, message, path, Map.of());
    }

    public static ApiErrorResponse of(
            String code,
            String message,
            String path,
            Map<String, String> details) {
        Map<String, String> safeDetails = new LinkedHashMap<>();
        if (details != null) {
            details.forEach((key, value) -> {
                if (key != null && value != null) {
                    safeDetails.put(key, value);
                }
            });
        }
        return new ApiErrorResponse(
                false,
                code,
                message,
                message,
                TraceContext.getTraceId(),
                Instant.now().toString(),
                path,
                Map.copyOf(safeDetails));
    }
}
