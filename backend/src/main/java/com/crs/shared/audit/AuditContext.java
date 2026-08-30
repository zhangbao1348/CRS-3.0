package com.crs.shared.audit;

import com.crs.util.TraceContext;
import com.crs.util.TenantContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;

/** 业务审计快照，统一提供租户、操作者、链路标识和发生时间。 */
public record AuditContext(Integer tenantId, String actor, String traceId, Instant occurredAt) {
    /** 捕获当前请求线程的审计信息，避免业务模块重复读取多个上下文。 */
    public static AuditContext current() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String actor = authentication != null && authentication.isAuthenticated()
                ? authentication.getName() : "anonymous";
        return new AuditContext(TenantContext.getTenantId(), actor, TraceContext.getTraceId(), Instant.now());
    }
}
