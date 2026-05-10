package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 租户可对接渠道实体类
 * 对应数据库tenant_channels表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
