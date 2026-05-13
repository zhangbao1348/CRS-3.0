package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;

/**
 * 酒店价格操作日志实体类
 */



@Entity
@Table(name = "hotel_price_logs")
public class HotelPriceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    
    @Column(name = "hotel_code", nullable = false, length = 50)
    private String hotelCode;

    @Column(name = "rate_code", nullable = false, length = 50)
    private String rateCode;

    @Column(name = "operator_name", nullable = false, length = 100)
    private String operatorName;

    @Column(name = "operation_type", nullable = false, length = 20)
    private String operationType;

    @Column(name = "operation_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date operationTime = new Date();

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public String getHotelCode() { return hotelCode; }
    public void setHotelCode(String hotelCode) { this.hotelCode = hotelCode; }
    public String getRateCode() { return rateCode; }
    public void setRateCode(String rateCode) { this.rateCode = rateCode; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
    public Date getOperationTime() { return operationTime; }
    public void setOperationTime(Date operationTime) { this.operationTime = operationTime; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
