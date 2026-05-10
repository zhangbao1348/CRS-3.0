package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 基础价格实体类 (BasePrice)
 * 
 * <p>本类对应数据库中的 `base_prices` 表，记录了酒店各房型在特定日期下的原始售卖价格。</p>
 * 
 * <p>业务定义：</p>
 * <ul>
 *     <li>**原始价格 (basePrice)**：指未经任何折扣、税费或促销处理的酒店底价。</li>
 *     <li>**当前执行价 (price)**：指在特定售卖政策下，最终呈现给消费者的执行价格。</li>
 *     <li>**多维定位**：基于 [日期 + 酒店 + 房型 + 价格类型] 确定唯一的定价记录。</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "base_prices")
public class BasePrice {
    
    /** 价格记录内部主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 所属酒店 ID */
    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;
    
    /** 酒店外部编码冗余 */
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;
    
    /** 关联的价格类型 ID（对应价格分类，如 BAR） */
    @Column(name = "rate_type_id", nullable = false)
    private Integer rateTypeId;
    
    /** 价格类型编码冗余 */
    @Column(name = "rate_type_code", length = 50)
    private String rateTypeCode;

    /** 关联的房型 ID */
    @Column(name = "room_type_id", nullable = false)
    private Integer roomTypeId;
    
    /** 房型编码冗余 */
    @Column(name = "room_type_code", length = 50)
    private String roomTypeCode;
    
    /** 酒店底价/成本价 */
    @Column(name = "base_price", nullable = false)
    private Double basePrice;
    
    /** 最终对外销售的挂牌价/执行价 */
    @Column(name = "price", nullable = false)
    private Double price;
    
    /** 价格对应的营业日期 */
    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;
    
    /** 价格状态：active-有效可用，inactive-已失效 */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;
    
    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    /** 最后修改时间 */
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    // 关联关系 ---------------------------------------------------------
    
    /** 所属酒店实体引用 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", insertable = false, updatable = false)
    private Hotel hotel;
    
    /** 关联的价格类型实体引用 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_type_id", insertable = false, updatable = false)
    private RateType rateType;
    
    /** 关联的房型实体引用 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", insertable = false, updatable = false)
    private RoomType roomType;
    
    /**
     * 价格状态枚举
     */
    public enum Status {
        /** 有效 */
        active, 
        /** 无效 */
        inactive
    }
    
    /**
     * JPA 更新前自动刷新更新时间。
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}

