package com.crs.filter;

import com.crs.util.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        // 从请求头获取 traceId
        String traceId = request.getHeader("X-Trace-Id");
        com.crs.util.TraceContext.setTraceId(traceId);

        // 尝试从 HTTP Header 获取租户 ID
        String tenantIdStr = request.getHeader("X-Tenant-Id");
        
        logger.info("=== TenantInterceptor preHandle 开始 ===");
        logger.info("请求路径: {}", request.getRequestURI());
        logger.info("X-Tenant-Id 请求头: {}", tenantIdStr);
        logger.info("X-Trace-Id 追踪ID: {}", com.crs.util.TraceContext.getTraceId());
        
        if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
            try {
                Integer tenantId = Integer.parseInt(tenantIdStr);
                logger.info("从请求头解析到的租户ID: {}", tenantId);
                
                // 校验租户 ID 是否合法
                if (tenantId <= 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Invalid tenant ID");
                    logger.error("无效的租户ID: {}", tenantId);
                    return false;
                }
                
                // 将租户 ID 绑定 to 当前线程
                TenantContext.setTenantId(tenantId);
                // 同时存入 Request Attribute 方便后续在视图层或其它拦截器中使用
                request.setAttribute("tenantId", tenantId);
                
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid tenant ID format");
                logger.error("租户ID格式无效: {}", tenantIdStr, e);
                return false;
            }
        } else {
            // 如果 Header 为空，尝试从 JwtFilter 已经存入 ThreadLocal 的值中获取
            Integer existingTenantId = TenantContext.getTenantId();
            if (existingTenantId != null) {
                logger.info("X-Tenant-Id请求头不存在，使用从 token 中提取的租户 ID: {}", existingTenantId);
                request.setAttribute("tenantId", existingTenantId);
            } else {
                // 尝试从请求路径中提取 groupId (针对 /api/.../group/{groupId} 类型的路径)
                String uri = request.getRequestURI();
                if (uri.contains("/group/")) {
                    try {
                        String[] segments = uri.split("/");
                        for (int i = 0; i < segments.length; i++) {
                            if ("group".equals(segments[i]) && i + 1 < segments.length) {
                                String groupIdStr = segments[i + 1];
                                if (!"current".equals(groupIdStr)) {
                                    Integer groupId = Integer.parseInt(groupIdStr);
                                    logger.info("从路径中解析到的租户(集团)ID: {}", groupId);
                                    TenantContext.setTenantId(groupId);
                                    request.setAttribute("tenantId", groupId);
                                    return true;
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("尝试从路径解析租户ID失败: {}", e.getMessage());
                    }
                }

                // 恢复默认租户兜底逻辑：为了兼容性，在无法确定租户时使用默认 ID=1
                logger.warn("未识别到租户上下文，降级使用默认租户 ID: 1");
                TenantContext.setTenantId(1);
                request.setAttribute("tenantId", 1);
            }
        }
        
        logger.info("最终确定的租户 ID: {}", TenantContext.getTenantId());
        logger.info("=== TenantInterceptor preHandle 结束 ===");
        return true;
    }
    
    /**
     * 在整个请求处理完成（包括视图渲染）之后执行。
     * 核心职责：清理线程变量。
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 必须清理 TenantContext，防止在 Tomcat 等使用了线程池的容器中出现数据污染或内存泄漏
        logger.info("=== TenantInterceptor afterCompletion 开始 ===");
        logger.info("清理 TenantContext 与 TraceContext 线程变量");
        TenantContext.clear();
        com.crs.util.TraceContext.clear();
        logger.info("=== TenantInterceptor afterCompletion 结束 ===");
    }
}
