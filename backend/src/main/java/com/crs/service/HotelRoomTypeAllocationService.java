package com.crs.service;

import com.crs.entity.HotelRoomTypeAllocation;
import com.crs.repository.HotelRoomTypeAllocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HotelRoomTypeAllocationService {
    
    @Autowired
    private HotelRoomTypeAllocationRepository hotelRoomTypeAllocationRepository;
    
    public List<HotelRoomTypeAllocation> getAllocationsByHotelId(Integer hotelId) {
        return hotelRoomTypeAllocationRepository.findByHotelId(hotelId);
    }
    
    public HotelRoomTypeAllocation getAllocationByHotelAndRoomType(Integer hotelId, Integer roomTypeId) {
        return hotelRoomTypeAllocationRepository.findByHotelIdAndRoomTypeId(hotelId, roomTypeId);
    }
    
    public List<HotelRoomTypeAllocation> getAllocatedRoomTypesByHotelId(Integer hotelId) {
        return hotelRoomTypeAllocationRepository.findByHotelIdAndAllocated(hotelId, true);
    }
    
    public HotelRoomTypeAllocation createAllocation(HotelRoomTypeAllocation allocation) {
        return hotelRoomTypeAllocationRepository.save(allocation);
    }
    
    public HotelRoomTypeAllocation updateAllocation(HotelRoomTypeAllocation allocation) {
        return hotelRoomTypeAllocationRepository.save(allocation);
    }
    
    public void deleteAllocation(Integer id) {
        hotelRoomTypeAllocationRepository.deleteById(id);
    }
    
    public void deleteAllocationsByHotelId(Integer hotelId) {
        hotelRoomTypeAllocationRepository.deleteByHotelId(hotelId);
    }
}