package com.crs.repository;

import com.crs.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;

/**
 * 库存数据访问接口 (InventoryRepository)
 * 
 * <p>提供对 {@link Inventory} 实体的数据库操作能力。</p>
 * 
 * <p>规范要求：</p>
 * <ul>
 *     <li>**优先使用 Code 关联**：根据系统架构演进，优先使用 `hotelCode`, `ratePlanCode`, `roomTypeCode`, `channelCode` 系列方法。</li>
 *     <li>**已废弃 ID 方法**：`hotelId` 系列方法仅供旧有系统逻辑兼容，新开发应避免使用。</li>
 * </ul>
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    /** 获取指定租户下的所有库存记录 */
    List<Inventory> findByTenantId(Integer tenantId);

    // 业务关联已统一切换为基于业务编码 (Code) 进行检索。
    // 请优先使用下方的 ByCode 系列方法。

    // =========================================================================
    // 聚合与预警查询
    // =========================================================================

    /**
     * 查询库存预警记录。
     * 找出指定日期范围内可用房间数低于或等于阈值的活跃库存记录。
     */
    @org.springframework.data.jpa.repository.Query(
            "SELECT i FROM Inventory i WHERE i.tenantId = :tenantId AND i.availableRooms <= :threshold AND i.date >= :startDate AND i.date <= :endDate AND i.status = 'active' ORDER BY i.date, i.hotelCode")
    List<Inventory> findLowInventory(@org.springframework.data.repository.query.Param("tenantId") Integer tenantId,
                                     @org.springframework.data.repository.query.Param("threshold") int threshold,
                                     @org.springframework.data.repository.query.Param("startDate") Date startDate,
                                     @org.springframework.data.repository.query.Param("endDate") Date endDate);

    /**
     * 根据租户和酒店编码安全获取库存。
     */
    List<Inventory> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    /**
     * 根据租户、酒店编码和日期范围获取库存。
     */
    List<Inventory> findByTenantIdAndHotelCodeAndDateBetween(Integer tenantId, String hotelCode, Date startDate, Date endDate);

    /**
     * 跨日期查询租户酒店、计划和房型的库存组合。
     */
    List<Inventory> findByTenantIdAndHotelCodeAndRatePlanCodeAndRoomTypeCode(Integer tenantId, String hotelCode, String ratePlanCode, String roomTypeCode);

    /**
     * 精确查询租户特定编码组合下的单日库存。
     */
    Inventory findByTenantIdAndHotelCodeAndRatePlanCodeAndRoomTypeCodeAndDate(Integer tenantId, String hotelCode, String ratePlanCode, String roomTypeCode, Date date);

    /**
     * 精确查询租户特定编码组合下的时间段库存。
     */
    List<Inventory> findByTenantIdAndHotelCodeAndRatePlanCodeAndRoomTypeCodeAndDateBetween(Integer tenantId, String hotelCode, String ratePlanCode, String roomTypeCode, Date startDate, Date endDate);

    /**
     * 根据租户酒店和渠道编码查询库存。
     */
    List<Inventory> findByTenantIdAndHotelCodeAndChannelCode(Integer tenantId, String hotelCode, String channelCode);

    /**
     * 根据租户酒店、渠道及日期范围查询。
     */
    List<Inventory> findByTenantIdAndHotelCodeAndChannelCodeAndDateBetween(Integer tenantId, String hotelCode, String channelCode, Date startDate, Date endDate);

    /**
     * 租户五维编码模型下的精确库存定位。
     */
    Inventory findByTenantIdAndHotelCodeAndRatePlanCodeAndRoomTypeCodeAndChannelCodeAndDate(Integer tenantId, String hotelCode, String ratePlanCode, String roomTypeCode, String channelCode, Date date);

    /**
     * 根据租户酒店编码和指定日期查询。
     */
    List<Inventory> findByTenantIdAndHotelCodeAndDate(Integer tenantId, String hotelCode, Date date);

    /**
     * 根据租户酒店编码和状态查询。
     */
    List<Inventory> findByTenantIdAndHotelCodeAndStatus(Integer tenantId, String hotelCode, Inventory.Status status);

    /**
     * 针对特定酒店查询库存预警记录。
     */
    @org.springframework.data.jpa.repository.Query(
            "SELECT i FROM Inventory i WHERE i.tenantId = :tenantId AND i.hotelCode = :hotelCode " +
            "AND i.availableRooms <= :threshold AND i.date >= :startDate AND i.date <= :endDate " +
            "AND i.status = 'active' ORDER BY i.date ASC")
    List<Inventory> findLowInventoryByHotel(@org.springframework.data.repository.query.Param("tenantId") Integer tenantId,
                                            @org.springframework.data.repository.query.Param("hotelCode") String hotelCode,
                                            @org.springframework.data.repository.query.Param("threshold") int threshold,
                                            @org.springframework.data.repository.query.Param("startDate") Date startDate,
                                            @org.springframework.data.repository.query.Param("endDate") Date endDate);
}
