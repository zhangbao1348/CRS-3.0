package com.crs.controller;

import com.crs.entity.GroupRoomTypeHotel;
import com.crs.entity.Hotel;
import com.crs.service.GroupRoomTypeHotelService;
import com.crs.repository.GroupRoomTypeHotelRepository;
import com.crs.repository.HotelRepository;
import com.crs.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * GroupRoomTypeHotelController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【GroupRoomTypeHotelController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/08-集团管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 GroupRoomTypeHotelController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/group-room-type-hotels")
public class GroupRoomTypeHotelController {
    
    private final GroupRoomTypeHotelService groupRoomTypeHotelService;
    
    @Autowired
    private GroupRoomTypeHotelRepository groupRoomTypeHotelRepository;
    
    @Autowired
    private HotelRepository hotelRepository;
    
    @Autowired
    private com.crs.repository.GroupRoomTypeRepository groupRoomTypeRepository;
    
    public GroupRoomTypeHotelController(GroupRoomTypeHotelService groupRoomTypeHotelService) {
        this.groupRoomTypeHotelService = groupRoomTypeHotelService;
    }
    
    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }
    
    private boolean validateHotelTenant(String hotelCode) {
        return hotelRepository.findByHotelCodeAndTenantId(hotelCode, getCurrentTenantId()).isPresent();
    }
    
    /**
     * 简单的分配DTO，避免序列化循环引用
     */
    private static class AllocationDTO {
        private Integer id;
        private String groupRoomTypeCode;
        private String hotelCode;
        private Boolean allocated;
        private Boolean roomInfoEditable;
        private String createdAt;
        private String updatedAt;
        
        public AllocationDTO(GroupRoomTypeHotel allocation) {
            this.id = allocation.getId();
            this.groupRoomTypeCode = allocation.getGroupRoomTypeCode();
            this.hotelCode = allocation.getHotelCode();
            this.allocated = allocation.getAllocated();
            this.roomInfoEditable = allocation.getRoomInfoEditable();
            this.createdAt = allocation.getCreatedAt() != null ? allocation.getCreatedAt().toString() : "";
            this.updatedAt = allocation.getUpdatedAt() != null ? allocation.getUpdatedAt().toString() : "";
        }
        
        // Getters
        public Integer getId() { return id; }
        public String getGroupRoomTypeCode() { return groupRoomTypeCode; }
        public String getHotelCode() { return hotelCode; }
        public Boolean getAllocated() { return allocated; }
        public Boolean getRoomInfoEditable() { return roomInfoEditable; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
    }
    
    /**
     * 获取集团房型的酒店分配列表 (基于 ID，为了兼容前端，但内部已转向 CODE)
     */
    @GetMapping("/group/{groupRoomTypeId}")
    public ResponseEntity<?> getGroupRoomTypeHotels(@PathVariable Integer groupRoomTypeId) {
        try {
            var grt = groupRoomTypeRepository.findById(groupRoomTypeId)
                    .orElseThrow(() -> new RuntimeException("Group room type not found"));
            List<GroupRoomTypeHotel> allocations = groupRoomTypeHotelService.getGroupRoomTypeHotelsByCode(grt.getRoomTypeCode());
            List<AllocationDTO> allocationDTOs = allocations.stream()
                    .map(AllocationDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(allocationDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 获取酒店的集团房型分配列表 (基于 ID，为了兼容前端)
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<Map<String, Object>> getHotelRoomTypeAllocations(@PathVariable Integer hotelId) {
        Map<String, Object> response = new HashMap<>();
        try {
            var hotel = hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));
            List<GroupRoomTypeHotel> allocations = groupRoomTypeHotelService.getHotelRoomTypeAllocationsByCode(hotel.getHotelCode());
            List<AllocationDTO> allocationDTOs = allocations.stream()
                    .map(AllocationDTO::new)
                    .collect(Collectors.toList());
            response.put("success", true);
            response.put("data", allocationDTOs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> batchSaveRoomTypeAllocations(@RequestBody List<GroupRoomTypeHotel> allocations) {
        try {
            groupRoomTypeHotelService.batchSaveRoomTypeAllocations(allocations);
            return ResponseEntity.ok(Map.of("message", "Batch save successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/group/{groupRoomTypeId}/hotel/{hotelId}")
    public ResponseEntity<?> updateRoomTypeAllocation(
            @PathVariable Integer groupRoomTypeId,
            @PathVariable Integer hotelId,
            @RequestBody Map<String, Boolean> allocationData) {
        try {
            Boolean allocated = allocationData.getOrDefault("allocated", false);
            Boolean roomInfoEditable = allocationData.getOrDefault("roomInfoEditable", false);
            
            var groupRoomType = groupRoomTypeRepository.findById(groupRoomTypeId)
                    .orElseThrow(() -> new RuntimeException("Group room type not found"));
            var hotel = hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));
                    
            GroupRoomTypeHotel allocation = groupRoomTypeHotelService.updateRoomTypeAllocationByCode(
                    groupRoomType.getRoomTypeCode(), hotel.getHotelCode(), allocated, roomInfoEditable);
            
            return ResponseEntity.ok(new AllocationDTO(allocation));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/group/{groupRoomTypeId}/batch")
    public ResponseEntity<?> batchUpdateRoomTypeAllocations(
            @PathVariable Integer groupRoomTypeId,
            @RequestBody List<GroupRoomTypeHotel> allocations) {
        try {
            groupRoomTypeHotelService.batchUpdateRoomTypeAllocations(groupRoomTypeId, allocations);
            return ResponseEntity.ok(Map.of("message", "Batch update successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/by-code/hotel/{hotelCode}")
    public ResponseEntity<Map<String, Object>> getHotelRoomTypeAllocationsByCode(@PathVariable String hotelCode) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!validateHotelTenant(hotelCode)) {
                response.put("success", false);
                response.put("message", "无权访问该酒店数据");
                return ResponseEntity.status(403).body(response);
            }
            
            List<GroupRoomTypeHotel> allocations = groupRoomTypeHotelRepository.findByTenantIdAndHotelCode(getCurrentTenantId(), hotelCode);
            List<AllocationDTO> allocationDTOs = allocations.stream()
                    .map(AllocationDTO::new)
                    .collect(Collectors.toList());
            response.put("success", true);
            response.put("data", allocationDTOs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
