package com.crs.service.impl;

import com.crs.entity.CancellationPolicy;
import com.crs.repository.CancellationPolicyRepository;
import com.crs.repository.GroupRateCodeRepository;
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
    
    private final CancellationPolicyRepository cancellationPolicyRepository;
    private final GroupRateCodeRepository groupRateCodeRepository;

    @Autowired
    public CancellationPolicyServiceImpl(CancellationPolicyRepository cancellationPolicyRepository,
                                         GroupRateCodeRepository groupRateCodeRepository) {
        this.cancellationPolicyRepository = cancellationPolicyRepository;
        this.groupRateCodeRepository = groupRateCodeRepository;
    }
    
    @Override
    public List<CancellationPolicy> getAllPolicies() {
        return cancellationPolicyRepository.findByTenantId(getCurrentTenantId());
    }
    
    @Override
    public Optional<CancellationPolicy> getById(Integer id) {
        return cancellationPolicyRepository.findByIdAndTenantId(id, getCurrentTenantId());
    }

    @Override
    public Optional<CancellationPolicy> getByCode(String code) {
        return Optional.ofNullable(cancellationPolicyRepository.findByTenantIdAndCode(getCurrentTenantId(), code));
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
        validatePolicy(policy);
        // 检查代码是否已存在（同一租户下）
        if (cancellationPolicyRepository.existsByTenantIdAndCode(actualTenantId, policy.getCode())) {
            throw new IllegalArgumentException("该取消政策代码已存在");
        }
        policy.setId(null);
        policy.setTenantId(actualTenantId);
        policy.setGroupId(actualTenantId);
        return cancellationPolicyRepository.save(policy);
    }
    
    @Override
    public CancellationPolicy update(Integer id, CancellationPolicy policy) {
        // 检查政策是否存在且属于当前租户
        CancellationPolicy existingPolicy = getById(id)
                .orElseThrow(() -> new IllegalArgumentException("取消政策不存在或无权访问"));
        
        if (policy.getCode() != null && !existingPolicy.getCode().equals(policy.getCode())) {
            throw new IllegalArgumentException("取消政策代码保存后不可修改");
        }
        policy.setCode(existingPolicy.getCode());
        validatePolicy(policy);
        
        // 执行更新
        existingPolicy.setName(policy.getName());
        existingPolicy.setType(policy.getType());
        existingPolicy.setCancellationDays(policy.getCancellationDays());
        existingPolicy.setCancellationTime(policy.getCancellationTime());
        existingPolicy.setCancellationFeeType(policy.getCancellationFeeType());
        existingPolicy.setDescription(policy.getDescription());
        existingPolicy.setStatus(policy.getStatus());
        existingPolicy.setIsDefault(policy.getIsDefault());
        
        return cancellationPolicyRepository.save(existingPolicy);
    }
    
    @Override
    public void delete(Integer id) {
        CancellationPolicy existing = getById(id)
                .orElseThrow(() -> new IllegalArgumentException("取消政策不存在或无权访问"));
        long refCount = groupRateCodeRepository.countByGroupIdAndCancellationRule(
                getCurrentTenantId(), existing.getCode());
        if (refCount > 0) {
            throw new IllegalArgumentException("该取消政策已被 " + refCount + " 个房价码引用，无法删除");
        }
        cancellationPolicyRepository.delete(existing);
    }

    /** 校验取消规则并在类型切换后清除不再适用的条件字段。 */
    private void validatePolicy(CancellationPolicy policy) {
        if (policy.getName() == null || policy.getName().isBlank()
                || policy.getCode() == null || policy.getCode().isBlank()) {
            throw new IllegalArgumentException("取消政策名称和代码为必填项");
        }
        if (!("免费取消".equals(policy.getType()) || "限时扣费".equals(policy.getType())
                || "不可取消".equals(policy.getType()))) {
            throw new IllegalArgumentException("取消类型无效");
        }
        if ("限时扣费".equals(policy.getType())) {
            if (policy.getCancellationDays() == null || policy.getCancellationDays() < 1) {
                throw new IllegalArgumentException("提前天数必须是大于 0 的整数");
            }
            if (policy.getCancellationTime() == null
                    || !policy.getCancellationTime().matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {
                throw new IllegalArgumentException("取消截止时间必须使用HH:mm格式");
            }
            if (!("首晚".equals(policy.getCancellationFeeType())
                    || "全额房费".equals(policy.getCancellationFeeType()))) {
                throw new IllegalArgumentException("请选择扣费类型");
            }
        } else {
            policy.setCancellationDays(null);
            policy.setCancellationTime(null);
            policy.setCancellationFeeType(null);
        }
        if (!"active".equals(policy.getStatus()) && !"inactive".equals(policy.getStatus())) {
            throw new IllegalArgumentException("取消政策状态无效");
        }
    }
}
