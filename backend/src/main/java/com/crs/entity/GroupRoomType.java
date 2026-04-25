package com.crs.entity;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 集团房型实体类
 * 对应数据库group_room_types表
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "group_room_types")
public class GroupRoomType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "group_id", nullable = false)
    private Integer groupId;
    
    @Column(name = "room_type_code", nullable = false, unique = true, length = 50)
    private String roomTypeCode;
    
    @Column(name = "room_type_name", nullable = false, length = 100)
    private String roomTypeName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "room_type_category_id")
    private Integer roomTypeCategoryId;
    
    @Column(name = "max_occupancy")
    private Integer maxOccupancy;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "active";
    
    // 关联关系
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_category_id", insertable = false, updatable = false)
    private RoomTypeCategory roomTypeCategory;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
    
    // Getter and Setter methods
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
    
    // toString method without associations
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
