package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 酒店价格实体类
 * 存储酒店每日价格数据，按租户+酒店+房价码+房型+日期维度
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotel_prices", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "hotel_code", "rate_code", "room_type_code", "price_date"})
})
public class HotelPrice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;
    
    @Column(name = "hotel_code", nullable = false, length = 50)
    private String hotelCode;
    
    @Column(name = "rate_code", nullable = false, length = 50)
    private String rateCode;
    
    @Column(name = "room_type_code", nullable = false, length = 50)
    private String roomTypeCode;
    
    @Column(name = "price_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date priceDate;
    
    @Column(name = "price_with_tax", precision = 10, scale = 2)
    private BigDecimal priceWithTax;
    
    @Column(name = "price_without_tax", precision = 10, scale = 2)
    private BigDecimal priceWithoutTax;
    
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
