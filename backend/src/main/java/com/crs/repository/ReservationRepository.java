package com.crs.repository;

import com.crs.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;
import java.util.Optional;

/**
 * 预订订单数据访问接口 (ReservationRepository)
 * 
 * <p>
 * 提供对 {@link Reservation} 实体的全方位数据库交互能力。包含复杂的动态过滤查询、多维度数据统计以及 Dashboard 聚合分析。
 * </p>
 * 
 * <p>
 * 规范要求：
 * </p>
 * <ul>
 * <li>**多租户安全**：所有查询必须显式包含 `tenantId` 过滤，严禁出现跨租户数据访问。</li>
 * <li>**编码关联**：优先使用 `hotelCode`, `reservationCode`, `channelCode` 进行业务定位。</li>
 * <li>**性能优化**：Dashboard 相关的统计查询应尽量利用数据库聚合函数，避免加载大量实体对象。</li>
 * </ul>
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

        /**
         * 在租户维度下，根据预订码安全查询订单。
         */
        Optional<Reservation> findByReservationCodeAndTenantId(String reservationCode, Integer tenantId);

        /** 别名查询：租户维度下根据预订码查询 */
        Optional<Reservation> findByTenantIdAndReservationCode(Integer tenantId, String reservationCode);

        /**
         * 校验特定渠道下单号是否已存在（用于接口幂等性校验，基于 channelCode）。
         */
        boolean existsByTenantIdAndChannelCodeAndChannelOrderNumber(Integer tenantId, String channelCode, String channelOrderNumber);

        /** 获取酒店在特定日期范围内入住的订单 (基于 hotelCode) */
        List<Reservation> findByTenantIdAndHotelCodeAndCheckInDateBetween(Integer tenantId, String hotelCode, Date startDate, Date endDate);

        /** 获取酒店在特定日期范围内退房的订单 (基于 hotelCode) */
        List<Reservation> findByTenantIdAndHotelCodeAndCheckOutDateBetween(Integer tenantId, String hotelCode, Date startDate, Date endDate);

        /** 根据支付状态过滤 (基于 hotelCode) */
        List<Reservation> findByTenantIdAndHotelCodeAndPaymentStatus(Integer tenantId, String hotelCode, String paymentStatus);

        /** 按创建时间范围查询 (基于 hotelCode) */
        List<Reservation> findByTenantIdAndHotelCodeAndCreatedAtBetween(Integer tenantId, String hotelCode, Date startDate, Date endDate);

        // 旧的 ID 过滤查询已移除，请使用下方 findWithFiltersByCode 和 findListWithFiltersByCode

        /** 统计租户下特定业务状态的订单总数 */
        long countByTenantIdAndReservationStatus(Integer tenantId, String reservationStatus);

        // =========================================================================
        // Dashboard 聚合查询方法
        // =========================================================================

        /** 统计指定租户在指定时间之后创建的订单数 */
        long countByTenantIdAndCreatedAtGreaterThanEqual(Integer tenantId, Date since);

        /** 统计指定租户在指定日期入住的订单数 (排除特定状态) */
        long countByTenantIdAndCheckInDateAndStatusNot(Integer tenantId, Date checkInDate,
                        Reservation.Status excludeStatus);

        /** 统计需要人工介入（isManual=true）的活跃订单数 */
        long countByTenantIdAndIsManualAndStatus(Integer tenantId, Boolean isManual, Reservation.Status status);

        /** 汇总指定租户在日期范围内的总收入（排除取消订单） */
        @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r WHERE r.tenantId = :tenantId AND r.status <> 'cancelled' AND r.createdAt >= :startDate AND r.createdAt <= :endDate")
        java.math.BigDecimal sumTotalPriceByTenantIdAndDateRange(@Param("tenantId") Integer tenantId,
                        @Param("startDate") Date startDate, @Param("endDate") Date endDate);

        /** 获取租户下最近的订单快照 */
        List<Reservation> findTop10ByTenantIdAndStatusNotOrderByCreatedAtDesc(Integer tenantId,
                        Reservation.Status excludeStatus);

        /** 获取租户下最近订单快照（投影，避免触发跨酒店重复编码关联） */
        @Query("SELECT r.reservationCode, r.hotelName, r.roomTypeName, r.ratePlanName, r.channelName, " +
               "r.contactName, r.checkInDate, r.checkOutDate, r.nights, r.roomCount, r.totalPrice, " +
               "r.reservationStatus, r.paymentStatus, r.createdAt " +
               "FROM Reservation r WHERE r.tenantId = :tenantId AND r.status <> :excludeStatus " +
               "ORDER BY r.createdAt DESC")
        List<Object[]> findTop10SnapshotByTenantIdAndStatusNotOrderByCreatedAtDesc(
                        @Param("tenantId") Integer tenantId,
                        @Param("excludeStatus") Reservation.Status excludeStatus,
                        Pageable pageable);

        /** 按渠道分组统计订单贡献分布 (租户维度) */
        @Query("SELECT r.channelName, COUNT(r) FROM Reservation r WHERE r.tenantId = :tenantId AND r.status <> 'cancelled' AND r.createdAt >= :startDate GROUP BY r.channelName ORDER BY COUNT(r) DESC")
        List<Object[]> countByChannelGrouped(@Param("tenantId") Integer tenantId, @Param("startDate") Date startDate);

        /** 租户维度：按日期分组统计订单量趋势 */
        @Query("SELECT FUNCTION('DATE', r.createdAt), COUNT(r) FROM Reservation r WHERE r.tenantId = :tenantId AND r.status <> 'cancelled' AND r.createdAt >= :startDate GROUP BY FUNCTION('DATE', r.createdAt) ORDER BY FUNCTION('DATE', r.createdAt)")
        List<Object[]> countByDateGrouped(@Param("tenantId") Integer tenantId, @Param("startDate") Date startDate);

        // =========================================================================
        // 基于 CODE 关联的合规查询方法
        // =========================================================================

        List<Reservation> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

        List<Reservation> findByTenantIdAndHotelCodeAndReservationStatus(Integer tenantId, String hotelCode, String reservationStatus);

        List<Reservation> findByTenantIdAndHotelCodeAndChannelCode(Integer tenantId, String hotelCode, String channelCode);

        /** 动态多条件分页过滤查询 (使用 hotelCode) */
        @Query("SELECT r FROM Reservation r WHERE " +
                        "(:tenantId IS NULL OR r.tenantId = :tenantId) AND " +
                        "(:hotelCode IS NULL OR :hotelCode = '' OR r.hotelCode = :hotelCode) AND " +
                        "(:orderNo IS NULL OR :orderNo = '' OR r.reservationCode LIKE %:orderNo% OR r.channelOrderNumber LIKE %:orderNo%) AND "
                        +
                        "(:reservationStatus IS NULL OR :reservationStatus = '' OR r.reservationStatus = :reservationStatus) AND "
                        +
                        "(:channelCode IS NULL OR :channelCode = '' OR r.channelCode = :channelCode) AND " +
                        "(:guestName IS NULL OR :guestName = '' OR r.contactName LIKE %:guestName%) AND " +
                        "(:startDate IS NULL OR r.createdAt >= :startDate) AND " +
                        "(:endDate IS NULL OR r.createdAt <= :endDate) AND " +
                        "(:checkInStart IS NULL OR r.checkInDate >= :checkInStart) AND " +
                        "(:checkInEnd IS NULL OR r.checkInDate <= :checkInEnd) " +
                        "ORDER BY r.createdAt DESC")
        Page<Reservation> findWithFiltersByCode(
                        @Param("tenantId") Integer tenantId,
                        @Param("hotelCode") String hotelCode,
                        @Param("orderNo") String orderNo,
                        @Param("reservationStatus") String reservationStatus,
                        @Param("channelCode") String channelCode,
                        @Param("guestName") String guestName,
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate,
                        @Param("checkInStart") Date checkInStart,
                        @Param("checkInEnd") Date checkInEnd,
                        Pageable pageable);

        /** 动态多条件列表查询 (使用 hotelCode) */
        @Query("SELECT r FROM Reservation r WHERE " +
                        "(:tenantId IS NULL OR r.tenantId = :tenantId) AND " +
                        "(:hotelCode IS NULL OR :hotelCode = '' OR r.hotelCode = :hotelCode) AND " +
                        "(:orderNo IS NULL OR :orderNo = '' OR r.reservationCode LIKE %:orderNo% OR r.channelOrderNumber LIKE %:orderNo%) AND "
                        +
                        "(:reservationStatus IS NULL OR :reservationStatus = '' OR r.reservationStatus = :reservationStatus) AND "
                        +
                        "(:channelCode IS NULL OR :channelCode = '' OR r.channelCode = :channelCode) AND " +
                        "(:guestName IS NULL OR :guestName = '' OR r.contactName LIKE %:guestName%) AND " +
                        "(:startDate IS NULL OR r.createdAt >= :startDate) AND " +
                        "(:endDate IS NULL OR r.createdAt <= :endDate) AND " +
                        "(:checkInStart IS NULL OR r.checkInDate >= :checkInStart) AND " +
                        "(:checkInEnd IS NULL OR r.checkInDate <= :checkInEnd) " +
                        "ORDER BY r.createdAt DESC")
        List<Reservation> findListWithFiltersByCode(
                        @Param("tenantId") Integer tenantId,
                        @Param("hotelCode") String hotelCode,
                        @Param("orderNo") String orderNo,
                        @Param("reservationStatus") String reservationStatus,
                        @Param("channelCode") String channelCode,
                        @Param("guestName") String guestName,
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate,
                        @Param("checkInStart") Date checkInStart,
                        @Param("checkInEnd") Date checkInEnd);

        /** 统计酒店下特定状态的订单数 (使用 hotelCode) */
        long countByTenantIdAndHotelCodeAndReservationStatus(Integer tenantId, String hotelCode,
                        String reservationStatus);

        /** 获取酒店最近的订单列表 (使用 hotelCode) */
        List<Reservation> findTop10ByTenantIdAndHotelCodeOrderByCreatedAtDesc(Integer tenantId, String hotelCode);

        /** 获取酒店最近订单快照（投影，避免触发跨酒店重复编码关联） */
        @Query("SELECT r.reservationCode, r.hotelName, r.roomTypeName, r.ratePlanName, r.channelName, " +
               "r.contactName, r.checkInDate, r.checkOutDate, r.nights, r.roomCount, r.totalPrice, " +
               "r.reservationStatus, r.paymentStatus, r.createdAt " +
               "FROM Reservation r WHERE r.tenantId = :tenantId AND r.hotelCode = :hotelCode " +
               "ORDER BY r.createdAt DESC")
        List<Object[]> findTop10SnapshotByTenantIdAndHotelCodeOrderByCreatedAtDesc(
                        @Param("tenantId") Integer tenantId,
                        @Param("hotelCode") String hotelCode,
                        Pageable pageable);

        /** 按渠道编码分组统计 (租户维度) */
        @Query("SELECT r.channelCode, COUNT(r) FROM Reservation r WHERE r.tenantId = :tenantId AND r.status <> 'cancelled' AND r.createdAt >= :startDate GROUP BY r.channelCode ORDER BY COUNT(r) DESC")
        List<Object[]> countByChannelCodeGrouped(@Param("tenantId") Integer tenantId,
                        @Param("startDate") Date startDate);

        /** 按渠道编码分组统计 (酒店维度，使用 hotelCode) */
        @Query("SELECT r.channelCode, COUNT(r) FROM Reservation r WHERE r.tenantId = :tenantId AND r.hotelCode = :hotelCode AND r.status <> 'cancelled' AND r.createdAt >= :startDate GROUP BY r.channelCode ORDER BY COUNT(r) DESC")
        List<Object[]> countByHotelCodeAndChannelCodeGrouped(@Param("tenantId") Integer tenantId,
                        @Param("hotelCode") String hotelCode, @Param("startDate") Date startDate);

        /** 按日期分组统计趋势 (酒店维度，使用 hotelCode) */
        @Query("SELECT FUNCTION('DATE', r.createdAt), COUNT(r) FROM Reservation r WHERE r.tenantId = :tenantId AND r.hotelCode = :hotelCode AND r.status <> 'cancelled' AND r.createdAt >= :startDate GROUP BY FUNCTION('DATE', r.createdAt) ORDER BY FUNCTION('DATE', r.createdAt)")
        List<Object[]> countByHotelCodeAndDateGrouped(@Param("tenantId") Integer tenantId,
                        @Param("hotelCode") String hotelCode, @Param("startDate") Date startDate);

        /** 统计日期范围内的总收入 (酒店维度，使用 hotelCode) */
        @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r WHERE r.tenantId = :tenantId AND r.hotelCode = :hotelCode AND r.status <> 'cancelled' AND r.createdAt >= :startDate AND r.createdAt <= :endDate")
        java.math.BigDecimal sumTotalPriceByTenantIdAndHotelCodeAndDateRange(@Param("tenantId") Integer tenantId,
                        @Param("hotelCode") String hotelCode, @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate);

        /** 统计指定日期的入住订单数 (酒店维度，使用 hotelCode) */
        long countByTenantIdAndHotelCodeAndCheckInDateAndStatusNot(Integer tenantId, String hotelCode, Date checkInDate,
                        Reservation.Status excludeStatus);

        /** 统计指定日期的退房订单数 (酒店维度，使用 hotelCode) */
        long countByTenantIdAndHotelCodeAndCheckOutDateAndStatusNot(Integer tenantId, String hotelCode,
                        Date checkOutDate, Reservation.Status excludeStatus);

        /** 统计当前在住订单数 (酒店维度，使用 hotelCode) */
        long countByTenantIdAndHotelCodeAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanAndStatus(
                        Integer tenantId, String hotelCode, Date checkInDate, Date checkOutDate,
                        Reservation.Status status);

        /** 统计指定日期后创建的订单数 (酒店维度，使用 hotelCode) */
        long countByTenantIdAndHotelCodeAndCreatedAtGreaterThanEqual(Integer tenantId, String hotelCode, Date since);

        // =========================================================================
        // 高级经营分析聚合 (用于首页驾驶舱)
        // =========================================================================

        /**
         * 渠道价值矩阵统计 (酒店维度)
         * 返回: [渠道名称, 订单量, 总营收, 总间夜量]
         */
        @Query("SELECT r.channelName, COUNT(r), COALESCE(SUM(r.totalPrice), 0), COALESCE(SUM(r.nights * r.roomCount), 0) " +
               "FROM Reservation r WHERE r.tenantId = :tenantId AND r.hotelCode = :hotelCode " +
               "AND r.status <> 'cancelled' AND r.createdAt >= :startDate " +
               "GROUP BY r.channelName")
        List<Object[]> getChannelMatrixStats(@Param("tenantId") Integer tenantId, 
                                            @Param("hotelCode") String hotelCode, 
                                            @Param("startDate") Date startDate);

        /**
         * 每日经营业绩统计 (酒店维度，基于入住日期)
         * 返回: [入住日期, 已售间夜数, 营收金额]
         */
        @Query("SELECT r.checkInDate, COALESCE(SUM(r.nights * r.roomCount), 0), COALESCE(SUM(r.totalPrice), 0) " +
               "FROM Reservation r WHERE r.tenantId = :tenantId AND r.hotelCode = :hotelCode " +
               "AND r.status <> 'cancelled' AND r.checkInDate >= :startDate AND r.checkInDate <= :endDate " +
               "GROUP BY r.checkInDate ORDER BY r.checkInDate")
        List<Object[]> getDailyStatsByCheckIn(@Param("tenantId") Integer tenantId, 
                                             @Param("hotelCode") String hotelCode, 
                                             @Param("startDate") Date startDate, 
                                             @Param("endDate") Date endDate);

        /**
         * 预订流速统计 (Pickup - 酒店维度)
         * 返回: [创建日期, 新增预订间夜数]
         */
        @Query("SELECT FUNCTION('DATE', r.createdAt), COALESCE(SUM(r.nights * r.roomCount), 0) " +
               "FROM Reservation r WHERE r.tenantId = :tenantId AND r.hotelCode = :hotelCode " +
               "AND r.status <> 'cancelled' AND r.createdAt >= :startDate " +
               "GROUP BY FUNCTION('DATE', r.createdAt) ORDER BY FUNCTION('DATE', r.createdAt)")
        List<Object[]> getRecentPickupStatsByHotel(@Param("tenantId") Integer tenantId, 
                                                  @Param("hotelCode") String hotelCode, 
                                                  @Param("startDate") Date startDate);

        /**
         * 集团酒店业绩排行榜 (营收维度)
         * 返回: [酒店名称, 总营收, 订单量]
         */
        @Query("SELECT r.hotelName, SUM(r.totalPrice), COUNT(r) " +
               "FROM Reservation r WHERE r.tenantId = :tenantId " +
               "AND r.status <> 'cancelled' AND r.createdAt >= :startDate " +
               "GROUP BY r.hotelName ORDER BY SUM(r.totalPrice) DESC")
        List<Object[]> getGroupHotelRankingByRevenue(@Param("tenantId") Integer tenantId, 
                                                    @Param("startDate") Date startDate);

        /**
         * 全租户预订流速统计 (Pickup - 集团维度)
         * 返回: [创建日期, 新增预订间夜数]
         */
        @Query("SELECT FUNCTION('DATE', r.createdAt), SUM(r.nights * r.roomCount) " +
               "FROM Reservation r WHERE r.tenantId = :tenantId " +
               "AND r.status <> 'cancelled' AND r.createdAt >= :startDate " +
               "GROUP BY FUNCTION('DATE', r.createdAt) ORDER BY FUNCTION('DATE', r.createdAt)")
        List<Object[]> getRecentPickupStatsByTenant(@Param("tenantId") Integer tenantId, 
                                                   @Param("startDate") Date startDate);
}
