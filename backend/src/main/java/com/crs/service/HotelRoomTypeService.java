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
     * @param hotelCode 酒店编码
     * @return 房型列表
     */
    public List<HotelRoomType> getHotelRoomTypes(String hotelCode) {
        // 获取当前租户ID
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }
        
        // 检查酒店是否存在且属于当前租户
        hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId)
                .orElseThrow(() -> new RuntimeException("Hotel not found or access denied"));
        
        return hotelRoomTypeRepository.findDistinctByTenantIdAndHotelCode(tenantId, hotelCode);
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
        hotelRepository.findByHotelCodeAndTenantId(roomType.getHotelCode(), tenantId)
                .orElseThrow(() -> new RuntimeException("Hotel not found or access denied"));
        
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
        hotelRepository.findByHotelCodeAndTenantId(hotelRoomType.getHotelCode(), tenantId)
                .orElseThrow(() -> new RuntimeException("Hotel not found or access denied"));
        
        // 检查房型代码是否已存在
        if (hotelRoomTypeRepository.existsByTenantIdAndHotelCodeAndRoomTypeCode(
                tenantId, hotelRoomType.getHotelCode(), hotelRoomType.getRoomTypeCode())) {
            throw new RuntimeException("Room type code already exists for this hotel");
        }
        
        if (hotelRoomType.getRoomTypeCategoryCode() != null) {
            String categoryCode = hotelRoomType.getRoomTypeCategoryCode().trim();
            hotelRoomType.setRoomTypeCategoryCode(categoryCode.isEmpty() ? null : categoryCode);
        }
        
        hotelRoomType.setTenantId(tenantId);
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
        
        // 验证现有房型所有权
        HotelRoomType existingHotelRoomType = getHotelRoomTypeById(id)
                .orElseThrow(() -> new RuntimeException("Hotel room type not found or access denied"));
        
        // 验证目标酒店所有权
        hotelRepository.findByHotelCodeAndTenantId(hotelRoomType.getHotelCode(), tenantId)
                .orElseThrow(() -> new RuntimeException("Target hotel not found or access denied"));
        
        // 检查房型代码是否已存在（排除当前房型）
        Optional<HotelRoomType> existingByCode = hotelRoomTypeRepository.findByTenantIdAndHotelCodeAndRoomTypeCode(
                tenantId, hotelRoomType.getHotelCode(), hotelRoomType.getRoomTypeCode());
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
        if (hotelRoomType.getRoomTypeCategoryCode() != null) {
            String categoryCode = hotelRoomType.getRoomTypeCategoryCode().trim();
            if (!categoryCode.isEmpty()) {
                existingHotelRoomType.setRoomTypeCategoryCode(categoryCode);
            }
        }
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
        hotelRepository.findByHotelCodeAndTenantId(roomType.getHotelCode(), tenantId)
                .orElseThrow(() -> new RuntimeException("Hotel not found or access denied"));
        
        hotelRoomTypeRepository.deleteById(id);
    }
    
    /**
     * 根据酒店ID和状态获取房型列表
     * @param hotelId 酒店ID
     * @param status 状态
     * @return 房型列表
     */
    public List<HotelRoomType> getHotelRoomTypesByStatus(String hotelCode, String status) {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }
        hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId)
                .orElseThrow(() -> new RuntimeException("Hotel not found or access denied"));
        
        return hotelRoomTypeRepository.findDistinctByTenantIdAndHotelCodeAndStatus(tenantId, hotelCode, status);
    }

    public List<HotelRoomType> getHotelRoomTypesByHotelCode(String hotelCode) {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }
        var hotel = hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with code: " + hotelCode));
        return hotelRoomTypeRepository.findDistinctByTenantIdAndHotelCode(tenantId, hotelCode);
    }

    public Optional<HotelRoomType> getHotelRoomTypeByHotelCodeAndRoomTypeCode(String hotelCode, String roomTypeCode) {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }
        var hotel = hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with code: " + hotelCode));
        return hotelRoomTypeRepository.findByTenantIdAndHotelCodeAndRoomTypeCode(tenantId, hotelCode, roomTypeCode);
    }

    public List<HotelRoomType> getHotelRoomTypesByHotelCodeAndStatus(String hotelCode, String status) {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }
        var hotel = hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with code: " + hotelCode));
        return hotelRoomTypeRepository.findDistinctByTenantIdAndHotelCodeAndStatus(tenantId, hotelCode, status);
    }
}
