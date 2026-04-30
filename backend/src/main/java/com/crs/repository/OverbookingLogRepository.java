package com.crs.repository;

import com.crs.entity.OverbookingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OverbookingLogRepository extends JpaRepository<OverbookingLog, Integer> {
    List<OverbookingLog> findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeOrderByOperationTimeDesc(
            Integer tenantId, String hotelCode, String dimensionType, String dimensionCode);

    List<OverbookingLog> findByTenantIdAndHotelCodeAndDimensionTypeOrderByOperationTimeDesc(
            Integer tenantId, String hotelCode, String dimensionType);
}
