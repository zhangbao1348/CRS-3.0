package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;

/**
 * 操作日志实体类
 * 对应数据库operation_logs表
 */



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

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
