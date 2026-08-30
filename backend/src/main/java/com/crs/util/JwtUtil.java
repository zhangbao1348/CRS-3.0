package com.crs.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类 (JwtUtil)
 * 
 * <p>本类负责系统中所有身份验证令牌 (JSON Web Token) 的全生命周期管理，包括：</p>
 * <ul>
 *     <li>根据用户信息和租户上下文生成访问令牌 (Access Token)。</li>
 *     <li>生成刷新令牌 (Refresh Token) 以延长登录有效期。</li>
 *     <li>从加密令牌中解析出用户名、过期时间以及关键的业务字段（如 tenantId）。</li>
 *     <li>验证令牌的合法性与时效性。</li>
 * </ul>
 */
@Component
public class JwtUtil {

    private static final int MINIMUM_SECRET_BYTES = 32;

    /** JWT 签名密钥，从环境变量映射的配置加载 */
    private final SecretKey signingKey;

    /** 访问令牌有效期（秒） */
    private final long expiration;

    /** 刷新令牌有效期（秒） */
    private final long refreshExpiration;

    /**
     * 初始化 JWT 签名器，并拒绝长度不足的密钥。
     *
     * @param secret 环境变量提供的签名密钥
     * @param expiration 访问令牌有效期（秒）
     * @param refreshExpiration 刷新令牌有效期（秒）
     */
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        byte[] secretBytes = secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MINIMUM_SECRET_BYTES) {
            throw new IllegalArgumentException("JWT 签名密钥至少需要 32 字节");
        }
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        this.expiration = expiration;
        this.refreshExpiration = refreshExpiration;
    }
    
    /**
     * 为指定用户生成基础 JWT 令牌。
     * 
     * @param username 用户名
     * @return 签名后的 JWT 字符串
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, expiration);
    }
    
    /**
     * 为指定用户生成包含租户信息的 JWT 令牌。
     * 
     * @param username 用户名
     * @param tenantId 关联的租户 ID
     * @return 签名后的 JWT 字符串
     */
    public String generateToken(String username, Integer tenantId) {
        Map<String, Object> claims = new HashMap<>();
        if (tenantId != null) {
            // 将租户 ID 存入 Token 载荷，方便跨服务或拦截器获取
            claims.put("tenantId", tenantId);
        }
        return createToken(claims, username, expiration);
    }
    
    /**
     * 生成刷新令牌（较长有效期）。
     * 
     * @param username 用户名
     * @return 刷新令牌字符串
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, refreshExpiration);
    }
    
    /**
     * 生成包含租户信息的刷新令牌。
     * 
     * @param username 用户名
     * @param tenantId 租户 ID
     * @return 刷新令牌字符串
     */
    public String generateRefreshToken(String username, Integer tenantId) {
        Map<String, Object> claims = new HashMap<>();
        if (tenantId != null) {
            claims.put("tenantId", tenantId);
        }
        return createToken(claims, username, refreshExpiration);
    }
    
    /**
     * 从 JWT 令牌载荷 (Claims) 中安全提取租户 ID。
     * 
     * @param token JWT 令牌
     * @return 解析出的租户 ID，若不存在则返回 null
     */
    public Integer extractTenantId(String token) {
        Claims claims = extractAllClaims(token);
        Object tenantId = claims.get("tenantId");
        return tenantId != null ? Integer.parseInt(tenantId.toString()) : null;
    }
    
    /**
     * 执行底层令牌构建逻辑。
     * 
     * @param claims 业务自定义载荷数据
     * @param subject 令牌主题（通常为用户名）
     * @param expirationTime 过期偏移量
     * @return 构建完成并签名的令牌字符串
     */
    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // 设置失效时刻 = 当前时间 + 有效秒数
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime * 1000))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 从令牌中解析用户名。
     * 
     * @param token JWT 令牌
     * @return 用户名 (Subject)
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    /**
     * 解析令牌获取完整的 Claims 数据。
     * 
     * @param token JWT 令牌
     * @return Claims 对象
     * @throws io.jsonwebtoken.JwtException 若签名不匹配或格式有误则抛出异常
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
    }
    
    /**
     * 判断令牌是否已超过有效期。
     * 
     * @param token JWT 令牌
     * @return true 表示已过期，需重新登录或刷新；false 表示依然有效
     */
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
    
    /**
     * 综合验证令牌的合法性。
     * 
     * @param token 待验证的令牌
     * @param username 期望的用户名
     * @return 只有用户名一致且未过期时返回 true
     */
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
