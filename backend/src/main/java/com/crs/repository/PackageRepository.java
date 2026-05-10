package com.crs.repository;

import com.crs.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 增值包价数据访问接口 (PackageRepository)
 * 
 * <p>提供对 {@link Package} 实体的数据库交互能力。支持基于租户、编码、类型及状态的多维度增值项目检索。</p>
 */
@Repository
public interface PackageRepository extends JpaRepository<Package, Integer> {
    
    /**
     * 获取指定租户下的所有增值包价配置。
     * 
     * @param tenantId 租户 ID
     * @return 包价列表
     */
    List<Package> findByTenantId(Integer tenantId);
    
    /**
     * 在指定租户内，根据编码精确查找。
     * 
     * @param tenantId 租户 ID
     * @param code 包价编码
     * @return 包价实体的 Optional 对象
     */
    Optional<Package> findByTenantIdAndCode(Integer tenantId, String code);
    
    /**
     * 根据名称模糊搜索租户下的包价。
     * 
     * @param tenantId 租户 ID
     * @param name 包价名称关键字
     * @return 包价列表
     */
    List<Package> findByTenantIdAndNameContaining(Integer tenantId, String name);
    
    /**
     * 按租户及状态过滤。
     * 
     * @param tenantId 租户 ID
     * @param status 状态
     * @return 包价列表
     */
    List<Package> findByTenantIdAndStatus(Integer tenantId, Package.Status status);
    
    /**
     * 获取指定租户下特定类型的包价（如：仅查询餐饮类包价）。
     * 
     * @param tenantId 租户 ID
     * @param type 类型
     * @return 包价列表
     */
    List<Package> findByTenantIdAndType(Integer tenantId, String type);
    
    /**
     * 校验租户内是否存在重复的包价编码。
     */
    boolean existsByTenantIdAndCode(Integer tenantId, String code);
    
    // =====================================================================
    // 已废弃方法：缺少租户约束（存在跨租户数据风险，禁止新代码使用）
    // =====================================================================
    
    /** 
     * @deprecated 请改用 {@link #findByTenantIdAndCode(Integer, String)} 
     */
    @Deprecated
    Optional<Package> findByCode(String code);
    
    /** 
     * @deprecated 请改用 {@link #findByTenantIdAndNameContaining(Integer, String)} 
     */
    @Deprecated
    List<Package> findByNameContaining(String name);
    
    /** 
     * @deprecated 请改用 {@link #findByTenantIdAndStatus(Integer, Package.Status)} 
     */
    @Deprecated
    List<Package> findByStatus(Package.Status status);
    
    /** 
     * @deprecated 请改用 {@link #existsByTenantIdAndCode(Integer, String)} 
     */
    @Deprecated
    boolean existsByCode(String code);
}

