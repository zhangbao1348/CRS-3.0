package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 基础价格实体类
 * 对应数据库base_prices表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "base_prices")
public class BasePrice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;
    
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;
    
    @Column(name = "rate_type_id", nullable = false)
    private Integer rateTypeId;
    
    @Column(name = "rate_type_code", length = 50)
    private String rateTypeCode;

    @Column(name = "room_type_id", nullable = false)
    private Integer roomTypeId;
    
    @Column(name = "room_type_code", length = 50)
    private String roomTypeCode;
    
    @Column(name = "base_price", nullable = false)
    private Double basePrice;
    
    @Column(name = "price", nullable = false)
    private Double price;
    
    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;
    
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
    @JoinColumn(name = "rate_type_id", insertable = false, updatable = false)
    private RateType rateType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", insertable = false, updatable = false)
    private RoomType roomType;
    
    // 状态枚举
    public enum Status {
        active, inactive
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}
