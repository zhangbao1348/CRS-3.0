package com.crs.repository;

import com.crs.entity.BookingControlLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingControlLogRepository extends JpaRepository<BookingControlLog, Integer> {
    List<BookingControlLog> findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeOrderByOperationTimeDesc(
            Integer tenantId, String hotelCode, String dimensionType, String dimensionCode);
}
