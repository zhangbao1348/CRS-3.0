package com.crs.config;

import com.crs.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 安全核心配置类 (SecurityConfig)
 * 
 * <p>本类基于 Spring Security 框架，定义了 CRS 系统的安全策略和认证机制。</p>
 * 
 * <p>主要配置项包括：</p>
 * <ul>
 *     <li>**无状态会话管理**：由于采用 JWT 认证，系统禁用了 Session 机制，改为完全的无状态模式。</li>
 *     <li>**CSRF 禁用**：针对 API 类型应用，禁用了跨站请求伪造保护。</li>
 *     <li>**URL 访问控制**：定义了哪些接口需要认证，哪些接口可以匿名访问（当前配置为全放行，依赖业务层或后续增强）。</li>
 *     <li>**JWT 过滤器集成**：在标准身份验证过滤器之前插入自定义的 {@link JwtFilter}。</li>
 *     <li>**加密算法**：指定 BCrypt 为系统默认的密码加密/匹配算法。</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /** 自定义的 JWT 身份验证过滤器 */
    @Autowired
    private JwtFilter jwtFilter;
    
    /**
     * 配置安全过滤链。
     * 
     * @param http HttpSecurity 安全配置对象
     * @return 构建后的安全过滤链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（跨站请求伪造）保护，因为我们使用 JWT
            .csrf(csrf -> csrf.disable())
            // 设置会话管理策略为无状态，不创建也不使用 Session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置请求授权规则
            .authorizeHttpRequests(authorize -> authorize
                // 暂时放行所有 /api/** 请求，业务层进行细粒度校验
                .requestMatchers("/api/**").permitAll()
                // 放行 Swagger UI 文档相关接口
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                // 放行监控端点
                .requestMatchers("/actuator/**").permitAll()
                // 其它任何请求均放行
                .anyRequest().permitAll()
            )
            // 将 JWT 过滤器添加到用户名密码认证过滤器之前
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * 配置密码加密器。
     * 
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 获取身份验证管理器，用于处理登录请求。
     * 
     * @param config 身份验证配置
     * @return 身份验证管理器 Bean
     * @throws Exception 获取异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

