package com.crs.repository;

import com.crs.entity.ReportDailyReservationSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportDailyReservationSummaryRepository extends JpaRepository<ReportDailyReservationSummary, Integer> {

    Optional<ReportDailyReservationSummary> findByTenantIdAndReportDateAndHotelCodeAndChannelCodeAndMarketCodeAndRateCategoryCodeAndRatePlanCodeAndRoomTypeCodeAndReservationStatus(
        Integer tenantId, LocalDate reportDate, String hotelCode, String channelCode,
        String marketCode, String rateCategoryCode, String ratePlanCode, String roomTypeCode, String reservationStatus
    );

    List<ReportDailyReservationSummary> findByTenantIdAndReportDateBetween(Integer tenantId, LocalDate startDate, LocalDate endDate);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM report_daily_reservation_summary WHERE tenant_id = :tenantId AND report_date = :reportDate", nativeQuery = true)
    void deleteByTenantIdAndReportDate(@Param("tenantId") Integer tenantId, @Param("reportDate") LocalDate reportDate);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM report_daily_reservation_summary WHERE tenant_id = :tenantId AND report_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    void deleteByTenantIdAndReportDateBetween(@Param("tenantId") Integer tenantId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
