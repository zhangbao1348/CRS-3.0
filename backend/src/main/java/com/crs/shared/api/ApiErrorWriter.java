package com.crs.shared.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** 在 MVC 之外（Security、Interceptor）输出统一 JSON 错误。 */
public final class ApiErrorWriter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ApiErrorWriter() {
    }

    public static void write(
            HttpServletRequest request,
            HttpServletResponse response,
            int status,
            String code,
            String message) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("X-Trace-Id", com.crs.util.TraceContext.getTraceId());
        OBJECT_MAPPER.writeValue(
                response.getWriter(),
                ApiErrorResponse.of(code, message, request == null ? null : request.getRequestURI()));
    }
}
