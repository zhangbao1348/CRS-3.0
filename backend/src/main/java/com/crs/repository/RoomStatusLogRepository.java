package com.crs.repository;

import com.crs.entity.RoomStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomStatusLogRepository extends JpaRepository<RoomStatusLog, Integer> {
    List<RoomStatusLog> findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeOrderByOperationTimeDesc(
            Integer tenantId, String hotelCode, String dimensionType, String dimensionCode);
}
