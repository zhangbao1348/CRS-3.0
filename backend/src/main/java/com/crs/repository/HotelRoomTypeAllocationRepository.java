package com.crs.repository;

import com.crs.entity.HotelRoomTypeAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HotelRoomTypeAllocationRepository extends JpaRepository<HotelRoomTypeAllocation, Integer> {
    
    List<HotelRoomTypeAllocation> findByHotelId(Integer hotelId);
    
    HotelRoomTypeAllocation findByHotelIdAndRoomTypeId(Integer hotelId, Integer roomTypeId);
    
    List<HotelRoomTypeAllocation> findByHotelIdAndAllocated(Integer hotelId, Boolean allocated);
    
    void deleteByHotelId(Integer hotelId);
}