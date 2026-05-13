package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;

/**
 * 增值包价实体类 (Package)
 * 
 * <p>本类对应数据库中的 `packages` 表，定义了随房价包含或可额外购买的增值服务项目（如：单早、双早、下午茶、接机服务等）。</p>
 * 
 * <p>业务配置：</p>
 * <ul>
 *     <li>**发放规则**：通过 `quantityType` (固定份数/按人数) 和 `frequency` (每日发放/每单发放) 组合定义。</li>
 *     <li>**定价逻辑**：`priceType` 决定了价格是由集团统一设定还是由酒店自行调整。</li>
 *     <li>**财务归类**：通过 `type` 区分餐饮、交通、娱乐等不同性质的增值项目。</li>
 * </ul>
 */



@Entity
@Table(name = "packages")
public class Package {
    
    /** 包价内部主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 所属租户 ID */
    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;
    
    /** 包价唯一编码 (如 'BF_SINGLE', 'DINNER_SET') */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
    
    /** 包价显示名称 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /** 详细描述 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /** 状态：active-启用，inactive-停用 */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;
    
    /** 
     * 包价类型
     * 可选值：breakfast(早餐), lunch(午餐), dinner(晚餐), entertainment(娱乐), transportation(交通) 等
     */
    @Column(name = "type", nullable = false, length = 50)
    private String type;
    
    /** 
     * 份数类型
     * 可选值：fixed(固定份数), per_person(按成人人数), per_child(按儿童人数), per_room(按房间数)
     */
    @Column(name = "quantity_type", nullable = false, length = 20)
    private String quantityType;
    
    /** 固定份数 (当 quantityType 为 fixed 时有效) */
    @Column(name = "fixed_quantity")
    private Integer fixedQuantity;
    
    /** 
     * 发放频率
     * 可选值：daily(每天一次), once(整个入住周期仅一次), first_night(仅首晚)
     */
    @Column(name = "frequency", nullable = false, length = 50)
    private String frequency;
    
    /** 
     * 定价模式
     * 可选值：group(集团统一定价), hotel(酒店差异定价)
     */
    @Column(name = "price_type", nullable = false, length = 20)
    private String priceType;
    
    /** 固定价格 (单位：元) */
    @Column(name = "fixed_price")
    private Double fixedPrice;
    
    /** 价格是否已包含税费 */
    @Column(name = "tax_included", nullable = false)
    private Boolean taxIncluded = false;
    
    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    /** 最后更新时间 */
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    /**
     * 包价状态枚举
     */
    public enum Status {
        /** 启用 */
        active, 
        /** 停用 */
        inactive
    }
    
    /**
     * JPA 更新前自动刷新更新时间。
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getQuantityType() { return quantityType; }
    public void setQuantityType(String quantityType) { this.quantityType = quantityType; }
    public Integer getFixedQuantity() { return fixedQuantity; }
    public void setFixedQuantity(Integer fixedQuantity) { this.fixedQuantity = fixedQuantity; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public String getPriceType() { return priceType; }
    public void setPriceType(String priceType) { this.priceType = priceType; }
    public Double getFixedPrice() { return fixedPrice; }
    public void setFixedPrice(Double fixedPrice) { this.fixedPrice = fixedPrice; }
    public Boolean getTaxIncluded() { return taxIncluded; }
    public void setTaxIncluded(Boolean taxIncluded) { this.taxIncluded = taxIncluded; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}

