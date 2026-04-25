package com.crs.repository;

import com.crs.entity.HotelPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 酒店价格仓库接口
 */
@Repository
public interface HotelPriceRepository extends JpaRepository<HotelPrice, Integer> {
    
    List<HotelPrice> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);
    
    List<HotelPrice> findByTenantIdAndHotelCodeAndRateCode(Integer tenantId, String hotelCode, String rateCode);
    
    List<HotelPrice> findByTenantIdAndHotelCodeAndRateCodeAndRoomTypeCode(
            Integer tenantId, String hotelCode, String rateCode, String roomTypeCode);
    
    List<HotelPrice> findByTenantIdAndHotelCodeAndPriceDateBetween(
            Integer tenantId, String hotelCode, Date startDate, Date endDate);
    
    List<HotelPrice> findByTenantIdAndHotelCodeAndRateCodeAndPriceDateBetween(
            Integer tenantId, String hotelCode, String rateCode, Date startDate, Date endDate);
    
    List<HotelPrice> findByTenantIdAndHotelCodeAndRateCodeAndRoomTypeCodeAndPriceDateBetween(
            Integer tenantId, String hotelCode, String rateCode, String roomTypeCode, Date startDate, Date endDate);
    
    Optional<HotelPrice> findByTenantIdAndHotelCodeAndRateCodeAndRoomTypeCodeAndPriceDate(
            Integer tenantId, String hotelCode, String rateCode, String roomTypeCode, Date priceDate);
}
