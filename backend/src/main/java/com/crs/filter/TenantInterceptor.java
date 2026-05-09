package com.crs.filter;

import com.crs.util.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 租户拦截器，用于验证租户ID的合法性
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TenantInterceptor.class);
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tenantIdStr = request.getHeader("X-Tenant-Id");
        
        logger.info("=== TenantInterceptor preHandle 开始 ===");
        logger.info("请求路径: {}", request.getRequestURI());
        logger.info("X-Tenant-Id 请求头: {}", tenantIdStr);
        
        if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
            try {
                Integer tenantId = Integer.parseInt(tenantIdStr);
                logger.info("从请求头解析到的租户ID: {}", tenantId);
                if (tenantId <= 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Invalid tenant ID");
                    logger.error("无效的租户ID: {}", tenantId);
                    return false;
                }
                TenantContext.setTenantId(tenantId);
                request.setAttribute("tenantId", tenantId);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid tenant ID format");
                logger.error("租户ID格式无效: {}", tenantIdStr, e);
                return false;
            }
        } else {
            Integer existingTenantId = TenantContext.getTenantId();
            if (existingTenantId != null) {
                logger.info("X-Tenant-Id请求头不存在，使用JwtFilter从token中提取的租户ID: {}", existingTenantId);
                request.setAttribute("tenantId", existingTenantId);
            } else {
                logger.info("X-Tenant-Id请求头不存在且token中无租户ID，使用默认租户ID: 1");
                TenantContext.setTenantId(1);
                request.setAttribute("tenantId", 1);
            }
        }
        
        logger.info("最终租户ID: {}", TenantContext.getTenantId());
        logger.info("=== TenantInterceptor preHandle 结束 ===");
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理TenantContext，防止内存泄漏
        logger.info("=== TenantInterceptor afterCompletion 开始 ===");
        logger.info("清理TenantContext");
        TenantContext.clear();
        logger.info("=== TenantInterceptor afterCompletion 结束 ===");
    }
}
