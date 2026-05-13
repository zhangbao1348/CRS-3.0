package com.crs.service;

import com.crs.entity.RoomType;
import com.crs.repository.RoomTypeRepository;
import com.crs.util.TenantContext;
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
    
    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    /**
     * 获取所有酒店房型列表
     * @return 酒店房型列表
     */
    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findByTenantId(getCurrentTenantId());
    }
    
    /**
     * 根据ID获取酒店房型
     * @param id 酒店房型ID
     * @return 酒店房型信息
     */
    public Optional<RoomType> getRoomTypeById(Integer id) {
        return roomTypeRepository.findById(id)
                .filter(rt -> rt.getTenantId() != null && rt.getTenantId().equals(getCurrentTenantId()));
    }
    
    /**
     * 根据酒店代码获取酒店房型列表
     * @param hotelCode 酒店代码
     * @return 酒店房型列表
     */
    public List<RoomType> getRoomTypesByHotelCode(String hotelCode) {
        return roomTypeRepository.findByTenantIdAndHotelCode(getCurrentTenantId(), hotelCode);
    }
    
    public Optional<RoomType> getRoomTypeByHotelCodeAndCode(String hotelCode, String code) {
        return roomTypeRepository.findByTenantIdAndHotelCodeAndCode(getCurrentTenantId(), hotelCode, code);
    }
    
    /**
     * 创建酒店房型
     * @param roomType 酒店房型信息
     * @return 创建的酒店房型信息
     */
    public RoomType createRoomType(RoomType roomType) {
        Integer tenantId = getCurrentTenantId();
        roomType.setTenantId(tenantId);
        // 检查酒店内房型代码是否已存在
        if (roomTypeRepository.existsByTenantIdAndHotelCodeAndCode(tenantId, roomType.getHotelCode(), roomType.getCode())) {
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
        RoomType existingRoomType = getRoomTypeById(id)
                .orElseThrow(() -> new RuntimeException("Room type not found or access denied"));
        
        Integer tenantId = getCurrentTenantId();
        // 如果房型代码变更，检查新代码是否已存在
        if (!existingRoomType.getCode().equals(roomType.getCode()) && 
                roomTypeRepository.existsByTenantIdAndHotelCodeAndCode(tenantId, roomType.getHotelCode(), roomType.getCode())) {
            throw new RuntimeException("Room type code already exists in this hotel");
        }
        
        existingRoomType.setHotelCode(roomType.getHotelCode());
        existingRoomType.setGroupRoomTypeCode(roomType.getGroupRoomTypeCode());
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
        RoomType existing = getRoomTypeById(id)
                .orElseThrow(() -> new RuntimeException("Room type not found or access denied"));
        roomTypeRepository.delete(existing);
    }
    
    /**
     * 根据状态获取酒店房型列表
     * @param status 状态
     * @return 酒店房型列表
     */
    public List<RoomType> getRoomTypesByStatus(RoomType.Status status) {
        return roomTypeRepository.findByTenantIdAndStatus(getCurrentTenantId(), status);
    }
}
