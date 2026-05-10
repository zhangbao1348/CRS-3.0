package com.crs.repository;

import com.crs.entity.HotelFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * 酒店设施数据访问接口 (HotelFacilityRepository)
 * 
 * <p>提供对 {@link HotelFacility} 实体的数据库操作能力。支持基于酒店编码的多维度设施检索与过滤。</p>
 * 
 * <p>安全规范：</p>
 * <ul>
 *     <li>**租户隔离**：所有查询必须携带 `tenantId`，以确保集团层级的数据安全。</li>
 *     <li>**编码定位**：优先使用 `hotelCode` 和 `facilityCode` 进行业务操作。</li>
 * </ul>
 */
public interface HotelFacilityRepository extends JpaRepository<HotelFacility, Integer> {
    
    // =====================================================================
    // 合规方法：必须包含 tenantId（符合多租户隔离规范）
    // =====================================================================

    /**
     * 获取指定酒店下的所有设施。
     * 
     * @param tenantId 租户 ID
     * @param hotelCode 酒店编码
     * @return 设施列表
     */
    List<HotelFacility> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    /**
     * 按类型获取酒店设施。
     * 
     * @param tenantId 租户 ID
     * @param hotelCode 酒店编码
     * @param facilityType 设施类型
     * @return 设施列表
     */
    List<HotelFacility> findByTenantIdAndHotelCodeAndFacilityType(Integer tenantId, String hotelCode, String facilityType);

    /**
     * 获取酒店下特定可用状态的设施。
     * 
     * @param tenantId 租户 ID
     * @param hotelCode 酒店编码
     * @param available 是否可用
     * @return 设施列表
     */
    List<HotelFacility> findByTenantIdAndHotelCodeAndAvailable(Integer tenantId, String hotelCode, Boolean available);

    /**
     * 批量获取指定编码的酒店设施。
     * 
     * @param tenantId 租户 ID
     * @param hotelCode 酒店编码
     * @param facilityCodes 设施编码集合
     * @return 设施列表
     */
    @Query("SELECT hf FROM HotelFacility hf WHERE hf.tenantId = :tenantId AND hf.hotelCode = :hotelCode AND hf.facilityCode IN (:facilityCodes)")
    List<HotelFacility> findByTenantIdAndHotelCodeAndFacilityCodes(@Param("tenantId") Integer tenantId, @Param("hotelCode") String hotelCode, @Param("facilityCodes") List<String> facilityCodes);

    // =====================================================================
    // 已废弃方法：缺少 tenantId 约束（存在跨租户数据风险，禁止新代码使用）
    // =====================================================================

    /** 
     * @deprecated 请改用 {@link #findByTenantIdAndHotelCode(Integer, String)} 
     */
    @Deprecated
    List<HotelFacility> findByHotelCode(String hotelCode);

    /** 
     * @deprecated 请改用 {@link #findByTenantIdAndHotelCodeAndFacilityType(Integer, String, String)} 
     */
    @Deprecated
    List<HotelFacility> findByHotelCodeAndFacilityType(String hotelCode, String facilityType);

    /** 
     * @deprecated 请改用 {@link #findByTenantIdAndHotelCodeAndAvailable(Integer, String, Boolean)} 
     */
    @Deprecated
    List<HotelFacility> findByHotelCodeAndAvailable(String hotelCode, Boolean available);

    /** 
     * @deprecated 请改用 {@link #findByTenantIdAndHotelCodeAndFacilityCodes(Integer, String, List)} 
     */
    @Deprecated
    @Query("SELECT hf FROM HotelFacility hf WHERE hf.hotelCode = :hotelCode AND hf.facilityCode IN (:facilityCodes)")
    List<HotelFacility> findByHotelCodeAndFacilityCodes(@Param("hotelCode") String hotelCode, @Param("facilityCodes") List<String> facilityCodes);

    // =====================================================================
    // 已废弃方法：使用 hotelId（仅作兼容保留，禁止新代码使用）
    // =====================================================================

    /** 
     * @deprecated 内部 ID 关联已过时。请改用 {@link #findByTenantIdAndHotelCode(Integer, String)} 
     */
    @Deprecated
    List<HotelFacility> findByHotelId(Integer hotelId);
    
    /** 
     * @deprecated 内部 ID 关联已过时。请改用 {@link #findByTenantIdAndHotelCodeAndFacilityType(Integer, String, String)} 
     */
    @Deprecated
    List<HotelFacility> findByHotelIdAndFacilityType(Integer hotelId, String facilityType);
    
    /** 
     * @deprecated 内部 ID 关联已过时。请改用 {@link #findByTenantIdAndHotelCodeAndAvailable(Integer, String, Boolean)} 
     */
    @Deprecated
    List<HotelFacility> findByHotelIdAndAvailable(Integer hotelId, Boolean available);
    
    /** 
     * @deprecated 内部 ID 关联已过时。请改用 {@link #findByTenantIdAndHotelCodeAndFacilityCodes(Integer, String, List)} 
     */
    @Deprecated
    @Query("SELECT hf FROM HotelFacility hf WHERE hf.hotelId = :hotelId AND hf.facilityCode IN (:facilityCodes)")
    List<HotelFacility> findByHotelIdAndFacilityCodes(@Param("hotelId") Integer hotelId, @Param("facilityCodes") List<String> facilityCodes);
}