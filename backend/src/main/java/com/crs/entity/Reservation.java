package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id")
    private Integer tenantId;

    @Column(name = "reservation_code", nullable = false, unique = true, length = 50)
    private String reservationCode;

    @Column(name = "channel_order_number", length = 100)
    private String channelOrderNumber;

    @Column(name = "pms_number", length = 100)
    private String pmsNumber;

    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;

    @Column(name = "hotel_code", length = 50)
    private String hotelCode;

    @Column(name = "hotel_name", length = 200)
    private String hotelName;

    @Column(name = "room_type_id", nullable = false)
    private Integer roomTypeId;

    @Column(name = "room_type_code", length = 50)
    private String roomTypeCode;

    @Column(name = "room_type_name", length = 200)
    private String roomTypeName;

    @Column(name = "rate_plan_id", nullable = false)
    private Integer ratePlanId;

    @Column(name = "rate_plan_code", length = 50)
    private String ratePlanCode;

    @Column(name = "rate_plan_name", length = 200)
    private String ratePlanName;

    @Column(name = "channel_id", nullable = false)
    private Integer channelId;

    @Column(name = "channel_code", length = 50)
    private String channelCode;

    @Column(name = "channel_name", length = 100)
    private String channelName;

    @Column(name = "market_code_id")
    private Integer marketCodeId;

    @Column(name = "market_code", length = 50)
    private String marketCode;

    @Column(name = "source_code_id")
    private Integer sourceCodeId;

    @Column(name = "source_code", length = 50)
    private String sourceCode;

    @Column(name = "check_in_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date checkInDate;

    @Column(name = "check_out_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date checkOutDate;

    @Column(name = "nights")
    private Integer nights;

    @Column(name = "room_count", nullable = false)
    private Integer roomCount = 1;

    @Column(name = "adult_count", nullable = false)
    private Integer adultCount = 1;

    @Column(name = "child_count")
    private Integer childCount = 0;

    @Column(name = "contact_name", length = 100)
    private String contactName;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "member_no", length = 50)
    private String memberNo;

    @Column(name = "member_level", length = 30)
    private String memberLevel;

    @Column(name = "original_price", precision = 12, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "CNY";

    @Column(name = "guarantee_type", length = 50)
    private String guaranteeType;

    @Column(name = "guarantee_info", columnDefinition = "TEXT")
    private String guaranteeInfo;

    @Column(name = "cancellation_policy_code", length = 50)
    private String cancellationPolicyCode;

    @Column(name = "cancellation_policy_desc", length = 500)
    private String cancellationPolicyDesc;

    @Column(name = "guarantee_policy_code", length = 50)
    private String guaranteePolicyCode;

    @Column(name = "guarantee_policy_desc", length = 500)
    private String guaranteePolicyDesc;

    @Column(name = "special_request", length = 500)
    private String specialRequest;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "guest_remark", length = 500)
    private String guestRemark;

    @Column(name = "hotel_remark", length = 500)
    private String hotelRemark;

    @Column(name = "is_manual")
    private Boolean isManual = false;

    @Column(name = "manual_reason", length = 500)
    private String manualReason;

    @Column(name = "commission_rate", precision = 5, scale = 4)
    private BigDecimal commissionRate;

    @Column(name = "commission_amount", precision = 12, scale = 2)
    private BigDecimal commissionAmount;

    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus = "unpaid";

    @Column(name = "reservation_status", nullable = false, length = 30)
    private String reservationStatus = "confirmed";

    @Column(name = "payment_deadline")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDeadline;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;

    @Column(name = "order_source", length = 30)
    private String orderSource = "channel";

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "cancelled_by", length = 50)
    private String cancelledBy;

    @Column(name = "cancelled_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cancelledAt;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    @Column(name = "completed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    @Deprecated
    @Column(name = "guest_name", length = 100)
    private String guestName;

    @Deprecated
    @Column(name = "guest_email", length = 100)
    private String guestEmail;

    @Deprecated
    @Column(name = "guest_phone", length = 50)
    private String guestPhone;

    @Deprecated
    @Column(name = "credit_card_info", length = 255)
    private String creditCardInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", insertable = false, updatable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_id", insertable = false, updatable = false)
    private RatePlan ratePlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", insertable = false, updatable = false)
    private RoomType roomType;

    public enum Status {
        active, cancelled, completed
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    public String getContactName() {
        if (contactName != null && !contactName.isBlank()) return contactName;
        return guestName;
    }

    public String getContactPhone() {
        if (contactPhone != null && !contactPhone.isBlank()) return contactPhone;
        return guestPhone;
    }

    public String getContactEmail() {
        if (contactEmail != null && !contactEmail.isBlank()) return contactEmail;
        return guestEmail;
    }
}
