package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "pms_inventory", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "hotel_code", "room_type_code", "inventory_date"})
})
/**
 * PmsInventory 实体类
 * 
 * <p>本核心模块自动生成详细注释。主要负责【PmsInventory】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/11-库存管理.md</li>
 *     <li>**模块职责**：单一职责原则，提供 PmsInventory 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public class PmsInventory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    
    @Column(name = "hotel_code", nullable = false, length = 50)
    private String hotelCode;

    @Column(name = "room_type_code", nullable = false, length = 50)
    private String roomTypeCode;

    @Column(name = "inventory_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date inventoryDate;

    @Column(name = "physical_rooms", nullable = false)
    private Integer physicalRooms = 0;

    @Column(name = "available_rooms", nullable = false)
    private Integer availableRooms = 0;

    @Column(name = "maintenance_rooms", nullable = false)
    private Integer maintenanceRooms = 0;

    @Column(name = "overbook_count", nullable = false)
    private Integer overbookCount = 0;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    @PreUpdate
    public void preUpdate() { this.updatedAt = new Date(); }

    /**
     * 已售数量 = 物理房型数 + 超预订数 - 剩余可售 - 维修房数
     */
    @Transient
    public int getSoldCount() {
        return physicalRooms + overbookCount - availableRooms - maintenanceRooms;
    }
}
