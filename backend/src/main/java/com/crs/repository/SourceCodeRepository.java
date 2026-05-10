package com.crs.repository;

import com.crs.entity.SourceCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单来源码数据访问接口 (SourceCodeRepository)
 * 
 * <p>提供对 {@link SourceCode} 实体的数据库交互能力。支持基于租户隔离的多级预订来源检索。</p>
 */
@Repository
public interface SourceCodeRepository extends JpaRepository<SourceCode, Integer> {

    /**
     * 获取指定父节点下的所有来源码。
     * 
     * @param parentId 父 ID
     * @return 来源码列表
     */
    List<SourceCode> findByParentId(Integer parentId);

    /**
     * 获取指定租户下的所有来源码定义。
     * 
     * @param tenantId 租户 ID
     * @return 来源码列表
     */
    List<SourceCode> findByTenantId(Integer tenantId);

    /**
     * 获取指定租户下、特定父分类下的子来源。
     * 
     * @param tenantId 租户 ID
     * @param parentId 父 ID
     * @return 子来源码列表
     */
    List<SourceCode> findByTenantIdAndParentId(Integer tenantId, Integer parentId);

    /** 
     * @deprecated 缺少租户约束，存在越权风险。请改用 {@link #findByTenantIdAndCode(Integer, String)} 
     */
    @Deprecated
    SourceCode findByCode(String code);

    /**
     * 在指定租户内，根据来源码编码精确查找。
     * 
     * @param tenantId 租户 ID
     * @param code 来源码
     * @return 来源码实体
     */
    SourceCode findByTenantIdAndCode(Integer tenantId, String code);

    /**
     * 在指定租户内，根据层级查找来源码。
     * 
     * @param tenantId 租户 ID
     * @param level 层级
     * @return 来源码列表
     */
    List<SourceCode> findByTenantIdAndLevel(Integer tenantId, Integer level);

    /**
     * 根据租户 ID 和父级来源码编码查找。
     */
    List<SourceCode> findByTenantIdAndParentCode(Integer tenantId, String parentCode);

    /**
     * 根据租户、自身编码及父级编码精确查找。
     */
    SourceCode findByTenantIdAndCodeAndParentCode(Integer tenantId, String code, String parentCode);
}