package com.crs.entity;





import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 酒店价格实体类
 * 存储酒店每日价格数据，按租户+酒店+房价码+房型+日期维度
 */



@Entity
@Table(name = "hotel_prices", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "hotel_code", "rate_code", "room_type_code", "price_date"})
})
/**
 * HotelPrice 实体类
 * 
 * <p>本核心模块自动生成详细注释。主要负责【HotelPrice】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/09-系统设置.md</li>
 *     <li>**模块职责**：单一职责原则，提供 HotelPrice 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public class HotelPrice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;
    
    
    @Column(name = "hotel_code", nullable = false, length = 50)
    private String hotelCode;
    
    @Column(name = "rate_code", nullable = false, length = 50)
    private String rateCode;
    
    @Column(name = "room_type_code", nullable = false, length = 50)
    private String roomTypeCode;
    
    @Column(name = "price_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date priceDate;
    
    @Column(name = "price_with_tax", precision = 10, scale = 2)
    private BigDecimal priceWithTax;
    
    @Column(name = "price_without_tax", precision = 10, scale = 2)
    private BigDecimal priceWithoutTax;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "active";
    
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
    public String getRateCode() { return rateCode; }
    public void setRateCode(String rateCode) { this.rateCode = rateCode; }
    public String getRoomTypeCode() { return roomTypeCode; }
    public void setRoomTypeCode(String roomTypeCode) { this.roomTypeCode = roomTypeCode; }
    public Date getPriceDate() { return priceDate; }
    public void setPriceDate(Date priceDate) { this.priceDate = priceDate; }
    public BigDecimal getPriceWithTax() { return priceWithTax; }
    public void setPriceWithTax(BigDecimal priceWithTax) { this.priceWithTax = priceWithTax; }
    public BigDecimal getPriceWithoutTax() { return priceWithoutTax; }
    public void setPriceWithoutTax(BigDecimal priceWithoutTax) { this.priceWithoutTax = priceWithoutTax; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
