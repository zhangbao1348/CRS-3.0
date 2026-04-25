package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 价格计划包价关联实体类
 * 对应数据库rate_plan_packages表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rate_plan_packages")
public class RatePlanPackage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "rate_plan_id", nullable = false)
    private Integer ratePlanId;
    
    @Column(name = "rate_plan_code", length = 50)
    private String ratePlanCode;
    
    @Column(name = "package_id", nullable = false)
    private Integer packageId;
    
    @Column(name = "package_code", length = 50)
    private String packageCode;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_id", insertable = false, updatable = false)
    private RatePlan ratePlan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", insertable = false, updatable = false)
    private Package packageEntity;
}
