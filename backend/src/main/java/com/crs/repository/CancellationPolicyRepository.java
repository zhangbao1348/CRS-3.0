package com.crs.repository;

import com.crs.entity.CancellationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 取消政策仓库接口
 * 用于取消政策数据的CRUD操作
 */
@Repository
public interface CancellationPolicyRepository extends JpaRepository<CancellationPolicy, Integer> {
    
    /**
     * 根据租户ID查询取消政策
     * @param tenantId 租户ID
     * @return 取消政策列表
     */
    List<CancellationPolicy> findByTenantId(Integer tenantId);
    
    /**
     * 根据集团ID查询取消政策
     * @param groupId 集团ID
     * @return 取消政策列表
     */
    List<CancellationPolicy> findByGroupId(Integer groupId);
    
    /**
     * 根据状态查询取消政策
     * @param status 状态
     * @return 取消政策列表
     */
    List<CancellationPolicy> findByStatus(String status);
    
    /**
     * 检查政策代码是否存在（同一租户下）
     * @param tenantId 租户ID
     * @param code 政策代码
     * @return 是否存在
     */
    boolean existsByTenantIdAndCode(Integer tenantId, String code);
    
    /**
     * 检查政策代码是否存在
     * @param code 政策代码
     * @return 是否存在
     */
    boolean existsByCode(String code);
    
    /**
     * 根据政策代码查询取消政策
     * @param code 政策代码
     * @return 取消政策
     */
    CancellationPolicy findByCode(String code);
}
