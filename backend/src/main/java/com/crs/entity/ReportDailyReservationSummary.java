package com.crs.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * 每日预订聚合汇总表实体类 (ReportDailyReservationSummary)
 */
@Entity
@Table(
    name = "report_daily_reservation_summary",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_daily_summary",
            columnNames = {
                "tenant_id", "report_date", "hotel_code", "channel_code", 
                "market_code", "rate_category_code", "rate_plan_code", "room_type_code",
                "reservation_status"
            }
        )
    },
    indexes = {
        @Index(name = "idx_summary_base", columnList = "tenant_id, report_date, hotel_code"),
        @Index(name = "idx_summary_channel", columnList = "channel_code"),
        @Index(name = "idx_summary_market", columnList = "market_code"),
        @Index(name = "idx_summary_category", columnList = "rate_category_code"),
        @Index(name = "idx_summary_rate_plan", columnList = "rate_plan_code"),
        @Index(name = "idx_summary_status", columnList = "reservation_status")
    }
)
public class ReportDailyReservationSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "hotel_code", nullable = false, length = 50)
    private String hotelCode;

    @Column(name = "channel_code", nullable = false, length = 50)
    private String channelCode;

    @Column(name = "market_code", nullable = false, length = 50)
    private String marketCode;

    @Column(name = "rate_category_code", nullable = false, length = 50)
    private String rateCategoryCode;

    @Column(name = "rate_plan_code", nullable = false, length = 50)
    private String ratePlanCode;

    @Column(name = "room_type_code", nullable = false, length = 50)
    private String roomTypeCode;

    @Column(name = "reservation_status", nullable = false, length = 30)
    private String reservationStatus = "confirmed";

    @Column(name = "order_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer orderCount = 0;

    @Column(name = "room_nights", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer roomNights = 0;

    @Column(name = "total_revenue", nullable = false, precision = 12, scale = 2, columnDefinition = "DECIMAL(12,2) DEFAULT 0.00")
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(name = "points_redeemed", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer pointsRedeemed = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public String getHotelCode() { return hotelCode; }
    public void setHotelCode(String hotelCode) { this.hotelCode = hotelCode; }

    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }

    public String getMarketCode() { return marketCode; }
    public void setMarketCode(String marketCode) { this.marketCode = marketCode; }

    public String getRateCategoryCode() { return rateCategoryCode; }
    public void setRateCategoryCode(String rateCategoryCode) { this.rateCategoryCode = rateCategoryCode; }

    public String getRatePlanCode() { return ratePlanCode; }
    public void setRatePlanCode(String ratePlanCode) { this.ratePlanCode = ratePlanCode; }

    public String getRoomTypeCode() { return roomTypeCode; }
    public void setRoomTypeCode(String roomTypeCode) { this.roomTypeCode = roomTypeCode; }

    public String getReservationStatus() { return reservationStatus; }
    public void setReservationStatus(String reservationStatus) { this.reservationStatus = reservationStatus; }

    public Integer getOrderCount() { return orderCount; }
    public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }

    public Integer getRoomNights() { return roomNights; }
    public void setRoomNights(Integer roomNights) { this.roomNights = roomNights; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public Integer getPointsRedeemed() { return pointsRedeemed; }
    public void setPointsRedeemed(Integer pointsRedeemed) { this.pointsRedeemed = pointsRedeemed; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
