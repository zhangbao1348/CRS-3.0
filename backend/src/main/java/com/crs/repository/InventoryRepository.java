package com.crs.repository;

import com.crs.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;

/**
 * 库存仓库接口
 * 用于库存数据的CRUD操作
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    
    /**
     * 根据酒店ID查询库存列表
     * @param hotelId 酒店ID
     * @return 库存列表
     */
    List<Inventory> findByHotelId(Integer hotelId);
    
    /**
     * 根据酒店ID和状态查询库存列表
     * @param hotelId 酒店ID
     * @param status 状态
     * @return 库存列表
     */
    List<Inventory> findByHotelIdAndStatus(Integer hotelId, Inventory.Status status);
    
    /**
     * 根据酒店ID、价格计划ID和房型ID查询库存
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @return 库存列表
     */
    List<Inventory> findByHotelIdAndRatePlanIdAndRoomTypeId(Integer hotelId, Integer ratePlanId, Integer roomTypeId);
    
    /**
     * 根据日期范围查询库存
     * @param hotelId 酒店ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 库存列表
     */
    List<Inventory> findByHotelIdAndDateBetween(Integer hotelId, Date startDate, Date endDate);
    
    /**
     * 根据酒店ID、价格计划ID、房型ID和日期查询库存
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param date 日期
     * @return 库存信息
     */
    Inventory findByHotelIdAndRatePlanIdAndRoomTypeIdAndDate(Integer hotelId, Integer ratePlanId, Integer roomTypeId, Date date);
    
    /**
     * 根据酒店ID、价格计划ID、房型ID和日期范围查询库存
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 库存列表
     */
    List<Inventory> findByHotelIdAndRatePlanIdAndRoomTypeIdAndDateBetween(Integer hotelId, Integer ratePlanId, Integer roomTypeId, Date startDate, Date endDate);
    
    /**
     * 根据状态查询库存
     * @param status 状态
     * @return 库存列表
     */
    List<Inventory> findByStatus(Inventory.Status status);
    
    /**
     * 根据渠道ID查询库存
     * @param channelId 渠道ID
     * @return 库存列表
     */
    List<Inventory> findByChannelId(Integer channelId);
    
    /**
     * 根据酒店ID和渠道ID查询库存
     * @param hotelId 酒店ID
     * @param channelId 渠道ID
     * @return 库存列表
     */
    List<Inventory> findByHotelIdAndChannelId(Integer hotelId, Integer channelId);
    
    /**
     * 根据酒店ID、渠道ID和日期范围查询库存
     * @param hotelId 酒店ID
     * @param channelId 渠道ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 库存列表
     */
    List<Inventory> findByHotelIdAndChannelIdAndDateBetween(Integer hotelId, Integer channelId, Date startDate, Date endDate);
    
    /**
     * 根据酒店ID、价格计划ID、房型ID、渠道ID和日期查询库存
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param channelId 渠道ID
     * @param date 日期
     * @return 库存信息
     */
    Inventory findByHotelIdAndRatePlanIdAndRoomTypeIdAndChannelIdAndDate(Integer hotelId, Integer ratePlanId, Integer roomTypeId, Integer channelId, Date date);

    // =========================================================================
    // Dashboard 聚合查询方法
    // =========================================================================

    /**
     * 查询库存预警：指定日期范围内可用库存 <= threshold 的记录
     * @param threshold 预警阈值
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 低库存记录列表
     */
    @org.springframework.data.jpa.repository.Query(
            "SELECT i FROM Inventory i WHERE i.availableRooms <= :threshold AND i.date >= :startDate AND i.date <= :endDate AND i.status = 'active' ORDER BY i.date, i.hotelCode")
    List<Inventory> findLowInventory(@org.springframework.data.repository.query.Param("threshold") int threshold,
                                     @org.springframework.data.repository.query.Param("startDate") Date startDate,
                                     @org.springframework.data.repository.query.Param("endDate") Date endDate);

    /**
     * 查询指定酒店在某一天的所有库存
     * @param hotelId 酒店ID
     * @param date 日期
     * @return 库存列表
     */
    List<Inventory> findByHotelIdAndDate(Integer hotelId, Date date);

    List<Inventory> findByHotelCode(String hotelCode);

    List<Inventory> findByHotelCodeAndDateBetween(String hotelCode, Date startDate, Date endDate);

    List<Inventory> findByHotelCodeAndRatePlanCodeAndRoomTypeCode(String hotelCode, String ratePlanCode, String roomTypeCode);

    Inventory findByHotelCodeAndRatePlanCodeAndRoomTypeCodeAndDate(String hotelCode, String ratePlanCode, String roomTypeCode, Date date);

    List<Inventory> findByHotelCodeAndRatePlanCodeAndRoomTypeCodeAndDateBetween(String hotelCode, String ratePlanCode, String roomTypeCode, Date startDate, Date endDate);

    List<Inventory> findByHotelCodeAndChannelCode(String hotelCode, String channelCode);

    List<Inventory> findByHotelCodeAndChannelCodeAndDateBetween(String hotelCode, String channelCode, Date startDate, Date endDate);

    Inventory findByHotelCodeAndRatePlanCodeAndRoomTypeCodeAndChannelCodeAndDate(String hotelCode, String ratePlanCode, String roomTypeCode, String channelCode, Date date);

    List<Inventory> findByHotelCodeAndDate(String hotelCode, Date date);

    List<Inventory> findByHotelCodeAndStatus(String hotelCode, Inventory.Status status);
}
