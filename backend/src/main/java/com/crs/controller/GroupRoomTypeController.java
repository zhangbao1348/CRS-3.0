package com.crs.controller;

import com.crs.entity.GroupRoomType;
import com.crs.entity.GroupRoomTypeHotel;
import com.crs.repository.GroupRoomTypeRepository;
import com.crs.service.GroupRoomTypeService;
import com.crs.util.CodeValidator;
import com.crs.util.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/group-room-types")
public class GroupRoomTypeController {
    
    private final GroupRoomTypeService groupRoomTypeService;
    private final GroupRoomTypeRepository groupRoomTypeRepository;
    
    public GroupRoomTypeController(GroupRoomTypeService groupRoomTypeService, GroupRoomTypeRepository groupRoomTypeRepository) {
        this.groupRoomTypeService = groupRoomTypeService;
        this.groupRoomTypeRepository = groupRoomTypeRepository;
    }
    
    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        return tenantId != null ? tenantId : 1;
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getGroupRoomTypes() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<GroupRoomType> groupRoomTypes = groupRoomTypeService.getAllGroupRoomTypes();
            response.put("success", true);
            response.put("data", groupRoomTypes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupRoomTypeById(@PathVariable Integer id) {
        try {
            var groupRoomType = groupRoomTypeService.getGroupRoomTypeById(id)
                    .orElseThrow(() -> new RuntimeException("Group room type not found"));
            return ResponseEntity.ok(groupRoomType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getGroupRoomTypesByGroupId(
            @PathVariable Integer groupId,
            @RequestParam(required = false) Integer categoryId) {
        try {
            List<GroupRoomType> groupRoomTypes;
            if (categoryId != null) {
                groupRoomTypes = groupRoomTypeService.getGroupRoomTypesByGroupIdAndCategory(groupId, categoryId);
            } else {
                groupRoomTypes = groupRoomTypeService.getGroupRoomTypesByGroupId(groupId);
            }
            return ResponseEntity.ok(groupRoomTypes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/group/{groupId}/count")
    public ResponseEntity<?> countByGroupId(@PathVariable Integer groupId) {
        long count = groupRoomTypeService.countByGroupId(groupId);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    @GetMapping("/{id}/allocations")
    public ResponseEntity<?> getAllocationsByGroupRoomTypeId(@PathVariable Integer id) {
        List<GroupRoomTypeHotel> allocations = groupRoomTypeService.getAllocationsByGroupRoomTypeId(id);
        return ResponseEntity.ok(allocations);
    }
    
    @PostMapping
    public ResponseEntity<?> createGroupRoomType(@RequestBody GroupRoomType groupRoomType) {
        try {
            if (groupRoomType.getRoomTypeCode() != null && !CodeValidator.isValid(groupRoomType.getRoomTypeCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            var createdGroupRoomType = groupRoomTypeService.createGroupRoomType(groupRoomType);
            return ResponseEntity.ok(createdGroupRoomType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroupRoomType(@PathVariable Integer id, @RequestBody GroupRoomType groupRoomType) {
        try {
            if (groupRoomType.getRoomTypeCode() != null && !CodeValidator.isValid(groupRoomType.getRoomTypeCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            var updatedGroupRoomType = groupRoomTypeService.updateGroupRoomType(id, groupRoomType);
            return ResponseEntity.ok(updatedGroupRoomType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroupRoomType(@PathVariable Integer id) {
        return ResponseEntity.badRequest().body(Map.of("error", "集团房型原则上不允许物理删除，如需废弃请进行停用操作"));
    }
    
    @PutMapping("/{id}/enable")
    public ResponseEntity<?> enableGroupRoomType(@PathVariable Integer id) {
        try {
            var enabledGroupRoomType = groupRoomTypeService.enableGroupRoomType(id);
            return ResponseEntity.ok(enabledGroupRoomType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/disable")
    public ResponseEntity<?> disableGroupRoomType(@PathVariable Integer id) {
        try {
            var disabledGroupRoomType = groupRoomTypeService.disableGroupRoomType(id);
            return ResponseEntity.ok(disabledGroupRoomType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/allocate")
    public ResponseEntity<?> allocateToHotels(
            @PathVariable Integer id,
            @RequestBody List<GroupRoomTypeHotel> allocations) {
        try {
            groupRoomTypeService.allocateToHotels(id, allocations);
            return ResponseEntity.ok(Map.of("message", "Room type allocated to hotels successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ===== CODE-based endpoints =====
    
    /**
     * 根据房型代码获取集团房型
     * @param code 房型代码
     * @return 集团房型对象
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getGroupRoomTypeByCode(@PathVariable String code) {
        var groupRoomType = groupRoomTypeRepository.findByGroupIdAndRoomTypeCode(getCurrentTenantId(), code);
        if (groupRoomType.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groupRoomType.get());
    }
    
    /**
     * 根据房型代码更新集团房型
     * @param code 房型代码
     * @param groupRoomType 集团房型对象
     * @return 更新后的集团房型对象
     */
    @PutMapping("/code/{code}")
    public ResponseEntity<?> updateGroupRoomTypeByCode(@PathVariable String code, @RequestBody GroupRoomType groupRoomType) {
        var existing = groupRoomTypeRepository.findByGroupIdAndRoomTypeCode(getCurrentTenantId(), code);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            var updatedGroupRoomType = groupRoomTypeService.updateGroupRoomType(existing.get().getId(), groupRoomType);
            return ResponseEntity.ok(updatedGroupRoomType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 根据房型代码删除集团房型
     * @param code 房型代码
     * @return 删除结果
     */
    @DeleteMapping("/code/{code}")
    public ResponseEntity<?> deleteGroupRoomTypeByCode(@PathVariable String code) {
        return ResponseEntity.badRequest().body(Map.of("error", "集团房型原则上不允许物理删除，如需废弃请进行停用操作"));
    }
    
    /**
     * 根据房型代码启用集团房型
     * @param code 房型代码
     * @return 启用后的集团房型对象
     */
    @PutMapping("/code/{code}/enable")
    public ResponseEntity<?> enableGroupRoomTypeByCode(@PathVariable String code) {
        var existing = groupRoomTypeRepository.findByGroupIdAndRoomTypeCode(getCurrentTenantId(), code);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            var enabledGroupRoomType = groupRoomTypeService.enableGroupRoomType(existing.get().getId());
            return ResponseEntity.ok(enabledGroupRoomType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 根据房型代码停用集团房型
     * @param code 房型代码
     * @return 停用后的集团房型对象
     */
    @PutMapping("/code/{code}/disable")
    public ResponseEntity<?> disableGroupRoomTypeByCode(@PathVariable String code) {
        var existing = groupRoomTypeRepository.findByGroupIdAndRoomTypeCode(getCurrentTenantId(), code);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            var disabledGroupRoomType = groupRoomTypeService.disableGroupRoomType(existing.get().getId());
            return ResponseEntity.ok(disabledGroupRoomType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/by-code/group/{groupCode}")
    public ResponseEntity<Map<String, Object>> getGroupRoomTypesByGroupCode(
            @PathVariable String groupCode,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<GroupRoomType> groupRoomTypes;
            if (status != null) {
                groupRoomTypes = groupRoomTypeService.getGroupRoomTypesByGroupCodeAndStatus(groupCode, status);
            } else if (categoryCode != null) {
                groupRoomTypes = groupRoomTypeService.getGroupRoomTypesByGroupCodeAndCategoryCode(groupCode, categoryCode);
            } else {
                groupRoomTypes = groupRoomTypeService.getGroupRoomTypesByGroupCode(groupCode);
            }
            response.put("success", true);
            response.put("data", groupRoomTypes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/by-code/group/{groupCode}/room-type/{roomTypeCode}")
    public ResponseEntity<Map<String, Object>> getGroupRoomTypeByGroupCodeAndRoomTypeCode(
            @PathVariable String groupCode, @PathVariable String roomTypeCode) {
        Map<String, Object> response = new HashMap<>();
        try {
            var groupRoomType = groupRoomTypeService.getGroupRoomTypeByGroupCodeAndRoomTypeCode(groupCode, roomTypeCode)
                    .orElseThrow(() -> new RuntimeException("Group room type not found"));
            response.put("success", true);
            response.put("data", groupRoomType);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
