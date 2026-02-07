package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 房价码实体类
 * 对应数据库rate_codes表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rate_codes")
public class RateCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "rate_code", nullable = false, unique = true, length = 50)
    private String rateCode;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", length = 200)
    private String description;
    
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    // 关联关系
    @OneToMany(mappedBy = "rateCode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HotelRateCodeAllocation> hotelRateCodeAllocations;
    
    // 状态枚举
    public enum Status {
        active, inactive
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}