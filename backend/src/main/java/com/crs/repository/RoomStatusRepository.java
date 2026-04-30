package com.crs.repository;

import com.crs.entity.RoomStatusRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomStatusRepository extends JpaRepository<RoomStatusRecord, Integer> {

    List<RoomStatusRecord> findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndStatusDateBetween(
            Integer tenantId, String hotelCode, String dimensionType, String dimensionCode,
            Date startDate, Date endDate);

    Optional<RoomStatusRecord> findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndStatusDate(
            Integer tenantId, String hotelCode, String dimensionType, String dimensionCode, Date statusDate);
}
