package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 取消政策实体类
 * 对应数据库cancellation_policies表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cancellation_policies")
public class CancellationPolicy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
    
    @Column(name = "type", nullable = false, length = 50)
    private String type;
    
    @Column(name = "cancellation_days")
    private Integer cancellationDays;
    
    @Column(name = "cancellation_time", length = 10)
    private String cancellationTime;
    
    @Column(name = "cancellation_fee_type", length = 50)
    private String cancellationFeeType;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "status", length = 20)
    private String status = "active";
    
    @Column(name = "group_id")
    private Integer groupId;
    
    @Column(name = "tenant_id")
    private Integer tenantId;
    
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
}
