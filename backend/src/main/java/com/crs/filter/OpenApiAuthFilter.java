package com.crs.filter;

import com.crs.entity.TenantChannel;
import com.crs.repository.TenantChannelRepository;
import com.crs.util.TraceContext;
import com.crs.util.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 开放API认证过滤器
 * 
 * <p>通过 X-Api-Key + X-Api-Secret 验证渠道身份。仅对 /api/open/** 路径生效。
 * 同时绑定请求的 traceId 至 TraceContext 中，并在请求生命周期结束时清理上下文。</p>
 */
@Component
public class OpenApiAuthFilter extends OncePerRequestFilter {

    @Autowired
    private TenantChannelRepository tenantChannelRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/open/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 绑定请求的 TraceId
        String traceId = request.getHeader("X-Trace-Id");
        TraceContext.setTraceId(traceId);

        String apiKey = request.getHeader("X-Api-Key");
        String apiSecret = request.getHeader("X-Api-Secret");

        if (apiKey == null || apiKey.isBlank()) {
            writeError(response, 401, "缺少 X-Api-Key 请求头");
            TraceContext.clear();
            return;
        }
        if (apiSecret == null || apiSecret.isBlank()) {
            writeError(response, 401, "缺少 X-Api-Secret 请求头");
            TraceContext.clear();
            return;
        }

        TenantChannel channel = tenantChannelRepository.findByAccessKey(apiKey);
        if (channel == null) {
            writeError(response, 401, "无效的 API Key");
            TraceContext.clear();
            return;
        }
        if (!apiSecret.equals(channel.getAccessSecret())) {
            writeError(response, 401, "API Secret 不匹配");
            TraceContext.clear();
            return;
        }
        if (!"active".equals(channel.getStatus())) {
            writeError(response, 401, "渠道已停用");
            TraceContext.clear();
            return;
        }
        if (!Boolean.TRUE.equals(channel.getConnected())) {
            writeError(response, 401, "渠道未连接");
            TraceContext.clear();
            return;
        }

        // 将渠道信息存入请求属性，供Controller使用
        request.setAttribute("openApiChannel", channel);
        
        try {
            // 关键：将渠道所属租户存入安全上下文，确保后续 Service 逻辑能自动应用隔离
            TenantContext.setTenantId(channel.getTenantId());
            filterChain.doFilter(request, response);
        } finally {
            // 必须清理，防止线程复用导致上下文污染
            TenantContext.clear();
            TraceContext.clear();
        }
    }

    private void writeError(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        String body = objectMapper.writeValueAsString(Map.of(
                "code", code,
                "message", message,
                "timestamp", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        ));
        response.getWriter().write(body);
    }
}
