package com.crs.controller;

import com.crs.entity.GroupFacility;
import com.crs.service.GroupFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/group-facilities")
public class GroupFacilityController {
    
    @Autowired
    private GroupFacilityService groupFacilityService;
    
    /**
     * 获取所有集团设施
     * @return 设施列表
     */
    @GetMapping
    public ResponseEntity<List<GroupFacility>> getAllFacilities() {
        List<GroupFacility> facilities = groupFacilityService.getAllFacilities();
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
    public ResponseEntity<GroupFacility> createFacility(@RequestBody GroupFacility facility) {
        GroupFacility createdFacility = groupFacilityService.createFacility(facility);
        return ResponseEntity.ok(createdFacility);
    }
    
    /**
     * 更新设施
     * @param id 设施ID
     * @param facility 设施对象
     * @return 更新后的设施对象
     */
    @PutMapping("/{id}")
    public ResponseEntity<GroupFacility> updateFacility(@PathVariable Integer id, @RequestBody GroupFacility facility) {
        facility.setId(id);
        GroupFacility updatedFacility = groupFacilityService.updateFacility(facility);
        return ResponseEntity.ok(updatedFacility);
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
}
