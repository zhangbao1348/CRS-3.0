package com.crs.repository;

import com.crs.entity.MarketCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 市场码仓库接口
 */
@Repository
public interface MarketCodeRepository extends JpaRepository<MarketCode, Integer> {

    /**
     * 根据租户ID查询所有市场码
     */
    List<MarketCode> findByTenantId(Integer tenantId);

    /**
     * 根据租户ID和父ID查询市场码
     */
    List<MarketCode> findByTenantIdAndParentId(Integer tenantId, Integer parentId);

    /**
     * 根据租户ID和CODE查询市场码
     */
    MarketCode findByTenantIdAndCode(Integer tenantId, String code);

    /**
     * 根据租户ID和级别查询市场码
     */
    List<MarketCode> findByTenantIdAndLevel(Integer tenantId, Integer level);

    List<MarketCode> findByTenantIdAndParentCode(Integer tenantId, String parentCode);

    MarketCode findByTenantIdAndCodeAndParentCode(Integer tenantId, String code, String parentCode);
}
