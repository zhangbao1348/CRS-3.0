package com.crs.shared.api;

import com.crs.util.TraceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @AfterEach
    void clearTrace() {
        TraceContext.clear();
    }

    @Test
    void shouldPreserveCompatibleFieldsForBusinessException() {
        TraceContext.setTraceId("trace-business-001");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/sample/404");

        var response = handler.handleApiException(
                ApiException.notFound("SAMPLE_NOT_FOUND", "样例不存在"),
                request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().success());
        assertEquals("SAMPLE_NOT_FOUND", response.getBody().code());
        assertEquals("样例不存在", response.getBody().message());
        assertEquals("样例不存在", response.getBody().error());
        assertEquals("trace-business-001", response.getBody().traceId());
        assertEquals("/api/sample/404", response.getBody().path());
    }

    @Test
    void shouldHideUnexpectedExceptionMessage() {
        TraceContext.setTraceId("trace-unexpected-001");
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/sample");

        var response = handler.handleUnexpected(
                new IllegalStateException("database password=should-not-leak"),
                request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_ERROR", response.getBody().code());
        assertEquals("系统处理失败，请稍后重试", response.getBody().message());
        assertFalse(response.getBody().message().contains("password"));
    }
}
