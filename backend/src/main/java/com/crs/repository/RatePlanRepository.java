package com.crs.repository;

import com.crs.entity.RatePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 价格计划数据访问接口 (RatePlanRepository)
 * 
 * <p>提供对 {@link RatePlan} 实体的数据库交互能力。支持基于租户、酒店、价格编码以及派生关系的复杂查询。</p>
 */
@Repository
public interface RatePlanRepository extends JpaRepository<RatePlan, Integer> {

    /** 在租户维度下根据主键查询价格计划。 */
    Optional<RatePlan> findByIdAndTenantId(Integer id, Integer tenantId);

    /** 获取指定租户下的所有价格计划 */
    List<RatePlan> findByTenantId(Integer tenantId);

    /**
     * 根据租户及来源集团房价码查找关联的所有单店价格计划。
     */
    List<RatePlan> findByTenantIdAndSourceGroupRateCode(Integer tenantId, String sourceGroupRateCode);

    /**
     * 根据租户及酒店外部编码获取价格计划列表。
     */
    List<RatePlan> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    /**
     * 获取指定租户酒店下，基于某个父级计划派生的所有子计划。
     */
    List<RatePlan> findByTenantIdAndHotelCodeAndParentRateCodeAndStatus(Integer tenantId, String hotelCode, String parentRateCode, String status);

    /**
     * 根据租户及酒店外部编码获取特定状态的计划。
     */
    List<RatePlan> findByTenantIdAndHotelCodeAndStatus(Integer tenantId, String hotelCode, String status);

    /**
     * 根据租户、酒店外部编码和计划编码查找计划。
     */
    Optional<RatePlan> findByTenantIdAndHotelCodeAndRateCode(Integer tenantId, String hotelCode, String rateCode);

    /**
     * 校验在指定租户及酒店编码下计划编码是否存在。
     */
    boolean existsByTenantIdAndHotelCodeAndRateCode(Integer tenantId, String hotelCode, String rateCode);

    /**
     * 校验计划编码是否存在（排除自身）。
     */
    boolean existsByTenantIdAndHotelCodeAndRateCodeAndIdNot(Integer tenantId, String hotelCode, String rateCode, Integer id);

    /**
     * 根据租户、集团房价码和酒店编码查找对应的单店计划。
     */
    List<RatePlan> findByTenantIdAndSourceGroupRateCodeAndHotelCode(Integer tenantId, String sourceGroupRateCode, String hotelCode);

    long countByTenantIdAndMarketCode(Integer tenantId, String marketCode);

    long countByTenantIdAndSourceCode(Integer tenantId, String sourceCode);

    long countByTenantIdAndRateCategory(Integer tenantId, String rateCategory);

}
