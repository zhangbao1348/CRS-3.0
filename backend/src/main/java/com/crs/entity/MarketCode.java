package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 市场码定义实体类 (MarketCode)
 * 
 * <p>本类对应数据库中的 `market_codes` 表，用于定义酒店业务的市场细分分类（如：散客、协议、中介、集团等）。</p>
 * 
 * <p>业务价值：</p>
 * <ul>
 *     <li>**财务统计**：作为收入分析的核心维度，帮助酒店了解不同客户群体的贡献度。</li>
 *     <li>**多级分类**：支持通过 `parentId` 构建市场细分树（如：协议客户 -> 500强企业 -> 华为）。</li>
 *     <li>**定价参考**：价格计划 (RatePlan) 通常会关联特定的市场码，以实现差异化定价。</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "market_codes")
public class MarketCode {
    
    /** 市场码内部主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 所属租户 ID */
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    /** 市场码唯一编码 (如 'FIT', 'CORP', 'GRP') */
    @Column(name = "code", nullable = false, length = 50)
    private String code;
    
    /** 市场码显示名称 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /** 业务描述 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /** 父级市场码 ID */
    @Column(name = "parent_id")
    private Integer parentId;
    
    /** 父级市场码编码冗余 */
    @Column(name = "parent_code", length = 50)
    private String parentCode;
    
    /** 分类层级：1-大类，2-细分子类 */
    @Column(name = "level", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer level = 1;
    
    /** 状态：active-启用，inactive-停用 */
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
     * 市场码状态枚举
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
}

