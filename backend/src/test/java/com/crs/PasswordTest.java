package com.crs;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 测试当前数据库中的哈希值
        String currentHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH";
        String password = "admin123";
        
        System.out.println("Testing current hash: " + currentHash);
        System.out.println("Password: " + password);
        System.out.println("Match result: " + encoder.matches(password, currentHash));
        
        // 生成新的哈希值
        String newHash = encoder.encode(password);
        System.out.println("New hash: " + newHash);
        System.out.println("New hash match: " + encoder.matches(password, newHash));
    }
}