package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;

/**
 * 渠道酒店映射实体类
 * 对应数据库channel_hotel_mappings表
 */



@Entity
@Table(name = "channel_hotel_mappings")
public class ChannelHotelMapping {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "tenant_id")
    private Integer tenantId;

    @Column(name = "channel_id")
    private Integer channelId;
    
    @Column(name = "channel_code", length = 50)
    private String channelCode;
    
    @Column(name = "channel_name", length = 50)
    private String channelName;

    @Column(name = "hotel_id")
    private Integer hotelId;
    
    @Column(name = "hotel_name", length = 100)
    private String hotelName;
    
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;
    
    @Column(name = "channel_hotel_code", nullable = false, length = 100)
    private String channelHotelCode;
    
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
    public Integer getChannelId() { return channelId; }
    public void setChannelId(Integer channelId) { this.channelId = channelId; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }
    public Integer getHotelId() { return hotelId; }
    public void setHotelId(Integer hotelId) { this.hotelId = hotelId; }
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public String getHotelCode() { return hotelCode; }
    public void setHotelCode(String hotelCode) { this.hotelCode = hotelCode; }
    public String getChannelHotelCode() { return channelHotelCode; }
    public void setChannelHotelCode(String channelHotelCode) { this.channelHotelCode = channelHotelCode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
