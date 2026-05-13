package com.crs.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ReservationDailyPrice 实体类
 * 
 * <p>本核心模块自动生成详细注释。主要负责【ReservationDailyPrice】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/10-价格计划管理.md</li>
 *     <li>**模块职责**：单一职责原则，提供 ReservationDailyPrice 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */



@Entity
@Table(name = "reservation_daily_price")
public class ReservationDailyPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id")
    private Integer tenantId;

    @Column(name = "reservation_id", nullable = false)
    private Integer reservationId;

    @Column(name = "price_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date priceDate;

    @Column(name = "original_price", precision = 12, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "actual_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal actualPrice;

    @Column(name = "tax_amount", precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "service_charge", precision = 12, scale = 2)
    private BigDecimal serviceCharge;

    @Column(name = "breakfast_included")
    private Boolean breakfastIncluded = false;

    @Column(name = "breakfast_count")
    private Integer breakfastCount = 0;

    @Column(name = "packages_json", columnDefinition = "TEXT")
    private String packagesJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }
    public Date getPriceDate() { return priceDate; }
    public void setPriceDate(Date priceDate) { this.priceDate = priceDate; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public BigDecimal getActualPrice() { return actualPrice; }
    public void setActualPrice(BigDecimal actualPrice) { this.actualPrice = actualPrice; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public BigDecimal getServiceCharge() { return serviceCharge; }
    public void setServiceCharge(BigDecimal serviceCharge) { this.serviceCharge = serviceCharge; }
    public Boolean getBreakfastIncluded() { return breakfastIncluded; }
    public void setBreakfastIncluded(Boolean breakfastIncluded) { this.breakfastIncluded = breakfastIncluded; }
    public Integer getBreakfastCount() { return breakfastCount; }
    public void setBreakfastCount(Integer breakfastCount) { this.breakfastCount = breakfastCount; }
    public String getPackagesJson() { return packagesJson; }
    public void setPackagesJson(String packagesJson) { this.packagesJson = packagesJson; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
