package com.crs.service;

import com.crs.entity.GroupRoomType;
import com.crs.entity.GroupRoomTypeHotel;
import com.crs.entity.Hotel;
import com.crs.entity.HotelRoomType;
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

/**
 * GroupRoomTypeService 服务接口 (Service Interface)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【GroupRoomTypeService】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/08-集团管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 GroupRoomTypeService 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
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
    
    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    public List<GroupRoomType> getAllGroupRoomTypes() {
        return groupRoomTypeRepository.findByGroupId(getCurrentTenantId());
    }
    
    public Optional<GroupRoomType> getGroupRoomTypeById(Integer id) {
        return groupRoomTypeRepository.findById(id)
                .filter(rt -> rt.getGroupId() != null && rt.getGroupId().equals(getCurrentTenantId()));
    }
    
    public List<GroupRoomType> getGroupRoomTypesByGroupId(Integer groupId) {
        return groupRoomTypeRepository.findByGroupId(getCurrentTenantId());
    }
    
    public List<GroupRoomType> getGroupRoomTypesByGroupIdAndCategory(Integer groupId, Integer categoryId) {
        List<GroupRoomType> allTypes = getGroupRoomTypesByGroupId(getCurrentTenantId());
        if (categoryId == null) {
            return allTypes;
        }
        return allTypes.stream()
                .filter(rt -> categoryId.equals(rt.getRoomTypeCategoryId()))
                .collect(Collectors.toList());
    }
    
    public Optional<GroupRoomType> getGroupRoomTypeByCode(String roomTypeCode) {
        return groupRoomTypeRepository.findByGroupIdAndRoomTypeCode(getCurrentTenantId(), roomTypeCode);
    }
    
    public GroupRoomType createGroupRoomType(GroupRoomType groupRoomType) {
        Integer tenantId = getCurrentTenantId();
        if (groupRoomTypeRepository.existsByGroupIdAndRoomTypeCode(tenantId, groupRoomType.getRoomTypeCode())) {
            throw new RuntimeException("Room type code already exists");
        }
        groupRoomType.setGroupId(tenantId);
        return groupRoomTypeRepository.save(groupRoomType);
    }
    
    public GroupRoomType updateGroupRoomType(Integer id, GroupRoomType groupRoomType) {
        GroupRoomType existingGroupRoomType = getGroupRoomTypeById(id)
                .orElseThrow(() -> new RuntimeException("Group room type not found or access denied"));
        
        Integer tenantId = getCurrentTenantId();
        if (!existingGroupRoomType.getRoomTypeCode().equals(groupRoomType.getRoomTypeCode()) && 
                groupRoomTypeRepository.existsByGroupIdAndRoomTypeCode(tenantId, groupRoomType.getRoomTypeCode())) {
            throw new RuntimeException("Room type code already exists");
        }
        
        existingGroupRoomType.setRoomTypeCode(groupRoomType.getRoomTypeCode());
        existingGroupRoomType.setRoomTypeName(groupRoomType.getRoomTypeName());
        existingGroupRoomType.setDescription(groupRoomType.getDescription());
        existingGroupRoomType.setMaxOccupancy(groupRoomType.getMaxOccupancy());
        existingGroupRoomType.setSortOrder(groupRoomType.getSortOrder());
        existingGroupRoomType.setStatus(groupRoomType.getStatus());
        
        GroupRoomType updated = groupRoomTypeRepository.save(existingGroupRoomType);
        
        syncToHotelRoomTypes(updated);
        
        return updated;
    }
    
    private void syncToHotelRoomTypes(GroupRoomType groupRoomType) {
        Integer tenantId = getCurrentTenantId();
        var hotelRoomTypes = hotelRoomTypeRepository.findByTenantIdAndGroupRoomTypeCode(tenantId, groupRoomType.getRoomTypeCode());
        var allocations = groupRoomTypeHotelRepository.findByTenantIdAndGroupRoomTypeCode(tenantId, groupRoomType.getRoomTypeCode());
        
        for (var hotelRoomType : hotelRoomTypes) {
            var allocation = allocations.stream()
                    .filter(a -> a.getHotelCode().equals(hotelRoomType.getHotelCode()))
                    .findFirst();
            
            if (allocation.isPresent() && !allocation.get().getRoomInfoEditable()) {
                hotelRoomType.setRoomTypeName(groupRoomType.getRoomTypeName());
                hotelRoomType.setDescription(groupRoomType.getDescription());
                hotelRoomType.setMaxOccupancy(groupRoomType.getMaxOccupancy());
                hotelRoomType.setStatus(groupRoomType.getStatus());
                hotelRoomTypeRepository.save(hotelRoomType);
            }
        }
    }
    
    public GroupRoomType enableGroupRoomType(Integer id) {
        GroupRoomType groupRoomType = getGroupRoomTypeById(id)
                .orElseThrow(() -> new RuntimeException("Group room type not found or access denied"));
        groupRoomType.setStatus("active");
        GroupRoomType updated = groupRoomTypeRepository.save(groupRoomType);
        syncToHotelRoomTypes(updated);
        return updated;
    }
    
    public GroupRoomType disableGroupRoomType(Integer id) {
        GroupRoomType groupRoomType = getGroupRoomTypeById(id)
                .orElseThrow(() -> new RuntimeException("Group room type not found or access denied"));
        groupRoomType.setStatus("inactive");
        GroupRoomType updated = groupRoomTypeRepository.save(groupRoomType);
        syncToHotelRoomTypes(updated);
        return updated;
    }
    
    @Transactional
    public void deleteGroupRoomType(Integer id) {
        GroupRoomType groupRoomType = getGroupRoomTypeById(id)
                .orElseThrow(() -> new RuntimeException("Group room type not found or access denied"));
        
        Integer tenantId = getCurrentTenantId();
        var hotelRoomTypes = hotelRoomTypeRepository.findByTenantIdAndGroupRoomTypeCode(tenantId, groupRoomType.getRoomTypeCode());
        if (!hotelRoomTypes.isEmpty()) {
            throw new RuntimeException("Cannot delete: room type is allocated to hotels");
        }
        
        groupRoomTypeHotelRepository.deleteByTenantIdAndGroupRoomTypeCode(tenantId, groupRoomType.getRoomTypeCode());
        groupRoomTypeRepository.delete(groupRoomType);
    }
    
    public List<GroupRoomType> getGroupRoomTypesByStatus(String status) {
        return groupRoomTypeRepository.findByGroupIdAndStatus(getCurrentTenantId(), status);
    }
    
    @Transactional
    public void allocateToHotels(Integer groupRoomTypeId, List<GroupRoomTypeHotel> allocations) {
        GroupRoomType groupRoomType = getGroupRoomTypeById(groupRoomTypeId)
                .orElseThrow(() -> new RuntimeException("Group room type not found or access denied"));
        
        Integer tenantId = getCurrentTenantId();
        groupRoomTypeHotelRepository.deleteByTenantIdAndGroupRoomTypeCode(tenantId, groupRoomType.getRoomTypeCode());
        
        for (var allocation : allocations) {
            allocation.setGroupRoomTypeCode(groupRoomType.getRoomTypeCode());
            allocation.setTenantId(tenantId);
            
            String hotelCode = allocation.getHotelCode();
            if (hotelCode == null || hotelCode.isEmpty()) {
                continue;
            }
            
            groupRoomTypeHotelRepository.save(allocation);
            
            if (Boolean.TRUE.equals(allocation.getAllocated())) {
                groupRoomTypeHotelService.createOrUpdateHotelRoomTypeByCode(groupRoomType.getRoomTypeCode(), hotelCode);
            } else {
                groupRoomTypeHotelService.deleteHotelRoomTypeByCode(groupRoomType.getRoomTypeCode(), hotelCode);
            }
        }
    }
    
    public List<GroupRoomTypeHotel> getAllocationsByGroupRoomTypeCode(String groupRoomTypeCode) {
        return groupRoomTypeHotelRepository.findByTenantIdAndGroupRoomTypeCode(getCurrentTenantId(), groupRoomTypeCode);
    }
    
    public long countByGroupId(Integer groupId) {
        return groupRoomTypeRepository.countByGroupId(getCurrentTenantId());
    }

    public List<GroupRoomType> getGroupRoomTypesByGroupCode(String groupCode) {
        // 强制使用当前租户上下文，groupCode 仅作为辅助过滤
        return groupRoomTypeRepository.findByGroupId(getCurrentTenantId()).stream()
                .filter(rt -> groupCode.equals(rt.getGroupCode()))
                .collect(Collectors.toList());
    }

    public List<GroupRoomType> getGroupRoomTypesByGroupCodeAndStatus(String groupCode, String status) {
        return groupRoomTypeRepository.findByGroupIdAndStatus(getCurrentTenantId(), status).stream()
                .filter(rt -> groupCode.equals(rt.getGroupCode()))
                .collect(Collectors.toList());
    }

    public Optional<GroupRoomType> getGroupRoomTypeByGroupCodeAndRoomTypeCode(String groupCode, String roomTypeCode) {
        return groupRoomTypeRepository.findByGroupIdAndRoomTypeCode(getCurrentTenantId(), roomTypeCode)
                .filter(rt -> groupCode.equals(rt.getGroupCode()));
    }

    public List<GroupRoomType> getGroupRoomTypesByGroupCodeAndCategoryCode(String groupCode, String categoryCode) {
        List<GroupRoomType> all = groupRoomTypeRepository.findByGroupId(getCurrentTenantId()).stream()
                .filter(rt -> groupCode.equals(rt.getGroupCode()))
                .collect(Collectors.toList());
        if (categoryCode == null) {
            return all;
        }
        return all.stream()
                .filter(rt -> categoryCode.equals(rt.getRoomTypeCategoryCode()))
                .collect(Collectors.toList());
    }
}
