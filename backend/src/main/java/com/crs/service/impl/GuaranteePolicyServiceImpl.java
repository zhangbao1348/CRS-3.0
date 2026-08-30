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
        return guaranteePolicyRepository.findByIdAndTenantId(id, getCurrentTenantId());
    }
    
    @Override
    public List<GuaranteePolicy> getByGroupId(Integer groupId) {
        return guaranteePolicyRepository.findByGroupId(getCurrentTenantId());
    }
    
    @Override
    public GuaranteePolicy create(GuaranteePolicy policy) {
        Integer tenantId = getCurrentTenantId();
        validatePolicy(policy);
        // 检查代码是否已存在（同一租户下）
        if (guaranteePolicyRepository.existsByTenantIdAndCode(tenantId, policy.getCode())) {
            throw new IllegalArgumentException("该担保政策代码已存在");
        }
        policy.setId(null);
        policy.setTenantId(tenantId);
        policy.setGroupId(tenantId);
        policy.setType(GuaranteePolicyTypeUtil.normalizeType(policy.getType()));
        return guaranteePolicyRepository.save(policy);
    }
    
    @Override
    public GuaranteePolicy update(Integer id, GuaranteePolicy policy) {
        // 检查政策是否存在且属于当前租户
        GuaranteePolicy existingPolicy = getById(id)
                .orElseThrow(() -> new IllegalArgumentException("担保政策不存在或无权访问"));
        
        if (policy.getCode() != null && !existingPolicy.getCode().equals(policy.getCode())) {
            throw new IllegalArgumentException("担保政策代码保存后不可修改");
        }
        policy.setCode(existingPolicy.getCode());
        validatePolicy(policy);
        
        // 执行更新
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

    /** 验证担保类型及信用卡条件字段，并清除隐藏字段旧值。 */
    private void validatePolicy(GuaranteePolicy policy) {
        if (policy.getName() == null || policy.getName().isBlank()
                || policy.getCode() == null || policy.getCode().isBlank()) {
            throw new IllegalArgumentException("担保政策名称和代码为必填项");
        }
        if (!GuaranteePolicyTypeUtil.isSupportedType(policy.getType())) {
            throw new IllegalArgumentException("担保类型无效");
        }
        String normalizedType = GuaranteePolicyTypeUtil.normalizeType(policy.getType());
        if ("credit_card".equals(normalizedType)) {
            if (!("一律担保".equals(policy.getGuaranteeSubType()) || "超时担保".equals(policy.getGuaranteeSubType()))) {
                throw new IllegalArgumentException("请选择担保子类型");
            }
            if (!("首晚".equals(policy.getGuaranteeAmount()) || "全额".equals(policy.getGuaranteeAmount()))) {
                throw new IllegalArgumentException("请选择担保金额");
            }
            if ("超时担保".equals(policy.getGuaranteeSubType())
                    && (policy.getLatestArrivalTime() == null
                    || !policy.getLatestArrivalTime().matches("^([01]\\d|2[0-3]):[0-5]\\d$"))) {
                throw new IllegalArgumentException("请输入正确的 24 小时制时间，例如 18:00");
            }
        } else {
            policy.setGuaranteeSubType(null);
            policy.setGuaranteeAmount(null);
            policy.setLatestArrivalTime(null);
        }
        if (!"active".equals(policy.getStatus()) && !"inactive".equals(policy.getStatus())) {
            throw new IllegalArgumentException("担保政策状态无效");
        }
    }
}
