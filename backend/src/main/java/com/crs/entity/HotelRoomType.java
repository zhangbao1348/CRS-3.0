package com.crs.entity;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 酒店房型实体类
 * 对应数据库hotel_room_types表
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotel_room_types")
public class HotelRoomType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;
    
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;
    
    @Column(name = "group_room_type_id")
    private Integer groupRoomTypeId;
    
    @Column(name = "group_room_type_code", length = 50)
    private String groupRoomTypeCode;
    
    @Column(name = "room_type_code", nullable = false, length = 50)
    private String roomTypeCode;
    
    @Column(name = "room_type_name", nullable = false, length = 100)
    private String roomTypeName;
    
    @Column(name = "english_name", length = 200)
    private String englishName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "area")
    private java.math.BigDecimal area;
    
    @Column(name = "floor", length = 50)
    private String floor;
    
    @Column(name = "window_type", length = 20)
    private String windowType;
    
    @Column(name = "bed_type", length = 50)
    private String bedType;
    
    @Column(name = "max_occupancy")
    private Integer maxOccupancy;
    
    @Column(name = "max_children")
    private Integer maxChildren;
    
    @Column(name = "total_rooms")
    private Integer totalRooms;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    @Column(name = "room_type_category_id")
    private Integer roomTypeCategoryId;
    
    @Column(name = "room_type_category_code", length = 50)
    private String roomTypeCategoryCode;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "active";
    
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
    
    // 关联关系
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", insertable = false, updatable = false)
    private Hotel hotel;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_room_type_id", insertable = false, updatable = false)
    private GroupRoomType groupRoomType;
    
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
    
    // toString method without associations
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