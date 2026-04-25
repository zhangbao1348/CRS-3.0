package com.crs.service;

import com.crs.entity.RateType;
import com.crs.repository.RateTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RateTypeService {
    
    @Autowired
    private RateTypeRepository rateTypeRepository;
    
    public List<RateType> getAllRateTypes(Integer tenantId) {
        return rateTypeRepository.findByTenantId(tenantId);
    }
    
    public RateType getRateTypeById(Integer tenantId, Integer id) {
        RateType rateType = rateTypeRepository.findById(id).orElse(null);
        if (rateType != null && rateType.getTenantId() != null && rateType.getTenantId().equals(tenantId)) {
            return rateType;
        }
        return null;
    }
    
    public RateType getRateTypeByCode(Integer tenantId, String code) {
        return rateTypeRepository.findByTenantIdAndCode(tenantId, code);
    }
    
    public List<RateType> getActiveRateTypes(Integer tenantId) {
        return rateTypeRepository.findByTenantIdAndStatus(tenantId, RateType.Status.active);
    }
    
    public RateType createRateType(Integer tenantId, RateType rateType) {
        rateType.setTenantId(tenantId);
        return rateTypeRepository.save(rateType);
    }
    
    public RateType updateRateType(Integer tenantId, RateType rateType) {
        RateType existing = getRateTypeById(tenantId, rateType.getId());
        if (existing != null) {
            if (rateType.getCode() != null) {
                existing.setCode(rateType.getCode());
            }
            if (rateType.getName() != null) {
                existing.setName(rateType.getName());
            }
            if (rateType.getDescription() != null) {
                existing.setDescription(rateType.getDescription());
            }
            if (rateType.getSortOrder() != null) {
                existing.setSortOrder(rateType.getSortOrder());
            }
            if (rateType.getStatus() != null) {
                existing.setStatus(rateType.getStatus());
            }
            return rateTypeRepository.save(existing);
        }
        return null;
    }
    
    public void deleteRateType(Integer tenantId, Integer id) {
        RateType existing = getRateTypeById(tenantId, id);
        if (existing != null) {
            rateTypeRepository.deleteById(id);
        }
    }
    
    public boolean isCodeUnique(Integer tenantId, String code, Integer excludeId) {
        try {
            RateType existing = rateTypeRepository.findByTenantIdAndCode(tenantId, code);
            return existing == null || (excludeId != null && existing.getId().equals(excludeId));
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Transactional
    public List<RateType> batchCreateRateTypes(Integer tenantId, List<RateType> rateTypes) {
        List<RateType> savedRateTypes = new ArrayList<>();
        for (RateType rateType : rateTypes) {
            rateType.setTenantId(tenantId);
            savedRateTypes.add(rateTypeRepository.save(rateType));
        }
        return savedRateTypes;
    }

    @Transactional
    public List<RateType> initDefaultRateTypesForTenant(Integer tenantId) {
        List<RateType> defaultRateTypes = new ArrayList<>();
        
        RateType bar = new RateType();
        bar.setTenantId(tenantId);
        bar.setCode("BAR");
        bar.setName("最佳可用房价");
        bar.setDescription("酒店的标准房价，适用于所有客人");
        bar.setSortOrder(1);
        bar.setStatus(RateType.Status.active);
        defaultRateTypes.add(rateTypeRepository.save(bar));
        
        RateType corp = new RateType();
        corp.setTenantId(tenantId);
        corp.setCode("CORP");
        corp.setName("企业协议价");
        corp.setDescription("与企业客户签订的协议价格");
        corp.setSortOrder(2);
        corp.setStatus(RateType.Status.active);
        defaultRateTypes.add(rateTypeRepository.save(corp));
        
        RateType promo = new RateType();
        promo.setTenantId(tenantId);
        promo.setCode("PROMO");
        promo.setName("促销价");
        promo.setDescription("特别促销活动价格");
        promo.setSortOrder(3);
        promo.setStatus(RateType.Status.active);
        defaultRateTypes.add(rateTypeRepository.save(promo));
        
        RateType group = new RateType();
        group.setTenantId(tenantId);
        group.setCode("GROUP");
        group.setName("团队价");
        group.setDescription("适用于团队预订的价格");
        group.setSortOrder(4);
        group.setStatus(RateType.Status.active);
        defaultRateTypes.add(rateTypeRepository.save(group));
        
        RateType packageRate = new RateType();
        packageRate.setTenantId(tenantId);
        packageRate.setCode("PACKAGE");
        packageRate.setName("包价");
        packageRate.setDescription("包含额外服务的套餐价格");
        packageRate.setSortOrder(5);
        packageRate.setStatus(RateType.Status.active);
        defaultRateTypes.add(rateTypeRepository.save(packageRate));
        
        RateType longstay = new RateType();
        longstay.setTenantId(tenantId);
        longstay.setCode("LONGSTAY");
        longstay.setName("长住价");
        longstay.setDescription("适用于长期住宿客人的优惠价格");
        longstay.setSortOrder(6);
        longstay.setStatus(RateType.Status.active);
        defaultRateTypes.add(rateTypeRepository.save(longstay));
        
        return defaultRateTypes;
    }
}
