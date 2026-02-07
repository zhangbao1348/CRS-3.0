package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 操作日志实体类
 * 对应数据库operation_logs表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "operation_logs")
public class OperationLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "operator", nullable = false, length = 50)
    private String operator;
    
    @Column(name = "time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
    
    @Column(name = "type", nullable = false, length = 50)
    private String type;
    
    @Column(name = "action", nullable = false, length = 50)
    private String action;
    
    @Column(name = "details", nullable = false, columnDefinition = "TEXT")
    private String details;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}
