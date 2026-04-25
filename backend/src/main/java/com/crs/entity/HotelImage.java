package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 酒店图片实体类
 * 对应数据库hotel_images表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotel_images")
public class HotelImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;
    
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;
    
    @Column(name = "image_type", nullable = false, length = 50)
    private String imageType;
    
    @Column(name = "image_path", nullable = false, length = 255)
    private String imagePath;
    
    @Column(name = "image_name", nullable = false, length = 100)
    private String imageName;
    
    @Column(name = "description", length = 200)
    private String description;
    
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", insertable = false, updatable = false)
    @JsonIgnore
    private Hotel hotel;
}