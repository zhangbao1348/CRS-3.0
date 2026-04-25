package com.crs.repository;

import com.crs.entity.GuaranteePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 担保政策仓库接口
 * 用于担保政策数据的CRUD操作
 */
@Repository
public interface GuaranteePolicyRepository extends JpaRepository<GuaranteePolicy, Integer> {
    
    /**
     * 根据租户ID查询担保政策
     * @param tenantId 租户ID
     * @return 担保政策列表
     */
    List<GuaranteePolicy> findByTenantId(Integer tenantId);
    
    /**
     * 根据集团ID查询担保政策
     * @param groupId 集团ID
     * @return 担保政策列表
     */
    List<GuaranteePolicy> findByGroupId(Integer groupId);
    
    /**
     * 根据状态查询担保政策
     * @param status 状态
     * @return 担保政策列表
     */
    List<GuaranteePolicy> findByStatus(String status);
    
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
     * 根据政策代码查询担保政策
     * @param code 政策代码
     * @return 担保政策
     */
    GuaranteePolicy findByCode(String code);
}
