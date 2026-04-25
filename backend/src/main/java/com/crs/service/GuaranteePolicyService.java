package com.crs.service;

import com.crs.entity.GuaranteePolicy;

import java.util.List;
import java.util.Optional;

/**
 * 担保政策服务接口
 * 用于担保政策的业务逻辑处理
 */
public interface GuaranteePolicyService {
    
    /**
     * 获取所有担保政策
     * @return 担保政策列表
     */
    List<GuaranteePolicy> getAllPolicies();
    
    /**
     * 根据租户ID获取担保政策
     * @param tenantId 租户ID
     * @return 担保政策列表
     */
    List<GuaranteePolicy> getByTenantId(Integer tenantId);
    
    /**
     * 根据ID获取担保政策
     * @param id 政策ID
     * @return 担保政策
     */
    Optional<GuaranteePolicy> getById(Integer id);
    
    /**
     * 根据集团ID获取担保政策
     * @param groupId 集团ID
     * @return 担保政策列表
     */
    List<GuaranteePolicy> getByGroupId(Integer groupId);
    
    /**
     * 创建担保政策
     * @param tenantId 租户ID
     * @param policy 担保政策
     * @return 创建的担保政策
     */
    GuaranteePolicy create(Integer tenantId, GuaranteePolicy policy);
    
    /**
     * 更新担保政策
     * @param id 政策ID
     * @param policy 担保政策
     * @return 更新后的担保政策
     */
    GuaranteePolicy update(Integer id, GuaranteePolicy policy);
    
    /**
     * 删除担保政策
     * @param id 政策ID
     */
    void delete(Integer id);
}
