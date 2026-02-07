package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;

/**
 * 集团设施实体类
 * 对应数据库group_facilities表，存储集团级别的标准设施
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "group_facilities")
public class GroupFacility {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "facility_type", nullable = false, length = 50)
    private String facilityType;
    
    @Column(name = "facility_name", nullable = false, length = 100)
    private String facilityName;
    
    @Column(name = "facility_code", nullable = false, length = 50, unique = true)
    private String facilityCode;
    
    @Column(name = "available", nullable = false)
    private Boolean available = true;
    
    @Column(name = "description", length = 255)
    private String description;
}
