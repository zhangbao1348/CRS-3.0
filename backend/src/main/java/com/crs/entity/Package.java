package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 包价实体类
 * 对应数据库packages表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "packages")
public class Package {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;
    
    @Column(name = "type", nullable = false, length = 50)
    private String type; // 包价类型：早餐、午餐、晚餐、综合等
    
    @Column(name = "quantity_type", nullable = false, length = 20)
    private String quantityType; // 份数类型：fixed(固定份数)、per_person(按人数)
    
    @Column(name = "fixed_quantity")
    private Integer fixedQuantity; // 固定份数
    
    @Column(name = "frequency", nullable = false, length = 50)
    private String frequency; // 发放频率：每天出现一次、每次入住出现一次等
    
    @Column(name = "price_type", nullable = false, length = 20)
    private String priceType; // 价格类型：group(集团统一价格)、hotel(酒店设置价格)
    
    @Column(name = "fixed_price")
    private Double fixedPrice; // 固定价格
    
    @Column(name = "tax_included", nullable = false)
    private Boolean taxIncluded = false; // 是否含税
    
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate; // 开始日期
    
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate; // 结束日期
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    // 状态枚举
    public enum Status {
        active, inactive
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}
