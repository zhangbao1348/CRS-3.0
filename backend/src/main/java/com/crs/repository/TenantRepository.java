package com.crs.repository;

import com.crs.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 租户数据访问接口 (TenantRepository)
 * 
 * <p>本接口继承自 {@link JpaRepository}，提供对 {@link Tenant} 实体的标准 CRUD 操作以及自定义查询逻辑。</p>
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Integer> {
    
    /**
     * 根据租户编码查找租户。
     * 
     * @param tenantCode 租户唯一编码
     * @return 包含租户实体的 Optional 对象
     */
    Optional<Tenant> findByTenantCode(String tenantCode);
    
    /**
     * 检查指定的租户编码是否已存在。
     * 用于在创建新租户时进行唯一性校验。
     * 
     * @param tenantCode 租户唯一编码
     * @return 若存在则返回 true，否则返回 false
     */
    boolean existsByTenantCode(String tenantCode);
}

