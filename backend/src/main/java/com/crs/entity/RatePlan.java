package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 价格计划实体类
 * 对应数据库rate_plans表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rate_plans")
public class RatePlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;
    
    @Column(name = "rate_code", nullable = false, length = 50)
    private String rateCode;
    
    @Column(name = "rate_name", nullable = false, length = 100)
    private String rateName;
    
    @Column(name = "rate_category", length = 50)
    private String rateCategory;
    
    @Column(name = "market_code_id", nullable = false)
    private Integer marketCodeId;
    
    @Column(name = "channel_code_id", nullable = false)
    private Integer channelCodeId;
    
    @Column(name = "source_code_id", nullable = false)
    private Integer sourceCodeId;
    
    @Column(name = "type", nullable = false, length = 20)
    private String type;
    
    @Column(name = "parent_rate_code", length = 50)
    private String parentRateCode;
    
    @Column(name = "discount")
    private Double discount;
    
    @Column(name = "rounding", length = 20)
    private String rounding;
    
    @Column(name = "room_type_diff_id")
    private Integer roomTypeDiffId;
    
    @Column(name = "person_diff_id")
    private Integer personDiffId;
    
    @Column(name = "guarantee_policy_id", nullable = false)
    private Integer guaranteePolicyId;
    
    @Column(name = "cancellation_policy_id", nullable = false)
    private Integer cancellationPolicyId;
    
    @Column(name = "coupon_rule", nullable = false, length = 20)
    private String couponRule;
    
    @Column(name = "promotion_rule", nullable = false, length = 20)
    private String promotionRule;
    
    @Column(name = "allow_points")
    private Boolean allowPoints = false;
    
    @Column(name = "min_advance_booking")
    private Integer minAdvanceBooking;
    
    @Column(name = "max_advance_booking")
    private Integer maxAdvanceBooking;
    
    @Column(name = "min_stay_nights")
    private Integer minStayNights;
    
    @Column(name = "max_stay_nights")
    private Integer maxStayNights;
    
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", insertable = false, updatable = false)
    private Hotel hotel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_code_id", insertable = false, updatable = false)
    private MarketCode marketCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_code_id", insertable = false, updatable = false)
    private ChannelCode channelCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_code_id", insertable = false, updatable = false)
    private SourceCode sourceCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_diff_id", insertable = false, updatable = false)
    private RoomTypeDiffSystem roomTypeDiffSystem;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_diff_id", insertable = false, updatable = false)
    private PersonDiffSystem personDiffSystem;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guarantee_policy_id", insertable = false, updatable = false)
    private GroupGuaranteePolicy guaranteePolicy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancellation_policy_id", insertable = false, updatable = false)
    private GroupCancellationPolicy cancellationPolicy;
    
    // 状态枚举
    public enum Status {
        active, inactive
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}
