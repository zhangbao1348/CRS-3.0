package com.crs.repository;

import com.crs.entity.SourceCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 来源码仓库接口
 */
@Repository
public interface SourceCodeRepository extends JpaRepository<SourceCode, Integer> {

    /**
     * 根据父ID查询来源码
     */
    List<SourceCode> findByParentId(Integer parentId);

    /**
     * 根据租户ID查询所有来源码
     */
    List<SourceCode> findByTenantId(Integer tenantId);

    /**
     * 根据租户ID和父ID查询来源码
     */
    List<SourceCode> findByTenantIdAndParentId(Integer tenantId, Integer parentId);

    /**
     * 根据CODE查询来源码
     */
    SourceCode findByCode(String code);

    /**
     * 根据租户ID和CODE查询来源码
     */
    SourceCode findByTenantIdAndCode(Integer tenantId, String code);

    /**
     * 根据租户ID和级别查询来源码
     */
    List<SourceCode> findByTenantIdAndLevel(Integer tenantId, Integer level);

    List<SourceCode> findByTenantIdAndParentCode(Integer tenantId, String parentCode);

    SourceCode findByTenantIdAndCodeAndParentCode(Integer tenantId, String code, String parentCode);
}