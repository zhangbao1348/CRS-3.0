package com.crs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 属性配置类 (JwtConfig)
 * 
 * <p>本类负责映射并持有配置文件 (如 application.yml) 中以 `jwt` 为前缀的相关属性。
 * 集中管理令牌的签名密钥及各类令牌的有效期，为 {@link com.crs.util.JwtUtil} 提供配置支持。</p>
 */
@Configuration
public class JwtConfig {
    
    /** 令牌签名密钥：用于保证令牌不被篡改的二进制秘密字符串 */
    @Value("${jwt.secret}")
    private String secret;
    
    /** 访问令牌有效期：单位为秒，过期后前端需使用刷新令牌换取新令牌 */
    @Value("${jwt.expiration}")
    private long expiration;
    
    /** 刷新令牌有效期：单位为秒，通常设为访问令牌的数倍，用于静默续期 */
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;
    
    public String getSecret() {
        return secret;
    }
    
    public long getExpiration() {
        return expiration;
    }
    
    public long getRefreshExpiration() {
        return refreshExpiration;
    }
}

