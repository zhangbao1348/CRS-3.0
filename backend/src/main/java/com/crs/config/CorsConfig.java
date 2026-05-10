package com.crs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域资源共享配置类 (CorsConfig)
 * 
 * <p>本类负责解决浏览器同源策略导致的跨域访问限制，确保前端 React 应用能够顺利调用后端 Spring Boot 提供的 REST API。</p>
 * 
 * <p>配置说明：</p>
 * <ul>
 *     <li>**允许来源 (Allowed Origins)**：当前配置为全开放 (`*`)，支持任何来源的访问。生产环境建议限制为特定的前端域名。</li>
 *     <li>**允许方法 (Allowed Methods)**：允许 GET, POST, PUT, DELETE, OPTIONS 等所有标准的 HTTP 方法。</li>
 *     <li>**允许头部 (Allowed Headers)**：允许所有自定义请求头（如 X-Tenant-Id, Authorization）。</li>
 *     <li>**预检有效期 (Max Age)**：设置 OPTIONS 预检请求的缓存时间，减少网络往返开销。</li>
 * </ul>
 */
@Configuration
public class CorsConfig {
    
    /**
     * 配置跨域过滤器。
     * 
     * @return CorsFilter 实例
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有域名进行跨域访问
        config.addAllowedOriginPattern("*");
        // 允许所有 HTTP 请求方法
        config.addAllowedMethod("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许发送 Cookie 等凭证信息
        config.setAllowCredentials(true);
        // 预检请求的有效期（单位：秒）
        config.setMaxAge(3600L);
        
        // 为特定的路径模式应用跨域策略
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        
        return new CorsFilter(source);
    }
}