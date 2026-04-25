package com.crs.service;

import com.crs.entity.CancellationPolicy;

import java.util.List;
import java.util.Optional;

/**
 * 取消政策服务接口
 * 用于取消政策的业务逻辑处理
 */
public interface CancellationPolicyService {
    
    /**
     * 获取所有取消政策
     * @return 取消政策列表
     */
    List<CancellationPolicy> getAllPolicies();
    
    /**
     * 根据租户ID获取取消政策
     * @param tenantId 租户ID
     * @return 取消政策列表
     */
    List<CancellationPolicy> getByTenantId(Integer tenantId);
    
    /**
     * 根据ID获取取消政策
     * @param id 政策ID
     * @return 取消政策
     */
    Optional<CancellationPolicy> getById(Integer id);
    
    /**
     * 根据集团ID获取取消政策
     * @param groupId 集团ID
     * @return 取消政策列表
     */
    List<CancellationPolicy> getByGroupId(Integer groupId);
    
    /**
     * 创建取消政策
     * @param tenantId 租户ID
     * @param policy 取消政策
     * @return 创建的取消政策
     */
    CancellationPolicy create(Integer tenantId, CancellationPolicy policy);
    
    /**
     * 更新取消政策
     * @param id 政策ID
     * @param policy 取消政策
     * @return 更新后的取消政策
     */
    CancellationPolicy update(Integer id, CancellationPolicy policy);
    
    /**
     * 删除取消政策
     * @param id 政策ID
     */
    void delete(Integer id);
}
