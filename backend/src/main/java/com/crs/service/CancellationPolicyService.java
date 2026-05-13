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
     * 根据ID获取取消政策
     * @param id 政策ID
     * @return 取消政策
     */
    Optional<CancellationPolicy> getById(Integer id);
    
    /**
     * 创建取消政策
     * @param policy 取消政策
     * @return 创建的取消政策
     */
    CancellationPolicy create(CancellationPolicy policy);
    
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
