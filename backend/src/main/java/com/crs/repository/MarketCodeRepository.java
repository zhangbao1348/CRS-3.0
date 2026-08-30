package com.crs.repository;

import com.crs.entity.MarketCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 市场码数据访问接口 (MarketCodeRepository)
 * 
 * <p>提供对 {@link MarketCode} 实体的数据库交互能力。支持基于租户隔离的多级市场细分检索。</p>
 */
@Repository
public interface MarketCodeRepository extends JpaRepository<MarketCode, Integer> {

    /** 按主键与租户双重约束查询。 */
    Optional<MarketCode> findByIdAndTenantId(Integer id, Integer tenantId);

    /**
     * 获取指定租户下的所有市场码定义。
     * 
     * @param tenantId 租户 ID
     * @return 市场码列表
     */
    List<MarketCode> findByTenantId(Integer tenantId);

    /**
     * 获取指定租户下、特定父分类下的子细分。
     * 
     * @param tenantId 租户 ID
     * @param parentId 父分类 ID
     * @return 子市场码列表
     */
    List<MarketCode> findByTenantIdAndParentId(Integer tenantId, Integer parentId);

    /**
     * 在指定租户内，根据市场码编码精确查找。
     * 
     * @param tenantId 租户 ID
     * @param code 市场码编码
     * @return 市场码实体
     */
    MarketCode findByTenantIdAndCode(Integer tenantId, String code);

    /**
     * 在指定租户内，根据层级查找市场码。
     * 常用于区分大类市场与具体细分。
     * 
     * @param tenantId 租户 ID
     * @param level 层级
     * @return 市场码列表
     */
    List<MarketCode> findByTenantIdAndLevel(Integer tenantId, Integer level);

    /**
     * 根据租户 ID 和父级市场码编码查找。
     */
    List<MarketCode> findByTenantIdAndParentCode(Integer tenantId, String parentCode);

    /**
     * 根据租户、自身编码及父级编码精确查找。
     */
    MarketCode findByTenantIdAndCodeAndParentCode(Integer tenantId, String code, String parentCode);
}
