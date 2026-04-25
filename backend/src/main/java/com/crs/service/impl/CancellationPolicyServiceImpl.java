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
        return cancellationPolicyRepository.findAll();
    }
    
    @Override
    public List<CancellationPolicy> getByTenantId(Integer tenantId) {
        return cancellationPolicyRepository.findByTenantId(tenantId);
    }
    
    @Override
    public Optional<CancellationPolicy> getById(Integer id) {
        return cancellationPolicyRepository.findById(id);
    }
    
    @Override
    public List<CancellationPolicy> getByGroupId(Integer groupId) {
        return cancellationPolicyRepository.findByGroupId(groupId);
    }
    
    @Override
    public CancellationPolicy create(Integer tenantId, CancellationPolicy policy) {
        // 检查代码是否已存在（同一租户下）
        if (cancellationPolicyRepository.existsByTenantIdAndCode(tenantId, policy.getCode())) {
            throw new IllegalArgumentException("取消政策代码已存在");
        }
        policy.setTenantId(tenantId);
        return cancellationPolicyRepository.save(policy);
    }
    
    @Override
    public CancellationPolicy update(Integer id, CancellationPolicy policy) {
        // 检查政策是否存在
        Optional<CancellationPolicy> existingPolicyOpt = cancellationPolicyRepository.findById(id);
        if (!existingPolicyOpt.isPresent()) {
            throw new IllegalArgumentException("取消政策不存在");
        }
        
        CancellationPolicy existingPolicy = existingPolicyOpt.get();
        
        // 检查代码是否已存在（同一租户下，排除当前ID）
        if (!existingPolicy.getCode().equals(policy.getCode()) && 
            cancellationPolicyRepository.existsByTenantIdAndCode(existingPolicy.getTenantId(), policy.getCode())) {
            throw new IllegalArgumentException("取消政策代码已存在");
        }
        
        // 保留租户ID
        policy.setId(id);
        policy.setTenantId(existingPolicy.getTenantId());
        policy.setCreatedAt(existingPolicy.getCreatedAt());
        
        return cancellationPolicyRepository.save(policy);
    }
    
    @Override
    public void delete(Integer id) {
        if (!cancellationPolicyRepository.existsById(id)) {
            throw new IllegalArgumentException("取消政策不存在");
        }
        cancellationPolicyRepository.deleteById(id);
    }
}
