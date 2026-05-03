package com.crs.repository;

import com.crs.entity.ReservationGuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationGuestRepository extends JpaRepository<ReservationGuest, Integer> {

    List<ReservationGuest> findByReservationIdOrderBySortOrderAsc(Integer reservationId);

    List<ReservationGuest> findByReservationIdAndGuestType(Integer reservationId, String guestType);

    void deleteByReservationId(Integer reservationId);
}
