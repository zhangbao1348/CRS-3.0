package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rate_plans")
public class RatePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id")
    private Integer tenantId;

    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;

    @Column(name = "hotel_code", length = 50)
    private String hotelCode;

    @Column(name = "source_group_rate_code", length = 50)
    private String sourceGroupRateCode;

    @Column(name = "rate_code", nullable = false, length = 50)
    private String rateCode;

    @Column(name = "rate_name", nullable = false, length = 100)
    private String rateName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "rate_category", length = 50)
    private String rateCategory;

    @Column(name = "market_code", length = 50)
    private String marketCode;

    @Column(name = "source_code", length = 50)
    private String sourceCode;

    @Column(name = "rate_type", length = 20)
    private String rateType = "basic";

    @Column(name = "parent_rate_code", length = 50)
    private String parentRateCode;

    @Column(name = "derivative_level", length = 20)
    private String derivativeLevel = "basic";

    @Column(name = "discount")
    private Double discount;

    @Column(name = "rounding", length = 20)
    private String rounding;

    @Column(name = "guarantee_rule", length = 50)
    private String guaranteeRule;

    @Column(name = "cancellation_rule", length = 50)
    private String cancellationRule;

    @Column(name = "coupon_rule", length = 20)
    private String couponRule = "unlimited";

    @Column(name = "promotion_rule", length = 20)
    private String promotionRule = "unlimited";

    @Column(name = "allow_points")
    private Boolean allowPoints = false;

    @Column(name = "points_type", length = 20)
    private String pointsType;

    @Column(name = "points_value")
    private Double pointsValue;

    @Column(name = "applicable_room_types", columnDefinition = "JSON")
    private String applicableRoomTypes;

    @Column(name = "packages", columnDefinition = "JSON")
    private String packages;

    @Column(name = "personal_membership", columnDefinition = "JSON")
    private String personalMembership;

    @Column(name = "company_membership", columnDefinition = "JSON")
    private String companyMembership;

    @Column(name = "advance_booking_min")
    private Integer advanceBookingMin;

    @Column(name = "advance_booking_max")
    private Integer advanceBookingMax;

    @Column(name = "minimum_stay_min")
    private Integer minimumStayMin;

    @Column(name = "minimum_stay_max")
    private Integer minimumStayMax;

    @Column(name = "booking_start_time", length = 10)
    private String bookingStartTime;

    @Column(name = "booking_end_time", length = 10)
    private String bookingEndTime;

    @Column(name = "checkin_start_time", length = 10)
    private String checkinStartTime;

    @Column(name = "checkin_end_time", length = 10)
    private String checkinEndTime;

    @Column(name = "room_type_diff_code", length = 50)
    private String roomTypeDiffCode;

    @Column(name = "person_diff_code", length = 50)
    private String personDiffCode;

    @Column(name = "status", nullable = false, length = 20)
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
