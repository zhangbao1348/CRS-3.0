package com.crs.service;

import com.crs.entity.GroupRoomType;
import com.crs.entity.GroupRoomTypeHotel;
import com.crs.repository.GroupRoomTypeRepository;
import com.crs.repository.GroupRoomTypeHotelRepository;
import com.crs.repository.HotelRoomTypeRepository;
import com.crs.util.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupRoomTypeService {
    
    private final GroupRoomTypeRepository groupRoomTypeRepository;
    private final GroupRoomTypeHotelRepository groupRoomTypeHotelRepository;
    private final HotelRoomTypeRepository hotelRoomTypeRepository;
    private final GroupRoomTypeHotelService groupRoomTypeHotelService;
    
    public GroupRoomTypeService(
            GroupRoomTypeRepository groupRoomTypeRepository,
            GroupRoomTypeHotelRepository groupRoomTypeHotelRepository,
            HotelRoomTypeRepository hotelRoomTypeRepository,
            GroupRoomTypeHotelService groupRoomTypeHotelService) {
        this.groupRoomTypeRepository = groupRoomTypeRepository;
        this.groupRoomTypeHotelRepository = groupRoomTypeHotelRepository;
        this.hotelRoomTypeRepository = hotelRoomTypeRepository;
        this.groupRoomTypeHotelService = groupRoomTypeHotelService;
    }
    
    public List<GroupRoomType> getAllGroupRoomTypes() {
        // 获取当前租户ID
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }
        return groupRoomTypeRepository.findByGroupId(tenantId);
    }
    
    public Optional<GroupRoomType> getGroupRoomTypeById(Integer id) {
        return groupRoomTypeRepository.findById(id);
    }
    
    public List<GroupRoomType> getGroupRoomTypesByGroupId(Integer groupId) {
        return groupRoomTypeRepository.findByGroupId(groupId);
    }
    
    public List<GroupRoomType> getGroupRoomTypesByGroupIdAndCategory(Integer groupId, Integer categoryId) {
        List<GroupRoomType> allTypes = groupRoomTypeRepository.findByGroupId(groupId);
        if (categoryId == null) {
            return allTypes;
        }
        return allTypes.stream()
                .filter(rt -> categoryId.equals(rt.getRoomTypeCategoryId()))
                .collect(Collectors.toList());
    }
    
    public Optional<GroupRoomType> getGroupRoomTypeByCode(String roomTypeCode) {
        return groupRoomTypeRepository.findByRoomTypeCode(roomTypeCode);
    }
    
    public GroupRoomType createGroupRoomType(GroupRoomType groupRoomType) {
        if (groupRoomTypeRepository.existsByRoomTypeCode(groupRoomType.getRoomTypeCode())) {
            throw new RuntimeException("Room type code already exists");
        }
        return groupRoomTypeRepository.save(groupRoomType);
    }
    
    public GroupRoomType updateGroupRoomType(Integer id, GroupRoomType groupRoomType) {
        GroupRoomType existingGroupRoomType = groupRoomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group room type not found"));
        
        if (!existingGroupRoomType.getRoomTypeCode().equals(groupRoomType.getRoomTypeCode()) && 
                groupRoomTypeRepository.existsByRoomTypeCode(groupRoomType.getRoomTypeCode())) {
            throw new RuntimeException("Room type code already exists");
        }
        
        existingGroupRoomType.setGroupId(groupRoomType.getGroupId());
        existingGroupRoomType.setRoomTypeCode(groupRoomType.getRoomTypeCode());
        existingGroupRoomType.setRoomTypeName(groupRoomType.getRoomTypeName());
        existingGroupRoomType.setDescription(groupRoomType.getDescription());
        existingGroupRoomType.setRoomTypeCategoryId(groupRoomType.getRoomTypeCategoryId());
        existingGroupRoomType.setMaxOccupancy(groupRoomType.getMaxOccupancy());
        existingGroupRoomType.setSortOrder(groupRoomType.getSortOrder());
        existingGroupRoomType.setStatus(groupRoomType.getStatus());
        
        GroupRoomType updated = groupRoomTypeRepository.save(existingGroupRoomType);
        
        syncToHotelRoomTypes(updated);
        
        return updated;
    }
    
    private void syncToHotelRoomTypes(GroupRoomType groupRoomType) {
        var hotelRoomTypes = hotelRoomTypeRepository.findByGroupRoomTypeId(groupRoomType.getId());
        var allocations = groupRoomTypeHotelRepository.findByGroupRoomTypeId(groupRoomType.getId());
        
        for (var hotelRoomType : hotelRoomTypes) {
            var allocation = allocations.stream()
                    .filter(a -> a.getHotelId().equals(hotelRoomType.getHotelId()))
                    .findFirst();
            
            if (allocation.isPresent() && !allocation.get().getRoomInfoEditable()) {
                hotelRoomType.setRoomTypeName(groupRoomType.getRoomTypeName());
                hotelRoomType.setDescription(groupRoomType.getDescription());
                hotelRoomType.setRoomTypeCategoryId(groupRoomType.getRoomTypeCategoryId());
                hotelRoomType.setMaxOccupancy(groupRoomType.getMaxOccupancy());
                hotelRoomType.setStatus(groupRoomType.getStatus());
                hotelRoomTypeRepository.save(hotelRoomType);
            }
        }
    }
    
    public GroupRoomType enableGroupRoomType(Integer id) {
        GroupRoomType groupRoomType = groupRoomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group room type not found"));
        groupRoomType.setStatus("active");
        GroupRoomType updated = groupRoomTypeRepository.save(groupRoomType);
        syncToHotelRoomTypes(updated);
        return updated;
    }
    
    public GroupRoomType disableGroupRoomType(Integer id) {
        GroupRoomType groupRoomType = groupRoomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group room type not found"));
        groupRoomType.setStatus("inactive");
        GroupRoomType updated = groupRoomTypeRepository.save(groupRoomType);
        syncToHotelRoomTypes(updated);
        return updated;
    }
    
    @Transactional
    public void deleteGroupRoomType(Integer id) {
        if (!groupRoomTypeRepository.existsById(id)) {
            throw new RuntimeException("Group room type not found");
        }
        
        var hotelRoomTypes = hotelRoomTypeRepository.findByGroupRoomTypeId(id);
        if (!hotelRoomTypes.isEmpty()) {
            throw new RuntimeException("Cannot delete: room type is allocated to hotels");
        }
        
        groupRoomTypeHotelRepository.deleteByGroupRoomTypeId(id);
        groupRoomTypeRepository.deleteById(id);
    }
    
    public List<GroupRoomType> getGroupRoomTypesByStatus(String status) {
        return groupRoomTypeRepository.findByStatus(status);
    }
    
    @Transactional
    public void allocateToHotels(Integer groupRoomTypeId, List<GroupRoomTypeHotel> allocations) {
        if (!groupRoomTypeRepository.existsById(groupRoomTypeId)) {
            throw new RuntimeException("Group room type not found");
        }
        
        groupRoomTypeHotelRepository.deleteByGroupRoomTypeId(groupRoomTypeId);
        
        for (var allocation : allocations) {
            allocation.setGroupRoomTypeId(groupRoomTypeId);
            groupRoomTypeHotelRepository.save(allocation);
            
            // 创建或删除酒店房型
            if (Boolean.TRUE.equals(allocation.getAllocated())) {
                groupRoomTypeHotelService.createOrUpdateHotelRoomType(groupRoomTypeId, allocation.getHotelId());
            } else {
                groupRoomTypeHotelService.deleteHotelRoomType(groupRoomTypeId, allocation.getHotelId());
            }
        }
    }
    
    public List<GroupRoomTypeHotel> getAllocationsByGroupRoomTypeId(Integer groupRoomTypeId) {
        return groupRoomTypeHotelRepository.findByGroupRoomTypeId(groupRoomTypeId);
    }
    
    public long countByGroupId(Integer groupId) {
        return groupRoomTypeRepository.countByGroupId(groupId);
    }
}
