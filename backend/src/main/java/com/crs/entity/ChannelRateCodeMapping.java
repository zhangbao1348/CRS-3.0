package com.crs.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 渠道房价映射实体类
 * 对应数据库channel_rate_code_mappings表
 */



@Entity
@Table(name = "channel_rate_code_mappings")
public class ChannelRateCodeMapping {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "tenant_id")
    private Integer tenantId;

    @Column(name = "channel_id", nullable = false)
    private Integer channelId;

    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;

    @Column(name = "rate_code_id", nullable = false)
    private Integer rateCodeId;
    
    @Column(name = "channel_code", length = 50)
    private String channelCode;
    
    @Column(name = "channel_name", length = 50)
    private String channelName;
    
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;
    
    @Column(name = "hotel_name", length = 100)
    private String hotelName;
    
    @Column(name = "rate_code_name", length = 100)
    private String rateCodeName;
    
    @Column(name = "rate_code", length = 50)
    private String rateCode;
    
    @Column(name = "channel_rate_code", nullable = false, length = 100)
    private String channelRateCode;
    
    @Column(name = "channel_rate_name", length = 100)
    private String channelRateName;
    
    @Column(name = "markup", precision = 5, scale = 2)
    private BigDecimal markup = BigDecimal.ZERO;
    
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
    public Integer getHotelId() { return hotelId; }
    public void setHotelId(Integer hotelId) { this.hotelId = hotelId; }
    public Integer getRateCodeId() { return rateCodeId; }
    public void setRateCodeId(Integer rateCodeId) { this.rateCodeId = rateCodeId; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }
    public String getHotelCode() { return hotelCode; }
    public void setHotelCode(String hotelCode) { this.hotelCode = hotelCode; }
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public String getRateCodeName() { return rateCodeName; }
    public void setRateCodeName(String rateCodeName) { this.rateCodeName = rateCodeName; }
    public String getRateCode() { return rateCode; }
    public void setRateCode(String rateCode) { this.rateCode = rateCode; }
    public String getChannelRateCode() { return channelRateCode; }
    public void setChannelRateCode(String channelRateCode) { this.channelRateCode = channelRateCode; }
    public String getChannelRateName() { return channelRateName; }
    public void setChannelRateName(String channelRateName) { this.channelRateName = channelRateName; }
    public BigDecimal getMarkup() { return markup; }
    public void setMarkup(BigDecimal markup) { this.markup = markup; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
