package com.crs.repository;

import com.crs.entity.PmsSyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PmsSyncLogRepository extends JpaRepository<PmsSyncLog, Integer> {

    List<PmsSyncLog> findByTenantIdAndHotelCodeOrderBySyncTimeDesc(
            Integer tenantId, String hotelCode);
}
