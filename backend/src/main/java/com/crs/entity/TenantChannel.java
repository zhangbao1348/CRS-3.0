package com.crs.entity;





import jakarta.persistence.*;
import java.util.Date;

/**
 * 租户可对接渠道实体类
 * 对应数据库tenant_channels表
 */



@Entity
@Table(name = "tenant_channels")
public class TenantChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)

    private Integer tenantId;

    @Column(name = "channel_name", nullable = false, length = 100)
    private String channelName;

    @Column(name = "channel_code", nullable = false, length = 50)
    private String channelCode;

    @Column(name = "connected", nullable = false)
    private Boolean connected = false;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "switch_channel", length = 50)
    private String switchChannel;

    @Column(name = "access_key", length = 200)
    private String accessKey;

    @Column(name = "access_secret", length = 500)
    private String accessSecret;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "active";

    @Column(name = "price_rounding", length = 20)
    private String priceRounding = "keep";

    @Column(name = "prepaid_commission_type", length = 20)
    private String prepaidCommissionType = "percentage";

    @Column(name = "prepaid_commission_value", precision = 10, scale = 2)
    private java.math.BigDecimal prepaidCommissionValue;

    @Column(name = "postpaid_commission_type", length = 20)
    private String postpaidCommissionType = "percentage";

    @Column(name = "postpaid_commission_value", precision = 10, scale = 2)
    private java.math.BigDecimal postpaidCommissionValue;

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
    public Boolean getConnected() { return connected; }
    public void setConnected(Boolean connected) { this.connected = connected; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getSwitchChannel() { return switchChannel; }
    public void setSwitchChannel(String switchChannel) { this.switchChannel = switchChannel; }
    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }
    public String getAccessSecret() { return accessSecret; }
    public void setAccessSecret(String accessSecret) { this.accessSecret = accessSecret; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriceRounding() { return priceRounding; }
    public void setPriceRounding(String priceRounding) { this.priceRounding = priceRounding; }
    public String getPrepaidCommissionType() { return prepaidCommissionType; }
    public void setPrepaidCommissionType(String prepaidCommissionType) { this.prepaidCommissionType = prepaidCommissionType; }
    public java.math.BigDecimal getPrepaidCommissionValue() { return prepaidCommissionValue; }
    public void setPrepaidCommissionValue(java.math.BigDecimal prepaidCommissionValue) { this.prepaidCommissionValue = prepaidCommissionValue; }
    public String getPostpaidCommissionType() { return postpaidCommissionType; }
    public void setPostpaidCommissionType(String postpaidCommissionType) { this.postpaidCommissionType = postpaidCommissionType; }
    public java.math.BigDecimal getPostpaidCommissionValue() { return postpaidCommissionValue; }
    public void setPostpaidCommissionValue(java.math.BigDecimal postpaidCommissionValue) { this.postpaidCommissionValue = postpaidCommissionValue; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
