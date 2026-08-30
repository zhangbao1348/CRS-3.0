package com.crs.config;

import com.crs.filter.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
 *     <li>**URL 访问控制**：仅开放认证入口、开放 API、接口文档与健康检查，其余业务 API 强制认证。</li>
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
                // 浏览器跨域预检请求不携带 JWT，必须先行放行
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 认证入口与刷新入口允许匿名访问
                .requestMatchers(HttpMethod.POST,
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/api/auth/refresh-token").permitAll()
                // 开放 API 使用 X-Api-Key + X-Api-Secret 独立认证
                .requestMatchers("/api/open/**").permitAll()
                // PMS Webhook 使用独立 HMAC + 时间窗认证，不依赖浏览器 JWT
                .requestMatchers(HttpMethod.POST, "/webhooks/pms/**").permitAll()
                // 放行 Swagger UI 文档相关接口
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                // 只匿名开放健康与基础信息，防止后续误暴露其它 actuator 端点
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/actuator/**").denyAll()
                // 租户与权限初始化属于超级管理员能力
                .requestMatchers("/api/tenants/**", "/api/permission-init/**", "/api/test-permission/**")
                    .hasRole("super_admin")
                .requestMatchers(HttpMethod.POST, "/api/auth/register").hasRole("super_admin")
                // 身份、角色与菜单管理会改变系统权限边界，只允许超级管理员操作
                .requestMatchers("/api/users/**", "/api/roles/**", "/api/menus/**")
                    .hasRole("super_admin")
                // 旧操作日志表尚无租户字段，在完成数据迁移前不向租户用户开放
                .requestMatchers("/api/operation-logs/**").hasRole("super_admin")
                // 集团设施表没有 tenant_id，是全局共享目录；租户用户可读，只有超级管理员可改
                .requestMatchers(HttpMethod.POST, "/api/group-facilities/**").hasRole("super_admin")
                .requestMatchers(HttpMethod.PUT, "/api/group-facilities/**").hasRole("super_admin")
                .requestMatchers(HttpMethod.DELETE, "/api/group-facilities/**").hasRole("super_admin")
                // 其它业务 API 必须持有有效 JWT
                .requestMatchers("/api/**").authenticated()
                // 非 API 静态资源保持兼容
                .anyRequest().permitAll()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, exception) ->
                    writeSecurityError(request, response, HttpServletResponse.SC_UNAUTHORIZED,
                            "AUTHENTICATION_REQUIRED", "未认证或令牌无效"))
                .accessDeniedHandler((request, response, exception) ->
                    writeSecurityError(request, response, HttpServletResponse.SC_FORBIDDEN,
                            "ACCESS_DENIED", "无权访问该资源"))
            )
            // 将 JWT 过滤器添加到用户名密码认证过滤器之前
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * 输出统一的 JSON 安全错误，保持前端 401/403 处理契约稳定。
     *
     * @param response HTTP 响应
     * @param status HTTP 状态码
     * @param message 对外安全提示
     */
    private static void writeSecurityError(
            jakarta.servlet.http.HttpServletRequest request,
            HttpServletResponse response,
            int status,
            String code,
            String message)
            throws java.io.IOException {
        com.crs.shared.api.ApiErrorWriter.write(request, response, status, code, message);
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
