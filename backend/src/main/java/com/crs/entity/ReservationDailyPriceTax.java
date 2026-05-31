package com.crs.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单每日价格税费细表实体类
 */
@Entity
@Table(name = "reservation_daily_price_taxes")
public class ReservationDailyPriceTax {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reservation_daily_price_id", nullable = false)
    private Integer reservationDailyPriceId;

    @Column(name = "tax_code", nullable = false, length = 50)
    private String taxCode;

    @Column(name = "tax_name", nullable = false, length = 100)
    private String taxName;

    @Column(name = "rate_amount", nullable = false, precision = 10, scale = 4)
    private BigDecimal rateAmount;

    @Column(name = "calculated_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal calculatedAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReservationDailyPriceId() {
        return reservationDailyPriceId;
    }

    public void setReservationDailyPriceId(Integer reservationDailyPriceId) {
        this.reservationDailyPriceId = reservationDailyPriceId;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public BigDecimal getRateAmount() {
        return rateAmount;
    }

    public void setRateAmount(BigDecimal rateAmount) {
        this.rateAmount = rateAmount;
    }

    public BigDecimal getCalculatedAmount() {
        return calculatedAmount;
    }

    public void setCalculatedAmount(BigDecimal calculatedAmount) {
        this.calculatedAmount = calculatedAmount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
