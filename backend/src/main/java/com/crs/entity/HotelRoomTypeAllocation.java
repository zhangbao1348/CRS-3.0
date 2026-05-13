package com.crs.entity;


import jakarta.persistence.*;

/**
 * 酒店房型分配实体类 (HotelRoomTypeAllocation)
 * 已根据规范移除物理 ID 关联。
 */



@Entity
@Table(name = "hotel_room_type_allocations")
public class HotelRoomTypeAllocation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    @Column(name = "hotel_code", length = 50, nullable = false)
    private String hotelCode;
    
    @Column(name = "room_type_code", length = 50, nullable = false)
    private String roomTypeCode;
    
    @Column(name = "allocated", nullable = false)
    private Boolean allocated = false;
    
    @Column(name = "room_info_editable", nullable = false)
    private Boolean roomInfoEditable = false;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_code", referencedColumnName = "hotel_code", insertable = false, updatable = false)
    private Hotel hotel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_code", referencedColumnName = "code", insertable = false, updatable = false)
    private RoomType roomType;

    // 手动补全 Getter/Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public String getHotelCode() { return hotelCode; }
    public void setHotelCode(String hotelCode) { this.hotelCode = hotelCode; }
    public String getRoomTypeCode() { return roomTypeCode; }
    public void setRoomTypeCode(String roomTypeCode) { this.roomTypeCode = roomTypeCode; }
    public Boolean getAllocated() { return allocated; }
    public void setAllocated(Boolean allocated) { this.allocated = allocated; }
    public Boolean getRoomInfoEditable() { return roomInfoEditable; }
    public void setRoomInfoEditable(Boolean roomInfoEditable) { this.roomInfoEditable = roomInfoEditable; }

    /** @deprecated 仅用于兼容旧 ID 代码 */
    @Deprecated
    public Integer getHotelId() { return null; }
    /** @deprecated 仅用于兼容旧 ID 代码 */
    @Deprecated
    public void setHotelId(Integer hotelId) { }
    /** @deprecated 仅用于兼容旧 ID 代码 */
    @Deprecated
    public Integer getRoomTypeId() { return null; }
    /** @deprecated 仅用于兼容旧 ID 代码 */
    @Deprecated
    public void setRoomTypeId(Integer roomTypeId) { }
}