package com.crs.repository;

import com.crs.entity.HotelRateCodeAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface HotelRateCodeAllocationRepository extends JpaRepository<HotelRateCodeAllocation, Integer> {
    
    List<HotelRateCodeAllocation> findByTenantId(Integer tenantId);
    
    List<HotelRateCodeAllocation> findByHotelCode(String hotelCode);
    
    HotelRateCodeAllocation findByHotelCodeAndRateCode(String hotelCode, String rateCode);
    
    List<HotelRateCodeAllocation> findByTenantIdAndHotelCodeAndRateCode(Integer tenantId, String hotelCode, String rateCode);
    
    List<HotelRateCodeAllocation> findByTenantIdAndRateCode(Integer tenantId, String rateCode);
    
    List<HotelRateCodeAllocation> findByHotelCodeAndAllocated(String hotelCode, Boolean allocated);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM HotelRateCodeAllocation h WHERE h.hotelCode = :hotelCode")
    void deleteByHotelCode(@Param("hotelCode") String hotelCode);
}