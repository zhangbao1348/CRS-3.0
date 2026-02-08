package com.crs.service;

import com.crs.entity.GroupRoomTypeHotel;
import com.crs.entity.HotelRoomType;
import com.crs.repository.GroupRoomTypeHotelRepository;
import com.crs.repository.HotelRoomTypeRepository;
import com.crs.repository.GroupRoomTypeRepository;
import com.crs.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 集团房型和酒店关联服务类
 * 用于处理集团房型和酒店关联的业务逻辑
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
     * @param groupRoomTypeId 集团房型ID
     * @return 分配列表
     */
    public List<GroupRoomTypeHotel> getGroupRoomTypeHotels(Integer groupRoomTypeId) {
        return groupRoomTypeHotelRepository.findByGroupRoomTypeId(groupRoomTypeId);
    }
    
    /**
     * 更新酒店房型分配状态
     * @param groupRoomTypeId 集团房型ID
     * @param hotelId 酒店ID
     * @param allocated 是否分配
     * @param roomInfoEditable 房型信息是否可修改
     * @return 关联信息
     */
    public GroupRoomTypeHotel updateRoomTypeAllocation(
            Integer groupRoomTypeId, 
            Integer hotelId, 
            Boolean allocated, 
            Boolean roomInfoEditable) {
        // 检查集团房型是否存在
        if (!groupRoomTypeRepository.existsById(groupRoomTypeId)) {
            throw new RuntimeException("Group room type not found");
        }
        
        // 检查酒店是否存在
        if (!hotelRepository.existsById(hotelId)) {
            throw new RuntimeException("Hotel not found");
        }
        
        // 查找或创建关联
        Optional<GroupRoomTypeHotel> existingAllocation = 
                groupRoomTypeHotelRepository.findByGroupRoomTypeIdAndHotelId(groupRoomTypeId, hotelId);
        
        GroupRoomTypeHotel allocation;
        if (existingAllocation.isPresent()) {
            allocation = existingAllocation.get();
        } else {
            allocation = new GroupRoomTypeHotel();
            allocation.setGroupRoomTypeId(groupRoomTypeId);
            allocation.setHotelId(hotelId);
        }
        
        allocation.setAllocated(allocated);
        allocation.setRoomInfoEditable(roomInfoEditable);
        
        // 保存关联信息
        GroupRoomTypeHotel savedAllocation = groupRoomTypeHotelRepository.save(allocation);
        
        // 如果分配，创建或更新酒店房型
        if (allocated) {
            createOrUpdateHotelRoomType(groupRoomTypeId, hotelId);
        } else {
            // 如果取消分配，删除酒店房型
            deleteHotelRoomType(groupRoomTypeId, hotelId);
        }
        
        return savedAllocation;
    }
    
    /**
     * 创建或更新酒店房型
     * @param groupRoomTypeId 集团房型ID
     * @param hotelId 酒店ID
     */
    private void createOrUpdateHotelRoomType(Integer groupRoomTypeId, Integer hotelId) {
        // 获取集团房型信息
        var groupRoomType = groupRoomTypeRepository.findById(groupRoomTypeId)
                .orElseThrow(() -> new RuntimeException("Group room type not found"));
        
        // 检查酒店房型是否已存在
        Optional<HotelRoomType> existingHotelRoomType = 
                hotelRoomTypeRepository.findByHotelIdAndRoomTypeCode(hotelId, groupRoomType.getRoomTypeCode());
        
        HotelRoomType hotelRoomType;
        if (existingHotelRoomType.isPresent()) {
            // 更新现有房型
            hotelRoomType = existingHotelRoomType.get();
        } else {
            // 创建新房型
            hotelRoomType = new HotelRoomType();
            hotelRoomType.setHotelId(hotelId);
            hotelRoomType.setGroupRoomTypeId(groupRoomTypeId);
            hotelRoomType.setRoomTypeCode(groupRoomType.getRoomTypeCode());
        }
        
        // 更新房型信息
        hotelRoomType.setRoomTypeName(groupRoomType.getRoomTypeName());
        hotelRoomType.setDescription(groupRoomType.getDescription());
        hotelRoomType.setStatus(groupRoomType.getStatus());
        
        hotelRoomTypeRepository.save(hotelRoomType);
    }
    
    /**
     * 删除酒店房型
     * @param groupRoomTypeId 集团房型ID
     * @param hotelId 酒店ID
     */
    private void deleteHotelRoomType(Integer groupRoomTypeId, Integer hotelId) {
        // 获取集团房型信息
        var groupRoomType = groupRoomTypeRepository.findById(groupRoomTypeId)
                .orElseThrow(() -> new RuntimeException("Group room type not found"));
        
        // 查找并删除酒店房型
        Optional<HotelRoomType> existingHotelRoomType = 
                hotelRoomTypeRepository.findByHotelIdAndRoomTypeCode(hotelId, groupRoomType.getRoomTypeCode());
        
        existingHotelRoomType.ifPresent(hotelRoomTypeRepository::delete);
    }
    
    /**
     * 批量更新酒店房型分配
     * @param groupRoomTypeId 集团房型ID
     * @param allocations 分配列表
     */
    public void batchUpdateRoomTypeAllocations(Integer groupRoomTypeId, List<GroupRoomTypeHotel> allocations) {
        for (GroupRoomTypeHotel allocation : allocations) {
            updateRoomTypeAllocation(
                    groupRoomTypeId,
                    allocation.getHotelId(),
                    allocation.getAllocated(),
                    allocation.getRoomInfoEditable());
        }
    }
    
    /**
     * 批量保存酒店房型分配
     * @param allocations 分配列表
     */
    public void batchSaveRoomTypeAllocations(List<GroupRoomTypeHotel> allocations) {
        for (GroupRoomTypeHotel allocation : allocations) {
            updateRoomTypeAllocation(
                    allocation.getGroupRoomTypeId(),
                    allocation.getHotelId(),
                    allocation.getAllocated(),
                    allocation.getRoomInfoEditable());
        }
    }
}
