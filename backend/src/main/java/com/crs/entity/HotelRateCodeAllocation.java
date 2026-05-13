package com.crs.entity;





import jakarta.persistence.*;

/**
 * 酒店房价码分配与权限控制实体 (HotelRateCodeAllocation)
 * 
 * <p>本类对应数据库中的 `hotel_rate_code_allocations` 表，是集团管理单店价格政策的核心控制点。</p>
 * 
 * <p>业务场景：</p>
 * <ul>
 *     <li>**资源分配**：集团定义标准房价码后，通过此实体将其“下发”给特定酒店。</li>
 *     <li>**分级管控**：集团可以精准控制酒店对该房价码的编辑权限，如是否允许修改价格、预订限制或担保规则等。</li>
 * </ul>
 */



@Entity
@Table(name = "hotel_rate_code_allocations")
public class HotelRateCodeAllocation {
    
    /** 分配记录内部主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 所属租户 ID */
    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;
    
    /** 酒店外部编码 */
    @Column(name = "hotel_code", nullable = false, length = 50)
    private String hotelCode;
    
    /** 价格计划编码 (Rate Code) */
    @Column(name = "rate_code", nullable = false, length = 50)
    private String rateCode;
    
    /** 是否已分配给该酒店使用 */
    @Column(name = "allocated", nullable = false)
    private Boolean allocated = false;
    
    /** 权限开关：是否允许酒店修改该计划的基础信息（如名称、描述） */
    @Column(name = "basic_info_editable", nullable = false)
    private Boolean basicInfoEditable = false;
    
    /** 权限开关：是否允许酒店修改该计划的价格信息（如派生折扣、底价） */
    @Column(name = "price_info_editable", nullable = false)
    private Boolean priceInfoEditable = false;
    
    /** 权限开关：是否允许酒店修改预订限制（如连住晚数、提前预订天数） */
    @Column(name = "booking_limit_editable", nullable = false)
    private Boolean bookingLimitEditable = false;
    
    /** 权限开关：是否允许酒店修改担保与取消规则 */
    @Column(name = "guarantee_rule_editable", nullable = false)
    private Boolean guaranteeRuleEditable = false;
    
    /** 权限开关：是否允许酒店关联该计划的促销活动 */
    @Column(name = "promotion_editable", nullable = false)
    private Boolean promotionEditable = false;
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public String getHotelCode() { return hotelCode; }
    public void setHotelCode(String hotelCode) { this.hotelCode = hotelCode; }
    public String getRateCode() { return rateCode; }
    public void setRateCode(String rateCode) { this.rateCode = rateCode; }
    public Boolean getAllocated() { return allocated; }
    public void setAllocated(Boolean allocated) { this.allocated = allocated; }
    public Boolean getBasicInfoEditable() { return basicInfoEditable; }
    public void setBasicInfoEditable(Boolean basicInfoEditable) { this.basicInfoEditable = basicInfoEditable; }
    public Boolean getPriceInfoEditable() { return priceInfoEditable; }
    public void setPriceInfoEditable(Boolean priceInfoEditable) { this.priceInfoEditable = priceInfoEditable; }
    public Boolean getBookingLimitEditable() { return bookingLimitEditable; }
    public void setBookingLimitEditable(Boolean bookingLimitEditable) { this.bookingLimitEditable = bookingLimitEditable; }
    public Boolean getGuaranteeRuleEditable() { return guaranteeRuleEditable; }
    public void setGuaranteeRuleEditable(Boolean guaranteeRuleEditable) { this.guaranteeRuleEditable = guaranteeRuleEditable; }
    public Boolean getPromotionEditable() { return promotionEditable; }
    public void setPromotionEditable(Boolean promotionEditable) { this.promotionEditable = promotionEditable; }
}