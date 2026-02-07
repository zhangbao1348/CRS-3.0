package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;

/**
 * 酒店设施实体类
 * 对应数据库hotel_facilities表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotel_facilities")
public class HotelFacility {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;
    
    @Column(name = "facility_type", nullable = false, length = 50)
    private String facilityType;
    
    @Column(name = "facility_name", nullable = false, length = 100)
    private String facilityName;
    
    @Column(name = "facility_code", nullable = false, length = 50)
    private String facilityCode;
    
    @Column(name = "available", nullable = false)
    private Boolean available = true;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", insertable = false, updatable = false)
    private Hotel hotel;
}