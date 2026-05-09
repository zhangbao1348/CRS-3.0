package com.crs.service;

import com.crs.entity.Hotel;
import com.crs.repository.HotelRepository;
import com.crs.util.TenantContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 酒店服务类
 * 用于处理酒店相关的业务逻辑
 */
@Service
public class HotelService {
    
    private final HotelRepository hotelRepository;
    
    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }
    
    /**
     * 获取所有酒店列表
     * @return 酒店列表
     */
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }
    
    /**
     * 根据ID获取酒店
     * @param id 酒店ID
     * @return 酒店信息
     */
    public Optional<Hotel> getHotelById(Integer id) {
        return hotelRepository.findById(id);
    }
    
    /**
     * 根据酒店代码获取酒店
     * @param hotelCode 酒店代码
     * @return 酒店信息
     */
    public Optional<Hotel> getHotelByCode(String hotelCode) {
        Integer tenantId = TenantContext.getTenantId();
        return hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId != null ? tenantId : 1);
    }
    
    /**
     * 根据租户ID获取酒店列表
     * @param tenantId 租户ID
     * @return 酒店列表
     */
    public List<Hotel> getHotelsByTenantId(Integer tenantId) {
        return hotelRepository.findByTenantId(tenantId);
    }
    
    /**
     * 根据租户ID和状态获取酒店列表
     * @param tenantId 租户ID
     * @param status 状态
     * @return 酒店列表
     */
    public List<Hotel> getHotelsByTenantIdAndStatus(Integer tenantId, Hotel.Status status) {
        return hotelRepository.findByTenantIdAndStatus(tenantId, status);
    }
    
    /**
     * 创建酒店
     * @param hotel 酒店信息
     * @return 创建的酒店信息
     */
    public Hotel createHotel(Hotel hotel) {
        Integer tenantId = hotel.getTenantId() != null ? hotel.getTenantId() : TenantContext.getTenantId();
        if (tenantId == null) tenantId = 1;
        if (hotelRepository.existsByHotelCodeAndTenantId(hotel.getHotelCode(), tenantId)) {
            throw new RuntimeException("Hotel code already exists");
        }
        
        return hotelRepository.save(hotel);
    }
    
    /**
     * 更新酒店
     * @param id 酒店ID
     * @param hotel 酒店信息
     * @return 更新后的酒店信息
     */
    public Hotel updateHotel(Integer id, Hotel hotel) {
        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        
        // 酒店代码不允许修改，保留原有代码
        // existingHotel.setHotelCode(hotel.getHotelCode());
        
        // 租户ID不允许修改，保留原有租户归属
        // existingHotel.setTenantId(hotel.getTenantId());
        
        existingHotel.setChineseName(hotel.getChineseName());
        existingHotel.setEnglishName(hotel.getEnglishName());
        existingHotel.setStarRating(hotel.getStarRating());
        existingHotel.setProvince(hotel.getProvince());
        existingHotel.setCity(hotel.getCity());
        existingHotel.setAddress(hotel.getAddress());
        existingHotel.setLongitude(hotel.getLongitude());
        existingHotel.setLatitude(hotel.getLatitude());
        existingHotel.setPhone(hotel.getPhone());
        existingHotel.setEmail(hotel.getEmail());
        existingHotel.setIntroduction(hotel.getIntroduction());
        existingHotel.setTotalRooms(hotel.getTotalRooms());
        existingHotel.setStatus(hotel.getStatus());
        
        // 更新酒店管控字段
        if (hotel.getAllowCreateRateCode() != null) {
            existingHotel.setAllowCreateRateCode(hotel.getAllowCreateRateCode());
        }
        if (hotel.getAllowCreateRoomType() != null) {
            existingHotel.setAllowCreateRoomType(hotel.getAllowCreateRoomType());
        }
        if (hotel.getSupportMultiPrice() != null) {
            existingHotel.setSupportMultiPrice(hotel.getSupportMultiPrice());
        }
        if (hotel.getMultiPriceOptions() != null) {
            existingHotel.setMultiPriceOptions(hotel.getMultiPriceOptions());
        }
        if (hotel.getSupportRoomTypePriceDiff() != null) {
            existingHotel.setSupportRoomTypePriceDiff(hotel.getSupportRoomTypePriceDiff());
        }
        if (hotel.getSupportPersonPriceDiff() != null) {
            existingHotel.setSupportPersonPriceDiff(hotel.getSupportPersonPriceDiff());
        }
        
        return hotelRepository.save(existingHotel);
    }
    
    /**
     * 删除酒店
     * @param id 酒店ID
     */
    public void deleteHotel(Integer id) {
        if (!hotelRepository.existsById(id)) {
            throw new RuntimeException("Hotel not found");
        }
        hotelRepository.deleteById(id);
    }
    
    /**
     * 根据状态获取酒店列表
     * @param status 状态
     * @return 酒店列表
     */
    public List<Hotel> getHotelsByStatus(Hotel.Status status) {
        return hotelRepository.findByStatus(status);
    }
    
    /**
     * 根据城市获取酒店列表
     * @param city 城市
     * @return 酒店列表
     */
    public List<Hotel> getHotelsByCity(String city) {
        return hotelRepository.findByCity(city);
    }
}
