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

    // =====================================================================
    // 已废弃方法：使用 hotelId/ratePlanId/channelId/roomTypeId
    // （仅为内部兼容，禁止新代码使用）
    // =====================================================================

    /**
     * @deprecated 请使用 {@link #findByHotelCode(String)}
     */
    @Deprecated
    List<Inventory> findByHotelId(Integer hotelId);
    
    /**
     * @deprecated 请使用 {@link #findByHotelCodeAndStatus(String, Inventory.Status)}
     */
    @Deprecated
    List<Inventory> findByHotelIdAndStatus(Integer hotelId, Inventory.Status status);
    
    /**
     * @deprecated 请使用 {@link #findByHotelCodeAndRatePlanCodeAndRoomTypeCode(String, String, String)}
     */
    @Deprecated
    List<Inventory> findByHotelIdAndRatePlanIdAndRoomTypeId(Integer hotelId, Integer ratePlanId, Integer roomTypeId);
    
    /**
     * @deprecated 请使用 {@link #findByHotelCodeAndDateBetween(String, Date, Date)}
     */
    @Deprecated
    List<Inventory> findByHotelIdAndDateBetween(Integer hotelId, Date startDate, Date endDate);

    /**
     * 根据 ID 组合定位特定日期的库存记录。
     */
    Inventory findByHotelIdAndRatePlanIdAndRoomTypeIdAndDate(Integer hotelId, Integer ratePlanId, Integer roomTypeId, Date date);
    
    /**
     * 根据 ID 组合查询指定日期范围内的库存列表。
     */
    List<Inventory> findByHotelIdAndRatePlanIdAndRoomTypeIdAndDateBetween(Integer hotelId, Integer ratePlanId, Integer roomTypeId, Date startDate, Date endDate);
    
    /**
     * 全局查找特定状态的库存。
     */
    List<Inventory> findByStatus(Inventory.Status status);
    
    /**
     * 根据渠道 ID 查询库存分配列表。
     */
    List<Inventory> findByChannelId(Integer channelId);
    
    /**
     * 根据酒店 ID 和渠道 ID 查询库存列表。
     */
    List<Inventory> findByHotelIdAndChannelId(Integer hotelId, Integer channelId);
    
    /**
     * 查询指定渠道在特定时间段内的库存记录。
     */
    List<Inventory> findByHotelIdAndChannelIdAndDateBetween(Integer hotelId, Integer channelId, Date startDate, Date endDate);
    
    /**
     * 精确查询五维模型下的单条库存记录。
     */
    Inventory findByHotelIdAndRatePlanIdAndRoomTypeIdAndChannelIdAndDate(Integer hotelId, Integer ratePlanId, Integer roomTypeId, Integer channelId, Date date);

    // =========================================================================
    // 聚合与预警查询
    // =========================================================================

    /**
     * 查询库存预警记录。
     * 找出指定日期范围内可用房间数低于或等于阈值的活跃库存记录。
     * 
     * @param threshold 预警阈值（通常为 5 或 10）
     * @param startDate 统计起始日期
     * @param endDate 统计截止日期
     * @return 预警列表
     */
    @org.springframework.data.jpa.repository.Query(
            "SELECT i FROM Inventory i WHERE i.availableRooms <= :threshold AND i.date >= :startDate AND i.date <= :endDate AND i.status = 'active' ORDER BY i.date, i.hotelCode")
    List<Inventory> findLowInventory(@org.springframework.data.repository.query.Param("threshold") int threshold,
                                     @org.springframework.data.repository.query.Param("startDate") Date startDate,
                                     @org.springframework.data.repository.query.Param("endDate") Date endDate);

    /**
     * 查询指定酒店在某一天的所有记录。
     */
    List<Inventory> findByHotelIdAndDate(Integer hotelId, Date date);

    // =========================================================================
    // 外部编码 (Code) 优先的查询方法（推荐使用）
    // =========================================================================

    /**
     * 根据酒店编码获取所有库存。
     */
    List<Inventory> findByHotelCode(String hotelCode);

    /**
     * 根据酒店编码和日期范围获取库存。
     */
    List<Inventory> findByHotelCodeAndDateBetween(String hotelCode, Date startDate, Date endDate);

    /**
     * 跨日期查询酒店、计划和房型的库存组合。
     */
    List<Inventory> findByHotelCodeAndRatePlanCodeAndRoomTypeCode(String hotelCode, String ratePlanCode, String roomTypeCode);

    /**
     * 精确查询特定编码组合下的单日库存。
     */
    Inventory findByHotelCodeAndRatePlanCodeAndRoomTypeCodeAndDate(String hotelCode, String ratePlanCode, String roomTypeCode, Date date);

    /**
     * 精确查询特定编码组合下的时间段库存。
     */
    List<Inventory> findByHotelCodeAndRatePlanCodeAndRoomTypeCodeAndDateBetween(String hotelCode, String ratePlanCode, String roomTypeCode, Date startDate, Date endDate);

    /**
     * 根据酒店和渠道编码查询库存。
     */
    List<Inventory> findByHotelCodeAndChannelCode(String hotelCode, String channelCode);

    /**
     * 根据酒店、渠道及日期范围查询。
     */
    List<Inventory> findByHotelCodeAndChannelCodeAndDateBetween(String hotelCode, String channelCode, Date startDate, Date endDate);

    /**
     * 五维编码模型下的精确库存定位。
     */
    Inventory findByHotelCodeAndRatePlanCodeAndRoomTypeCodeAndChannelCodeAndDate(String hotelCode, String ratePlanCode, String roomTypeCode, String channelCode, Date date);

    /**
     * 根据酒店编码和指定日期查询。
     */
    List<Inventory> findByHotelCodeAndDate(String hotelCode, Date date);

    /**
     * 根据酒店编码和状态查询。
     */
    List<Inventory> findByHotelCodeAndStatus(String hotelCode, Inventory.Status status);
}
