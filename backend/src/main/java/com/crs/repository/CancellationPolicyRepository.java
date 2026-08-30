package com.crs.repository;

import com.crs.entity.CancellationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 取消政策数据访问接口 (CancellationPolicyRepository)
 * 
 * <p>提供对 {@link CancellationPolicy} 实体的数据库交互能力。支持基于租户隔离的政策检索与唯一性校验。</p>
 */
@Repository
public interface CancellationPolicyRepository extends JpaRepository<CancellationPolicy, Integer> {

    /** 按主键与租户双重约束查询。 */
    Optional<CancellationPolicy> findByIdAndTenantId(Integer id, Integer tenantId);
    
    /**
     * 获取指定租户下的所有取消政策。
     * 
     * @param tenantId 租户 ID
     * @return 政策列表
     */
    List<CancellationPolicy> findByTenantId(Integer tenantId);
    
    /**
     * 获取指定集团下的取消政策模板。
     * 
     * @param groupId 集团 ID
     * @return 政策列表
     */
    List<CancellationPolicy> findByGroupId(Integer groupId);
    
    /**
     * 按状态全局过滤政策。
     * 
     * @param status 状态 (如 active)
     * @return 政策列表
     */
    List<CancellationPolicy> findByStatus(String status);
    
    // =====================================================================
    // 合规方法：必须包含 tenantId（符合多租户隔离规范）
    // =====================================================================

    /**
     * 在指定租户内，根据编码精确查找政策。
     * 
     * @param tenantId 租户 ID
     * @param code 政策编码
     * @return 取消政策实体
     */
    CancellationPolicy findByTenantIdAndCode(Integer tenantId, String code);

    /**
     * 校验租户内是否存在重复的政策编码。
     * 
     * @param tenantId 租户 ID
     * @param code 政策编码
     * @return 存在返回 true
     */
    boolean existsByTenantIdAndCode(Integer tenantId, String code);

}
