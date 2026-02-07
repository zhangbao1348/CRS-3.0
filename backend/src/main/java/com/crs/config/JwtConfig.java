package com.crs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置类
 * 管理JWT相关的配置信息
 */
@Configuration
public class JwtConfig {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private long expiration;
    
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
