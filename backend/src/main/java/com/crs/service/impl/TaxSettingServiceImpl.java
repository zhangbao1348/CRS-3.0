package com.crs.service.impl;

import com.crs.entity.TaxSetting;
import com.crs.repository.TaxSettingRepository;
import com.crs.service.TaxSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 税率设置服务实现类
 * 用于税率设置的业务逻辑实现
 */
@Service
public class TaxSettingServiceImpl implements TaxSettingService {
    
    @Autowired
    private TaxSettingRepository taxSettingRepository;
    
    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    @Override
    public List<TaxSetting> getAllTaxSettings(Integer tenantId) {
        return taxSettingRepository.findByTenantId(getCurrentTenantId());
    }
    
    @Override
    public Optional<TaxSetting> getById(Integer tenantId, Integer id) {
        return taxSettingRepository.findById(id)
                .filter(ts -> ts.getTenantId() != null && ts.getTenantId().equals(getCurrentTenantId()));
    }
    
    @Override
    public TaxSetting getByTaxCode(Integer tenantId, String taxCode) {
        return taxSettingRepository.findByTenantIdAndTaxCode(getCurrentTenantId(), taxCode);
    }
    
    @Override
    public TaxSetting create(Integer tenantId, TaxSetting taxSetting) {
        Integer currentTenantId = getCurrentTenantId();
        if (!isTaxCodeUnique(currentTenantId, taxSetting.getTaxCode(), null)) {
            throw new IllegalArgumentException("税率编码已存在");
        }
        taxSetting.setTenantId(currentTenantId);
        return taxSettingRepository.save(taxSetting);
    }
    
    @Override
    public TaxSetting update(Integer tenantId, Integer id, TaxSetting taxSetting) {
        Integer currentTenantId = getCurrentTenantId();
        Optional<TaxSetting> existingOpt = getById(currentTenantId, id);
        if (!existingOpt.isPresent()) {
            throw new IllegalArgumentException("税率设置不存在或无权访问");
        }
        
        TaxSetting existing = existingOpt.get();
        
        if (!isTaxCodeUnique(currentTenantId, taxSetting.getTaxCode(), id)) {
            throw new IllegalArgumentException("税率编码已存在");
        }
        
        if (taxSetting.getTaxCode() != null) {
            existing.setTaxCode(taxSetting.getTaxCode());
        }
        if (taxSetting.getLegalName() != null) {
            existing.setLegalName(taxSetting.getLegalName());
        }
        if (taxSetting.getBearer() != null) {
            existing.setBearer(taxSetting.getBearer());
        }
        if (taxSetting.getBaseType() != null) {
            existing.setBaseType(taxSetting.getBaseType());
        }
        if (taxSetting.getRateAmount() != null) {
            existing.setRateAmount(taxSetting.getRateAmount());
        }
        if (taxSetting.getRateCurrency() != null) {
            existing.setRateCurrency(taxSetting.getRateCurrency());
        }
        if (taxSetting.getCalculationRule() != null) {
            existing.setCalculationRule(taxSetting.getCalculationRule());
        }
        if (taxSetting.getDeductible() != null) {
            existing.setDeductible(taxSetting.getDeductible());
        }
        if (taxSetting.getRefundable() != null) {
            existing.setRefundable(taxSetting.getRefundable());
        }
        if (taxSetting.getSettlementRule() != null) {
            existing.setSettlementRule(taxSetting.getSettlementRule());
        }
        if (taxSetting.getComplianceRequirements() != null) {
            existing.setComplianceRequirements(taxSetting.getComplianceRequirements());
        }
        if (taxSetting.getRemarks() != null) {
            existing.setRemarks(taxSetting.getRemarks());
        }
        if (taxSetting.getStatus() != null) {
            existing.setStatus(taxSetting.getStatus());
        }
        
        return taxSettingRepository.save(existing);
    }
    
    @Override
    public void delete(Integer tenantId, Integer id) {
        Optional<TaxSetting> taxSetting = getById(getCurrentTenantId(), id);
        if (!taxSetting.isPresent()) {
            throw new IllegalArgumentException("税率设置不存在或无权访问");
        }
        taxSettingRepository.delete(taxSetting.get());
    }
    
    @Override
    public boolean isTaxCodeUnique(Integer tenantId, String taxCode, Integer excludeId) {
        TaxSetting existing = taxSettingRepository.findByTenantIdAndTaxCode(getCurrentTenantId(), taxCode);
        return existing == null || (excludeId != null && existing.getId().equals(excludeId));
    }
    
    @Override
    @Transactional
    public List<TaxSetting> batchCreateTaxSettings(Integer tenantId, List<TaxSetting> taxSettings) {
        List<TaxSetting> savedTaxSettings = new ArrayList<>();
        Integer currentTenantId = getCurrentTenantId();
        for (TaxSetting taxSetting : taxSettings) {
            taxSetting.setTenantId(currentTenantId);
            savedTaxSettings.add(taxSettingRepository.save(taxSetting));
        }
        return savedTaxSettings;
    }
    
