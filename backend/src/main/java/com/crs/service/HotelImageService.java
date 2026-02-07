package com.crs.service;

import com.crs.entity.HotelImage;
import com.crs.repository.HotelImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HotelImageService {
    
    @Autowired
    private HotelImageRepository hotelImageRepository;
    
    public List<HotelImage> getImagesByHotelId(Integer hotelId) {
        return hotelImageRepository.findByHotelId(hotelId);
    }
    
    public List<HotelImage> getImagesByType(Integer hotelId, String imageType) {
        return hotelImageRepository.findByHotelIdAndImageType(hotelId, imageType);
    }
    
    public List<HotelImage> getImagesByHotelIdOrderBySort(Integer hotelId) {
        return hotelImageRepository.findByHotelIdOrderBySortOrderAsc(hotelId);
    }
    
    public HotelImage createImage(HotelImage image) {
        return hotelImageRepository.save(image);
    }
    
    public HotelImage updateImage(HotelImage image) {
        return hotelImageRepository.save(image);
    }
    
    public void deleteImage(Integer id) {
        hotelImageRepository.deleteById(id);
    }
    
    public void deleteImagesByHotelId(Integer hotelId) {
        hotelImageRepository.deleteByHotelId(hotelId);
    }
}