package com.crs.repository;

import com.crs.entity.InventoryQuotaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventoryQuotaLogRepository extends JpaRepository<InventoryQuotaLog, Integer> {
    List<InventoryQuotaLog> findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeOrderByOperationTimeDesc(
            Integer tenantId, String hotelCode, String dimensionType, String dimensionCode);

    List<InventoryQuotaLog> findByTenantIdAndHotelCodeAndDimensionTypeOrderByOperationTimeDesc(
            Integer tenantId, String hotelCode, String dimensionType);
}
