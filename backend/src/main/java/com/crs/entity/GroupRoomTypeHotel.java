package com.crs.entity;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 集团房型和酒店关联实体类
 * 对应数据库group_room_type_hotel表
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "group_room_type_hotel")
public class GroupRoomTypeHotel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "group_room_type_id", nullable = false)
    private Integer groupRoomTypeId;
    
    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;
    
    @Column(name = "allocated", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean allocated = false;
    
    @Column(name = "room_info_editable", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean roomInfoEditable = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    // 关联关系
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_room_type_id", insertable = false, updatable = false)
    private GroupRoomType groupRoomType;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", insertable = false, updatable = false)
    private Hotel hotel;
    
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
    
    public Integer getGroupRoomTypeId() {
        return groupRoomTypeId;
    }
    
    public void setGroupRoomTypeId(Integer groupRoomTypeId) {
        this.groupRoomTypeId = groupRoomTypeId;
    }
    
    public Integer getHotelId() {
        return hotelId;
    }
    
    public void setHotelId(Integer hotelId) {
        this.hotelId = hotelId;
    }
    
    public Boolean getAllocated() {
        return allocated;
    }
    
    public void setAllocated(Boolean allocated) {
        this.allocated = allocated;
    }
    
    public Boolean getRoomInfoEditable() {
        return roomInfoEditable;
    }
    
    public void setRoomInfoEditable(Boolean roomInfoEditable) {
        this.roomInfoEditable = roomInfoEditable;
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
    
    public GroupRoomType getGroupRoomType() {
        return groupRoomType;
    }
    
    public void setGroupRoomType(GroupRoomType groupRoomType) {
        this.groupRoomType = groupRoomType;
    }
    
    public Hotel getHotel() {
        return hotel;
    }
    
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
    
    // toString method without associations
    @Override
    public String toString() {
        return "GroupRoomTypeHotel{" +
                "id=" + id +
                ", groupRoomTypeId=" + groupRoomTypeId +
                ", hotelId=" + hotelId +
                ", allocated=" + allocated +
                ", roomInfoEditable=" + roomInfoEditable +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}