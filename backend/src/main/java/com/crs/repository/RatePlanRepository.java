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

    /**
     * 获取指定酒店下的所有价格计划。
     * 
     * @param hotelId 酒店 ID
     * @return 价格计划列表
     */
    List<RatePlan> findByHotelId(Integer hotelId);

    /**
     * 获取指定酒店下特定状态的价格计划。
     * 
     * @param hotelId 酒店 ID
     * @param status 状态 (active/inactive)
     * @return 价格计划列表
     */
    List<RatePlan> findByHotelIdAndStatus(Integer hotelId, String status);

    /**
     * 在指定酒店内，根据价格计划编码查找。
     * 
     * @param hotelId 酒店 ID
     * @param rateCode 价格计划编码
     * @return 包含计划的 Optional 对象
     */
    Optional<RatePlan> findByHotelIdAndRateCode(Integer hotelId, String rateCode);

    /**
     * 根据来源集团房价码查找关联的所有单店价格计划。
     * 
     * @param sourceGroupRateCode 集团房价码
     * @return 价格计划列表
     */
    List<RatePlan> findBySourceGroupRateCode(String sourceGroupRateCode);

    /**
     * 校验酒店内是否存在重复的价格计划编码。
     * 
     * @param hotelId 酒店 ID
     * @param rateCode 待校验的代码
     * @return 存在返回 true，否则返回 false
     */
    boolean existsByHotelIdAndRateCode(Integer hotelId, String rateCode);

    /**
     * 校验酒店内是否存在重复的价格计划编码（排除自身）。
     * 用于修改时的唯一性检查。
     * 
     * @param hotelId 酒店 ID
     * @param rateCode 待校验的代码
     * @param id 当前记录 ID
     * @return 存在返回 true，否则返回 false
     */
    boolean existsByHotelIdAndRateCodeAndIdNot(Integer hotelId, String rateCode, Integer id);

    /**
     * 根据酒店外部编码获取价格计划列表。
     * 
     * @param hotelCode 酒店外部编码
     * @return 价格计划列表
     */
    List<RatePlan> findByHotelCode(String hotelCode);

    /**
     * 获取指定酒店下，基于某个父级计划派生的所有子计划。
     * 
     * @param hotelCode 酒店外部编码
     * @param parentRateCode 父级计划编码
     * @param status 状态
     * @return 派生计划列表
     */
    List<RatePlan> findByHotelCodeAndParentRateCodeAndStatus(String hotelCode, String parentRateCode, String status);

    /**
     * 根据酒店外部编码获取特定状态的计划。
     * 
     * @param hotelCode 酒店外部编码
     * @param status 状态
     * @return 价格计划列表
     */
    List<RatePlan> findByHotelCodeAndStatus(String hotelCode, String status);

    /**
     * 根据酒店外部编码和计划编码查找计划。
     * 
     * @param hotelCode 酒店外部编码
     * @param rateCode 计划编码
     * @return 包含计划的 Optional 对象
     */
    Optional<RatePlan> findByHotelCodeAndRateCode(String hotelCode, String rateCode);

    /**
     * 校验在指定酒店编码下计划编码是否存在。
     * 
     * @param hotelCode 酒店编码
     * @param rateCode 计划编码
     * @return 存在返回 true
     */
    boolean existsByHotelCodeAndRateCode(String hotelCode, String rateCode);

    /**
     * 校验计划编码是否存在（排除自身）。
     */
    boolean existsByHotelCodeAndRateCodeAndIdNot(String hotelCode, String rateCode, Integer id);

    /**
     * 根据集团房价码和酒店编码查找对应的单店计划。
     */
    List<RatePlan> findBySourceGroupRateCodeAndHotelCode(String sourceGroupRateCode, String hotelCode);

    /**
     * 租户维度：获取指定租户下某个酒店的所有计划。
     * 
     * @param tenantId 租户 ID
     * @param hotelCode 酒店编码
     * @return 计划列表
     */
    List<RatePlan> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    /**
     * 租户维度：获取指定租户下某个酒店的特定状态计划。
     */
    List<RatePlan> findByTenantIdAndHotelCodeAndStatus(Integer tenantId, String hotelCode, String status);

    /**
     * 租户维度：根据集团房价码查找该租户下的所有关联计划。
     */
    List<RatePlan> findByTenantIdAndSourceGroupRateCode(Integer tenantId, String sourceGroupRateCode);

    /**
     * 租户维度：精确查找指定租户、酒店及编码的价格计划。
     */
    Optional<RatePlan> findByTenantIdAndHotelCodeAndRateCode(Integer tenantId, String hotelCode, String rateCode);
}

