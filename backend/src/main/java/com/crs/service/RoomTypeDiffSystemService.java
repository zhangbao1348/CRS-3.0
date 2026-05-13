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
    
    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    /**
     * 获取当前租户下所有房型差价体系列表
     */
    public List<RoomTypeDiffSystem> getAllRoomTypeDiffSystems() {
        return roomTypeDiffSystemRepository.findByTenantId(getCurrentTenantId());
    }
    
    /**
     * 根据ID获取房型差价体系
     * @param id 房型差价体系ID
     * @return 房型差价体系信息
     */
    public Optional<RoomTypeDiffSystem> getRoomTypeDiffSystemById(Integer id) {
        return roomTypeDiffSystemRepository.findById(id)
                .filter(s -> s.getTenantId() != null && s.getTenantId().equals(getCurrentTenantId()));
    }
    
    /**
     * 根据酒店编码获取房型差价体系列表
     * @param hotelCode 酒店编码
     * @return 房型差价体系列表
     */
    public List<RoomTypeDiffSystem> getRoomTypeDiffSystemsByHotelCode(String hotelCode) {
        return roomTypeDiffSystemRepository.findByTenantIdAndHotelCode(getCurrentTenantId(), hotelCode);
    }
    
    /** @deprecated 请使用 getRoomTypeDiffSystemsByHotelCode */
    @Deprecated
    public List<RoomTypeDiffSystem> getRoomTypeDiffSystemsByHotelId(Integer hotelId) {
        return List.of(); // 快速失败，强制上层重构
    }
    
    /**
     * 创建房型差价体系
     */
    public RoomTypeDiffSystem createRoomTypeDiffSystem(RoomTypeDiffSystem roomTypeDiffSystem) {
        roomTypeDiffSystem.setTenantId(getCurrentTenantId());
        return roomTypeDiffSystemRepository.save(roomTypeDiffSystem);
    }
    
    /**
     * 更新房型差价体系
     */
    public RoomTypeDiffSystem updateRoomTypeDiffSystem(Integer id, RoomTypeDiffSystem roomTypeDiffSystem) {
        RoomTypeDiffSystem existing = getRoomTypeDiffSystemById(id)
                .orElseThrow(() -> new RuntimeException("Room type diff system not found or access denied"));
        
        existing.setHotelCode(roomTypeDiffSystem.getHotelCode());
        existing.setCode(roomTypeDiffSystem.getCode());
        existing.setName(roomTypeDiffSystem.getName());
        existing.setDescription(roomTypeDiffSystem.getDescription());
        existing.setStatus(roomTypeDiffSystem.getStatus());
        
        return roomTypeDiffSystemRepository.save(existing);
    }
    
    /**
     * 删除房型差价体系
     * @param id 房型差价体系ID
     */
    public void deleteRoomTypeDiffSystem(Integer id) {
        RoomTypeDiffSystem existing = getRoomTypeDiffSystemById(id)
                .orElseThrow(() -> new RuntimeException("Room type diff system not found or access denied"));
        roomTypeDiffSystemRepository.delete(existing);
    }
}
