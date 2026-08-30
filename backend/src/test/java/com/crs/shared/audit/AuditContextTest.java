package com.crs.shared.audit;

import com.crs.util.TenantContext;
import com.crs.util.TraceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuditContextTest {

    @AfterEach
    void cleanup() {
        TenantContext.clear();
        TraceContext.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldCaptureTenantActorAndTraceAsOneSnapshot() {
        TenantContext.setTenantId(7);
        TraceContext.setTraceId("trace-policy-1234");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("operator", "n/a", List.of()));

        AuditContext context = AuditContext.current();

        assertEquals(7, context.tenantId());
        assertEquals("operator", context.actor());
        assertEquals("trace-policy-1234", context.traceId());
        assertNotNull(context.occurredAt());
    }
}
