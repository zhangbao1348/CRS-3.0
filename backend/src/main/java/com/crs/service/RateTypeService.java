package com.crs.service;

import com.crs.entity.RateType;
import com.crs.repository.RateTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * RateTypeService 服务接口 (Service Interface)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【RateTypeService】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/10-价格计划管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 RateTypeService 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Service
public class RateTypeService {
    
    @Autowired
    private RateTypeRepository rateTypeRepository;
    
    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    public List<RateType> getAllRateTypes(Integer tenantId) {
        return rateTypeRepository.findByTenantId(getCurrentTenantId());
    }
    
    public RateType getRateTypeById(Integer tenantId, Integer id) {
        return rateTypeRepository.findByIdAndTenantId(id, getCurrentTenantId()).orElse(null);
    }
    
    public RateType getRateTypeByCode(Integer tenantId, String code) {
        return rateTypeRepository.findByTenantIdAndCode(getCurrentTenantId(), code);
    }
    
    public List<RateType> getActiveRateTypes(Integer tenantId) {
        return rateTypeRepository.findByTenantIdAndStatus(getCurrentTenantId(), RateType.Status.active);
    }
    
    public RateType createRateType(Integer tenantId, RateType rateType) {
        rateType.setTenantId(getCurrentTenantId());
        return rateTypeRepository.save(rateType);
    }
    
    public RateType updateRateType(Integer tenantId, RateType rateType) {
        Integer currentTenantId = getCurrentTenantId();
        RateType existing = getRateTypeById(currentTenantId, rateType.getId());
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
        RateType existing = getRateTypeById(getCurrentTenantId(), id);
        if (existing != null) {
            rateTypeRepository.delete(existing);
        }
    }
    
    public boolean isCodeUnique(Integer tenantId, String code, Integer excludeId) {
        RateType existing = rateTypeRepository.findByTenantIdAndCode(getCurrentTenantId(), code);
        return existing == null || (excludeId != null && existing.getId().equals(excludeId));
    }

    @Transactional
    public List<RateType> batchCreateRateTypes(Integer tenantId, List<RateType> rateTypes) {
        Integer currentTenantId = getCurrentTenantId();
        List<RateType> savedRateTypes = new ArrayList<>();
        for (RateType rateType : rateTypes) {
            rateType.setTenantId(currentTenantId);
            savedRateTypes.add(rateTypeRepository.save(rateType));
        }
        return savedRateTypes;
    }

    @Transactional
    public List<RateType> initDefaultRateTypesForTenant(Integer tenantId) {
        Integer currentTenantId = getCurrentTenantId();
        List<RateType> defaultRateTypes = new ArrayList<>();
        
        RateType bar = new RateType();
        bar.setTenantId(currentTenantId);
        bar.setCode("BAR");
        bar.setName("最佳可用房价");
        bar.setDescription("酒店的标准房价，适用于所有客人");
        bar.setSortOrder(1);
        bar.setStatus(RateType.Status.active);
        defaultRateTypes.add(rateTypeRepository.save(bar));
        
        RateType corp = new RateType();
        corp.setTenantId(currentTenantId);
        corp.setCode("CORP");
        corp.setName("企业协议价");
        corp.setDescription("与企业客户签订的协议价格");
        corp.setSortOrder(2);
        corp.setStatus(RateType.Status.active);
        defaultRateTypes.add(rateTypeRepository.save(corp));
        
        RateType promo = new RateType();
        promo.setTenantId(currentTenantId);
        promo.setCode("PROMO");
        promo.setName("促销价");
        promo.setDescription("特别促销活动价格");
        promo.setSortOrder(3);
        promo.setStatus(RateType.Status.active);
        defaultRateTypes.add(rateTypeRepository.save(promo));
        
        RateType group = new RateType();
        group.setTenantId(currentTenantId);
        group.setCode("GROUP");
        group.setName("团队价");
        group.setDescription("适用于团队预订的价格");
        group.setSortOrder(4);
        group.setStatus(RateType.Status.active);
        defaultRateTypes.add(rateTypeRepository.save(group));
        
        RateType packageRate = new RateType();
        packageRate.setTenantId(currentTenantId);
        packageRate.setCode("PACKAGE");
        packageRate.setName("包价");
        packageRate.setDescription("包含额外服务的套餐价格");
        packageRate.setSortOrder(5);
        packageRate.setStatus(RateType.Status.active);
        defaultRateTypes.add(rateTypeRepository.save(packageRate));
        
        RateType longstay = new RateType();
        longstay.setTenantId(currentTenantId);
        longstay.setCode("LONGSTAY");
        longstay.setName("长住价");
        longstay.setDescription("适用于长期住宿客人的优惠价格");
        longstay.setSortOrder(6);
        longstay.setStatus(RateType.Status.active);
        defaultRateTypes.add(rateTypeRepository.save(longstay));
        
        return defaultRateTypes;
    }
}
