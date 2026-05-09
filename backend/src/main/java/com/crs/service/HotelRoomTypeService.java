package com.crs.service;

import com.crs.entity.HotelRoomType;
import com.crs.repository.HotelRoomTypeRepository;
import com.crs.repository.HotelRepository;
import com.crs.util.TenantContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 酒店房型服务类
 * 用于处理酒店房型的业务逻辑
 */
@Service
public class HotelRoomTypeService {
    
    private final HotelRoomTypeRepository hotelRoomTypeRepository;
    private final HotelRepository hotelRepository;
    
    public HotelRoomTypeService(
            HotelRoomTypeRepository hotelRoomTypeRepository,
            HotelRepository hotelRepository) {
        this.hotelRoomTypeRepository = hotelRoomTypeRepository;
        this.hotelRepository = hotelRepository;
    }
    
    /**
     * 获取酒店的所有房型
     * @param hotelId 酒店ID
     * @return 房型列表
     */
    public List<HotelRoomType> getHotelRoomTypes(Integer hotelId) {
        // 获取当前租户ID
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }
        
        // 检查酒店是否存在且属于当前租户
        var hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        
        if (!hotel.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Access denied: Hotel does not belong to current tenant");
        }
        
        return hotelRoomTypeRepository.findByHotelId(hotelId);
    }
    
    /**
     * 根据ID获取酒店房型
     * @param id 房型ID
     * @return 房型信息
     */
    public Optional<HotelRoomType> getHotelRoomTypeById(Integer id) {
        // 获取当前租户ID
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }
        
        var roomType = hotelRoomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel room type not found"));
        
        // 检查酒店是否属于当前租户
        var hotel = hotelRepository.findById(roomType.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        
        if (!hotel.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Access denied: Hotel does not belong to current tenant");
        }
        
        return Optional.of(roomType);
    }
    
    /**
     * 创建酒店房型
     * @param hotelRoomType 房型信息
     * @return 创建的房型信息
     */
    public HotelRoomType createHotelRoomType(HotelRoomType hotelRoomType) {
        // 获取当前租户ID
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }
        
        // 检查酒店是否存在且属于当前租户
        var hotel = hotelRepository.findById(hotelRoomType.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        
        if (!hotel.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Access denied: Hotel does not belong to current tenant");
        }
        
        // 检查房型代码是否已存在
        if (hotelRoomTypeRepository.existsByHotelIdAndRoomTypeCode(
                hotelRoomType.getHotelId(), hotelRoomType.getRoomTypeCode())) {
            throw new RuntimeException("Room type code already exists for this hotel");
        }
        
        return hotelRoomTypeRepository.save(hotelRoomType);
    }
    
    /**
     * 更新酒店房型
     * @param id 房型ID
     * @param hotelRoomType 房型信息
     * @return 更新后的房型信息
     */
    public HotelRoomType updateHotelRoomType(Integer id, HotelRoomType hotelRoomType) {
        // 获取当前租户ID
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }
        
        HotelRoomType existingHotelRoomType = hotelRoomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel room type not found"));
        
        // 检查酒店是否存在且属于当前租户
        var hotel = hotelRepository.findById(hotelRoomType.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        
        if (!hotel.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Access denied: Hotel does not belong to current tenant");
        }
        
        // 检查房型代码是否已存在（排除当前房型）
        Optional<HotelRoomType> existingByCode = hotelRoomTypeRepository.findByHotelIdAndRoomTypeCode(
                hotelRoomType.getHotelId(), hotelRoomType.getRoomTypeCode());
        if (existingByCode.isPresent() && !existingByCode.get().getId().equals(id)) {
            throw new RuntimeException("Room type code already exists for this hotel");
        }
        
        // 更新房型信息
        existingHotelRoomType.setRoomTypeName(hotelRoomType.getRoomTypeName());
        existingHotelRoomType.setDescription(hotelRoomType.getDescription());
        existingHotelRoomType.setEnglishName(hotelRoomType.getEnglishName());
        existingHotelRoomType.setTotalRooms(hotelRoomType.getTotalRooms());
        existingHotelRoomType.setArea(hotelRoomType.getArea());
        existingHotelRoomType.setFloor(hotelRoomType.getFloor());
        existingHotelRoomType.setWindowType(hotelRoomType.getWindowType());
        existingHotelRoomType.setBedType(hotelRoomType.getBedType());
        existingHotelRoomType.setMaxOccupancy(hotelRoomType.getMaxOccupancy());
        existingHotelRoomType.setMaxChildren(hotelRoomType.getMaxChildren());
        if (hotelRoomType.getStatus() != null) {
            existingHotelRoomType.setStatus(hotelRoomType.getStatus());
        }
        
        return hotelRoomTypeRepository.save(existingHotelRoomType);
    }
    
    /**
     * 删除酒店房型
     * @param id 房型ID
     */
    public void deleteHotelRoomType(Integer id) {
        // 获取当前租户ID
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }
        
        var roomType = hotelRoomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel room type not found"));
        
        // 检查酒店是否属于当前租户
        var hotel = hotelRepository.findById(roomType.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        
        if (!hotel.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Access denied: Hotel does not belong to current tenant");
        }
        
        hotelRoomTypeRepository.deleteById(id);
    }
    
    /**
     * 根据酒店ID和状态获取房型列表
     * @param hotelId 酒店ID
     * @param status 状态
     * @return 房型列表
     */
    public List<HotelRoomType> getHotelRoomTypesByStatus(Integer hotelId, String status) {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }
        var hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        if (!hotel.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Access denied: Hotel does not belong to current tenant");
        }
        return hotelRoomTypeRepository.findByHotelIdAndStatus(hotelId, status);
    }

    public List<HotelRoomType> getHotelRoomTypesByHotelCode(String hotelCode) {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }
        var hotel = hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with code: " + hotelCode));
        return hotelRoomTypeRepository.findByHotelCode(hotelCode);
    }

    public Optional<HotelRoomType> getHotelRoomTypeByHotelCodeAndRoomTypeCode(String hotelCode, String roomTypeCode) {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }
        var hotel = hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with code: " + hotelCode));
        return hotelRoomTypeRepository.findByHotelCodeAndRoomTypeCode(hotelCode, roomTypeCode);
    }

    public List<HotelRoomType> getHotelRoomTypesByHotelCodeAndStatus(String hotelCode, String status) {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }
        var hotel = hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with code: " + hotelCode));
        return hotelRoomTypeRepository.findByHotelCodeAndStatus(hotelCode, status);
    }
}
