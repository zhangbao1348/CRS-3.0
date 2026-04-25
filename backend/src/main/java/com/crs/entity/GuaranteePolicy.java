package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 担保政策实体类
 * 对应数据库guarantee_policies表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "guarantee_policies")
public class GuaranteePolicy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
    
    @Column(name = "type", nullable = false, length = 50)
    private String type;
    
    @Column(name = "guarantee_sub_type", length = 50)
    private String guaranteeSubType;
    
    @Column(name = "guarantee_amount", length = 50)
    private String guaranteeAmount;
    
    @Column(name = "latest_arrival_time", length = 10)
    private String latestArrivalTime;
    
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
