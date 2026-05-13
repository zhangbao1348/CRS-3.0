package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;

/**
 * 担保政策实体类 (GuaranteePolicy)
 * 
 * <p>本类对应数据库中的 `guarantee_policies` 表，规定了预订是否需要提供担保（如信用卡、预付定金）以及最晚保留时间。</p>
 * 
 * <p>核心配置：</p>
 * <ul>
 *     <li>**保留时刻**：`latestArrivalTime` 定义了非担保预订的最晚保留时间（如 18:00）。</li>
 *     <li>**担保额度**：`guaranteeAmount` 定义了担保所需的金额或比例（如：全额、首晚）。</li>
 *     <li>**担保方式**：`guaranteeSubType` 区分信用卡担保、现金担保、公司账号担保等。</li>
 * </ul>
 */



@Entity
@Table(name = "guarantee_policies")
public class GuaranteePolicy {
    
    /** 政策主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 政策显示名称 (如 '信用卡担保', '18点前免费保留') */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /** 政策唯一编码 */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
    
    /** 担保类型 (如 'none', 'guaranteed', 'prepaid') */
    @Column(name = "type", nullable = false, length = 50)
    private String type;
    
    /** 
     * 担保子类型
     * 可选值：credit_card(信用卡), deposit(定金), company_account(公司账户)
     */
    @Column(name = "guarantee_sub_type", length = 50)
    private String guaranteeSubType;
    
    /** 
     * 担保额度
     * 可选值：first_night(首晚), full_stay(全程), fixed_amount(固定金额)
     */
    @Column(name = "guarantee_amount", length = 50)
    private String guaranteeAmount;
    
    /** 非担保订单的最晚保留时间 (格式如 '18:00') */
    @Column(name = "latest_arrival_time", length = 10)
    private String latestArrivalTime;
    
    /** 政策详细条款说明 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /** 状态：active-启用，inactive-停用 */
    @Column(name = "status", length = 20)
    private String status = "active";
    
    /** 关联的集团 ID (若是集团统一模板) */
    @Column(name = "group_id")
    private Integer groupId;
    
    /** 所属租户 ID */
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    /** 最后更新时间 */
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    /**
     * JPA 更新前自动刷新更新时间。
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    /** 是否为默认政策 (0: 否, 1: 是) */
    @Column(name = "is_default")
    private Integer isDefault = 0;

    /** 信用卡类型 (若是信用卡担保，如 'visa', 'mastercard') */
    @Column(name = "card_type", length = 50)
    private String cardType;

    /** 最晚入住时刻 (冗余字段，部分逻辑使用) */
    @Column(name = "latest_check_in_time", length = 10)
    private String latestCheckInTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getGuaranteeSubType() { return guaranteeSubType; }
    public void setGuaranteeSubType(String guaranteeSubType) { this.guaranteeSubType = guaranteeSubType; }
    public String getGuaranteeAmount() { return guaranteeAmount; }
    public void setGuaranteeAmount(String guaranteeAmount) { this.guaranteeAmount = guaranteeAmount; }
    public String getLatestArrivalTime() { return latestArrivalTime; }
    public void setLatestArrivalTime(String latestArrivalTime) { this.latestArrivalTime = latestArrivalTime; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getGroupId() { return groupId; }
    public void setGroupId(Integer groupId) { this.groupId = groupId; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public Integer getIsDefault() { return isDefault; }
    public void setIsDefault(Integer isDefault) { this.isDefault = isDefault; }
    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }
    public String getLatestCheckInTime() { return latestCheckInTime; }
    public void setLatestCheckInTime(String latestCheckInTime) { this.latestCheckInTime = latestCheckInTime; }
}

