package com.crs.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码工具类
 * 用于密码加密和验证
 */
@Component
public class PasswordUtil {
    
    private final BCryptPasswordEncoder encoder;
    
    public PasswordUtil() {
        this.encoder = new BCryptPasswordEncoder();
    }
    
    /**
     * 加密密码
     * @param password 原始密码
     * @return 加密后的密码
     */
    public String encryptPassword(String password) {
        return encoder.encode(password);
    }
    
    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
