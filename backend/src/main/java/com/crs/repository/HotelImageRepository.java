package com.crs.repository;

import com.crs.entity.HotelImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HotelImageRepository extends JpaRepository<HotelImage, Integer> {
    
    List<HotelImage> findByHotelId(Integer hotelId);
    
    List<HotelImage> findByHotelIdAndImageType(Integer hotelId, String imageType);
    
    List<HotelImage> findByHotelIdOrderBySortOrderAsc(Integer hotelId);
    
    void deleteByHotelId(Integer hotelId);

    List<HotelImage> findByHotelCode(String hotelCode);

    List<HotelImage> findByHotelCodeAndImageType(String hotelCode, String imageType);

    List<HotelImage> findByHotelCodeOrderBySortOrderAsc(String hotelCode);

    void deleteByHotelCode(String hotelCode);
}