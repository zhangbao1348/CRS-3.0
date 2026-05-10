package com.crs.repository;

import com.crs.entity.HotelRateCodeAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 酒店房价码分配数据访问接口 (HotelRateCodeAllocationRepository)
 * 
 * <p>提供对 {@link HotelRateCodeAllocation} 实体的数据库交互。负责管理房价码在租户、酒店层级的可见性与编辑权限。</p>
 * 
 * <p>安全规范：</p>
 * <ul>
 *     <li>**租户隔离**：所有查询与删除操作必须包含 `tenantId`，以防止跨租户越权操作。</li>
 *     <li>**编码关联**：优先使用 `hotelCode` 和 `rateCode` 进行业务定位。</li>
 * </ul>
 */
public interface HotelRateCodeAllocationRepository extends JpaRepository<HotelRateCodeAllocation, Integer> {

    // =====================================================================
    // 合规方法：必须包含 tenantId（符合多租户隔离规范）
    // =====================================================================

    /**
     * 获取指定租户下、某个酒店的所有房价码分配及权限记录。
     * 
     * @param tenantId 租户 ID
     * @param hotelCode 酒店编码
     * @return 分配记录列表
     */
    List<HotelRateCodeAllocation> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    /**
     * 在指定租户内，精确查找某个酒店对特定房价码的分配权限。
     * 
     * @param tenantId 租户 ID
     * @param hotelCode 酒店编码
     * @param rateCode 房价码
     * @return 分配记录列表
     */
    List<HotelRateCodeAllocation> findByTenantIdAndHotelCodeAndRateCode(Integer tenantId, String hotelCode, String rateCode);

    /**
     * 获取指定租户下、某个房价码被分配给的所有酒店记录。
     * 
     * @param tenantId 租户 ID
     * @param rateCode 房价码
     * @return 分配记录列表
     */
    List<HotelRateCodeAllocation> findByTenantIdAndRateCode(Integer tenantId, String rateCode);

    /**
     * 获取指定租户下的所有分配记录。
     * 
     * @param tenantId 租户 ID
     * @return 分配记录列表
     */
    List<HotelRateCodeAllocation> findByTenantId(Integer tenantId);

    /**
     * 安全删除：仅删除属于该租户的特定酒店分配记录。
     * 
     * @param tenantId 租户 ID
     * @param hotelCode 酒店编码
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM HotelRateCodeAllocation h WHERE h.tenantId = :tenantId AND h.hotelCode = :hotelCode")
    void deleteByTenantIdAndHotelCode(@Param("tenantId") Integer tenantId, @Param("hotelCode") String hotelCode);

    // =====================================================================
    // 已废弃方法：缺少 tenantId 约束（存在跨租户数据风险，禁止新代码使用）
    // =====================================================================

    /** 
     * @deprecated 存在安全隐患。请改用 {@link #findByTenantIdAndHotelCode(Integer, String)} 
     */
    @Deprecated
    List<HotelRateCodeAllocation> findByHotelCode(String hotelCode);

    /** 
     * @deprecated 存在安全隐患。请改用 {@link #findByTenantIdAndHotelCodeAndRateCode(Integer, String, String)} 
     */
    @Deprecated
    HotelRateCodeAllocation findByHotelCodeAndRateCode(String hotelCode, String rateCode);

    /** 
     * @deprecated 存在安全隐患。请改用过滤后的 {@link #findByTenantIdAndHotelCode(Integer, String)} 
     */
    @Deprecated
    List<HotelRateCodeAllocation> findByHotelCodeAndAllocated(String hotelCode, Boolean allocated);

    /** 
     * @deprecated 越权风险极高。请改用 {@link #deleteByTenantIdAndHotelCode(Integer, String)} 
     */
    @Deprecated
    @Modifying
    @Transactional
    @Query("DELETE FROM HotelRateCodeAllocation h WHERE h.hotelCode = :hotelCode")
    void deleteByHotelCode(@Param("hotelCode") String hotelCode);
}