    @Override
    @Transactional
    public List<TaxSetting> initDefaultTaxSettingsForTenant(Integer tenantId) {
        List<TaxSetting> defaultTaxSettings = new ArrayList<>();
        Integer currentTenantId = getCurrentTenantId();
        
        TaxSetting vatCn = new TaxSetting();
        vatCn.setTenantId(currentTenantId);
        vatCn.setTaxCode("VAT-CN-001");
        vatCn.setLegalName("中国增值税(VAT)");
        vatCn.setBearer("guest");
        vatCn.setBaseType("net_excluding_service");
        vatCn.setRateAmount(new BigDecimal("6"));
        vatCn.setRateCurrency("%");
        vatCn.setCalculationRule("inclusive");
        vatCn.setDeductible("yes");
        vatCn.setRefundable("full");
        vatCn.setSettlementRule("checkin");
        vatCn.setComplianceRequirements("欧盟 VAT 需按季度申报，留存订单明细10年");
        vatCn.setRemarks("东京都宿泊税费1万日元以上免征，美国纽约长租30天以上免征");
        vatCn.setStatus("active");
        defaultTaxSettings.add(taxSettingRepository.save(vatCn));
        
        TaxSetting cityTaxFr = new TaxSetting();
        cityTaxFr.setTenantId(currentTenantId);
        cityTaxFr.setTaxCode("CITYTAX-FR-PAR-001");
        cityTaxFr.setLegalName("法国巴黎城市税");
        cityTaxFr.setBearer("guest");
        cityTaxFr.setBaseType("per_person_room");
        cityTaxFr.setRateAmount(new BigDecimal("4"));
        cityTaxFr.setRateCurrency("EUR");
        cityTaxFr.setCalculationRule("exclusive");
        cityTaxFr.setDeductible("no");
        cityTaxFr.setRefundable("none");
        cityTaxFr.setSettlementRule("prepaid");
        cityTaxFr.setComplianceRequirements("日本宿泊税确认后不可退");
        cityTaxFr.setRemarks("法国巴黎城市税，按人头每晚4欧元");
        cityTaxFr.setStatus("active");
        defaultTaxSettings.add(taxSettingRepository.save(cityTaxFr));
        
        TaxSetting tourismJp = new TaxSetting();
        tourismJp.setTenantId(currentTenantId);
        tourismJp.setTaxCode("TOURISM-JP-TKY-001");
        tourismJp.setLegalName("日本东京都宿泊税");
        tourismJp.setBearer("guest");
        tourismJp.setBaseType("per_person_room");
        tourismJp.setRateAmount(new BigDecimal("200"));
        tourismJp.setRateCurrency("CNY");
        tourismJp.setCalculationRule("exclusive");
        tourismJp.setDeductible("no");
        tourismJp.setRefundable("partial");
        tourismJp.setSettlementRule("checkin");
        tourismJp.setComplianceRequirements("1万日元以上免征");
        tourismJp.setRemarks("日本东京都宿泊税，1万日元以上免征");
        tourismJp.setStatus("active");
        defaultTaxSettings.add(taxSettingRepository.save(tourismJp));
        
        TaxSetting vatDe = new TaxSetting();
        vatDe.setTenantId(currentTenantId);
        vatDe.setTaxCode("VAT-DE-001");
        vatDe.setLegalName("德国增值税(VAT)");
        vatDe.setBearer("hotel");
        vatDe.setBaseType("including_service");
        vatDe.setRateAmount(new BigDecimal("19"));
        vatDe.setRateCurrency("%");
        vatDe.setCalculationRule("inclusive");
        vatDe.setDeductible("yes");
        vatDe.setRefundable("full");
        vatDe.setSettlementRule("self_report");
        vatDe.setComplianceRequirements("德国VAT按月申报");
        vatDe.setRemarks("德国增值税，标准税率19%");
        vatDe.setStatus("active");
        defaultTaxSettings.add(taxSettingRepository.save(vatDe));
        
        TaxSetting taxUsNy = new TaxSetting();
        taxUsNy.setTenantId(currentTenantId);
        taxUsNy.setTaxCode("TAX-US-NYC-001");
        taxUsNy.setLegalName("美国纽约市酒店税");
        taxUsNy.setBearer("guest");
        taxUsNy.setBaseType("order_total");
        taxUsNy.setRateAmount(new BigDecimal("5.875"));
        taxUsNy.setRateCurrency("%");
        taxUsNy.setCalculationRule("exclusive");
        taxUsNy.setDeductible("no");
        taxUsNy.setRefundable("full");
        taxUsNy.setSettlementRule("prepaid");
        taxUsNy.setComplianceRequirements("美国纽约长租30天以上免征");
        taxUsNy.setRemarks("美国纽约市酒店税，长租30天以上免征");
        taxUsNy.setStatus("inactive");
        defaultTaxSettings.add(taxSettingRepository.save(taxUsNy));
        
        return defaultTaxSettings;
    }
}
