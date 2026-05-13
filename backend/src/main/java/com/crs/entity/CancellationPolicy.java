package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;

/**
 * 取消政策实体类 (CancellationPolicy)
 * 
 * <p>本类对应数据库中的 `cancellation_policies` 表，定义了预订在何种条件下可以免费取消，以及逾期取消时如何收取违约金。</p>
 * 
 * <p>关键业务逻辑：</p>
 * <ul>
 *     <li>**取消期限**：通过 `cancellationDays` (天数) 和 `cancellationTime` (具体时间点) 定义免费取消的截止时刻。</li>
 *     <li>**违约成本**：`cancellationFeeType` 定义了扣费模式（如：扣除首晚房费、扣除固定金额、全额扣除等）。</li>
 *     <li>**模板继承**：支持集团定义模板，酒店进行引用的管理模式。</li>
 * </ul>
 */



@Entity
@Table(name = "cancellation_policies")
public class CancellationPolicy {
    
    /** 政策主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 政策显示名称 (如 '限时免费取消', '不可取消') */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /** 政策唯一编码 */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
    
    /** 政策类型 (如 'flexible', 'non_refundable', 'guaranteed') */
    @Column(name = "type", nullable = false, length = 50)
    private String type;
    
    /** 提前取消的天数 (如 1 表示提前 1 天) */
    @Column(name = "cancellation_days")
    private Integer cancellationDays;
    
    /** 具体的取消截止时间 (格式如 '18:00') */
    @Column(name = "cancellation_time", length = 10)
    private String cancellationTime;
    
    /** 
     * 违约金扣费类型
     * 可选值：first_night(首晚房费), full_amount(全额房费), fixed_amount(固定金额), percentage(订单比例)
     */
    @Column(name = "cancellation_fee_type", length = 50)
    private String cancellationFeeType;
    
    /** 政策详细条款说明（展示给客人） */
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

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getCancellationDays() { return cancellationDays; }
    public void setCancellationDays(Integer cancellationDays) { this.cancellationDays = cancellationDays; }
    public String getCancellationTime() { return cancellationTime; }
    public void setCancellationTime(String cancellationTime) { this.cancellationTime = cancellationTime; }
    public String getCancellationFeeType() { return cancellationFeeType; }
    public void setCancellationFeeType(String cancellationFeeType) { this.cancellationFeeType = cancellationFeeType; }
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
}

