package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 库存实时管理实体类 (Inventory)
 * 
 * <p>本类对应数据库中的 `inventory` 表，记录了酒店房型在特定日期、特定价格计划及特定渠道下的库存状态。</p>
 * 
 * <p>业务核心：</p>
 * <ul>
 *     <li>**维度精细化**：库存是基于 [日期 + 酒店 + 房型 + 价格计划 + 渠道] 的五维模型。</li>
 *     <li>**动态可用性**：通过 `availableRooms` (总可用) 和 `allocated_rooms` (已售/已占) 实时反映房间状态。</li>
 *     <li>**分销控制**：支持针对特定 `channelCode` (如携程、美团、官网) 进行独立的库存分配与售卖控制。</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory")
public class Inventory {
    
    /** 库存记录内部主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 所属酒店 ID */
    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;
    
    /** 酒店外部编码冗余 */
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;
    
    /** 关联的价格计划 ID */
    @Column(name = "rate_plan_id", nullable = false)
    private Integer ratePlanId;
    
    /** 价格计划编码冗余 */
    @Column(name = "rate_plan_code", length = 50)
    private String ratePlanCode;
    
    /** 关联的房型 ID */
    @Column(name = "room_type_id", nullable = false)
    private Integer roomTypeId;
    
    /** 房型编码冗余 */
    @Column(name = "room_type_code", length = 50)
    private String roomTypeCode;
    
    /** 关联的分销渠道 ID (若是全渠道库存则为 null 或默认 ID) */
    @Column(name = "channel_id")
    private Integer channelId;
    
    /** 渠道编码冗余 (如 'OTA', 'WEB', 'APP') */
    @Column(name = "channel_code", length = 50)
    private String channelCode;
    
    /** 库存日期 */
    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;
    
    /** 该日期下的可用房间数 (房型总剩余或分配给该计划的额度) */
    @Column(name = "available_rooms", nullable = false)
    private Integer availableRooms = 0;
    
    /** 该日期下已预订或已占用的房间数 */
    @Column(name = "allocated_rooms", nullable = false)
    private Integer allocatedRooms = 0;
    
    /** 库存状态：active-开放售卖，inactive-关闭售卖（即房满或人工停售） */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;
    
    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    /** 最后更新时间 */
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    // 关联关系 ---------------------------------------------------------
    
    /** 所属酒店实体引用 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", insertable = false, updatable = false)
    private Hotel hotel;
    
    /** 关联的价格计划实体引用 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_id", insertable = false, updatable = false)
    private RatePlan ratePlan;
    
    /** 关联的房型实体引用 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", insertable = false, updatable = false)
    private RoomType roomType;
    
    /** 关联的渠道定义实体引用 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", insertable = false, updatable = false)
    private ChannelCode channel;
    
    /**
     * 库存状态枚举
     */
    public enum Status {
        /** 正常售卖 */
        active, 
        /** 停止售卖 */
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

