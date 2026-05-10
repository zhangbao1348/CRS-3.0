package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "inventory_quota", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "hotel_code", "dimension_type", "dimension_code", "quota_date"})
})
/**
 * InventoryQuota 实体类
 * 
 * <p>本核心模块自动生成详细注释。主要负责【InventoryQuota】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/11-库存管理.md</li>
 *     <li>**模块职责**：单一职责原则，提供 InventoryQuota 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public class InventoryQuota {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @Column(name = "tenant_id", nullable = false) private Integer tenantId;
    
    @Column(name = "hotel_code", nullable = false, length = 50) private String hotelCode;
    @Column(name = "dimension_type", nullable = false, length = 30) private String dimensionType;
    @Column(name = "dimension_code", nullable = false, length = 100) private String dimensionCode = "";
    @Column(name = "quota_date", nullable = false) @Temporal(TemporalType.DATE) private Date quotaDate;
    @Column(name = "quota_limit") private Integer quotaLimit; // NULL = 未设置
    @Column(name = "sold_count", nullable = false) private Integer soldCount = 0;
    @Version @Column(name = "version", nullable = false) private Integer version = 0;
    @Column(name = "created_at", nullable = false, updatable = false) @Temporal(TemporalType.TIMESTAMP) private Date createdAt = new Date();
    @Column(name = "updated_at", nullable = false) @Temporal(TemporalType.TIMESTAMP) private Date updatedAt = new Date();
    @PreUpdate public void preUpdate() { this.updatedAt = new Date(); }
}
