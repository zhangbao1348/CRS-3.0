package com.crs.repository;

import com.crs.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 包价仓库接口
 * 用于包价数据的CRUD操作
 */
@Repository
public interface PackageRepository extends JpaRepository<Package, Integer> {
    
    /**
     * 根据租户ID查询所有包价
     * @param tenantId 租户ID
     * @return 包价列表
     */
    List<Package> findByTenantId(Integer tenantId);
    
    /**
     * 根据租户ID和包价代码查询包价
     * @param tenantId 租户ID
     * @param code 包价代码
     * @return 包价信息
     */
    Optional<Package> findByTenantIdAndCode(Integer tenantId, String code);
    
    /**
     * 根据租户ID和包价名称查询包价
     * @param tenantId 租户ID
     * @param name 包价名称
     * @return 包价列表
     */
    List<Package> findByTenantIdAndNameContaining(Integer tenantId, String name);
    
    /**
     * 根据租户ID和状态查询包价
     * @param tenantId 租户ID
     * @param status 状态
     * @return 包价列表
     */
    List<Package> findByTenantIdAndStatus(Integer tenantId, Package.Status status);
    
    /**
     * 根据租户ID和类型查询包价
     * @param tenantId 租户ID
     * @param type 包价类型
     * @return 包价列表
     */
    List<Package> findByTenantIdAndType(Integer tenantId, String type);
    
    /**
     * 检查租户内包价代码是否存在
     * @param tenantId 租户ID
     * @param code 包价代码
     * @return 是否存在
     */
    boolean existsByTenantIdAndCode(Integer tenantId, String code);
    
    // 保留向后兼容的方法
    
    /**
     * 根据包价代码查询包价（已废弃，使用findByTenantIdAndCode代替）
     * @param code 包价代码
     * @return 包价信息
     */
    @Deprecated
    Optional<Package> findByCode(String code);
    
    /**
     * 根据包价名称查询包价（已废弃，使用findByTenantIdAndNameContaining代替）
     * @param name 包价名称
     * @return 包价列表
     */
    @Deprecated
    List<Package> findByNameContaining(String name);
    
    /**
     * 根据状态查询包价（已废弃，使用findByTenantIdAndStatus代替）
     * @param status 状态
     * @return 包价列表
     */
    @Deprecated
    List<Package> findByStatus(Package.Status status);
    
    /**
     * 检查包价代码是否存在（已废弃，使用existsByTenantIdAndCode代替）
     * @param code 包价代码
     * @return 是否存在
     */
    @Deprecated
    boolean existsByCode(String code);
}
