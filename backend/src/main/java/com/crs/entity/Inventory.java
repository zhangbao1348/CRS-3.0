package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;

/**
 * 库存实时管理实体类 (Inventory)
 */



@Entity
@Table(name = "inventory")
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    @Column(name = "hotel_code", length = 50, nullable = false)
    private String hotelCode;
    
    @Column(name = "rate_plan_code", length = 50)
    private String ratePlanCode;
    
    @Column(name = "room_type_code", length = 50)
    private String roomTypeCode;
    
    @Column(name = "channel_code", length = 50)
    private String channelCode;
    
    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;
    
    @Column(name = "available_rooms", nullable = false)
    private Integer availableRooms = 0;
    
    @Column(name = "allocated_rooms", nullable = false)
    private Integer allocatedRooms = 0;
    
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_code", referencedColumnName = "hotel_code", insertable = false, updatable = false)
    private Hotel hotel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_code", referencedColumnName = "rate_code", insertable = false, updatable = false)
    private RatePlan ratePlan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_code", referencedColumnName = "code", insertable = false, updatable = false)
    private RoomType roomType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_code", referencedColumnName = "code", insertable = false, updatable = false)
    private ChannelCode channel;
    
    public enum Status {
        active, inactive
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public String getHotelCode() { return hotelCode; }
    public void setHotelCode(String hotelCode) { this.hotelCode = hotelCode; }
    public String getRatePlanCode() { return ratePlanCode; }
    public void setRatePlanCode(String ratePlanCode) { this.ratePlanCode = ratePlanCode; }
    public String getRoomTypeCode() { return roomTypeCode; }
    public void setRoomTypeCode(String roomTypeCode) { this.roomTypeCode = roomTypeCode; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public Integer getAvailableRooms() { return availableRooms; }
    public void setAvailableRooms(Integer availableRooms) { this.availableRooms = availableRooms; }
    public Integer getAllocatedRooms() { return allocatedRooms; }
    public void setAllocatedRooms(Integer allocatedRooms) { this.allocatedRooms = allocatedRooms; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
