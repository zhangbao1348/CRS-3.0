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
         * 根据系统唯一预订码查询订单。
         */
        Reservation findByReservationCode(String reservationCode);

        /**
         * 在租户维度下，根据预订码安全查询订单。
         */
        Optional<Reservation> findByReservationCodeAndTenantId(String reservationCode, Integer tenantId);

        /**
         * 校验特定渠道下单号是否已存在（用于接口幂等性校验）。
         */
        boolean existsByChannelIdAndChannelOrderNumber(Integer channelId, String channelOrderNumber);

        /**
         * 获取酒店下的所有订单。
         */
        List<Reservation> findByHotelId(Integer hotelId);

        /**
         * 获取酒店下特定逻辑状态的订单。
         */
        List<Reservation> findByHotelIdAndStatus(Integer hotelId, Reservation.Status status);

        /**
         * 获取酒店下特定业务状态的订单。
         */
        List<Reservation> findByHotelIdAndReservationStatus(Integer hotelId, String reservationStatus);

        /**
         * 获取渠道下的所有订单。
         */
        List<Reservation> findByChannelId(Integer channelId);

        /**
         * 获取酒店在特定日期范围内入住的订单。
         */
        List<Reservation> findByHotelIdAndCheckInDateBetween(Integer hotelId, Date startDate, Date endDate);

        /**
         * 获取酒店在特定日期范围内退房的订单。
         */
        List<Reservation> findByHotelIdAndCheckOutDateBetween(Integer hotelId, Date startDate, Date endDate);

        /**
         * 获取指定日期在住的订单列表。
         */
        List<Reservation> findByHotelIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanAndStatus(
                        Integer hotelId, Date date, Date date2, Reservation.Status status);

        /**
         * 根据支付状态过滤。
         */
        List<Reservation> findByHotelIdAndPaymentStatus(Integer hotelId, String paymentStatus);

        /**
         * 按创建时间范围查询。
         */
        List<Reservation> findByHotelIdAndCreatedAtBetween(Integer hotelId, Date startDate, Date endDate);

        /**
         * 动态多条件分页过滤查询。
         * 支持单号、状态、渠道、客人姓名、创建时间及入住日期等维度的组合搜索。
         */
        @Query("SELECT r FROM Reservation r WHERE " +
                        "(:tenantId IS NULL OR r.tenantId = :tenantId) AND " +
                        "(:hotelId IS NULL OR r.hotelId = :hotelId) AND " +
                        "(:orderNo IS NULL OR :orderNo = '' OR r.reservationCode LIKE %:orderNo% OR r.channelOrderNumber LIKE %:orderNo%) AND "
                        +
                        "(:reservationStatus IS NULL OR :reservationStatus = '' OR r.reservationStatus = :reservationStatus) AND "
                        +
                        "(:channelId IS NULL OR r.channelId = :channelId) AND " +
                        "(:guestName IS NULL OR :guestName = '' OR r.contactName LIKE %:guestName%) AND " +
                        "(:startDate IS NULL OR r.createdAt >= :startDate) AND " +
                        "(:endDate IS NULL OR r.createdAt <= :endDate) AND " +
                        "(:checkInStart IS NULL OR r.checkInDate >= :checkInStart) AND " +
                        "(:checkInEnd IS NULL OR r.checkInDate <= :checkInEnd) " +
                        "ORDER BY r.createdAt DESC")
        Page<Reservation> findWithFilters(
                        @Param("tenantId") Integer tenantId,
                        @Param("hotelId") Integer hotelId,
                        @Param("orderNo") String orderNo,
                        @Param("reservationStatus") String reservationStatus,
                        @Param("channelId") Integer channelId,
                        @Param("guestName") String guestName,
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate,
                        @Param("checkInStart") Date checkInStart,
                        @Param("checkInEnd") Date checkInEnd,
                        Pageable pageable);

        /**
         * 动态多条件列表查询。
         */
        @Query("SELECT r FROM Reservation r WHERE " +
                        "(:tenantId IS NULL OR r.tenantId = :tenantId) AND " +
                        "(:hotelId IS NULL OR r.hotelId = :hotelId) AND " +
                        "(:orderNo IS NULL OR :orderNo = '' OR r.reservationCode LIKE %:orderNo% OR r.channelOrderNumber LIKE %:orderNo%) AND "
                        +
                        "(:reservationStatus IS NULL OR :reservationStatus = '' OR r.reservationStatus = :reservationStatus) AND "
                        +
                        "(:channelId IS NULL OR r.channelId = :channelId) AND " +
                        "(:guestName IS NULL OR :guestName = '' OR r.contactName LIKE %:guestName%) AND " +
                        "(:startDate IS NULL OR r.createdAt >= :startDate) AND " +
                        "(:endDate IS NULL OR r.createdAt <= :endDate) AND " +
                        "(:checkInStart IS NULL OR r.checkInDate >= :checkInStart) AND " +
                        "(:checkInEnd IS NULL OR r.checkInDate <= :checkInEnd) " +
                        "ORDER BY r.createdAt DESC")
        List<Reservation> findListWithFilters(
                        @Param("tenantId") Integer tenantId,
                        @Param("hotelId") Integer hotelId,
                        @Param("orderNo") String orderNo,
                        @Param("reservationStatus") String reservationStatus,
                        @Param("channelId") Integer channelId,
                        @Param("guestName") String guestName,
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate,
                        @Param("checkInStart") Date checkInStart,
                        @Param("checkInEnd") Date checkInEnd);

        /** 统计租户下特定业务状态的订单总数 */
        long countByTenantIdAndReservationStatus(Integer tenantId, String reservationStatus);

        /** 统计酒店下特定业务状态的订单总数 */
        long countByTenantIdAndHotelIdAndReservationStatus(Integer tenantId, Integer hotelId, String reservationStatus);

        // =========================================================================
        // Dashboard 聚合查询方法
        // =========================================================================

        /** 统计指定租户在指定时间之后创建的订单数 */
        long countByTenantIdAndCreatedAtGreaterThanEqual(Integer tenantId, Date since);

        /** 统计指定租户+酒店在指定时间之后创建的订单数 */
        long countByTenantIdAndHotelIdAndCreatedAtGreaterThanEqual(Integer tenantId, Integer hotelId, Date since);

        /** 统计指定租户在指定日期入住的订单数 (排除特定状态) */
        long countByTenantIdAndCheckInDateAndStatusNot(Integer tenantId, Date checkInDate,
                        Reservation.Status excludeStatus);

        /** 统计指定租户+酒店在指定日期入住的订单数 */
        long countByTenantIdAndHotelIdAndCheckInDateAndStatusNot(Integer tenantId, Integer hotelId, Date checkInDate,
                        Reservation.Status excludeStatus);

        /** 统计指定租户+酒店在指定日期退房的订单数 */
        long countByTenantIdAndHotelIdAndCheckOutDateAndStatusNot(Integer tenantId, Integer hotelId, Date checkOutDate,
                        Reservation.Status excludeStatus);

        /** 统计需要人工介入（isManual=true）的活跃订单数 */
        long countByTenantIdAndIsManualAndStatus(Integer tenantId, Boolean isManual, Reservation.Status status);

        /**
         * 统计在住订单数（checkInDate <= today AND checkOutDate > today AND status = active）
         */
        long countByTenantIdAndHotelIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanAndStatus(
                        Integer tenantId, Integer hotelId, Date checkInDate, Date checkOutDate,
                        Reservation.Status status);

        /** 汇总指定租户在日期范围内的总收入（排除取消订单） */
        @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r WHERE r.tenantId = :tenantId AND r.status <> 'cancelled' AND r.createdAt >= :startDate AND r.createdAt <= :endDate")
        java.math.BigDecimal sumTotalPriceByTenantIdAndDateRange(@Param("tenantId") Integer tenantId,
                        @Param("startDate") Date startDate, @Param("endDate") Date endDate);

        /** 汇总指定租户+酒店在日期范围内的总收入 */
        @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r WHERE r.tenantId = :tenantId AND r.hotelId = :hotelId AND r.status <> 'cancelled' AND r.createdAt >= :startDate AND r.createdAt <= :endDate")
        java.math.BigDecimal sumTotalPriceByTenantIdAndHotelIdAndDateRange(@Param("tenantId") Integer tenantId,
                        @Param("hotelId") Integer hotelId, @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate);

        /** 获取租户下最近的订单快照 */
        List<Reservation> findTop10ByTenantIdAndStatusNotOrderByCreatedAtDesc(Integer tenantId,
                        Reservation.Status excludeStatus);

        /** 获取指定酒店最近的订单列表 */
        List<Reservation> findTop10ByTenantIdAndHotelIdOrderByCreatedAtDesc(Integer tenantId, Integer hotelId);

        /** 按渠道分组统计订单贡献分布 (租户维度) */
        @Query("SELECT r.channelName, COUNT(r) FROM Reservation r WHERE r.tenantId = :tenantId AND r.status <> 'cancelled' AND r.createdAt >= :startDate GROUP BY r.channelName ORDER BY COUNT(r) DESC")
        List<Object[]> countByChannelGrouped(@Param("tenantId") Integer tenantId, @Param("startDate") Date startDate);

        /** 按渠道分组统计订单贡献分布 (酒店维度) */
        @Query("SELECT r.channelName, COUNT(r) FROM Reservation r WHERE r.tenantId = :tenantId AND r.hotelId = :hotelId AND r.status <> 'cancelled' AND r.createdAt >= :startDate GROUP BY r.channelName ORDER BY COUNT(r) DESC")
        List<Object[]> countByHotelAndChannelGrouped(@Param("tenantId") Integer tenantId,
                        @Param("hotelId") Integer hotelId, @Param("startDate") Date startDate);

        /** 酒店维度：按日期分组统计订单量趋势 */
        @Query("SELECT FUNCTION('DATE', r.createdAt), COUNT(r) FROM Reservation r WHERE r.tenantId = :tenantId AND r.hotelId = :hotelId AND r.status <> 'cancelled' AND r.createdAt >= :startDate GROUP BY FUNCTION('DATE', r.createdAt) ORDER BY FUNCTION('DATE', r.createdAt)")
        List<Object[]> countByHotelAndDateGrouped(@Param("tenantId") Integer tenantId,
                        @Param("hotelId") Integer hotelId, @Param("startDate") Date startDate);

        /** 租户维度：按日期分组统计订单量趋势 */
        @Query("SELECT FUNCTION('DATE', r.createdAt), COUNT(r) FROM Reservation r WHERE r.tenantId = :tenantId AND r.status <> 'cancelled' AND r.createdAt >= :startDate GROUP BY FUNCTION('DATE', r.createdAt) ORDER BY FUNCTION('DATE', r.createdAt)")
        List<Object[]> countByDateGrouped(@Param("tenantId") Integer tenantId, @Param("startDate") Date startDate);

        // =========================================================================
        // 基于 CODE 关联的合规查询方法
        // =========================================================================

        List<Reservation> findByHotelCode(String hotelCode);

        List<Reservation> findByHotelCodeAndReservationStatus(String hotelCode, String reservationStatus);

        List<Reservation> findByHotelCodeAndChannelCode(String hotelCode, String channelCode);

        List<Reservation> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

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
}
