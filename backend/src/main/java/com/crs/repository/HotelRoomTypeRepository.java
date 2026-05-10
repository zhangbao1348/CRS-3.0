package com.crs.repository;

import com.crs.entity.HotelRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 酒店房型数据访问接口 (HotelRoomTypeRepository)
 * 
 * <p>
 * 提供对 {@link HotelRoomType} 实体的数据库交互能力。支持基于酒店、集团模板、房型编码的多维度查询。
 * </p>
 * 
 * <p>
 * 规范要求：
 * </p>
 * <ul>
 * <li>**安全隔离**：核心业务查询必须携带 `tenantId` 和 `hotelCode`。</li>
 * <li>**编码优先**：优先使用 `roomTypeCode` 和 `groupRoomTypeCode` 进行业务定位。</li>
 * </ul>
 */
@Repository
public interface HotelRoomTypeRepository extends JpaRepository<HotelRoomType, Integer> {

    /**
     * 根据内部 ID 获取酒店的所有房型。
     * 
     * @param hotelId 酒店 ID
     * @return 房型列表
     */
    List<HotelRoomType> findByHotelId(Integer hotelId);

    /**
     * 查找所有关联了特定集团标准房型的本地房型。
     * 
     * @param groupRoomTypeId 集团房型 ID
     * @return 房型列表
     */
    List<HotelRoomType> findByGroupRoomTypeId(Integer groupRoomTypeId);

    /**
     * 在指定酒店内，根据房型代码查找。
     * 
     * @param hotelId      酒店 ID
     * @param roomTypeCode 房型代码
     * @return 房型实体的 Optional 对象
     */
    Optional<HotelRoomType> findByHotelIdAndRoomTypeCode(Integer hotelId, String roomTypeCode);

    /**
     * 根据酒店 ID 和状态过滤房型。
     * 
     * @param hotelId 酒店 ID
     * @param status  状态
     * @return 房型列表
     */
    List<HotelRoomType> findByHotelIdAndStatus(Integer hotelId, String status);

    /**
     * 校验酒店内是否存在该房型代码。
     */
    boolean existsByHotelIdAndRoomTypeCode(Integer hotelId, String roomTypeCode);

    /**
     * 精确查找特定集团房型在特定酒店的本地实现。
     */
    Optional<HotelRoomType> findByGroupRoomTypeIdAndHotelId(Integer groupRoomTypeId, Integer hotelId);

    /**
     * 获取酒店下有序的房型列表。
     */
    List<HotelRoomType> findByHotelIdOrderBySortOrderAsc(Integer hotelId);

    /**
     * 统计指定酒店的房型总数。
     */
    long countByHotelId(Integer hotelId);

    /**
     * 根据分类 ID 查询房型。
     */
    List<HotelRoomType> findByRoomTypeCategoryId(Integer roomTypeCategoryId);

    // =====================================================================
    // 合规方法：必须包含 tenantId（符合多租户隔离规范）
    // =====================================================================

    /**
     * 租户维度：获取指定酒店下的所有房型。
     */
    List<HotelRoomType> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    /**
     * 租户维度：精确查找酒店内的房型。
     */
    Optional<HotelRoomType> findByTenantIdAndHotelCodeAndRoomTypeCode(Integer tenantId, String hotelCode,
            String roomTypeCode);

    /**
     * 租户维度：按状态过滤酒店房型。
     */
    List<HotelRoomType> findByTenantIdAndHotelCodeAndStatus(Integer tenantId, String hotelCode, String status);

    /**
     * 租户维度：校验编码是否存在。
     */
    boolean existsByTenantIdAndHotelCodeAndRoomTypeCode(Integer tenantId, String hotelCode, String roomTypeCode);

    /**
     * 租户维度：获取有序房型列表。
     */
    List<HotelRoomType> findByTenantIdAndHotelCodeOrderBySortOrderAsc(Integer tenantId, String hotelCode);

    /**
     * 租户维度：统计房型总数。
     */
    long countByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    /**
     * 根据集团标准房型编码查找所有本地实现。
     */
    List<HotelRoomType> findByGroupRoomTypeCode(String groupRoomTypeCode);

    /**
     * 查找特定标准房型在特定酒店的本地记录。
     */
    Optional<HotelRoomType> findByGroupRoomTypeCodeAndHotelCode(String groupRoomTypeCode, String hotelCode);

    /**
     * 根据分类编码查询。
     */
    List<HotelRoomType> findByRoomTypeCategoryCode(String roomTypeCategoryCode);

    // =====================================================================
    // 已废弃方法：缺少 tenantId 约束（存在跨租户数据风险，禁止新代码使用）
    // =====================================================================

    /**
     * @deprecated 请改用 {@link #findByTenantIdAndHotelCode(Integer, String)}
     */
    @Deprecated
    List<HotelRoomType> findByHotelCode(String hotelCode);

    /**
     * @deprecated 请改用
     *             {@link #findByTenantIdAndHotelCodeAndRoomTypeCode(Integer, String, String)}
     */
    @Deprecated
    Optional<HotelRoomType> findByHotelCodeAndRoomTypeCode(String hotelCode, String roomTypeCode);

    /**
     * @deprecated 请改用
     *             {@link #findByTenantIdAndHotelCodeAndStatus(Integer, String, String)}
     */
    @Deprecated
    List<HotelRoomType> findByHotelCodeAndStatus(String hotelCode, String status);

    /**
     * @deprecated 请改用
     *             {@link #existsByTenantIdAndHotelCodeAndRoomTypeCode(Integer, String, String)}
     */
    @Deprecated
    boolean existsByHotelCodeAndRoomTypeCode(String hotelCode, String roomTypeCode);

    /**
     * @deprecated 请改用
     *             {@link #findByTenantIdAndHotelCodeOrderBySortOrderAsc(Integer, String)}
     */
    @Deprecated
    List<HotelRoomType> findByHotelCodeOrderBySortOrderAsc(String hotelCode);

    /**
     * @deprecated 请改用 {@link #countByTenantIdAndHotelCode(Integer, String)}
     */
    @Deprecated
    long countByHotelCode(String hotelCode);
}
