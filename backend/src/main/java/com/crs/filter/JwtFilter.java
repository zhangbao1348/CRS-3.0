package com.crs.filter;

import com.crs.util.JwtUtil;
import com.crs.util.TenantContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JWT过滤器
 * 用于在请求中验证JWT令牌并设置用户认证信息
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    
    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
            throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwt = null;
        Integer tenantId = null;
        
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                try {
                    username = jwtUtil.extractUsername(jwt);
                    tenantId = jwtUtil.extractTenantId(jwt);
                } catch (Exception e) {
                    logger.error("Error extracting information from token: {}", e.getMessage());
                }
            }
            
            // 设置租户上下文
            if (tenantId != null) {
                TenantContext.setTenantId(tenantId);
                logger.debug("Tenant ID set to: {}", tenantId);
            }
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Error in JWT filter: {}", e.getMessage());
            throw e;
        } finally {
            // 关键安全清理：防止线程复用导致的租户上下文泄露
            TenantContext.clear();
        }
    }
}
