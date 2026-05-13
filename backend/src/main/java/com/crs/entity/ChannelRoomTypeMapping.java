package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;

/**
 * 渠道房型映射实体类
 * 对应数据库channel_room_type_mappings表
 */



@Entity
@Table(name = "channel_room_type_mappings")
public class ChannelRoomTypeMapping {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    @Column(name = "channel_code", length = 50)
    private String channelCode;
    
    @Column(name = "channel_name", length = 50)
    private String channelName;
    
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;
    
    @Column(name = "hotel_name", length = 100)
    private String hotelName;
    
    @Column(name = "room_type_name", length = 100)
    private String roomTypeName;
    
    @Column(name = "room_type_code", length = 50)
    private String roomTypeCode;
    
    @Column(name = "channel_room_type_code", nullable = false, length = 100)
    private String channelRoomTypeCode;
    
    @Column(name = "channel_room_type_name", length = 100)
    private String channelRoomTypeName;
    
    @Column(name = "status", length = 20)
    private String status = "active";
    
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

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }
    public String getHotelCode() { return hotelCode; }
    public void setHotelCode(String hotelCode) { this.hotelCode = hotelCode; }
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public String getRoomTypeName() { return roomTypeName; }
    public void setRoomTypeName(String roomTypeName) { this.roomTypeName = roomTypeName; }
    public String getRoomTypeCode() { return roomTypeCode; }
    public void setRoomTypeCode(String roomTypeCode) { this.roomTypeCode = roomTypeCode; }
    public String getChannelRoomTypeCode() { return channelRoomTypeCode; }
    public void setChannelRoomTypeCode(String channelRoomTypeCode) { this.channelRoomTypeCode = channelRoomTypeCode; }
    public String getChannelRoomTypeName() { return channelRoomTypeName; }
    public void setChannelRoomTypeName(String channelRoomTypeName) { this.channelRoomTypeName = channelRoomTypeName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
