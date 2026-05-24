package com.crs.service.impl;

import com.crs.entity.GuaranteePolicy;
import com.crs.repository.GuaranteePolicyRepository;
import com.crs.service.GuaranteePolicyService;
import com.crs.util.GuaranteePolicyTypeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 担保政策服务实现类
 * 提供担保政策管理的业务逻辑处理
 */
@Service
@Transactional
public class GuaranteePolicyServiceImpl implements GuaranteePolicyService {
    
    @Autowired
    private GuaranteePolicyRepository guaranteePolicyRepository;
    
    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    @Override
    public List<GuaranteePolicy> getAllPolicies() {
        return guaranteePolicyRepository.findByTenantId(getCurrentTenantId());
    }
    
    @Override
    public List<GuaranteePolicy> getByTenantId(Integer tenantId) {
        return guaranteePolicyRepository.findByTenantId(getCurrentTenantId());
    }
    
    @Override
    public Optional<GuaranteePolicy> getById(Integer id) {
        return guaranteePolicyRepository.findById(id)
                .filter(p -> p.getTenantId() != null && p.getTenantId().equals(getCurrentTenantId()));
    }
    
    @Override
    public List<GuaranteePolicy> getByGroupId(Integer groupId) {
        return guaranteePolicyRepository.findByGroupId(getCurrentTenantId());
    }
    
    @Override
    public GuaranteePolicy create(GuaranteePolicy policy) {
        Integer tenantId = getCurrentTenantId();
        if (!GuaranteePolicyTypeUtil.isSupportedType(policy.getType())) {
            throw new IllegalArgumentException("担保类型无效");
        }
        // 检查代码是否已存在（同一租户下）
        if (guaranteePolicyRepository.existsByTenantIdAndCode(tenantId, policy.getCode())) {
            throw new IllegalArgumentException("担保政策代码已存在");
        }
        policy.setTenantId(tenantId);
        policy.setType(GuaranteePolicyTypeUtil.normalizeType(policy.getType()));
        return guaranteePolicyRepository.save(policy);
    }
    
    @Override
    public GuaranteePolicy update(Integer id, GuaranteePolicy policy) {
        // 检查政策是否存在且属于当前租户
        GuaranteePolicy existingPolicy = getById(id)
                .orElseThrow(() -> new IllegalArgumentException("担保政策不存在或无权访问"));
        
        // 检查代码是否已存在（同一租户下，排除当前ID）
        if (!existingPolicy.getCode().equals(policy.getCode()) && 
            guaranteePolicyRepository.existsByTenantIdAndCode(existingPolicy.getTenantId(), policy.getCode())) {
            throw new IllegalArgumentException("担保政策代码已存在");
        }
        if (!GuaranteePolicyTypeUtil.isSupportedType(policy.getType())) {
            throw new IllegalArgumentException("担保类型无效");
        }
        
        // 执行更新
        existingPolicy.setCode(policy.getCode());
        existingPolicy.setName(policy.getName());
        existingPolicy.setDescription(policy.getDescription());
        existingPolicy.setStatus(policy.getStatus());
        existingPolicy.setIsDefault(policy.getIsDefault());
        existingPolicy.setType(GuaranteePolicyTypeUtil.normalizeType(policy.getType()));
        existingPolicy.setCardType(policy.getCardType());
        existingPolicy.setGuaranteeSubType(policy.getGuaranteeSubType());
        existingPolicy.setGuaranteeAmount(policy.getGuaranteeAmount());
        existingPolicy.setLatestArrivalTime(policy.getLatestArrivalTime());
        
        return guaranteePolicyRepository.save(existingPolicy);
    }
    
    @Override
    public void delete(Integer id) {
        GuaranteePolicy existing = getById(id)
                .orElseThrow(() -> new IllegalArgumentException("担保政策不存在或无权访问"));
        guaranteePolicyRepository.delete(existing);
    }
}
