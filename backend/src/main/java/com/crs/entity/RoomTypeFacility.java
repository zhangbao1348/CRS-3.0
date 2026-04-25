package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;

/**
 * 房型设施实体类
 * 对应数据库room_type_facilities表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room_type_facilities")
public class RoomTypeFacility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "room_type_id", nullable = false)
    private Integer roomTypeId;

    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;

    @Column(name = "hotel_code", length = 50)
    private String hotelCode;

    @Column(name = "room_type_code", length = 50)
    private String roomTypeCode;

    @Column(name = "facility_type", nullable = false, length = 50)
    private String facilityType;

    @Column(name = "facility_name", nullable = false, length = 100)
    private String facilityName;

    @Column(name = "facility_code", nullable = false, length = 50)
    private String facilityCode;

    @Column(name = "available", nullable = false)
    private Boolean available = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", insertable = false, updatable = false)
    @JsonIgnore
    private RoomType roomType;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}
