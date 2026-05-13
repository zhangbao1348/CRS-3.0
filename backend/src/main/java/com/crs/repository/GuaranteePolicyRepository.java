package com.crs.repository;

import com.crs.entity.GuaranteePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 担保政策数据访问接口 (GuaranteePolicyRepository)
 * 
 * <p>提供对 {@link GuaranteePolicy} 实体的数据库交互能力。支持基于租户隔离的担保政策检索与唯一性校验。</p>
 */
@Repository
public interface GuaranteePolicyRepository extends JpaRepository<GuaranteePolicy, Integer> {
    
    /**
     * 获取指定租户下的所有担保政策。
     * 
     * @param tenantId 租户 ID
     * @return 政策列表
     */
    List<GuaranteePolicy> findByTenantId(Integer tenantId);
    
    /**
     * 获取指定集团下的担保政策模板。
     * 
     * @param groupId 集团 ID
     * @return 政策列表
     */
    List<GuaranteePolicy> findByGroupId(Integer groupId);
    
    /**
     * 按状态全局过滤政策。
     * 
     * @param status 状态 (如 active)
     * @return 政策列表
     */
    List<GuaranteePolicy> findByStatus(String status);
    
    // =====================================================================
    // 合规方法：必须包含 tenantId（符合多租户隔离规范）
    // =====================================================================

    /**
     * 在指定租户内，根据编码精确查找政策。
     * 
     * @param tenantId 租户 ID
     * @param code 政策编码
     * @return 担保政策实体
     */
    GuaranteePolicy findByTenantIdAndCode(Integer tenantId, String code);

    /**
     * 校验租户内是否存在重复的政策编码。
     * 
     * @param tenantId 租户 ID
     * @param code 政策编码
     * @return 存在返回 true
     */
    boolean existsByTenantIdAndCode(Integer tenantId, String code);

}
