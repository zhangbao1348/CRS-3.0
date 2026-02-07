package com.crs.service;

import com.crs.entity.RoomType;
import com.crs.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 酒店房型服务类
 * 用于处理酒店房型相关的业务逻辑
 */
@Service
public class RoomTypeService {
    
    private final RoomTypeRepository roomTypeRepository;
    
    public RoomTypeService(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }
    
    /**
     * 获取所有酒店房型列表
     * @return 酒店房型列表
     */
    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }
    
    /**
     * 根据ID获取酒店房型
     * @param id 酒店房型ID
     * @return 酒店房型信息
     */
    public Optional<RoomType> getRoomTypeById(Integer id) {
        return roomTypeRepository.findById(id);
    }
    
    /**
     * 根据酒店ID获取酒店房型列表
     * @param hotelId 酒店ID
     * @return 酒店房型列表
     */
    public List<RoomType> getRoomTypesByHotelId(Integer hotelId) {
        return roomTypeRepository.findByHotelId(hotelId);
    }
    
    /**
     * 根据酒店ID和房型代码获取酒店房型
     * @param hotelId 酒店ID
     * @param code 房型代码
     * @return 酒店房型信息
     */
    public Optional<RoomType> getRoomTypeByHotelIdAndCode(Integer hotelId, String code) {
        return roomTypeRepository.findByHotelIdAndCode(hotelId, code);
    }
    
    /**
     * 创建酒店房型
     * @param roomType 酒店房型信息
     * @return 创建的酒店房型信息
     */
    public RoomType createRoomType(RoomType roomType) {
        // 检查酒店内房型代码是否已存在
        if (roomTypeRepository.existsByHotelIdAndCode(roomType.getHotelId(), roomType.getCode())) {
            throw new RuntimeException("Room type code already exists in this hotel");
        }
        return roomTypeRepository.save(roomType);
    }
    
    /**
     * 更新酒店房型
     * @param id 酒店房型ID
     * @param roomType 酒店房型信息
     * @return 更新后的酒店房型信息
     */
    public RoomType updateRoomType(Integer id, RoomType roomType) {
        RoomType existingRoomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room type not found"));
        
        // 如果房型代码变更，检查新代码是否已存在
        if (!existingRoomType.getCode().equals(roomType.getCode()) && 
                roomTypeRepository.existsByHotelIdAndCode(roomType.getHotelId(), roomType.getCode())) {
            throw new RuntimeException("Room type code already exists in this hotel");
        }
        
        existingRoomType.setHotelId(roomType.getHotelId());
        existingRoomType.setGroupRoomTypeId(roomType.getGroupRoomTypeId());
        existingRoomType.setCode(roomType.getCode());
        existingRoomType.setName(roomType.getName());
        existingRoomType.setDescription(roomType.getDescription());
        existingRoomType.setStatus(roomType.getStatus());
        
        return roomTypeRepository.save(existingRoomType);
    }
    
    /**
     * 删除酒店房型
     * @param id 酒店房型ID
     */
    public void deleteRoomType(Integer id) {
        if (!roomTypeRepository.existsById(id)) {
            throw new RuntimeException("Room type not found");
        }
        roomTypeRepository.deleteById(id);
    }
    
    /**
     * 根据状态获取酒店房型列表
     * @param status 状态
     * @return 酒店房型列表
     */
    public List<RoomType> getRoomTypesByStatus(RoomType.Status status) {
        return roomTypeRepository.findByStatus(status);
    }
}
