package com.crs.filter;

import com.crs.util.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 租户识别拦截器 (TenantInterceptor)
 * 
 * <p>本类负责在请求到达 Controller 之前，识别并提取当前请求所属的租户 ID。
 * 它是实现 SAAS 多租户数据隔离的第一道防线。</p>
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 尝试从 HTTP Header 获取租户 ID
        String tenantIdStr = request.getHeader("X-Tenant-Id");
        
        logger.debug("租户识别开始，path={}，headerTenant={}，traceId={}",
                request.getRequestURI(), tenantIdStr, com.crs.util.TraceContext.getTraceId());

        Integer authenticatedTenantId = TenantContext.getTenantId();
        boolean superAdmin = hasAuthority("ROLE_super_admin");
        
        if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
            try {
                Integer tenantId = Integer.parseInt(tenantIdStr);
                logger.debug("从请求头解析到租户 ID: {}", tenantId);
                
                // 校验租户 ID 是否合法
                if (tenantId <= 0) {
                    logger.error("无效的租户ID: {}", tenantId);
                    return writeTenantError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                            "INVALID_TENANT", "租户 ID 必须为正整数");
                }

                // 普通用户只能使用 JWT 中签发的租户；超级管理员可显式切换租户
                if (!superAdmin && authenticatedTenantId == null) {
                    logger.warn("非超级管理员缺少已认证租户上下文，path={}", request.getRequestURI());
                    return writeTenantError(request, response, HttpServletResponse.SC_FORBIDDEN,
                            "TENANT_CONTEXT_MISSING", "令牌缺少租户上下文");
                }
                if (!superAdmin && !tenantId.equals(authenticatedTenantId)) {
                    logger.warn("拒绝跨租户请求，tokenTenant={}，headerTenant={}，path={}",
                            authenticatedTenantId, tenantId, request.getRequestURI());
                    return writeTenantError(request, response, HttpServletResponse.SC_FORBIDDEN,
                            "TENANT_ACCESS_DENIED", "无权访问指定租户");
                }
                
                // 将租户 ID 绑定 to 当前线程
                TenantContext.setTenantId(tenantId);
                // 同时存入 Request Attribute 方便后续在视图层或其它拦截器中使用
                request.setAttribute("tenantId", tenantId);
                
            } catch (NumberFormatException e) {
                logger.error("租户ID格式无效: {}", tenantIdStr, e);
                return writeTenantError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                        "INVALID_TENANT", "租户 ID 格式无效");
            }
        } else {
            // 如果 Header 为空，尝试从 JwtFilter 已经存入 ThreadLocal 的值中获取
            Integer existingTenantId = authenticatedTenantId;
            if (existingTenantId != null) {
                logger.debug("请求头未指定租户，使用认证上下文租户 ID: {}", existingTenantId);
                request.setAttribute("tenantId", existingTenantId);
            } else {
                // 不再从路径猜测租户，也不再降级到默认租户 1，避免未授权数据落入错误租户
                int status = superAdmin
                        ? HttpServletResponse.SC_BAD_REQUEST
                        : HttpServletResponse.SC_FORBIDDEN;
                String message = superAdmin ? "请选择租户后重试" : "令牌缺少租户上下文";
                logger.warn("请求缺少可信租户上下文，superAdmin={}，path={}",
                        superAdmin, request.getRequestURI());
                return writeTenantError(request, response, status,
                        superAdmin ? "TENANT_SELECTION_REQUIRED" : "TENANT_CONTEXT_MISSING", message);
            }
        }
        
        logger.debug("最终确定租户 ID: {}", TenantContext.getTenantId());
        return true;
    }

    /**
     * 判断当前已认证用户是否具备指定角色权限。
     *
     * @param authority Spring Security 权限编码
     * @return 已认证且拥有权限时返回 true
     */
    private boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> authority.equals(grantedAuthority.getAuthority()));
    }

    /**
     * 输出租户边界错误，并终止本次 Controller 调用。
     *
     * @param response HTTP 响应
     * @param status HTTP 状态码
     * @param message 对外提示
     * @return 固定返回 false，供 preHandle 直接使用
     */
    private boolean writeTenantError(
            HttpServletRequest request,
            HttpServletResponse response,
            int status,
            String code,
            String message) throws Exception {
        com.crs.shared.api.ApiErrorWriter.write(request, response, status, code, message);
        return false;
    }
    
    /**
     * 在整个请求处理完成（包括视图渲染）之后执行。
     * 核心职责：清理线程变量。
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 必须清理 TenantContext，防止在 Tomcat 等使用了线程池的容器中出现数据污染或内存泄漏
        logger.debug("清理 TenantContext 线程变量");
        TenantContext.clear();
    }
}
