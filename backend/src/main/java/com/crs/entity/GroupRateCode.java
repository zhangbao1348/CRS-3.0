package com.crs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 集团房价码实体类
 * 对应数据库group_rate_codes表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "group_rate_codes")
public class GroupRateCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "group_id", nullable = false)
    private Integer groupId;
    
    @Column(name = "rate_code", nullable = false, unique = true, length = 50)
    private String rateCode;
    
    @Column(name = "rate_name", nullable = false, length = 100)
    private String rateName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "active";
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    // 关联关系
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Group group;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}
