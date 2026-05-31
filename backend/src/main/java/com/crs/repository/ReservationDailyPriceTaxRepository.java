package com.crs.repository;

import com.crs.entity.ReservationDailyPriceTax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单每日价格税费细表 Repository
 */
@Repository
public interface ReservationDailyPriceTaxRepository extends JpaRepository<ReservationDailyPriceTax, Integer> {
    
    /**
     * 根据每日价格明细ID查询多税费明细
     */
    List<ReservationDailyPriceTax> findByReservationDailyPriceId(Integer reservationDailyPriceId);
    
    /**
     * 根据每日价格明细ID列表查询所有对应的税费明细
     */
    List<ReservationDailyPriceTax> findByReservationDailyPriceIdIn(List<Integer> reservationDailyPriceIds);
}
