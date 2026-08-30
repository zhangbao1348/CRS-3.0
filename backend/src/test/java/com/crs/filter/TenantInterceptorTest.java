package com.crs.filter;

import com.crs.util.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TenantInterceptorTest {

    private final TenantInterceptor interceptor = new TenantInterceptor();

    @AfterEach
    void clearContexts() {
        TenantContext.clear();
        com.crs.util.TraceContext.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldRejectCrossTenantHeaderForRegularUser() throws Exception {
        authenticate("ROLE_user");
        TenantContext.setTenantId(1);
        MockHttpServletRequest request = requestWithTenant("2");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(403, response.getStatus());
        assertEquals(1, TenantContext.getTenantId());
    }

    @Test
    void shouldUseJwtTenantWhenHeaderIsMissing() throws Exception {
        authenticate("ROLE_user");
        TenantContext.setTenantId(3);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hotels");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertEquals(3, request.getAttribute("tenantId"));
    }

    @Test
    void shouldAllowSuperAdminToSelectTenant() throws Exception {
        authenticate("ROLE_super_admin");
        MockHttpServletRequest request = requestWithTenant("7");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertEquals(7, TenantContext.getTenantId());
        assertEquals(7, request.getAttribute("tenantId"));
    }

    @Test
    void shouldRejectSuperAdminRequestWithoutSelectedTenant() throws Exception {
        authenticate("ROLE_super_admin");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hotels");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(400, response.getStatus());
    }

    private void authenticate(String authority) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "tester", null, List.of(new SimpleGrantedAuthority(authority)));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private MockHttpServletRequest requestWithTenant(String tenantId) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hotels");
        request.addHeader("X-Tenant-Id", tenantId);
        return request;
    }
}
