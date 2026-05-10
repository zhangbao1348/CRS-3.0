package com.crs.config;

import com.crs.filter.TenantInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 核心配置类 (WebConfig)
 * 
 * <p>本类通过实现 WebMvcConfigurer 接口，对 Spring MVC 的行为进行自定义配置。</p>
 * 
 * <p>当前主要职责：</p>
 * <ul>
 *     <li>注册 {@link TenantInterceptor} 租户拦截器，确保所有业务 API 都能正确处理租户上下文。</li>
 *     <li>定义拦截路径与排除路径，防止拦截器影响登录、注册等公开接口的正常调用。</li>
 * </ul>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    /**
     * 自动注入租户拦截器实例。
     * 该拦截器负责从 HTTP 请求中提取租户标识。
     */
    @Autowired
    private TenantInterceptor tenantInterceptor;
    
    /**
     * 注册自定义拦截器链。
     * 
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册租户拦截器：
        // 1. 拦截所有以 /api/ 开头的业务请求。
        // 2. 排除 /api/auth/** 路径，因为认证接口（登录等）在租户确认之前执行。
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**");
    }
}

