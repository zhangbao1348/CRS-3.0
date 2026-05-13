package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;

/**
 * 集团担保政策实体类
 * 对应数据库group_guarantee_policies表
 */



@Entity
@Table(name = "group_guarantee_policies")
public class GroupGuaranteePolicy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "group_id", nullable = false)
    private Integer groupId;
    
    @Column(name = "policy_code", nullable = false, unique = true, length = 50)
    private String policyCode;
    
    @Column(name = "policy_name", nullable = false, length = 100)
    private String policyName;
    
    @Column(name = "policy_details", nullable = false, columnDefinition = "TEXT")
    private String policyDetails;
    
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;
    
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
