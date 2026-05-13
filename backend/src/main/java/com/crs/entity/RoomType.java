package com.crs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 房型实体类 (RoomType)
 * 
 * <p>本类对应数据库中的 `room_types` 表，定义了酒店的具体物理房型（如：高级大床房、行政套房等）。</p>
 * 
 * <p>设计要点：</p>
 * <ul>
 *     <li>**多级关联**：房型必须归属于特定的 {@link Hotel}。同时，它可能关联一个 {@link GroupRoomType}（集团标准房型），实现“集团统筹+单店适配”的业务模型。</li>
 *     <li>**编码体系**：包含 `code` (房型编码) 和 `groupRoomTypeCode` (集团房型编码)，用于多维度的同步与标识。</li>
 *     <li>**状态控制**：支持 active/inactive 状态，控制房型在预订端的可售性。</li>
 * </ul>
 */



@Entity
@Table(name = "room_types")
public class RoomType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 所属租户 ID */
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    /** 酒店编码冗余字段，方便快速查询 */
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;
    
    /** 集团标准房型编码冗余 */
    @Column(name = "group_room_type_code", length = 50)
    private String groupRoomTypeCode;
    
    /** 房型唯一编码 (如 'KNG', 'DLX') */
    @Column(name = "code", nullable = false, length = 50)
    private String code;
    
    /** 房型名称 (如 '高级大床房') */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /** 房型详细描述，通常包含面积、床型、窗户情况等 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /** 房型状态：active-可用，inactive-禁用 */
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
    
    /** 所属酒店实体的延迟加载引用 */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_code", referencedColumnName = "hotel_code", insertable = false, updatable = false)
    private Hotel hotel;
    
    /** 关联的集团标准房型实体的延迟加载引用 */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_room_type_code", referencedColumnName = "room_type_code", insertable = false, updatable = false)
    private GroupRoomType groupRoomType;
    
    /**
     * 房型状态枚举
     */
    public enum Status {
        /** 正常售卖中 */
        active, 
        /** 已下架或暂不售卖 */
        inactive
    }
    
    /**
     * JPA 更新前自动刷新更新时间。
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }

    public String getHotelCode() { return hotelCode; }
    public void setHotelCode(String hotelCode) { this.hotelCode = hotelCode; }

    public String getGroupRoomTypeCode() { return groupRoomTypeCode; }
    public void setGroupRoomTypeCode(String groupRoomTypeCode) { this.groupRoomTypeCode = groupRoomTypeCode; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public GroupRoomType getGroupRoomType() { return groupRoomType; }
    public void setGroupRoomType(GroupRoomType groupRoomType) { this.groupRoomType = groupRoomType; }
}

