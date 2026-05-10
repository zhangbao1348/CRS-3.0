package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;

/**
 * 酒店服务设施实体类 (HotelFacility)
 * 
 * <p>本类对应数据库中的 `hotel_facilities` 表，记录了单店酒店提供的各类硬件设施或配套服务（如：免费停车、Wi-Fi、健身房、会议室等）。</p>
 * 
 * <p>业务定义：</p>
 * <ul>
 *     <li>**分类管理**：通过 `facilityType` 区分通用设施、餐饮设施、休闲设施等。</li>
 *     <li>**状态控制**：`available` 字段用于快速切换某项设施的对外展示状态。</li>
 *     <li>**多维度关联**：支持基于 `hotelId` 或 `hotelCode` 的灵活关联查询。</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotel_facilities")
public class HotelFacility {
    
    /** 设施记录内部主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 所属酒店 ID */
    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;
    
    /** 酒店外部编码冗余 */
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;
    
    /** 所属租户 ID */
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    /** 设施大类 (如 'GENERAL', 'FOOD', 'LEISURE') */
    @Column(name = "facility_type", nullable = false, length = 50)
    private String facilityType;
    
    /** 设施显示名称 (如 '免费Wi-Fi', '24小时热水') */
    @Column(name = "facility_name", nullable = false, length = 100)
    private String facilityName;
    
    /** 设施唯一标识编码 */
    @Column(name = "facility_code", nullable = false, length = 50)
    private String facilityCode;
    
    /** 当前是否提供该设施 */
    @Column(name = "available", nullable = false)
    private Boolean available = true;
    
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
    @JsonIgnore
    private Hotel hotel;
    
    /**
     * JPA 更新前自动刷新更新时间。
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}