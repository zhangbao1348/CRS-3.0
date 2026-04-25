package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 渠道房价映射实体类
 * 对应数据库channel_rate_code_mappings表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "channel_rate_code_mappings")
public class ChannelRateCodeMapping {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "channel_id", nullable = false)
    private Integer channelId;
    
    @Column(name = "channel_code", length = 50)
    private String channelCode;
    
    @Column(name = "channel_name", length = 50)
    private String channelName;
    
    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;
    
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;
    
    @Column(name = "hotel_name", length = 100)
    private String hotelName;
    
    @Column(name = "rate_code_id", nullable = false)
    private Integer rateCodeId;
    
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
}
