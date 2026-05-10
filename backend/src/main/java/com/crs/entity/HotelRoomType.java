package com.crs.entity;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 酒店单店房型实体类 (HotelRoomType)
 * 
 * <p>本类对应数据库中的 `hotel_room_types` 表，记录了特定酒店内房型的详细物理属性及售卖状态。</p>
 * 
 * <p>核心属性：</p>
 * <ul>
 *     <li>**物理空间**：包含面积 (`area`)、楼层 (`floor`)、窗型 (`windowType`) 及床型 (`bedType`)。</li>
 *     <li>**入住约束**：定义最大成人人数 (`maxOccupancy`) 及最大儿童数 (`maxChildren`)。</li>
 *     <li>**库存规模**：`totalRooms` 记录了该房型在酒店内的总物理房间数。</li>
 *     <li>**关联继承**：通过 `groupRoomTypeId` 关联集团标准房型模板，支持“集团定义标准，单店差异化实施”。</li>
 * </ul>
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotel_room_types")
public class HotelRoomType {
    
    /** 酒店房型内部主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 所属酒店 ID */
    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;
    
    /** 酒店外部编码 */
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;
    
    /** 所属租户 ID */
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    /** 关联的集团标准房型 ID */
    @Column(name = "group_room_type_id")
    private Integer groupRoomTypeId;
    
    /** 集团标准房型编码 */
    @Column(name = "group_room_type_code", length = 50)
    private String groupRoomTypeCode;
    
    /** 本地房型编码 (如 'KNG', 'TWN')，酒店内唯一 */
    @Column(name = "room_type_code", nullable = false, length = 50)
    private String roomTypeCode;
    
    /** 本地房型名称 */
    @Column(name = "room_type_name", nullable = false, length = 100)
    private String roomTypeName;
    
    /** 房型英文名称 */
    @Column(name = "english_name", length = 200)
    private String englishName;
    
    /** 房型详细描述与卖点 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /** 房型面积 (平方米) */
    @Column(name = "area")
    private java.math.BigDecimal area;
    
    /** 所在楼层 (如 '3-5层') */
    @Column(name = "floor", length = 50)
    private String floor;
    
    /** 窗户类型 (有窗/无窗/部分有窗) */
    @Column(name = "window_type", length = 20)
    private String windowType;
    
    /** 床型描述 (如 '1.8米大床', '1.2米双床') */
    @Column(name = "bed_type", length = 50)
    private String bedType;
    
    /** 最大成人入住人数 */
    @Column(name = "max_occupancy")
    private Integer maxOccupancy;
    
    /** 最大儿童入住人数 */
    @Column(name = "max_children")
    private Integer maxChildren;
    
    /** 该房型的物理房间总数 */
    @Column(name = "total_rooms")
    private Integer totalRooms;
    
    /** UI 显示排序号 */
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    /** 关联的房型大类 ID */
    @Column(name = "room_type_category_id")
    private Integer roomTypeCategoryId;
    
    /** 房型大类编码 */
    @Column(name = "room_type_category_code", length = 50)
    private String roomTypeCategoryCode;
    
    /** 房型状态：active-启用，inactive-停用 */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "active";
    
    /** 房型分类实体引用 */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_category_id", insertable = false, updatable = false)
    private RoomTypeCategory roomTypeCategory;
    
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
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", insertable = false, updatable = false)
    private Hotel hotel;
    
    /** 关联的集团房型模板实体引用 */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_room_type_id", insertable = false, updatable = false)
    private GroupRoomType groupRoomType;
    
    /**
     * JPA 更新前自动刷新更新时间。
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
    
    // Getter 和 Setter --------------------------------------------------
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getHotelId() {
        return hotelId;
    }
    
    public void setHotelId(Integer hotelId) {
        this.hotelId = hotelId;
    }
    
    public String getHotelCode() {
        return hotelCode;
    }
    
    public void setHotelCode(String hotelCode) {
        this.hotelCode = hotelCode;
    }
    
    public Integer getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }
    
    public Integer getGroupRoomTypeId() {
        return groupRoomTypeId;
    }
    
    public void setGroupRoomTypeId(Integer groupRoomTypeId) {
        this.groupRoomTypeId = groupRoomTypeId;
    }
    
    public String getGroupRoomTypeCode() {
        return groupRoomTypeCode;
    }
    
    public void setGroupRoomTypeCode(String groupRoomTypeCode) {
        this.groupRoomTypeCode = groupRoomTypeCode;
    }
    
    public String getRoomTypeCode() {
        return roomTypeCode;
    }
    
    public void setRoomTypeCode(String roomTypeCode) {
        this.roomTypeCode = roomTypeCode;
    }
    
    public String getRoomTypeName() {
        return roomTypeName;
    }
    
    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }
    
    public String getEnglishName() {
        return englishName;
    }
    
    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public java.math.BigDecimal getArea() {
        return area;
    }
    
    public void setArea(java.math.BigDecimal area) {
        this.area = area;
    }
    
    public String getFloor() {
        return floor;
    }
    
    public void setFloor(String floor) {
        this.floor = floor;
    }
    
    public String getWindowType() {
        return windowType;
    }
    
    public void setWindowType(String windowType) {
        this.windowType = windowType;
    }
    
    public String getBedType() {
        return bedType;
    }
    
    public void setBedType(String bedType) {
        this.bedType = bedType;
    }
    
    public Integer getMaxOccupancy() {
        return maxOccupancy != null ? maxOccupancy : 2;
    }
    
    public void setMaxOccupancy(Integer maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }
    
    public Integer getMaxChildren() {
        return maxChildren;
    }
    
    public void setMaxChildren(Integer maxChildren) {
        this.maxChildren = maxChildren;
    }
    
    public Integer getTotalRooms() {
        return totalRooms;
    }
    
    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }
    
    public Integer getSortOrder() {
        return sortOrder != null ? sortOrder : 0;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Integer getRoomTypeCategoryId() {
        return roomTypeCategoryId;
    }
    
    public void setRoomTypeCategoryId(Integer roomTypeCategoryId) {
        this.roomTypeCategoryId = roomTypeCategoryId;
    }
    
    public String getRoomTypeCategoryCode() {
        return roomTypeCategoryCode;
    }
    
    public void setRoomTypeCategoryCode(String roomTypeCategoryCode) {
        this.roomTypeCategoryCode = roomTypeCategoryCode;
    }
    
    public RoomTypeCategory getRoomTypeCategory() {
        return roomTypeCategory;
    }
    
    public void setRoomTypeCategory(RoomTypeCategory roomTypeCategory) {
        this.roomTypeCategory = roomTypeCategory;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Hotel getHotel() {
        return hotel;
    }
    
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
    
    public GroupRoomType getGroupRoomType() {
        return groupRoomType;
    }
    
    public void setGroupRoomType(GroupRoomType groupRoomType) {
        this.groupRoomType = groupRoomType;
    }
    
    @Override
    public String toString() {
        return "HotelRoomType{" +
                "id=" + id +
                ", hotelId=" + hotelId +
                ", groupRoomTypeId=" + groupRoomTypeId +
                ", roomTypeCode='" + roomTypeCode + '\'' +
                ", roomTypeName='" + roomTypeName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}