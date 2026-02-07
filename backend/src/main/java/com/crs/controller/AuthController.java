package com.crs.controller;

import com.crs.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * 用于处理HTTP请求并调用认证服务
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * 用户登录
     * @param loginData 登录数据
     * @return 登录响应
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        try {
            Map<String, Object> response = authService.login(username, password);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 用户注册
     * @param registerData 注册数据
     * @return 注册响应
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registerData) {
        String username = registerData.get("username");
        String password = registerData.get("password");
        String name = registerData.get("name");
        String email = registerData.get("email");
        String role = registerData.getOrDefault("role", "USER");
        
        try {
            var user = authService.register(username, password, name, email, role);
            return ResponseEntity.ok(Map.of("message", "User registered successfully", "user", user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 刷新令牌
     * @param refreshData 刷新令牌数据
     * @return 刷新响应
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> refreshData) {
        String refreshToken = refreshData.get("refreshToken");
        
        try {
            Map<String, String> response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 获取当前用户信息
     * @return 当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        // 从SecurityContext中获取当前用户信息
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not authenticated"));
        }
        
        var userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of(
                "username", userDetails.getUsername(),
                "authorities", userDetails.getAuthorities()
        ));
    }
}
