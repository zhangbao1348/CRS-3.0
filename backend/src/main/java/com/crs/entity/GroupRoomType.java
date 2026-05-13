package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 集团标准房型实体类 (GroupRoomType)
 * 
 * <p>本类对应数据库中的 `group_room_types` 表。作为集团层级的“房型母版”，定义了集团统一的房型标准。</p>
 * 
 * <p>业务场景：</p>
 * <ul>
 *     <li>集团管理员定义标准房型（如：标准双床房），并将其下发给各酒店。</li>
 *     <li>各酒店在本地创建 {@link RoomType} 时，可选择关联此标准房型，以保持集团内房型统计的一致性。</li>
 * </ul>
 */


@Entity
@Table(name = "group_room_types")
public class GroupRoomType {
    
    /** 集团房型内部主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 所属集团 ID（对应 Tenant ID） */
    @Column(name = "group_id", nullable = false)
    private Integer groupId;
    
    /** 集团唯一编码 */
    @Column(name = "group_code", length = 50)
    private String groupCode;
    
    /** 集团标准房型代码 (如 'DBL', 'TWD')，全集团唯一 */
    @org.hibernate.annotations.NaturalId
    @Column(name = "room_type_code", nullable = false, unique = true, length = 50)
    private String roomTypeCode;
    
    /** 集团标准房型名称 */
    @Column(name = "room_type_name", nullable = false, length = 100)
    private String roomTypeName;
    
    /** 房型详细描述 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /** 房型分类 ID（如：大床房类、套房类） */
    @Column(name = "room_type_category_id")
    private Integer roomTypeCategoryId;
    
    /** 房型分类编码 */
    @Column(name = "room_type_category_code", length = 50)
    private String roomTypeCategoryCode;
    
    /** 标准最大入住人数 */
    @Column(name = "max_occupancy")
    private Integer maxOccupancy;
    
    /** 排序号，用于在 UI 列表中的显示顺序 */
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    /** 房型状态：active-启用，inactive-停用 */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "active";
    
    // 关联关系 ---------------------------------------------------------
    
    /** 关联的房型分类实体 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_category_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RoomTypeCategory roomTypeCategory;
    
    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    /** 最后更新时间 */
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
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
    
    public Integer getGroupId() {
        return groupId;
    }
    
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
    
    public String getGroupCode() {
        return groupCode;
    }
    
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public Integer getMaxOccupancy() {
        return maxOccupancy != null ? maxOccupancy : 2;
    }
    
    public void setMaxOccupancy(Integer maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }
    
    public Integer getSortOrder() {
        return sortOrder != null ? sortOrder : 0;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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
    
    @Override
    public String toString() {
        return "GroupRoomType{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", roomTypeCode='" + roomTypeCode + '\'' +
                ", roomTypeName='" + roomTypeName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

