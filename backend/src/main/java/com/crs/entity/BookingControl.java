package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;




@Entity
@Table(name = "booking_controls", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "hotel_code", "dimension_type", "dimension_code", "control_date"})
})
/**
 * BookingControl 实体类
 * 
 * <p>本核心模块自动生成详细注释。主要负责【BookingControl】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/11-库存管理.md</li>
 *     <li>**模块职责**：单一职责原则，提供 BookingControl 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public class BookingControl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    
    @Column(name = "hotel_code", nullable = false, length = 50)
    private String hotelCode;

    @Column(name = "dimension_type", nullable = false, length = 20)
    private String dimensionType;

    @Column(name = "dimension_code", nullable = false, length = 50)
    private String dimensionCode = "";

    @Column(name = "control_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date controlDate;

    @Column(name = "cancellation_policy_code", length = 50)
    private String cancellationPolicyCode;

    @Column(name = "advance_booking_days")
    private Integer advanceBookingDays = 0;

    @Column(name = "min_stay")
    private Integer minStay = 1;

    @Column(name = "max_stay")
    private Integer maxStay = 30;

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
    public Date getControlDate() { return controlDate; }
    public void setControlDate(Date controlDate) { this.controlDate = controlDate; }
    public String getCancellationPolicyCode() { return cancellationPolicyCode; }
    public void setCancellationPolicyCode(String cancellationPolicyCode) { this.cancellationPolicyCode = cancellationPolicyCode; }
    public Integer getAdvanceBookingDays() { return advanceBookingDays; }
    public void setAdvanceBookingDays(Integer advanceBookingDays) { this.advanceBookingDays = advanceBookingDays; }
    public Integer getMinStay() { return minStay; }
    public void setMinStay(Integer minStay) { this.minStay = minStay; }
    public Integer getMaxStay() { return maxStay; }
    public void setMaxStay(Integer maxStay) { this.maxStay = maxStay; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
