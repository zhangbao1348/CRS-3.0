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
    
    public GroupRoomTypeHotelController(GroupRoomTypeHotelService groupRoomTypeHotelService) {
        this.groupRoomTypeHotelService = groupRoomTypeHotelService;
    }
    
    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        return tenantId != null ? tenantId : 1;
    }
    
    private boolean validateHotelTenant(String hotelCode) {
        return hotelRepository.findByHotelCodeAndTenantId(hotelCode, getCurrentTenantId()).isPresent();
    }
    
    /**
     * 简单的分配DTO，避免序列化循环引用
     */
    private static class AllocationDTO {
        private Integer id;
        private Integer groupRoomTypeId;
        private String groupRoomTypeCode;
        private Integer hotelId;
        private String hotelCode;
        private Boolean allocated;
        private Boolean roomInfoEditable;
        private String createdAt;
        private String updatedAt;
        
        public AllocationDTO(GroupRoomTypeHotel allocation) {
            this.id = allocation.getId();
            this.groupRoomTypeId = allocation.getGroupRoomTypeId();
            this.groupRoomTypeCode = allocation.getGroupRoomTypeCode();
            this.hotelId = allocation.getHotelId();
            this.hotelCode = allocation.getHotelCode();
            this.allocated = allocation.getAllocated();
            this.roomInfoEditable = allocation.getRoomInfoEditable();
            this.createdAt = allocation.getCreatedAt().toString();
            this.updatedAt = allocation.getUpdatedAt().toString();
        }
        
        // Getters
        public Integer getId() { return id; }
        public Integer getGroupRoomTypeId() { return groupRoomTypeId; }
        public String getGroupRoomTypeCode() { return groupRoomTypeCode; }
        public Integer getHotelId() { return hotelId; }
        public String getHotelCode() { return hotelCode; }
        public Boolean getAllocated() { return allocated; }
        public Boolean getRoomInfoEditable() { return roomInfoEditable; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
    }
    
    /**
     * 获取集团房型的酒店分配列表
     * @param groupRoomTypeId 集团房型ID
     * @return 分配列表
     */
    @GetMapping("/group/{groupRoomTypeId}")
    public ResponseEntity<?> getGroupRoomTypeHotels(@PathVariable Integer groupRoomTypeId) {
        try {
            List<GroupRoomTypeHotel> allocations = groupRoomTypeHotelService.getGroupRoomTypeHotels(groupRoomTypeId);
            // 转换为DTO列表，避免序列化循环引用
            List<AllocationDTO> allocationDTOs = allocations.stream()
                    .map(AllocationDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(allocationDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 获取酒店的集团房型分配列表
     * @param hotelId 酒店ID
     * @return 分配列表
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<Map<String, Object>> getHotelRoomTypeAllocations(@PathVariable Integer hotelId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<GroupRoomTypeHotel> allocations = groupRoomTypeHotelService.getHotelRoomTypeAllocations(hotelId);
            // 转换为DTO列表，避免序列化循环引用
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
    
    /**
     * 批量保存酒店房型分配
     * @param allocations 分配列表
     * @return 成功响应
     */
    @PostMapping
    public ResponseEntity<?> batchSaveRoomTypeAllocations(@RequestBody List<GroupRoomTypeHotel> allocations) {
        try {
            groupRoomTypeHotelService.batchSaveRoomTypeAllocations(allocations);
            return ResponseEntity.ok(Map.of("message", "Batch save successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 更新酒店房型分配状态
     * @param groupRoomTypeId 集团房型ID
     * @param hotelId 酒店ID
     * @param allocationData 分配数据
     * @return 关联信息
     */
    @PutMapping("/group/{groupRoomTypeId}/hotel/{hotelId}")
    public ResponseEntity<?> updateRoomTypeAllocation(
            @PathVariable Integer groupRoomTypeId,
            @PathVariable Integer hotelId,
            @RequestBody Map<String, Boolean> allocationData) {
        try {
            Boolean allocated = allocationData.getOrDefault("allocated", false);
            Boolean roomInfoEditable = allocationData.getOrDefault("roomInfoEditable", false);
            
            GroupRoomTypeHotel allocation = groupRoomTypeHotelService.updateRoomTypeAllocation(
                    groupRoomTypeId, hotelId, allocated, roomInfoEditable);
            
            // 返回DTO对象
            return ResponseEntity.ok(new AllocationDTO(allocation));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 批量更新酒店房型分配
     * @param groupRoomTypeId 集团房型ID
     * @param allocations 分配列表
     * @return 成功响应
     */
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
            
            // 关联查询原则：必须使用 tenantId + hotelCode 双维度隔离，防止跨租户数据泄露
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
