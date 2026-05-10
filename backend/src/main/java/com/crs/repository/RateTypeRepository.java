package com.crs.repository;

import com.crs.entity.RateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 价格类型仓库接口
 * 用于价格类型数据的CRUD操作
 */
@Repository
public interface RateTypeRepository extends JpaRepository<RateType, Integer> {
    
    /**
     * 根据租户ID查询所有房价大类
     */
    List<RateType> findByTenantId(Integer tenantId);
    
    /**
     * 根据租户ID和状态查询房价大类
     */
    List<RateType> findByTenantIdAndStatus(Integer tenantId, RateType.Status status);
    
    /**
     * 根据租户ID和代码查询房价大类
     */
    RateType findByTenantIdAndCode(Integer tenantId, String code);
    
    /**
     * 检查价格类型代码是否存在（同一租户下）
     * @param tenantId 租户ID
     * @param code 价格类型代码
     * @return 是否存在
     */
    boolean existsByTenantIdAndCode(Integer tenantId, String code);

    // =====================================================================
    // 已废弃方法：缺少 tenantId 约束（存在跨租户风险，禁止新代码使用）
    // =====================================================================

    /**
     * @deprecated 缺少 tenantId 约束，请使用 findByTenantIdAndCode
     */
    @Deprecated
    Optional<RateType> findByCode(String code);

    /**
     * @deprecated 缺少 tenantId 约束，请使用 existsByTenantIdAndCode
     */
    @Deprecated
    boolean existsByCode(String code);

    /**
     * 根据价格类型名称查询价格类型
     * @param name 价格类型名称
     * @return 价格类型列表
     */
    List<RateType> findByNameContaining(String name);
    
    /**
     * 根据状态查询价格类型
     * @param status 状态
     * @return 价格类型列表
     */
    List<RateType> findByStatus(RateType.Status status);
}
