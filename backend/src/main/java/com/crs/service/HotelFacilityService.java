package com.crs.service;

import com.crs.entity.HotelFacility;
import com.crs.repository.HotelFacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HotelFacilityService {
    
    @Autowired
    private HotelFacilityRepository hotelFacilityRepository;
    
    public List<HotelFacility> getFacilitiesByHotelId(Integer hotelId) {
        return hotelFacilityRepository.findByHotelId(hotelId);
    }
    
    public List<HotelFacility> getFacilitiesByType(Integer hotelId, String facilityType) {
        return hotelFacilityRepository.findByHotelIdAndFacilityType(hotelId, facilityType);
    }
    
    public HotelFacility createFacility(HotelFacility facility) {
        return hotelFacilityRepository.save(facility);
    }
    
    public HotelFacility updateFacility(HotelFacility facility) {
        return hotelFacilityRepository.save(facility);
    }
    
    public void deleteFacility(Integer id) {
        hotelFacilityRepository.deleteById(id);
    }
    
    public void deleteFacilitiesByHotelId(Integer hotelId) {
        hotelFacilityRepository.deleteAll(getFacilitiesByHotelId(hotelId));
    }
    
    /**
     * 获取所有设施（集团管理用）
     * @return 所有设施列表
     */
    public List<HotelFacility> getAllFacilities() {
        return hotelFacilityRepository.findAll();
    }
}