package com.crs.shared.trace;

import com.crs.util.TraceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 全请求 Trace 生命周期过滤器。
 *
 * <p>先于 Spring Security 执行，使认证失败、租户拒绝和正常业务响应都具有同一个
 * 可检索 traceId；请求结束后统一清理 MDC 与决策快照。</p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceContextFilter extends OncePerRequestFilter {

    private static final String TRACE_HEADER = "X-Trace-Id";
    private static final Pattern SAFE_TRACE_ID = Pattern.compile("[A-Za-z0-9._-]{8,64}");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String requestedTraceId = request.getHeader(TRACE_HEADER);
        if (requestedTraceId == null || !SAFE_TRACE_ID.matcher(requestedTraceId).matches()) {
            requestedTraceId = null;
        }

        TraceContext.setTraceId(requestedTraceId);
        response.setHeader(TRACE_HEADER, TraceContext.getTraceId());
        try {
            filterChain.doFilter(request, response);
        } finally {
            TraceContext.clear();
        }
    }
}
