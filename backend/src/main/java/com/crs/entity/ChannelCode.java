package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;

/**
 * 分销渠道定义实体类 (ChannelCode)
 * 
 * <p>本类对应数据库中的 `channel_codes` 表，定义了系统中支持的所有预订来源（如：携程、美团、官网、微信小程序等）。</p>
 * 
 * <p>核心特性：</p>
 * <ul>
 *     <li>**租户隔离**：通过 `tenantId` 区分不同集团/租户自定义的渠道体系。</li>
 *     <li>**多级架构**：支持通过 `parentId` 和 `level` 定义树状渠道结构（如：OTA -> 携程 -> 携程商旅）。</li>
 *     <li>**库存关联**：在库存管理中，可针对特定渠道进行独立的库存分配。</li>
 * </ul>
 */



@Entity
@Table(name = "channel_codes")
public class ChannelCode {
    
    /** 渠道内部主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 所属租户 ID */
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    /** 渠道唯一编码 (如 'CTrip', 'Meituan') */
    @Column(name = "code", nullable = false, length = 50)
    private String code;
    
    /** 渠道显示名称 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /** 渠道业务描述 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /** 父级渠道 ID（用于多级渠道体系） */
    @Column(name = "parent_id")
    private Integer parentId;
    
    /** 父级渠道编码冗余 */
    @Column(name = "parent_code", length = 50)
    private String parentCode;
    
    /** 渠道层级：1-一级分类，2-二级渠道... */
    @Column(name = "level", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer level = 1;
    
    /** 渠道状态：active-启用中，inactive-已停用 */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;
    
    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    /** 最后更新时间 */
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    /**
     * 渠道状态枚举
     */
    public enum Status {
        /** 启用 */
        active, 
        /** 停用 */
        inactive
    }
    
    /**
     * JPA 更新前自动刷新更新时间。
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    /** 是否为默认渠道 (0: 否, 1: 是) */
    @Column(name = "is_default")
    private Integer isDefault = 0;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }
    public String getParentCode() { return parentCode; }
    public void setParentCode(String parentCode) { this.parentCode = parentCode; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public Integer getIsDefault() { return isDefault; }
    public void setIsDefault(Integer isDefault) { this.isDefault = isDefault; }
}

