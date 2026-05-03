package com.crs.repository;

import com.crs.entity.ReservationDailyPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;

@Repository
public interface ReservationDailyPriceRepository extends JpaRepository<ReservationDailyPrice, Integer> {

    List<ReservationDailyPrice> findByReservationIdOrderByPriceDateAsc(Integer reservationId);

    List<ReservationDailyPrice> findByReservationIdAndPriceDateBetween(Integer reservationId, Date startDate, Date endDate);

    void deleteByReservationId(Integer reservationId);
}
