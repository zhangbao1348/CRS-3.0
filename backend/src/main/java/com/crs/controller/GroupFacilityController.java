package com.crs.controller;

import com.crs.entity.GroupFacility;
import com.crs.repository.GroupFacilityRepository;
import com.crs.service.GroupFacilityService;
import com.crs.util.CodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * GroupFacilityController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【GroupFacilityController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/08-集团管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 GroupFacilityController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/group-facilities")
public class GroupFacilityController {
    
    @Autowired
    private GroupFacilityService groupFacilityService;
    
    @Autowired
    private GroupFacilityRepository groupFacilityRepository;
    
    /**
     * 获取所有集团设施
     * @return 设施列表
     */
    @GetMapping
    public ResponseEntity<List<GroupFacility>> getAllFacilities(@RequestParam(required = false) String scope) {
        List<GroupFacility> facilities;
        if (scope != null && !scope.isEmpty()) {
            facilities = groupFacilityRepository.findByScope(scope);
        } else {
            facilities = groupFacilityService.getAllFacilities();
        }
        return ResponseEntity.ok(facilities);
    }
    
    /**
     * 根据设施类型查询设施
     * @param type 设施类型
     * @return 设施列表
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<GroupFacility>> getFacilitiesByType(@PathVariable String type) {
        List<GroupFacility> facilities = groupFacilityService.getFacilitiesByType(type);
        return ResponseEntity.ok(facilities);
    }
    
    /**
     * 根据ID获取设施
     * @param id 设施ID
     * @return 设施对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<GroupFacility> getFacilityById(@PathVariable Integer id) {
        GroupFacility facility = groupFacilityService.getFacilityById(id);
        return facility != null ? ResponseEntity.ok(facility) : ResponseEntity.notFound().build();
    }
    
    /**
     * 创建设施
     * @param facility 设施对象
     * @return 创建的设施对象
     */
    @PostMapping
    public ResponseEntity<?> createFacility(@RequestBody GroupFacility facility) {
        try {
            if (facility.getFacilityCode() != null && !CodeValidator.isValid(facility.getFacilityCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            GroupFacility createdFacility = groupFacilityService.createFacility(facility);
            return ResponseEntity.ok(createdFacility);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        }
    }
    
    /**
     * 更新设施
     * @param id 设施ID
     * @param facility 设施对象
     * @return 更新后的设施对象
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFacility(@PathVariable Integer id, @RequestBody GroupFacility facility) {
        try {
            if (facility.getFacilityCode() != null && !CodeValidator.isValid(facility.getFacilityCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            facility.setId(id);
            GroupFacility updatedFacility = groupFacilityService.updateFacility(facility);
            return ResponseEntity.ok(updatedFacility);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        }
    }
    
    /**
     * 删除设施
     * @param id 设施ID
     * @return 响应实体
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacility(@PathVariable Integer id) {
        groupFacilityService.deleteFacility(id);
        return ResponseEntity.ok().build();
    }
    
    // ===== CODE-based endpoints =====
    
    /**
     * 根据设施代码更新设施
     * @param code 设施代码
     * @param facility 设施对象
     * @return 更新后的设施对象
     */
    @PutMapping("/code/{code}")
    public ResponseEntity<?> updateFacilityByCode(@PathVariable String code, @RequestBody GroupFacility facility) {
        GroupFacility existing = groupFacilityRepository.findByFacilityCode(code);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        facility.setId(existing.getId());
        GroupFacility updatedFacility = groupFacilityService.updateFacility(facility);
        return ResponseEntity.ok(updatedFacility);
    }
    
    /**
     * 根据设施代码删除设施
     * @param code 设施代码
     * @return 响应实体
     */
    @DeleteMapping("/code/{code}")
    public ResponseEntity<?> deleteFacilityByCode(@PathVariable String code) {
        GroupFacility existing = groupFacilityRepository.findByFacilityCode(code);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        groupFacilityService.deleteFacility(existing.getId());
        return ResponseEntity.ok().build();
    }
}
