package com.crs.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码处理工具类 (PasswordUtil)
 * 
 * <p>本类负责系统中用户登录密码的单向加密与验证逻辑。</p>
 * 
 * <p>技术说明：</p>
 * <ul>
 *     <li>采用 **BCrypt** 加密算法。这是一种强哈希算法，自带盐值 (Salt)，能有效抵御彩虹表攻击。</li>
 *     <li>BCrypt 算法具有“自适应性”，可通过调整工作因子来增加破解的时间成本，适应计算能力的提升。</li>
 * </ul>
 */
@Component
public class PasswordUtil {
    
    /** Spring Security 提供的 BCrypt 加密实现类 */
    private final BCryptPasswordEncoder encoder;
    
    /**
     * 构造函数，初始化加密器实例。
     */
    public PasswordUtil() {
        this.encoder = new BCryptPasswordEncoder();
    }
    
    /**
     * 将原始明文密码进行加密。
     * 在用户注册或修改密码时调用。
     * 
     * @param password 原始明文密码
     * @return 加密后的哈希字符串（包含算法标识、工作因子和随机盐值）
     */
    public String encryptPassword(String password) {
        return encoder.encode(password);
    }
    
    /**
     * 验证明文密码是否与存储的加密哈希匹配。
     * 在用户登录验证身份时调用。
     * 
     * @param rawPassword 客户端提交的原始明文密码
     * @param encodedPassword 数据库中存储的加密后的哈希字符串
     * @return 如果密码正确则返回 true；否则返回 false
     */
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}

