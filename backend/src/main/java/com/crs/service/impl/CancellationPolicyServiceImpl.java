package com.crs.service.impl;

import com.crs.entity.CancellationPolicy;
import com.crs.repository.CancellationPolicyRepository;
import com.crs.service.CancellationPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 取消政策服务实现类
 * 提供取消政策管理的业务逻辑处理
 */
@Service
@Transactional
public class CancellationPolicyServiceImpl implements CancellationPolicyService {
    
    @Autowired
    private CancellationPolicyRepository cancellationPolicyRepository;
    
    @Override
    public List<CancellationPolicy> getAllPolicies() {
        return cancellationPolicyRepository.findByTenantId(getCurrentTenantId());
    }
    
    @Override
    public Optional<CancellationPolicy> getById(Integer id) {
        Integer currentTenantId = getCurrentTenantId();
        return cancellationPolicyRepository.findById(id)
                .filter(p -> p.getTenantId() != null && p.getTenantId().equals(currentTenantId));
    }
    
    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    @Override
    public CancellationPolicy create(CancellationPolicy policy) {
        Integer actualTenantId = getCurrentTenantId();
        // 检查代码是否已存在（同一租户下）
        if (cancellationPolicyRepository.existsByTenantIdAndCode(actualTenantId, policy.getCode())) {
            throw new IllegalArgumentException("取消政策代码已存在");
        }
        policy.setTenantId(actualTenantId);
        return cancellationPolicyRepository.save(policy);
    }
    
    @Override
    public CancellationPolicy update(Integer id, CancellationPolicy policy) {
        // 检查政策是否存在且属于当前租户
        CancellationPolicy existingPolicy = getById(id)
                .orElseThrow(() -> new IllegalArgumentException("取消政策不存在或无权访问"));
        
        // 检查代码是否已存在（同一租户下，排除当前ID）
        if (policy.getCode() != null && !existingPolicy.getCode().equals(policy.getCode()) && 
            cancellationPolicyRepository.existsByTenantIdAndCode(existingPolicy.getTenantId(), policy.getCode())) {
            throw new IllegalArgumentException("取消政策代码已存在");
        }
        
        // 执行更新
        existingPolicy.setCode(policy.getCode());
        existingPolicy.setName(policy.getName());
        existingPolicy.setDescription(policy.getDescription());
        existingPolicy.setStatus(policy.getStatus());
        existingPolicy.setIsDefault(policy.getIsDefault());
        
        return cancellationPolicyRepository.save(existingPolicy);
    }
    
    @Override
    public void delete(Integer id) {
        CancellationPolicy existing = getById(id)
                .orElseThrow(() -> new IllegalArgumentException("取消政策不存在或无权访问"));
        cancellationPolicyRepository.delete(existing);
    }
}
