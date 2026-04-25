package com.crs.repository;

import com.crs.entity.HotelPriceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 酒店价格操作日志仓库接口
 */
@Repository
public interface HotelPriceLogRepository extends JpaRepository<HotelPriceLog, Integer> {

    List<HotelPriceLog> findByTenantIdAndHotelCodeAndRateCodeOrderByOperationTimeDesc(
            Integer tenantId, String hotelCode, String rateCode);

    List<HotelPriceLog> findByTenantIdAndHotelCodeOrderByOperationTimeDesc(
            Integer tenantId, String hotelCode);
}
