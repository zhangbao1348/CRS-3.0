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
    
    @Column(name = "group_room_type_id")
    private Integer groupRoomTypeId;
    
    @Column(name = "room_type_code", nullable = false, length = 50)
    private String roomTypeCode;
    
    @Column(name = "room_type_name", nullable = false, length = 100)
    private String roomTypeName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "active";
    
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
    
    public Integer getGroupRoomTypeId() {
        return groupRoomTypeId;
    }
    
    public void setGroupRoomTypeId(Integer groupRoomTypeId) {
        this.groupRoomTypeId = groupRoomTypeId;
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