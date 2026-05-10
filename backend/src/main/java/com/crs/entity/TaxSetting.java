package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 税率设置实体类
 * 对应数据库tax_settings表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
