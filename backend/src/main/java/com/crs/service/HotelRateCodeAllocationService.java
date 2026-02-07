package com.crs.service;

import com.crs.entity.HotelRateCodeAllocation;
import com.crs.repository.HotelRateCodeAllocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HotelRateCodeAllocationService {
    
    @Autowired
    private HotelRateCodeAllocationRepository hotelRateCodeAllocationRepository;
    
    public List<HotelRateCodeAllocation> getAllocationsByHotelId(Integer hotelId) {
        return hotelRateCodeAllocationRepository.findByHotelId(hotelId);
    }
    
    public HotelRateCodeAllocation getAllocationByHotelAndRateCode(Integer hotelId, Integer rateCodeId) {
        return hotelRateCodeAllocationRepository.findByHotelIdAndRateCodeId(hotelId, rateCodeId);
    }
    
    public List<HotelRateCodeAllocation> getAllocatedRateCodesByHotelId(Integer hotelId) {
        return hotelRateCodeAllocationRepository.findByHotelIdAndAllocated(hotelId, true);
    }
    
    public HotelRateCodeAllocation createAllocation(HotelRateCodeAllocation allocation) {
        return hotelRateCodeAllocationRepository.save(allocation);
    }
    
    public HotelRateCodeAllocation updateAllocation(HotelRateCodeAllocation allocation) {
        return hotelRateCodeAllocationRepository.save(allocation);
    }
    
    public void deleteAllocation(Integer id) {
        hotelRateCodeAllocationRepository.deleteById(id);
    }
    
    public void deleteAllocationsByHotelId(Integer hotelId) {
        hotelRateCodeAllocationRepository.deleteByHotelId(hotelId);
    }
}