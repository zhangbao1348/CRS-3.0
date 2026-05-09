package com.crs.repository;

import com.crs.entity.HotelFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface HotelFacilityRepository extends JpaRepository<HotelFacility, Integer> {
    
    List<HotelFacility> findByHotelId(Integer hotelId);
    
    List<HotelFacility> findByHotelIdAndFacilityType(Integer hotelId, String facilityType);
    
    List<HotelFacility> findByHotelIdAndAvailable(Integer hotelId, Boolean available);
    
    @Query("SELECT hf FROM HotelFacility hf WHERE hf.hotelId = :hotelId AND hf.facilityCode IN (:facilityCodes)")
    List<HotelFacility> findByHotelIdAndFacilityCodes(@Param("hotelId") Integer hotelId, @Param("facilityCodes") List<String> facilityCodes);

    List<HotelFacility> findByHotelCode(String hotelCode);

    List<HotelFacility> findByHotelCodeAndFacilityType(String hotelCode, String facilityType);

    List<HotelFacility> findByHotelCodeAndAvailable(String hotelCode, Boolean available);

    @Query("SELECT hf FROM HotelFacility hf WHERE hf.hotelCode = :hotelCode AND hf.facilityCode IN (:facilityCodes)")
    List<HotelFacility> findByHotelCodeAndFacilityCodes(@Param("hotelCode") String hotelCode, @Param("facilityCodes") List<String> facilityCodes);
}