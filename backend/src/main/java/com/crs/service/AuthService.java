package com.crs.service;

import com.crs.entity.User;
import com.crs.repository.UserRepository;
import com.crs.util.JwtUtil;
import com.crs.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务类
 * 用于处理用户登录、注册等认证相关的业务逻辑
 */
@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    
    public AuthService(UserRepository userRepository, PasswordUtil passwordUtil, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordUtil = passwordUtil;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 包含token和用户信息的Map
     */
    public Map<String, Object> login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordUtil.validatePassword(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        if (user.getStatus() != User.Status.active) {
            throw new RuntimeException("User account is inactive");
        }
        
        String token = jwtUtil.generateToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("refreshToken", refreshToken);
        response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
        ));
        
        return response;
    }
    
    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param name 姓名
     * @param email 邮箱
     * @param role 角色
     * @return 注册成功的用户信息
     */
    public User register(String username, String password, String name, String email, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordUtil.encryptPassword(password));
        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        user.setStatus(User.Status.active);
        
        return userRepository.save(user);
    }
    
    /**
     * 刷新令牌
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    public Map<String, String> refreshToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        if (!jwtUtil.validateToken(refreshToken, username)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        String newToken = jwtUtil.generateToken(username);
        Map<String, String> response = new HashMap<>();
        response.put("token", newToken);
        return response;
    }
}
