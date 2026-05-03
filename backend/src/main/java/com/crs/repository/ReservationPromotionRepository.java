package com.crs.repository;

import com.crs.entity.ReservationPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationPromotionRepository extends JpaRepository<ReservationPromotion, Integer> {

    List<ReservationPromotion> findByReservationId(Integer reservationId);

    void deleteByReservationId(Integer reservationId);
}
