package com.crs.repository;

import com.crs.entity.PmsInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PmsInventoryRepository extends JpaRepository<PmsInventory, Integer> {

    List<PmsInventory> findByTenantIdAndHotelCodeAndRoomTypeCodeAndInventoryDateBetween(
            Integer tenantId, String hotelCode, String roomTypeCode, Date startDate, Date endDate);

    List<PmsInventory> findByTenantIdAndHotelCodeAndInventoryDateBetween(
            Integer tenantId, String hotelCode, Date startDate, Date endDate);

    Optional<PmsInventory> findByTenantIdAndHotelCodeAndRoomTypeCodeAndInventoryDate(
            Integer tenantId, String hotelCode, String roomTypeCode, Date inventoryDate);
}
