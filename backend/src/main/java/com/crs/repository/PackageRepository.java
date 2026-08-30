package com.crs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crs.entity.Package;

/**
 * 增值包价数据访问接口 (PackageRepository)
 * 
 * <p>提供对 {@link Package} 实体的数据库交互能力。支持基于租户、编码、类型及状态的多维度增值项目检索。</p>
 */
@Repository
public interface PackageRepository extends JpaRepository<Package, Integer> {

    /** 按主键与租户双重约束查询。 */
    Optional<Package> findByIdAndTenantId(Integer id, Integer tenantId);
    
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
     * 按租户、编码集合和状态批量查询包价。
     *
     * @param tenantId 租户 ID
     * @param codes 包价编码集合
     * @param status 包价状态
     * @return 包价列表
     */
    List<Package> findByTenantIdAndCodeInAndStatus(Integer tenantId, List<String> codes, Package.Status status);
    
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
     * 组合条件搜索租户内包价。
     *
     * @param tenantId 租户 ID
     * @param keyword 关键词，同时匹配包价名称和包价代码
     * @param name 包价名称，支持模糊匹配
     * @param code 包价代码，支持模糊匹配
     * @param type 包价类型，精确匹配
     * @param frequency 发放频率，精确匹配
     * @param quantityType 计数方式，精确匹配
     * @param status 包价状态，精确匹配
     * @return 包价列表
     */
    @Query("""
        SELECT p
        FROM Package p
        WHERE p.tenantId = :tenantId
          AND (:keyword IS NULL OR p.name LIKE CONCAT('%', :keyword, '%') OR p.code LIKE CONCAT('%', :keyword, '%'))
          AND (:name IS NULL OR p.name LIKE CONCAT('%', :name, '%'))
          AND (:code IS NULL OR p.code LIKE CONCAT('%', :code, '%'))
          AND (:type IS NULL OR p.type = :type)
          AND (:frequency IS NULL OR p.frequency = :frequency)
          AND (:quantityType IS NULL OR p.quantityType = :quantityType)
          AND (:status IS NULL OR p.status = :status)
        ORDER BY p.updatedAt DESC, p.id DESC
        """)
    List<Package> searchPackages(
            @Param("tenantId") Integer tenantId,
            @Param("keyword") String keyword,
            @Param("name") String name,
            @Param("code") String code,
            @Param("type") String type,
            @Param("frequency") String frequency,
            @Param("quantityType") String quantityType,
            @Param("status") Package.Status status);
    
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
