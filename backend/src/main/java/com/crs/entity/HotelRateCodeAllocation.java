package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;

/**
 * 酒店房价码分配实体类
 * 对应数据库hotel_rate_code_allocations表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotel_rate_code_allocations")
public class HotelRateCodeAllocation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;
    
    @Column(name = "hotel_code", nullable = false, length = 50)
    private String hotelCode;
    
    @Column(name = "rate_code", nullable = false, length = 50)
    private String rateCode;
    
    @Column(name = "allocated", nullable = false)
    private Boolean allocated = false;
    
    @Column(name = "basic_info_editable", nullable = false)
    private Boolean basicInfoEditable = false;
    
    @Column(name = "price_info_editable", nullable = false)
    private Boolean priceInfoEditable = false;
    
    @Column(name = "booking_limit_editable", nullable = false)
    private Boolean bookingLimitEditable = false;
    
    @Column(name = "guarantee_rule_editable", nullable = false)
    private Boolean guaranteeRuleEditable = false;
    
    @Column(name = "promotion_editable", nullable = false)
    private Boolean promotionEditable = false;
}