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
        return taxSettingRepository.findByIdAndTenantId(id, getCurrentTenantId());
    }
    
    @Override
    public TaxSetting getByTaxCode(Integer tenantId, String taxCode) {
        return taxSettingRepository.findByTenantIdAndTaxCode(getCurrentTenantId(), taxCode);
    }
    
    @Override
    public TaxSetting create(Integer tenantId, TaxSetting taxSetting) {
        Integer currentTenantId = getCurrentTenantId();
        validateTaxSetting(taxSetting);
        if (!isTaxCodeUnique(currentTenantId, taxSetting.getTaxCode(), null)) {
            throw new IllegalArgumentException("该税率CODE已存在");
        }
        taxSetting.setId(null);
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

        if (taxSetting.getTaxCode() != null && !existing.getTaxCode().equals(taxSetting.getTaxCode())) {
            throw new IllegalArgumentException("税率编码保存后不可修改");
        }
        taxSetting.setTaxCode(existing.getTaxCode());
        validateTaxSetting(taxSetting);
        if (taxSetting.getLegalName() != null) {
            existing.setLegalName(taxSetting.getLegalName());
        }
        if (taxSetting.getRateAmount() != null) {
            existing.setRateAmount(taxSetting.getRateAmount());
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

    /** 与页面规则一致的服务端税率完整性校验。 */
    private void validateTaxSetting(TaxSetting taxSetting) {
        if (taxSetting.getTaxCode() == null || taxSetting.getTaxCode().isBlank()) {
            throw new IllegalArgumentException("税率CODE不能为空");
        }
        if (taxSetting.getLegalName() == null || taxSetting.getLegalName().isBlank()) {
            throw new IllegalArgumentException("税率名称不能为空");
        }
        if (taxSetting.getRateAmount() == null
                || taxSetting.getRateAmount().compareTo(BigDecimal.ZERO) < 0
                || taxSetting.getRateAmount().compareTo(new BigDecimal("100")) > 0
                || taxSetting.getRateAmount().scale() > 2) {
            throw new IllegalArgumentException("税率必须在 0% 到 100% 之间，最多保留两位小数");
        }
        if (!"active".equals(taxSetting.getStatus()) && !"inactive".equals(taxSetting.getStatus())) {
            throw new IllegalArgumentException("税率状态无效");
        }
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
        vatCn.setRateAmount(new BigDecimal("6"));
        vatCn.setStatus("active");
        defaultTaxSettings.add(taxSettingRepository.save(vatCn));
        
        TaxSetting serviceFee = new TaxSetting();
        serviceFee.setTenantId(currentTenantId);
        serviceFee.setTaxCode("SERVICE-CN-001");
        serviceFee.setLegalName("服务费");
        serviceFee.setRateAmount(new BigDecimal("10"));
        serviceFee.setStatus("active");
        defaultTaxSettings.add(taxSettingRepository.save(serviceFee));
        
        return defaultTaxSettings;
    }
}
