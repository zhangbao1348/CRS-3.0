package com.crs.controller;

import com.crs.entity.Group;
import com.crs.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 集团控制器
 * 用于处理HTTP请求并调用集团服务
 */
@RestController
@RequestMapping("/api/groups")
public class GroupController {
    
    private final GroupService groupService;
    
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }
    
    /**
     * 获取集团列表
     * @return 集团列表
     */
    @GetMapping
    public ResponseEntity<?> getGroups() {
        List<Group> groups = groupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }
    
    /**
     * 根据ID获取集团详情
     * @param id 集团ID
     * @return 集团详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable Integer id) {
        try {
            var group = groupService.getGroupById(id)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            return ResponseEntity.ok(group);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 创建集团
     * @param group 集团信息
     * @return 创建的集团信息
     */
    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody Group group) {
        try {
            var createdGroup = groupService.createGroup(group);
            return ResponseEntity.ok(createdGroup);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 更新集团
     * @param id 集团ID
     * @param group 集团信息
     * @return 更新后的集团信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroup(@PathVariable Integer id, @RequestBody Group group) {
        try {
            var updatedGroup = groupService.updateGroup(id, group);
            return ResponseEntity.ok(updatedGroup);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 删除集团
     * @param id 集团ID
     * @return 删除响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable Integer id) {
        try {
            groupService.deleteGroup(id);
            return ResponseEntity.ok(Map.of("message", "Group deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 根据状态获取集团列表
     * @param status 状态
     * @return 集团列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getGroupsByStatus(@PathVariable String status) {
        try {
            Group.Status statusEnum = Group.Status.valueOf(status);
            List<Group> groups = groupService.getGroupsByStatus(statusEnum);
            return ResponseEntity.ok(groups);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status"));
        }
    }
    
    /**
     * 根据集团名称搜索集团
     * @param name 集团名称
     * @return 集团列表
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchGroups(@RequestParam String name) {
        List<Group> groups = groupService.searchGroupsByName(name);
        return ResponseEntity.ok(groups);
    }
}
