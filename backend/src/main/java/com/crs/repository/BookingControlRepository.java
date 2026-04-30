package com.crs.repository;

import com.crs.entity.BookingControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingControlRepository extends JpaRepository<BookingControl, Integer> {

    List<BookingControl> findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndControlDateBetween(
            Integer tenantId, String hotelCode, String dimensionType, String dimensionCode,
            Date startDate, Date endDate);

    Optional<BookingControl> findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndControlDate(
            Integer tenantId, String hotelCode, String dimensionType, String dimensionCode, Date controlDate);
}
