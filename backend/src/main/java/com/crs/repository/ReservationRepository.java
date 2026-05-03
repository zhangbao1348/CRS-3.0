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
}
