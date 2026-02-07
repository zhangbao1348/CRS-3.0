package com.crs.service;

import com.crs.entity.RoomTypeDiffSystem;
import com.crs.repository.RoomTypeDiffSystemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 房型差价体系服务类
 * 用于处理房型差价体系相关的业务逻辑
 */
@Service
public class RoomTypeDiffSystemService {
    
    private final RoomTypeDiffSystemRepository roomTypeDiffSystemRepository;
    
    public RoomTypeDiffSystemService(RoomTypeDiffSystemRepository roomTypeDiffSystemRepository) {
        this.roomTypeDiffSystemRepository = roomTypeDiffSystemRepository;
    }
    
    /**
     * 获取所有房型差价体系列表
     * @return 房型差价体系列表
     */
    public List<RoomTypeDiffSystem> getAllRoomTypeDiffSystems() {
        return roomTypeDiffSystemRepository.findAll();
    }
    
    /**
     * 根据ID获取房型差价体系
     * @param id 房型差价体系ID
     * @return 房型差价体系信息
     */
    public Optional<RoomTypeDiffSystem> getRoomTypeDiffSystemById(Integer id) {
        return roomTypeDiffSystemRepository.findById(id);
    }
    
    /**
     * 根据酒店ID获取房型差价体系列表
     * @param hotelId 酒店ID
     * @return 房型差价体系列表
     */
    public List<RoomTypeDiffSystem> getRoomTypeDiffSystemsByHotelId(Integer hotelId) {
        return roomTypeDiffSystemRepository.findByHotelId(hotelId);
    }
    
    /**
     * 创建房型差价体系
     * @param roomTypeDiffSystem 房型差价体系信息
     * @return 创建的房型差价体系信息
     */
    public RoomTypeDiffSystem createRoomTypeDiffSystem(RoomTypeDiffSystem roomTypeDiffSystem) {
        return roomTypeDiffSystemRepository.save(roomTypeDiffSystem);
    }
    
    /**
     * 更新房型差价体系
     * @param id 房型差价体系ID
     * @param roomTypeDiffSystem 房型差价体系信息
     * @return 更新后的房型差价体系信息
     */
    public RoomTypeDiffSystem updateRoomTypeDiffSystem(Integer id, RoomTypeDiffSystem roomTypeDiffSystem) {
        RoomTypeDiffSystem existingRoomTypeDiffSystem = roomTypeDiffSystemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room type diff system not found"));
        
        existingRoomTypeDiffSystem.setHotelId(roomTypeDiffSystem.getHotelId());
        existingRoomTypeDiffSystem.setName(roomTypeDiffSystem.getName());
        existingRoomTypeDiffSystem.setDescription(roomTypeDiffSystem.getDescription());
        existingRoomTypeDiffSystem.setStatus(roomTypeDiffSystem.getStatus());
        
        return roomTypeDiffSystemRepository.save(existingRoomTypeDiffSystem);
    }
    
    /**
     * 删除房型差价体系
     * @param id 房型差价体系ID
     */
    public void deleteRoomTypeDiffSystem(Integer id) {
        if (!roomTypeDiffSystemRepository.existsById(id)) {
            throw new RuntimeException("Room type diff system not found");
        }
        roomTypeDiffSystemRepository.deleteById(id);
    }
}
