package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 系统用户实体类 (User)
 * 
 * <p>本类对应数据库中的 `users` 表，记录了系统管理员、酒店操作员等所有账号的身份信息。</p>
 * 
 * <p>核心职责：</p>
 * <ul>
 *     <li>**多租户管理**：每个用户必须属于一个确定的租户 (`tenantId`)。超级管理员可能属于系统默认租户。</li>
 *     <li>**身份识别**：存储加密后的密码、用户名、姓名等基础资料。</li>
 *     <li>**登录审计**：记录最后登录时间、登录 IP 等安全审计信息。</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    
    /** 用户内部主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 所属租户 ID。用户登录后，系统将依据此字段初始化 {@link com.crs.util.TenantContext} */
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    /** 登录用户名，系统内唯一 */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    /** 加密后的密码字符串 (通常采用 BCrypt 算法) */
    @Column(name = "password", nullable = false, length = 100)
    private String password;
    
    /** 用户真实姓名或昵称 */
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    /** 电子邮箱，用于找回密码或接收通知，全系统唯一 */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    /** 联系电话 */
    @Column(name = "phone", length = 20)
    private String phone;
    
    /** 头像图片 URL */
    @Column(name = "avatar", length = 255)
    private String avatar;
    
    /** 账号状态：active-正常，inactive-禁用 */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;
    
    /** 最后一次成功登录的时间 */
    @Column(name = "last_login_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTime;
    
    /** 最后一次成功登录的客户端 IP 地址 */
    @Column(name = "last_login_ip", length = 50)
    private String lastLoginIp;
    
    /** 账号创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    /** 账号资料最后更新时间 */
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    /**
     * 用户账号状态枚举
     */
    public enum Status {
        /** 正常使用 */
        active, 
        /** 已禁用，无法登录系统 */
        inactive
    }
    
    /**
     * JPA 更新前自动刷新更新时间。
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}

