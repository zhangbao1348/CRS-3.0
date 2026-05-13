package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;

/**
 * 渠道发布记录实体类
 */



@Entity
@Table(name = "channel_publish_records")
public class ChannelPublishRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    
    @Column(name = "hotel_code", nullable = false, length = 50)
    private String hotelCode;

    @Column(name = "channel_code", nullable = false, length = 50)
    private String channelCode;

    @Column(name = "rate_code", nullable = false, length = 50)
    private String rateCode;

    @Column(name = "room_type_code", nullable = false, length = 50)
    private String roomTypeCode;

    @Column(name = "status", length = 20)
    private String status = "published";

    @Column(name = "published_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishedAt;

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

    public String getHotelCode() { return hotelCode; }
    public void setHotelCode(String hotelCode) { this.hotelCode = hotelCode; }

    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }

    public String getRateCode() { return rateCode; }
    public void setRateCode(String rateCode) { this.rateCode = rateCode; }

    public String getRoomTypeCode() { return roomTypeCode; }
    public void setRoomTypeCode(String roomTypeCode) { this.roomTypeCode = roomTypeCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Date publishedAt) { this.publishedAt = publishedAt; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
