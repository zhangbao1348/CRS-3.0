package com.crs.controller;

import com.crs.entity.HotelFacility;
import com.crs.entity.Hotel;
import com.crs.service.HotelFacilityService;
import com.crs.repository.HotelRepository;
import com.crs.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * HotelFacilityController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【HotelFacilityController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/09-系统设置.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 HotelFacilityController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/hotel-facilities")
@CrossOrigin(origins = "*")
public class HotelFacilityController {
    
    @Autowired
    private HotelFacilityService hotelFacilityService;
    
    @Autowired
    private HotelRepository hotelRepository;
    
    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        return tenantId != null ? tenantId : 1;
    }
    
    private boolean validateHotelTenant(String hotelCode) {
        return hotelRepository.findByHotelCodeAndTenantId(hotelCode, getCurrentTenantId()).isPresent();
    }
    
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<HotelFacility>> getFacilitiesByHotelId(@PathVariable Integer hotelId) {
        List<HotelFacility> facilities = hotelFacilityService.getFacilitiesByHotelId(hotelId);
        return ResponseEntity.ok(facilities);
    }
    
    @GetMapping("/hotel/{hotelId}/type/{type}")
    public ResponseEntity<List<HotelFacility>> getFacilitiesByType(@PathVariable Integer hotelId, @PathVariable String type) {
        List<HotelFacility> facilities = hotelFacilityService.getFacilitiesByType(hotelId, type);
        return ResponseEntity.ok(facilities);
    }
    
    @PostMapping
    public ResponseEntity<HotelFacility> createFacility(@RequestBody HotelFacility facility) {
        if (facility.getHotelId() == null && facility.getHotelCode() != null && !facility.getHotelCode().isEmpty()) {
            Optional<Hotel> hotelOpt = hotelRepository.findByHotelCodeAndTenantId(facility.getHotelCode(), getCurrentTenantId());
            if (hotelOpt.isEmpty()) {
                return ResponseEntity.status(403).build();
            }
            facility.setHotelId(hotelOpt.get().getId());
        }
        HotelFacility createdFacility = hotelFacilityService.createFacility(facility);
        return ResponseEntity.ok(createdFacility);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<HotelFacility> updateFacility(@PathVariable Integer id, @RequestBody HotelFacility facility) {
        if (facility.getHotelCode() != null && !facility.getHotelCode().isEmpty()) {
            if (!validateHotelTenant(facility.getHotelCode())) {
                return ResponseEntity.status(403).build();
            }
        }
        facility.setId(id);
        HotelFacility updatedFacility = hotelFacilityService.updateFacility(facility);
        return ResponseEntity.ok(updatedFacility);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacility(@PathVariable Integer id) {
        hotelFacilityService.deleteFacility(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/hotel/{hotelId}")
    public ResponseEntity<Void> deleteFacilitiesByHotelId(@PathVariable Integer hotelId) {
        hotelFacilityService.deleteFacilitiesByHotelId(hotelId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/by-code/hotel/{hotelCode}")
    public ResponseEntity<List<HotelFacility>> getFacilitiesByHotelCode(@PathVariable String hotelCode) {
        if (!validateHotelTenant(hotelCode)) {
            return ResponseEntity.status(403).build();
        }
        List<HotelFacility> facilities = hotelFacilityService.getFacilitiesByHotelCode(hotelCode);
        return ResponseEntity.ok(facilities);
    }

    @GetMapping("/by-code/hotel/{hotelCode}/type/{type}")
    public ResponseEntity<List<HotelFacility>> getFacilitiesByHotelCodeAndType(@PathVariable String hotelCode, @PathVariable String type) {
        if (!validateHotelTenant(hotelCode)) {
            return ResponseEntity.status(403).build();
        }
        List<HotelFacility> facilities = hotelFacilityService.getFacilitiesByHotelCodeAndType(hotelCode, type);
        return ResponseEntity.ok(facilities);
    }

    @DeleteMapping("/by-code/hotel/{hotelCode}")
    public ResponseEntity<Void> deleteFacilitiesByHotelCode(@PathVariable String hotelCode) {
        if (!validateHotelTenant(hotelCode)) {
            return ResponseEntity.status(403).build();
        }
        hotelFacilityService.deleteFacilitiesByHotelCode(hotelCode);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<HotelFacility>> getAllFacilities() {
        List<HotelFacility> facilities = hotelFacilityService.getAllFacilities();
        return ResponseEntity.ok(facilities);
    }
}