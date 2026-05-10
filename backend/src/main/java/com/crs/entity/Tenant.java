package com.crs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 租户实体类 (Tenant)
 * 
 * <p>本类对应数据库中的 `tenants` 表，是 CRS 系统实现 SAAS 多租户架构的最顶层实体。</p>
 * 
 * <p>业务含义：</p>
 * <ul>
 *     <li>一个租户通常对应一个酒店集团（如万豪、希尔顿）或一个独立的连锁酒店品牌。</li>
 *     <li>所有业务数据（酒店、房型、价格、订单等）通过 `tenant_id` 物理或逻辑隔离，确保租户间的数据安全性。</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tenants")
public class Tenant {
    
    /** 租户唯一主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 租户编码 (如 'MARRIOTT')，用于唯一标识租户，通常在系统初始化或 API 调用中使用 */
    @Column(name = "tenant_code", nullable = false, unique = true, length = 50)
    private String tenantCode;
    
    /** 租户显示名称 (如 '万豪国际集团') */
    @Column(name = "tenant_name", nullable = false, length = 100)
    private String tenantName;
    
    /** 租户状态：active-启用，inactive-停用 */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;
    
    /** 租户服务到期日期，用于控制 SAAS 服务的访问权限 */
    @Column(name = "expire_date")
    @Temporal(TemporalType.DATE)
    private Date expireDate;
    
    /** 联系人姓名 */
    @Column(name = "contact_name", length = 50)
    private String contactName;
    
    /** 联系人电话 */
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;
    
    /** 联系人邮箱 */
    @Column(name = "contact_email", length = 100)
    private String contactEmail;
    
    /** 租户下属酒店额度或当前酒店数量统计 */
    @Column(name = "hotel_count")
    private Integer hotelCount = 0;
    
    /** 租户注册地址或办公地址 */
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    /** 记录创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    /** 记录最后更新时间 */
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    /**
     * 租户状态枚举
     */
    public enum Status {
        /** 启用状态，允许登录及业务操作 */
        active, 
        /** 停用状态，禁止登录及其下属所有酒店的 API 调用 */
        inactive
    }
    
    /**
     * JPA 生命周期回调，在更新前自动刷新更新时间。
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}

