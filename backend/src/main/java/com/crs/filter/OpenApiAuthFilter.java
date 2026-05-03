package com.crs.filter;

import com.crs.entity.TenantChannel;
import com.crs.repository.TenantChannelRepository;
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
 * 通过 X-Api-Key + X-Api-Secret 验证渠道身份
 * 仅对 /api/open/** 路径生效
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
        String apiKey = request.getHeader("X-Api-Key");
        String apiSecret = request.getHeader("X-Api-Secret");

        if (apiKey == null || apiKey.isBlank()) {
            writeError(response, 401, "缺少 X-Api-Key 请求头");
            return;
        }
        if (apiSecret == null || apiSecret.isBlank()) {
            writeError(response, 401, "缺少 X-Api-Secret 请求头");
            return;
        }

        TenantChannel channel = tenantChannelRepository.findByAccessKey(apiKey);
        if (channel == null) {
            writeError(response, 401, "无效的 API Key");
            return;
        }
        if (!apiSecret.equals(channel.getAccessSecret())) {
            writeError(response, 401, "API Secret 不匹配");
            return;
        }
        if (!"active".equals(channel.getStatus())) {
            writeError(response, 401, "渠道已停用");
            return;
        }
        if (!Boolean.TRUE.equals(channel.getConnected())) {
            writeError(response, 401, "渠道未连接");
            return;
        }

        // 将渠道信息存入请求属性，供Controller使用
        request.setAttribute("openApiChannel", channel);
        filterChain.doFilter(request, response);
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
