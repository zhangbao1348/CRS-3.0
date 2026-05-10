package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;

/**
 * 酒店房型分配实体类 (HotelRoomTypeAllocation)
 * 
 * <p>本类对应数据库中的 `hotel_room_type_allocations` 表，记录了集团标准房型分配给单店酒店的情况。</p>
 * 
 * <p>核心功能：</p>
 * <ul>
 *     <li>**资源映射**：将集团定义的标准房型（母版）分配给具体酒店实例。</li>
 *     <li>**权限隔离**：通过 `room_info_editable` 控制酒店是否允许修改房型的基础描述或设施信息。</li>
 *     <li>**多租户安全**：包含 `tenantId` 以确保集团层级的数据隔离。</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotel_room_type_allocations")
public class HotelRoomTypeAllocation {
    
    /** 分配记录内部主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 目标酒店 ID */
    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;
    
    /** 所属租户 ID */
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    /** 酒店外部编码冗余 */
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;
    
    /** 被分配的房型 ID */
    @Column(name = "room_type_id", nullable = false)
    private Integer roomTypeId;
    
    /** 房型代码冗余 */
    @Column(name = "room_type_code", length = 50)
    private String roomTypeCode;
    
    /** 是否已完成分配并对酒店可见 */
    @Column(name = "allocated", nullable = false)
    private Boolean allocated = false;
    
    /** 权限开关：是否允许酒店修改该房型的基础信息 */
    @Column(name = "room_info_editable", nullable = false)
    private Boolean roomInfoEditable = false;
    
    // 关联关系 ---------------------------------------------------------
    
    /** 目标酒店实体引用 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", insertable = false, updatable = false)
    private Hotel hotel;
    
    /** 被分配的房型实体引用 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", insertable = false, updatable = false)
    private RoomType roomType;
}