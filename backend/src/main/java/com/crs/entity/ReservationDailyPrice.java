package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation_daily_price")
public class ReservationDailyPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
}
