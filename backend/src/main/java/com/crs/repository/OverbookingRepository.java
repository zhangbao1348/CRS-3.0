package com.crs.repository;

import com.crs.entity.Overbooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OverbookingRepository extends JpaRepository<Overbooking, Integer> {
    List<Overbooking> findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndOverbookDateBetween(
            Integer tenantId, String hotelCode, String dimensionType, String dimensionCode,
            Date startDate, Date endDate);

    Optional<Overbooking> findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndOverbookDate(
            Integer tenantId, String hotelCode, String dimensionType, String dimensionCode, Date overbookDate);
}
