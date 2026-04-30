package com.crs.repository;

import com.crs.entity.InventoryQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryQuotaRepository extends JpaRepository<InventoryQuota, Integer> {
    List<InventoryQuota> findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndQuotaDateBetween(
            Integer tenantId, String hotelCode, String dimensionType, String dimensionCode, Date startDate, Date endDate);
    Optional<InventoryQuota> findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndQuotaDate(
            Integer tenantId, String hotelCode, String dimensionType, String dimensionCode, Date quotaDate);
}
