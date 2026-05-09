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

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    Reservation findByReservationCode(String reservationCode);

    Optional<Reservation> findByReservationCodeAndTenantId(String reservationCode, Integer tenantId);

    boolean existsByChannelIdAndChannelOrderNumber(Integer channelId, String channelOrderNumber);

    List<Reservation> findByHotelId(Integer hotelId);

    List<Reservation> findByHotelIdAndStatus(Integer hotelId, Reservation.Status status);

    List<Reservation> findByHotelIdAndReservationStatus(Integer hotelId, String reservationStatus);

    List<Reservation> findByChannelId(Integer channelId);

    List<Reservation> findByHotelIdAndCheckInDateBetween(Integer hotelId, Date startDate, Date endDate);

    List<Reservation> findByHotelIdAndCheckOutDateBetween(Integer hotelId, Date startDate, Date endDate);

    List<Reservation> findByHotelIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanAndStatus(
            Integer hotelId, Date date, Date date2, Reservation.Status status);

    List<Reservation> findByHotelIdAndPaymentStatus(Integer hotelId, String paymentStatus);

    List<Reservation> findByHotelIdAndCreatedAtBetween(Integer hotelId, Date startDate, Date endDate);

    @Query("SELECT r FROM Reservation r WHERE " +
            "(:tenantId IS NULL OR r.tenantId = :tenantId) AND " +
            "(:hotelId IS NULL OR r.hotelId = :hotelId) AND " +
            "(:orderNo IS NULL OR :orderNo = '' OR r.reservationCode LIKE %:orderNo% OR r.channelOrderNumber LIKE %:orderNo%) AND " +
            "(:reservationStatus IS NULL OR :reservationStatus = '' OR r.reservationStatus = :reservationStatus) AND " +
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

    @Query("SELECT r FROM Reservation r WHERE " +
            "(:tenantId IS NULL OR r.tenantId = :tenantId) AND " +
            "(:hotelId IS NULL OR r.hotelId = :hotelId) AND " +
            "(:orderNo IS NULL OR :orderNo = '' OR r.reservationCode LIKE %:orderNo% OR r.channelOrderNumber LIKE %:orderNo%) AND " +
            "(:reservationStatus IS NULL OR :reservationStatus = '' OR r.reservationStatus = :reservationStatus) AND " +
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

    long countByTenantIdAndReservationStatus(Integer tenantId, String reservationStatus);

    long countByTenantIdAndHotelIdAndReservationStatus(Integer tenantId, Integer hotelId, String reservationStatus);

    // =========================================================================
    // Dashboard 聚合查询方法
    // =========================================================================

    /** 统计指定租户在指定时间之后创建的订单数 */
    long countByTenantIdAndCreatedAtGreaterThanEqual(Integer tenantId, Date since);

    /** 统计指定租户+酒店在指定时间之后创建的订单数 */
    long countByTenantIdAndHotelIdAndCreatedAtGreaterThanEqual(Integer tenantId, Integer hotelId, Date since);

    /** 统计指定租户在指定日期入住的订单数 */
    long countByTenantIdAndCheckInDateAndStatusNot(Integer tenantId, Date checkInDate, Reservation.Status excludeStatus);

    /** 统计指定租户+酒店在指定日期入住的订单数 */
    long countByTenantIdAndHotelIdAndCheckInDateAndStatusNot(Integer tenantId, Integer hotelId, Date checkInDate, Reservation.Status excludeStatus);

    /** 统计指定租户+酒店在指定日期退房的订单数 */
    long countByTenantIdAndHotelIdAndCheckOutDateAndStatusNot(Integer tenantId, Integer hotelId, Date checkOutDate, Reservation.Status excludeStatus);

    /** 统计需要人工介入的订单数 */
    long countByTenantIdAndIsManualAndStatus(Integer tenantId, Boolean isManual, Reservation.Status status);

    /** 统计在住订单数（checkInDate <= today AND checkOutDate > today AND status = active） */
    long countByTenantIdAndHotelIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanAndStatus(
            Integer tenantId, Integer hotelId, Date checkInDate, Date checkOutDate, Reservation.Status status);

    /** 汇总指定租户在日期范围内的总收入 */
    @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r WHERE r.tenantId = :tenantId AND r.status <> 'cancelled' AND r.createdAt >= :startDate AND r.createdAt <= :endDate")
    java.math.BigDecimal sumTotalPriceByTenantIdAndDateRange(@Param("tenantId") Integer tenantId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /** 汇总指定租户+酒店在日期范围内的总收入 */
    @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r WHERE r.tenantId = :tenantId AND r.hotelId = :hotelId AND r.status <> 'cancelled' AND r.createdAt >= :startDate AND r.createdAt <= :endDate")
    java.math.BigDecimal sumTotalPriceByTenantIdAndHotelIdAndDateRange(@Param("tenantId") Integer tenantId, @Param("hotelId") Integer hotelId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /** 获取最近的订单列表 */
    List<Reservation> findTop10ByTenantIdAndStatusNotOrderByCreatedAtDesc(Integer tenantId, Reservation.Status excludeStatus);

    /** 获取指定酒店最近的订单列表 */
    List<Reservation> findTop10ByTenantIdAndHotelIdOrderByCreatedAtDesc(Integer tenantId, Integer hotelId);

    /** 按渠道分组统计订单数 */
    @Query("SELECT r.channelName, COUNT(r) FROM Reservation r WHERE r.tenantId = :tenantId AND r.status <> 'cancelled' AND r.createdAt >= :startDate GROUP BY r.channelName ORDER BY COUNT(r) DESC")
    List<Object[]> countByChannelGrouped(@Param("tenantId") Integer tenantId, @Param("startDate") Date startDate);

    @Query("SELECT r.channelName, COUNT(r) FROM Reservation r WHERE r.tenantId = :tenantId AND r.hotelId = :hotelId AND r.status <> 'cancelled' AND r.createdAt >= :startDate GROUP BY r.channelName ORDER BY COUNT(r) DESC")
    List<Object[]> countByHotelAndChannelGrouped(@Param("tenantId") Integer tenantId, @Param("hotelId") Integer hotelId, @Param("startDate") Date startDate);

    /** 按日期分组统计指定酒店的订单数（使用hotelId） */
    @Query("SELECT FUNCTION('DATE', r.createdAt), COUNT(r) FROM Reservation r WHERE r.tenantId = :tenantId AND r.hotelId = :hotelId AND r.status <> 'cancelled' AND r.createdAt >= :startDate GROUP BY FUNCTION('DATE', r.createdAt) ORDER BY FUNCTION('DATE', r.createdAt)")
    List<Object[]> countByHotelAndDateGrouped(@Param("tenantId") Integer tenantId, @Param("hotelId") Integer hotelId, @Param("startDate") Date startDate);

    /** 按日期分组统计订单数（用于趋势图） */
    @Query("SELECT FUNCTION('DATE', r.createdAt), COUNT(r) FROM Reservation r WHERE r.tenantId = :tenantId AND r.status <> 'cancelled' AND r.createdAt >= :startDate GROUP BY FUNCTION('DATE', r.createdAt) ORDER BY FUNCTION('DATE', r.createdAt)")
    List<Object[]> countByDateGrouped(@Param("tenantId") Integer tenantId, @Param("startDate") Date startDate);

    List<Reservation> findByHotelCode(String hotelCode);

    List<Reservation> findByHotelCodeAndReservationStatus(String hotelCode, String reservationStatus);

    List<Reservation> findByHotelCodeAndChannelCode(String hotelCode, String channelCode);

    List<Reservation> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    @Query("SELECT r FROM Reservation r WHERE " +
            "(:tenantId IS NULL OR r.tenantId = :tenantId) AND " +
            "(:hotelCode IS NULL OR :hotelCode = '' OR r.hotelCode = :hotelCode) AND " +
            "(:orderNo IS NULL OR :orderNo = '' OR r.reservationCode LIKE %:orderNo% OR r.channelOrderNumber LIKE %:orderNo%) AND " +
            "(:reservationStatus IS NULL OR :reservationStatus = '' OR r.reservationStatus = :reservationStatus) AND " +
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

    @Query("SELECT r FROM Reservation r WHERE " +
            "(:tenantId IS NULL OR r.tenantId = :tenantId) AND " +
            "(:hotelCode IS NULL OR :hotelCode = '' OR r.hotelCode = :hotelCode) AND " +
            "(:orderNo IS NULL OR :orderNo = '' OR r.reservationCode LIKE %:orderNo% OR r.channelOrderNumber LIKE %:orderNo%) AND " +
            "(:reservationStatus IS NULL OR :reservationStatus = '' OR r.reservationStatus = :reservationStatus) AND " +
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

    long countByTenantIdAndHotelCodeAndReservationStatus(Integer tenantId, String hotelCode, String reservationStatus);

    List<Reservation> findTop10ByTenantIdAndHotelCodeOrderByCreatedAtDesc(Integer tenantId, String hotelCode);

    /** 按渠道CODE分组统计订单数 */
    @Query("SELECT r.channelCode, COUNT(r) FROM Reservation r WHERE r.tenantId = :tenantId AND r.status <> 'cancelled' AND r.createdAt >= :startDate GROUP BY r.channelCode ORDER BY COUNT(r) DESC")
    List<Object[]> countByChannelCodeGrouped(@Param("tenantId") Integer tenantId, @Param("startDate") Date startDate);

    /** 按渠道CODE分组统计指定酒店的订单数 */
    @Query("SELECT r.channelCode, COUNT(r) FROM Reservation r WHERE r.tenantId = :tenantId AND r.hotelCode = :hotelCode AND r.status <> 'cancelled' AND r.createdAt >= :startDate GROUP BY r.channelCode ORDER BY COUNT(r) DESC")
    List<Object[]> countByHotelCodeAndChannelCodeGrouped(@Param("tenantId") Integer tenantId, @Param("hotelCode") String hotelCode, @Param("startDate") Date startDate);

    /** 按日期分组统计指定酒店的订单数（使用hotelCode） */
    @Query("SELECT FUNCTION('DATE', r.createdAt), COUNT(r) FROM Reservation r WHERE r.tenantId = :tenantId AND r.hotelCode = :hotelCode AND r.status <> 'cancelled' AND r.createdAt >= :startDate GROUP BY FUNCTION('DATE', r.createdAt) ORDER BY FUNCTION('DATE', r.createdAt)")
    List<Object[]> countByHotelCodeAndDateGrouped(@Param("tenantId") Integer tenantId, @Param("hotelCode") String hotelCode, @Param("startDate") Date startDate);

    /** 汇总指定租户+酒店CODE在日期范围内的总收入 */
    @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r WHERE r.tenantId = :tenantId AND r.hotelCode = :hotelCode AND r.status <> 'cancelled' AND r.createdAt >= :startDate AND r.createdAt <= :endDate")
    java.math.BigDecimal sumTotalPriceByTenantIdAndHotelCodeAndDateRange(@Param("tenantId") Integer tenantId, @Param("hotelCode") String hotelCode, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    long countByTenantIdAndHotelCodeAndCheckInDateAndStatusNot(Integer tenantId, String hotelCode, Date checkInDate, Reservation.Status excludeStatus);

    long countByTenantIdAndHotelCodeAndCheckOutDateAndStatusNot(Integer tenantId, String hotelCode, Date checkOutDate, Reservation.Status excludeStatus);

    long countByTenantIdAndHotelCodeAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanAndStatus(
            Integer tenantId, String hotelCode, Date checkInDate, Date checkOutDate, Reservation.Status status);

    long countByTenantIdAndHotelCodeAndCreatedAtGreaterThanEqual(Integer tenantId, String hotelCode, Date since);
}
