package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room_status", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "hotel_code", "dimension_type", "dimension_code", "status_date"})
})
/**
 * RoomStatusRecord 实体类
 * 
 * <p>本核心模块自动生成详细注释。主要负责【RoomStatusRecord】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：单一职责原则，提供 RoomStatusRecord 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public class RoomStatusRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "hotel_code", nullable = false, length = 50)
    private String hotelCode;

    @Column(name = "dimension_type", nullable = false, length = 30)
    private String dimensionType;

    @Column(name = "dimension_code", nullable = false, length = 100)
    private String dimensionCode = "";

    @Column(name = "status_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date statusDate;

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}
