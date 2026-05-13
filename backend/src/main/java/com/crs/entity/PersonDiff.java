package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;

/**
 * 人数差价实体类
 * 对应数据库person_diffs表
 */



@Entity
@Table(name = "person_diffs")
public class PersonDiff {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    @Column(name = "system_id", nullable = false)
    private Integer systemId;
    
    @Column(name = "system_code", length = 50)
    private String systemCode;
    
    @Column(name = "person_type", nullable = false, length = 50)
    private String personType;
    
    @Column(name = "value", nullable = false)
    private Double value;
    
    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;
    
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    
    @Column(name = "weekdays", nullable = false, length = 20)
    private String weekdays;
    
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id", insertable = false, updatable = false)
    private PersonDiffSystem system;
    
    // 状态枚举
    public enum Status {
        active, inactive
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public Integer getSystemId() { return systemId; }
    public void setSystemId(Integer systemId) { this.systemId = systemId; }
    public String getSystemCode() { return systemCode; }
    public void setSystemCode(String systemCode) { this.systemCode = systemCode; }
    public String getPersonType() { return personType; }
    public void setPersonType(String personType) { this.personType = personType; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getWeekdays() { return weekdays; }
    public void setWeekdays(String weekdays) { this.weekdays = weekdays; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
