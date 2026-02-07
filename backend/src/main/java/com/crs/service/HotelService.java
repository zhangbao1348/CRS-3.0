package com.crs.service;

import com.crs.entity.Hotel;
import com.crs.repository.HotelRepository;
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
        return hotelRepository.findByHotelCode(hotelCode);
    }
    
    /**
     * 根据集团ID获取酒店列表
     * @param groupId 集团ID
     * @return 酒店列表
     */
    public List<Hotel> getHotelsByGroupId(Integer groupId) {
        return hotelRepository.findByGroupId(groupId);
    }
    
    /**
     * 创建酒店
     * @param hotel 酒店信息
     * @return 创建的酒店信息
     */
    public Hotel createHotel(Hotel hotel) {
        // 检查酒店代码是否已存在
        if (hotelRepository.existsByHotelCode(hotel.getHotelCode())) {
            throw new RuntimeException("Hotel code already exists");
        }
        
        // 为group_id设置默认值，避免数据库约束错误
        if (hotel.getGroupId() == null) {
            hotel.setGroupId(1); // 设置默认集团ID为1
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
        
        // 酒店代码不允许修改，不需要检查代码变更
        
        // 酒店代码不允许修改，保留原有代码
        // existingHotel.setHotelCode(hotel.getHotelCode());
        existingHotel.setGroupId(hotel.getGroupId());
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
