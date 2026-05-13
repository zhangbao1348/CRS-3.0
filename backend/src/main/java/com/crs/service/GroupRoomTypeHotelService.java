package com.crs.service;

import com.crs.entity.GroupRoomTypeHotel;
import com.crs.entity.HotelRoomType;
import com.crs.repository.GroupRoomTypeHotelRepository;
import com.crs.repository.HotelRoomTypeRepository;
import com.crs.repository.GroupRoomTypeRepository;
import com.crs.repository.HotelRepository;
import com.crs.util.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 集团房型和酒店关联服务类
 * 已根据【CODE关联规范】重构，移除对 ID 的跨模块依赖。
 */
@Service
public class GroupRoomTypeHotelService {
    
    private final GroupRoomTypeHotelRepository groupRoomTypeHotelRepository;
    private final HotelRoomTypeRepository hotelRoomTypeRepository;
    private final GroupRoomTypeRepository groupRoomTypeRepository;
    private final HotelRepository hotelRepository;
    
    public GroupRoomTypeHotelService(
            GroupRoomTypeHotelRepository groupRoomTypeHotelRepository,
            HotelRoomTypeRepository hotelRoomTypeRepository,
            GroupRoomTypeRepository groupRoomTypeRepository,
            HotelRepository hotelRepository) {
        this.groupRoomTypeHotelRepository = groupRoomTypeHotelRepository;
        this.hotelRoomTypeRepository = hotelRoomTypeRepository;
        this.groupRoomTypeRepository = groupRoomTypeRepository;
        this.hotelRepository = hotelRepository;
    }
    
    /**
     * 获取集团房型的酒店分配列表
     * @param groupRoomTypeCode 集团房型编码
     * @return 分配列表
     */
    public List<GroupRoomTypeHotel> getGroupRoomTypeHotelsByCode(String groupRoomTypeCode) {
        Integer tenantId = TenantContext.getTenantId();
        return groupRoomTypeHotelRepository.findByTenantIdAndGroupRoomTypeCode(tenantId, groupRoomTypeCode);
    }
    
    /**
     * 获取当前酒店的房型分配列表
     * @param hotelCode 酒店编码
     * @return 分配列表
     */
    public List<GroupRoomTypeHotel> getHotelRoomTypeAllocationsByCode(String hotelCode) {
        Integer tenantId = TenantContext.getTenantId();
        return groupRoomTypeHotelRepository.findByTenantIdAndHotelCode(tenantId, hotelCode);
    }
    
    /**
     * 更新酒店房型分配状态 (基于 CODE)
     * @param groupRoomTypeCode 集团房型编码
     * @param hotelCode 酒店编码
     * @param allocated 是否分配
     * @param roomInfoEditable 房型信息是否可修改
     * @return 关联信息
     */
    @Transactional
    public GroupRoomTypeHotel updateRoomTypeAllocationByCode(
            String groupRoomTypeCode, 
            String hotelCode, 
            Boolean allocated, 
            Boolean roomInfoEditable) {
        
        Integer tenantId = TenantContext.getTenantId();
        
        // 查找或创建关联
        Optional<GroupRoomTypeHotel> existingAllocation = 
                groupRoomTypeHotelRepository.findByTenantIdAndGroupRoomTypeCodeAndHotelCode(tenantId, groupRoomTypeCode, hotelCode);
        
        GroupRoomTypeHotel allocation;
        if (existingAllocation.isPresent()) {
            allocation = existingAllocation.get();
        } else {
            allocation = new GroupRoomTypeHotel();
            allocation.setGroupRoomTypeCode(groupRoomTypeCode);
            allocation.setHotelCode(hotelCode);
            allocation.setTenantId(tenantId);
        }
        
        allocation.setAllocated(allocated);
        allocation.setRoomInfoEditable(roomInfoEditable);
        
        // 保存关联信息
        GroupRoomTypeHotel savedAllocation = groupRoomTypeHotelRepository.save(allocation);
        
        // 如果分配，创建或更新酒店房型
        if (allocated) {
            createOrUpdateHotelRoomTypeByCode(groupRoomTypeCode, hotelCode);
        } else {
            // 如果取消分配，停用酒店房型
            deleteHotelRoomTypeByCode(groupRoomTypeCode, hotelCode);
        }
        
        return savedAllocation;
    }
    
