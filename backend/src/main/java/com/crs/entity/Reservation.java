package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 预订实体类
 * 对应数据库reservation表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation")
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "reservation_code", nullable = false, unique = true, length = 50)
    private String reservationCode;
    
    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;
    
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;
    
    @Column(name = "rate_plan_id", nullable = false)
    private Integer ratePlanId;
    
    @Column(name = "rate_plan_code", length = 50)
    private String ratePlanCode;
    
    @Column(name = "room_type_id", nullable = false)
    private Integer roomTypeId;
    
    @Column(name = "room_type_code", length = 50)
    private String roomTypeCode;
    
    @Column(name = "channel_id", nullable = false)
    private Integer channelId;
    
    @Column(name = "channel_code", length = 50)
    private String channelCode;
    
    @Column(name = "market_code_id")
    private Integer marketCodeId;
    
    @Column(name = "market_code", length = 50)
    private String marketCode;
    
    @Column(name = "source_code_id")
    private Integer sourceCodeId;
    
    @Column(name = "source_code", length = 50)
    private String sourceCode;
    
    @Column(name = "guest_name", nullable = false, length = 100)
    private String guestName;
    
    @Column(name = "guest_email", length = 100)
    private String guestEmail;
    
    @Column(name = "guest_phone", nullable = false, length = 50)
    private String guestPhone;
    
    @Column(name = "check_in_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date checkInDate;
    
    @Column(name = "check_out_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date checkOutDate;
    
    @Column(name = "adult_count", nullable = false)
    private Integer adultCount = 1;
    
    @Column(name = "child_count")
    private Integer childCount = 0;
    
    @Column(name = "room_count", nullable = false)
    private Integer roomCount = 1;
    
    @Column(name = "total_price", nullable = false)
    private Double totalPrice = 0.0;
    
    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "CNY";
    
    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus = "unpaid";
    
    @Column(name = "reservation_status", nullable = false, length = 20)
    private String reservationStatus = "confirmed";
    
    @Column(name = "guarantee_type", length = 50)
    private String guaranteeType;
    
    @Column(name = "credit_card_info", length = 255)
    private String creditCardInfo;
    
    @Column(name = "special_request", length = 500)
    private String specialRequest;
    
    @Column(name = "notes", length = 500)
    private String notes;
    
    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;
    
    @Column(name = "modified_by", length = 50)
    private String modifiedBy;
    
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
    @JoinColumn(name = "rate_plan_id", insertable = false, updatable = false)
    private RatePlan ratePlan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", insertable = false, updatable = false)
    private RoomType roomType;
    
    // 状态枚举
    public enum Status {
        active, cancelled, completed
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}
