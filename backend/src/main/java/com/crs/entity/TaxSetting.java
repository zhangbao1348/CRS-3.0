package com.crs.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 税率设置实体类
 * 对应数据库tax_settings表
 */



@Entity
@Table(name = "tax_settings")
public class TaxSetting {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;
    
    @Column(name = "tax_code", nullable = false, unique = true, length = 50)
    private String taxCode;
    
    @Column(name = "legal_name", nullable = false, length = 100)
    private String legalName;
    
    @Column(name = "bearer", length = 20)
    private String bearer;
    
    @Column(name = "base_type", length = 50)
    private String baseType;
    
    @Column(name = "rate_amount", precision = 10, scale = 4)
    private BigDecimal rateAmount;
    
    @Column(name = "rate_currency", length = 10)
    private String rateCurrency;
    
    @Column(name = "calculation_rule", length = 20)
    private String calculationRule;
    
    @Column(name = "deductible", length = 10)
    private String deductible;
    
    @Column(name = "refundable", length = 20)
    private String refundable;
    
    @Column(name = "settlement_rule", length = 50)
    private String settlementRule;
    
    @Column(name = "compliance_requirements", columnDefinition = "TEXT")
    private String complianceRequirements;
    
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
    
    @Column(name = "status", length = 20)
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

    // 手动补全 Getter/Setter 解决 Lombok 兼容性问题
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }
    public String getLegalName() { return legalName; }
    public void setLegalName(String legalName) { this.legalName = legalName; }
    public String getBearer() { return bearer; }
    public void setBearer(String bearer) { this.bearer = bearer; }
    public String getBaseType() { return baseType; }
    public void setBaseType(String baseType) { this.baseType = baseType; }
    public BigDecimal getRateAmount() { return rateAmount; }
    public void setRateAmount(BigDecimal rateAmount) { this.rateAmount = rateAmount; }
    public String getRateCurrency() { return rateCurrency; }
    public void setRateCurrency(String rateCurrency) { this.rateCurrency = rateCurrency; }
    public String getCalculationRule() { return calculationRule; }
    public void setCalculationRule(String calculationRule) { this.calculationRule = calculationRule; }
    public String getDeductible() { return deductible; }
    public void setDeductible(String deductible) { this.deductible = deductible; }
    public String getRefundable() { return refundable; }
    public void setRefundable(String refundable) { this.refundable = refundable; }
    public String getSettlementRule() { return settlementRule; }
    public void setSettlementRule(String settlementRule) { this.settlementRule = settlementRule; }
    public String getComplianceRequirements() { return complianceRequirements; }
    public void setComplianceRequirements(String complianceRequirements) { this.complianceRequirements = complianceRequirements; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
