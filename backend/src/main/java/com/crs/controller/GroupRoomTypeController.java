package com.crs.controller;

import com.crs.entity.GroupRoomType;
import com.crs.service.GroupRoomTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 集团房型控制器
 * 用于处理HTTP请求并调用集团房型服务
 */
@RestController
@RequestMapping("/api/group-room-types")
public class GroupRoomTypeController {
    
    private final GroupRoomTypeService groupRoomTypeService;
    
    public GroupRoomTypeController(GroupRoomTypeService groupRoomTypeService) {
        this.groupRoomTypeService = groupRoomTypeService;
    }
    
    /**
     * 获取集团房型列表
     * @return 集团房型列表
     */
    @GetMapping
    public ResponseEntity<?> getGroupRoomTypes() {
        List<GroupRoomType> groupRoomTypes = groupRoomTypeService.getAllGroupRoomTypes();
        return ResponseEntity.ok(groupRoomTypes);
    }
    
    /**
     * 根据ID获取集团房型详情
     * @param id 集团房型ID
     * @return 集团房型详情
     */
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
    
    /**
     * 根据集团ID获取集团房型列表
     * @param groupId 集团ID
     * @return 集团房型列表
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getGroupRoomTypesByGroupId(@PathVariable Integer groupId) {
        List<GroupRoomType> groupRoomTypes = groupRoomTypeService.getGroupRoomTypesByGroupId(groupId);
        return ResponseEntity.ok(groupRoomTypes);
    }
    
    /**
     * 创建集团房型
     * @param groupRoomType 集团房型信息
     * @return 创建的集团房型信息
     */
    @PostMapping
    public ResponseEntity<?> createGroupRoomType(@RequestBody GroupRoomType groupRoomType) {
        try {
            var createdGroupRoomType = groupRoomTypeService.createGroupRoomType(groupRoomType);
            return ResponseEntity.ok(createdGroupRoomType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 更新集团房型
     * @param id 集团房型ID
     * @param groupRoomType 集团房型信息
     * @return 更新后的集团房型信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroupRoomType(@PathVariable Integer id, @RequestBody GroupRoomType groupRoomType) {
        try {
            var updatedGroupRoomType = groupRoomTypeService.updateGroupRoomType(id, groupRoomType);
            return ResponseEntity.ok(updatedGroupRoomType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 删除集团房型
     * @param id 集团房型ID
     * @return 删除响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroupRoomType(@PathVariable Integer id) {
        try {
            groupRoomTypeService.deleteGroupRoomType(id);
            return ResponseEntity.ok(Map.of("message", "Group room type deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
