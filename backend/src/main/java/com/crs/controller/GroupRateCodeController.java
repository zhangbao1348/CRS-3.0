package com.crs.controller;

import com.crs.entity.GroupRateCode;
import com.crs.service.GroupRateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 集团房价码控制器
 * 提供集团房价码的RESTful API接口
 */
@RestController
@RequestMapping("/api/group-rate-codes")
public class GroupRateCodeController {
    
    @Autowired
    private GroupRateCodeService groupRateCodeService;
    
    /**
     * 获取所有集团房价码
     * @return 集团房价码列表
     */
    @GetMapping
    public ResponseEntity<List<GroupRateCode>> getAllGroupRateCodes() {
        List<GroupRateCode> rateCodes = groupRateCodeService.getAllGroupRateCodes();
        return ResponseEntity.ok(rateCodes);
    }
    
    /**
     * 根据ID获取集团房价码
     * @param id 集团房价码ID
     * @return 集团房价码对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<GroupRateCode> getGroupRateCodeById(@PathVariable Integer id) {
        GroupRateCode rateCode = groupRateCodeService.getGroupRateCodeById(id);
        if (rateCode == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rateCode);
    }
    
    /**
     * 根据集团ID获取集团房价码列表
     * @param groupId 集团ID
     * @return 集团房价码列表
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<GroupRateCode>> getGroupRateCodesByGroupId(@PathVariable Integer groupId) {
        List<GroupRateCode> rateCodes = groupRateCodeService.getGroupRateCodesByGroupId(groupId);
        return ResponseEntity.ok(rateCodes);
    }
    
    /**
     * 根据房价码代码获取集团房价码
     * @param rateCode 房价码代码
     * @return 集团房价码对象
     */
    @GetMapping("/code/{rateCode}")
    public ResponseEntity<GroupRateCode> getGroupRateCodeByRateCode(@PathVariable String rateCode) {
        GroupRateCode rateCodeObj = groupRateCodeService.getGroupRateCodeByRateCode(rateCode);
        if (rateCodeObj == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rateCodeObj);
    }
    
    /**
     * 创建集团房价码
     * @param groupRateCode 集团房价码对象
     * @return 创建的集团房价码对象
     */
    @PostMapping
    public ResponseEntity<?> createGroupRateCode(@RequestBody GroupRateCode groupRateCode) {
        try {
            GroupRateCode createdRateCode = groupRateCodeService.createGroupRateCode(groupRateCode);
            return ResponseEntity.ok(createdRateCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("创建集团房价码失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新集团房价码
     * @param id 集团房价码ID
     * @param groupRateCode 集团房价码对象
     * @return 更新后的集团房价码对象
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroupRateCode(@PathVariable Integer id, @RequestBody GroupRateCode groupRateCode) {
        try {
            GroupRateCode updatedRateCode = groupRateCodeService.updateGroupRateCode(id, groupRateCode);
            return ResponseEntity.ok(updatedRateCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("更新集团房价码失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除集团房价码
     * @param id 集团房价码ID
     * @return 响应结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroupRateCode(@PathVariable Integer id) {
        try {
            groupRateCodeService.deleteGroupRateCode(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("删除集团房价码失败: " + e.getMessage());
        }
    }
    
    /**
     * 启用集团房价码
     * @param id 集团房价码ID
     * @return 启用后的集团房价码对象
     */
    @PutMapping("/{id}/enable")
    public ResponseEntity<?> enableGroupRateCode(@PathVariable Integer id) {
        try {
            GroupRateCode enabledRateCode = groupRateCodeService.enableGroupRateCode(id);
            return ResponseEntity.ok(enabledRateCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("启用集团房价码失败: " + e.getMessage());
        }
    }
    
    /**
     * 停用集团房价码
     * @param id 集团房价码ID
     * @return 停用后的集团房价码对象
     */
    @PutMapping("/{id}/disable")
    public ResponseEntity<?> disableGroupRateCode(@PathVariable Integer id) {
        try {
            GroupRateCode disabledRateCode = groupRateCodeService.disableGroupRateCode(id);
            return ResponseEntity.ok(disabledRateCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("停用集团房价码失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据集团ID和状态获取集团房价码列表
     * @param groupId 集团ID
     * @param status 状态
     * @return 集团房价码列表
     */
    @GetMapping("/group/{groupId}/status/{status}")
    public ResponseEntity<List<GroupRateCode>> getGroupRateCodesByGroupIdAndStatus(
            @PathVariable Integer groupId, 
            @PathVariable String status) {
        try {
            List<GroupRateCode> rateCodes = groupRateCodeService.getGroupRateCodesByGroupIdAndStatus(groupId, status);
            return ResponseEntity.ok(rateCodes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
