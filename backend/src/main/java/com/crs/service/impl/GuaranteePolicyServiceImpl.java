package com.crs.service.impl;

import com.crs.entity.GuaranteePolicy;
import com.crs.repository.GuaranteePolicyRepository;
import com.crs.service.GuaranteePolicyService;
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
    
    @Override
    public List<GuaranteePolicy> getAllPolicies() {
        return guaranteePolicyRepository.findAll();
    }
    
    @Override
    public List<GuaranteePolicy> getByTenantId(Integer tenantId) {
        return guaranteePolicyRepository.findByTenantId(tenantId);
    }
    
    @Override
    public Optional<GuaranteePolicy> getById(Integer id) {
        return guaranteePolicyRepository.findById(id);
    }
    
    @Override
    public List<GuaranteePolicy> getByGroupId(Integer groupId) {
        return guaranteePolicyRepository.findByGroupId(groupId);
    }
    
    @Override
    public GuaranteePolicy create(Integer tenantId, GuaranteePolicy policy) {
        // 检查代码是否已存在（同一租户下）
        if (guaranteePolicyRepository.existsByTenantIdAndCode(tenantId, policy.getCode())) {
            throw new IllegalArgumentException("担保政策代码已存在");
        }
        policy.setTenantId(tenantId);
        return guaranteePolicyRepository.save(policy);
    }
    
    @Override
    public GuaranteePolicy update(Integer id, GuaranteePolicy policy) {
        // 检查政策是否存在
        Optional<GuaranteePolicy> existingPolicyOpt = guaranteePolicyRepository.findById(id);
        if (!existingPolicyOpt.isPresent()) {
            throw new IllegalArgumentException("担保政策不存在");
        }
        
        GuaranteePolicy existingPolicy = existingPolicyOpt.get();
        
        // 检查代码是否已存在（同一租户下，排除当前ID）
        if (!existingPolicy.getCode().equals(policy.getCode()) && 
            guaranteePolicyRepository.existsByTenantIdAndCode(existingPolicy.getTenantId(), policy.getCode())) {
            throw new IllegalArgumentException("担保政策代码已存在");
        }
        
        // 保留租户ID
        policy.setId(id);
        policy.setTenantId(existingPolicy.getTenantId());
        policy.setCreatedAt(existingPolicy.getCreatedAt());
        
        return guaranteePolicyRepository.save(policy);
    }
    
    @Override
    public void delete(Integer id) {
        if (!guaranteePolicyRepository.existsById(id)) {
            throw new IllegalArgumentException("担保政策不存在");
        }
        guaranteePolicyRepository.deleteById(id);
    }
}
