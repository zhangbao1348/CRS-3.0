package com.crs.repository;

import com.crs.entity.ReservationPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationPaymentRepository extends JpaRepository<ReservationPayment, Integer> {

    List<ReservationPayment> findByReservationIdOrderByCreatedAtDesc(Integer reservationId);

    void deleteByReservationId(Integer reservationId);
}
