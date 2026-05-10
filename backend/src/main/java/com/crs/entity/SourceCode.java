package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 来源码定义实体类 (SourceCode)
 * 
 * <p>本类对应数据库中的 `source_codes` 表，用于定义订单的预订来源（如：前台步行、电话预订、微信小程序、携程直连等）。</p>
 * 
 * <p>业务定义：</p>
 * <ul>
 *     <li>**渠道细分**：虽然 `ChannelCode` 定义了物理渠道，但 `SourceCode` 可以更细致地追踪订单产生的具体途径。</li>
 *     <li>**多级追踪**：支持通过 `parentId` 定义树状来源（如：电子渠道 -> 移动端 -> 安卓 App）。</li>
 *     <li>**租户隔离**：各租户可根据自身运营需求自定义不同的订单来源统计维度。</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "source_codes")
public class SourceCode {
    
    /** 来源码内部主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 所属租户 ID */
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    /** 来源唯一编码 (如 'WALK', 'PHONE', 'APP') */
    @Column(name = "code", nullable = false, length = 50)
    private String code;
    
    /** 来源显示名称 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /** 业务描述 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /** 父级来源 ID */
    @Column(name = "parent_id")
    private Integer parentId;
    
    /** 父级来源编码冗余 */
    @Column(name = "parent_code", length = 50)
    private String parentCode;
    
    /** 来源层级：1-大类，2-细分子类 */
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
     * 来源码状态枚举
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