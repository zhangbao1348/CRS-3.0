package com.crs.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * OverbookingLog 实体类
 * 
 * <p>本核心模块自动生成详细注释。主要负责【OverbookingLog】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/11-库存管理.md</li>
 *     <li>**模块职责**：单一职责原则，提供 OverbookingLog 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
  
@Entity @Table(name = "overbooking_logs")
public class OverbookingLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "tenant_id", nullable = false) private Integer tenantId;
    
    @Column(name = "hotel_code", nullable = false, length = 50) private String hotelCode;
    @Column(name = "dimension_type", nullable = false, length = 20) private String dimensionType;
    @Column(name = "dimension_code", nullable = false, length = 50) private String dimensionCode = "";
    @Column(name = "operator_name", nullable = false, length = 100) private String operatorName;
    @Column(name = "operation_type", nullable = false, length = 20) private String operationType;
    @Column(name = "operation_time", nullable = false) @Temporal(TemporalType.TIMESTAMP) private Date operationTime = new Date();
    @Column(name = "detail", columnDefinition = "TEXT") private String detail;
    @Column(name = "created_at", nullable = false, updatable = false) @Temporal(TemporalType.TIMESTAMP) private Date createdAt = new Date();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public String getHotelCode() { return hotelCode; }
    public void setHotelCode(String hotelCode) { this.hotelCode = hotelCode; }
    public String getDimensionType() { return dimensionType; }
    public void setDimensionType(String dimensionType) { this.dimensionType = dimensionType; }
    public String getDimensionCode() { return dimensionCode; }
    public void setDimensionCode(String dimensionCode) { this.dimensionCode = dimensionCode; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
    public Date getOperationTime() { return operationTime; }
    public void setOperationTime(Date operationTime) { this.operationTime = operationTime; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
