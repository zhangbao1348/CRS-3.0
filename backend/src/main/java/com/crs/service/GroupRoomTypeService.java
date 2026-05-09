package com.crs.service;

import com.crs.entity.GroupRoomType;
import com.crs.entity.GroupRoomTypeHotel;
import com.crs.entity.Hotel;
import com.crs.repository.GroupRoomTypeRepository;
import com.crs.repository.GroupRoomTypeHotelRepository;
import com.crs.repository.HotelRepository;
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
    private final HotelRepository hotelRepository;
    
    public GroupRoomTypeService(
            GroupRoomTypeRepository groupRoomTypeRepository,
            GroupRoomTypeHotelRepository groupRoomTypeHotelRepository,
            HotelRoomTypeRepository hotelRoomTypeRepository,
            GroupRoomTypeHotelService groupRoomTypeHotelService,
            HotelRepository hotelRepository) {
        this.groupRoomTypeRepository = groupRoomTypeRepository;
        this.groupRoomTypeHotelRepository = groupRoomTypeHotelRepository;
        this.hotelRoomTypeRepository = hotelRoomTypeRepository;
        this.groupRoomTypeHotelService = groupRoomTypeHotelService;
        this.hotelRepository = hotelRepository;
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
        Integer tenantId = TenantContext.getTenantId();
        return groupRoomTypeRepository.findByGroupIdAndRoomTypeCode(tenantId != null ? tenantId : 1, roomTypeCode);
    }
    
    public GroupRoomType createGroupRoomType(GroupRoomType groupRoomType) {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) tenantId = 1;
        if (groupRoomTypeRepository.existsByGroupIdAndRoomTypeCode(tenantId, groupRoomType.getRoomTypeCode())) {
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
            
            Integer hotelId = allocation.getHotelId();
            if ((hotelId == null) && allocation.getHotelCode() != null && !allocation.getHotelCode().isEmpty()) {
                Integer tenantId = TenantContext.getTenantId();
                Hotel hotel = hotelRepository.findByHotelCodeAndTenantId(allocation.getHotelCode(), tenantId != null ? tenantId : 1).orElse(null);
                if (hotel != null) {
                    hotelId = hotel.getId();
                    allocation.setHotelId(hotelId);
                }
            }
            
            groupRoomTypeHotelRepository.save(allocation);
            
            if (Boolean.TRUE.equals(allocation.getAllocated()) && hotelId != null) {
                groupRoomTypeHotelService.createOrUpdateHotelRoomType(groupRoomTypeId, hotelId);
            } else if (hotelId != null) {
                groupRoomTypeHotelService.deleteHotelRoomType(groupRoomTypeId, hotelId);
            }
        }
    }
    
    public List<GroupRoomTypeHotel> getAllocationsByGroupRoomTypeId(Integer groupRoomTypeId) {
        return groupRoomTypeHotelRepository.findByGroupRoomTypeId(groupRoomTypeId);
    }
    
    public long countByGroupId(Integer groupId) {
        return groupRoomTypeRepository.countByGroupId(groupId);
    }

    public List<GroupRoomType> getGroupRoomTypesByGroupCode(String groupCode) {
        return groupRoomTypeRepository.findByGroupCode(groupCode);
    }

    public List<GroupRoomType> getGroupRoomTypesByGroupCodeAndStatus(String groupCode, String status) {
        return groupRoomTypeRepository.findByGroupCodeAndStatus(groupCode, status);
    }

    public Optional<GroupRoomType> getGroupRoomTypeByGroupCodeAndRoomTypeCode(String groupCode, String roomTypeCode) {
        return groupRoomTypeRepository.findByGroupCodeAndRoomTypeCode(groupCode, roomTypeCode);
    }

    public List<GroupRoomType> getGroupRoomTypesByGroupCodeAndCategoryCode(String groupCode, String categoryCode) {
        if (categoryCode == null) {
            return groupRoomTypeRepository.findByGroupCode(groupCode);
        }
        return groupRoomTypeRepository.findByGroupCodeAndRoomTypeCategoryCode(groupCode, categoryCode);
    }
}
