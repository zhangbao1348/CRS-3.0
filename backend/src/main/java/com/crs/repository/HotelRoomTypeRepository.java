package com.crs.repository;

import com.crs.entity.HotelRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.EntityGraph;
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

    /** 在租户维度下根据主键查询酒店房型。 */
    Optional<HotelRoomType> findByIdAndTenantId(Integer id, Integer tenantId);

    // 业务关联已统一切换为基于业务编码 (Code) 进行检索。
    // 请优先使用下方的 ByCode 系列方法。

    // =====================================================================
    // 合规方法：必须包含 tenantId（符合多租户隔离规范）
    // =====================================================================

    /**
     * 租户维度：获取指定酒店下的所有房型。
     */
    @EntityGraph(attributePaths = {"roomTypeCategory"})
    List<HotelRoomType> findDistinctByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    /**
     * 租户维度：精确查找酒店内的房型。
     */
    Optional<HotelRoomType> findByTenantIdAndHotelCodeAndRoomTypeCode(Integer tenantId, String hotelCode,
            String roomTypeCode);

    /**
     * 租户维度：按状态过滤酒店房型。
     */
    @EntityGraph(attributePaths = {"roomTypeCategory"})
    List<HotelRoomType> findDistinctByTenantIdAndHotelCodeAndStatus(Integer tenantId, String hotelCode, String status);

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
     * 租户维度：根据集团房型编码获取所有关联的酒店房型。
     */
    List<HotelRoomType> findByTenantIdAndGroupRoomTypeCode(Integer tenantId, String groupRoomTypeCode);

    /**
     * 根据集团标准房型编码查找所有本地实现。
     */
    List<HotelRoomType> findByGroupRoomTypeCode(String groupRoomTypeCode);

    /**
     * 查找特定标准房型在特定酒店的本地记录。
     */
    Optional<HotelRoomType> findByGroupRoomTypeCodeAndHotelCode(String groupRoomTypeCode, String hotelCode);

    /**
     * 根据酒店编码和房型编码查询 (不含租户ID)
     */
    Optional<HotelRoomType> findByHotelCodeAndRoomTypeCode(String hotelCode, String roomTypeCode);

    /**
     * 根据分类编码查询。
     */
    List<HotelRoomType> findByRoomTypeCategoryCode(String roomTypeCategoryCode);

    // 旧有的缺少 tenantId 约束的方法已移除。
}
