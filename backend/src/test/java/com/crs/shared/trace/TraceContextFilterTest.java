package com.crs.shared.trace;

import com.crs.util.TraceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraceContextFilterTest {

    private final TraceContextFilter filter = new TraceContextFilter();

    @AfterEach
    void clearTrace() {
        TraceContext.clear();
    }

    @Test
    void shouldPropagateSafeClientTraceIdAndClearAfterRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hotels");
        request.addHeader("X-Trace-Id", "client-trace-001");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> traceInsideChain = new AtomicReference<>();

        filter.doFilterInternal(request, response, (req, res) ->
                traceInsideChain.set(TraceContext.getTraceId()));

        assertEquals("client-trace-001", traceInsideChain.get());
        assertEquals("client-trace-001", response.getHeader("X-Trace-Id"));
        assertNull(org.slf4j.MDC.get("traceId"));
    }

    @Test
    void shouldReplaceUnsafeTraceId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hotels");
        request.addHeader("X-Trace-Id", "bad\ntrace");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> traceInsideChain = new AtomicReference<>();

        filter.doFilterInternal(request, response, (req, res) ->
                traceInsideChain.set(TraceContext.getTraceId()));

        assertNotEquals("bad\ntrace", traceInsideChain.get());
        assertTrue(traceInsideChain.get().matches("[a-f0-9]{32}"));
        assertEquals(traceInsideChain.get(), response.getHeader("X-Trace-Id"));
    }
}