    /**
     * 创建或更新酒店房型 (基于 CODE)
     * @param groupRoomTypeCode 集团房型编码
     * @param hotelCode 酒店编码
     */
    public void createOrUpdateHotelRoomTypeByCode(String groupRoomTypeCode, String hotelCode) {
        Integer tenantId = TenantContext.getTenantId();
        
        // 获取集团房型信息
        var groupRoomType = groupRoomTypeRepository.findByRoomTypeCode(groupRoomTypeCode)
                .orElseThrow(() -> new RuntimeException("Group room type not found: " + groupRoomTypeCode));
        
        // 获取酒店信息以获取 tenantId
        var hotel = hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId)
                .orElseThrow(() -> new RuntimeException("Hotel not found: " + hotelCode));
        
        // 检查酒店房型是否已存在
        Optional<HotelRoomType> existingHotelRoomType = 
                hotelRoomTypeRepository.findByTenantIdAndHotelCodeAndRoomTypeCode(tenantId, hotelCode, groupRoomTypeCode);
        
        HotelRoomType hotelRoomType;
        if (existingHotelRoomType.isPresent()) {
            hotelRoomType = existingHotelRoomType.get();
        } else {
            hotelRoomType = new HotelRoomType();
            hotelRoomType.setHotelCode(hotelCode);
            hotelRoomType.setRoomTypeCode(groupRoomTypeCode);
            hotelRoomType.setTenantId(hotel.getTenantId());
        }
        
        // 更新房型信息 (不再包含已删除的 ID 字段)
        hotelRoomType.setRoomTypeName(groupRoomType.getRoomTypeName());
        hotelRoomType.setDescription(groupRoomType.getDescription());
        hotelRoomType.setMaxOccupancy(groupRoomType.getMaxOccupancy());
        hotelRoomType.setSortOrder(groupRoomType.getSortOrder());
        hotelRoomType.setGroupRoomTypeCode(groupRoomTypeCode);
        hotelRoomType.setRoomTypeCategoryCode(groupRoomType.getRoomTypeCategoryCode());
        hotelRoomType.setStatus(groupRoomType.getStatus());
        
        hotelRoomTypeRepository.save(hotelRoomType);
    }
    
    /**
     * 删除酒店房型 (基于 CODE)
     * @param groupRoomTypeCode 集团房型编码
     * @param hotelCode 酒店编码
     */
    public void deleteHotelRoomTypeByCode(String groupRoomTypeCode, String hotelCode) {
        Optional<HotelRoomType> existingHotelRoomType = 
                hotelRoomTypeRepository.findByHotelCodeAndRoomTypeCode(hotelCode, groupRoomTypeCode);
        
        existingHotelRoomType.ifPresent(roomType -> {
            roomType.setStatus("inactive");
            hotelRoomTypeRepository.save(roomType);
        });
    }

    // =====================================================================
    // 适配旧 ID 方法 (已标记为 Deprecated，内部调用自动重定向到 CODE 方法)
    // =====================================================================

    @Deprecated
    public List<GroupRoomTypeHotel> getGroupRoomTypeHotels(Integer groupRoomTypeId) {
        var grt = groupRoomTypeRepository.findById(groupRoomTypeId).orElse(null);
        if (grt != null) return getGroupRoomTypeHotelsByCode(grt.getRoomTypeCode());
        return List.of();
    }

    @Deprecated
    public List<GroupRoomTypeHotel> getHotelRoomTypeAllocations(Integer hotelId) {
        var hotel = hotelRepository.findById(hotelId).orElse(null);
        if (hotel != null) return getHotelRoomTypeAllocationsByCode(hotel.getHotelCode());
        return List.of();
    }

    @Deprecated
    public void batchUpdateRoomTypeAllocations(Integer groupRoomTypeId, List<GroupRoomTypeHotel> allocations) {
        var grt = groupRoomTypeRepository.findById(groupRoomTypeId).orElse(null);
        if (grt == null) return;
        
        for (GroupRoomTypeHotel allocation : allocations) {
            updateRoomTypeAllocationByCode(
                    grt.getRoomTypeCode(),
                    allocation.getHotelCode(),
                    allocation.getAllocated(),
                    allocation.getRoomInfoEditable());
        }
    }

    @Deprecated
    public void batchSaveRoomTypeAllocations(List<GroupRoomTypeHotel> allocations) {
        for (GroupRoomTypeHotel allocation : allocations) {
            updateRoomTypeAllocationByCode(
                    allocation.getGroupRoomTypeCode(),
                    allocation.getHotelCode(),
                    allocation.getAllocated(),
                    allocation.getRoomInfoEditable());
        }
    }
}
