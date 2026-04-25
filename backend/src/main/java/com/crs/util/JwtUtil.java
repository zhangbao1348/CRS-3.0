package com.crs.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成和验证JWT令牌
 */
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private long expiration;
    
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;
    
    /**
     * 生成JWT令牌
     * @param username 用户名
     * @return JWT令牌
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, expiration);
    }
    
    /**
     * 生成JWT令牌（包含租户ID）
     * @param username 用户名
     * @param tenantId 租户ID
     * @return JWT令牌
     */
    public String generateToken(String username, Integer tenantId) {
        Map<String, Object> claims = new HashMap<>();
        if (tenantId != null) {
            claims.put("tenantId", tenantId);
        }
        return createToken(claims, username, expiration);
    }
    
    /**
     * 生成刷新令牌
     * @param username 用户名
     * @return 刷新令牌
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, refreshExpiration);
    }
    
    /**
     * 生成刷新令牌（包含租户ID）
     * @param username 用户名
     * @param tenantId 租户ID
     * @return 刷新令牌
     */
    public String generateRefreshToken(String username, Integer tenantId) {
        Map<String, Object> claims = new HashMap<>();
        if (tenantId != null) {
            claims.put("tenantId", tenantId);
        }
        return createToken(claims, username, refreshExpiration);
    }
    
    /**
     * 从JWT令牌中提取租户ID
     * @param token JWT令牌
     * @return 租户ID
     */
    public Integer extractTenantId(String token) {
        Claims claims = extractAllClaims(token);
        Object tenantId = claims.get("tenantId");
        return tenantId != null ? Integer.parseInt(tenantId.toString()) : null;
    }
    
    /**
     * 创建JWT令牌
     * @param claims 声明
     * @param subject 主题
     * @param expirationTime 过期时间
     * @return JWT令牌
     */
    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime * 1000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
    
    /**
     * 从JWT令牌中提取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    /**
     * 从JWT令牌中提取所有声明
     * @param token JWT令牌
     * @return 声明
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
    
    /**
     * 检查JWT令牌是否过期
     * @param token JWT令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
    
    /**
     * 验证JWT令牌
     * @param token JWT令牌
     * @param username 用户名
     * @return 是否有效
     */
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
