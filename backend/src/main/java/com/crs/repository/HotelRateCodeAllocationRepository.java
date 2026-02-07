package com.crs.repository;

import com.crs.entity.HotelRateCodeAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HotelRateCodeAllocationRepository extends JpaRepository<HotelRateCodeAllocation, Integer> {
    
    List<HotelRateCodeAllocation> findByHotelId(Integer hotelId);
    
    HotelRateCodeAllocation findByHotelIdAndRateCodeId(Integer hotelId, Integer rateCodeId);
    
    List<HotelRateCodeAllocation> findByHotelIdAndAllocated(Integer hotelId, Boolean allocated);
    
    void deleteByHotelId(Integer hotelId);
}