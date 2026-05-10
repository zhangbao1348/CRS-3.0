package com.crs.controller;

import com.crs.entity.Hotel;
import com.crs.entity.HotelRoomTypeAllocation;
import com.crs.repository.HotelRepository;
import com.crs.service.HotelRoomTypeAllocationService;
import com.crs.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * HotelRoomTypeAllocationController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【HotelRoomTypeAllocationController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/09-系统设置.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 HotelRoomTypeAllocationController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/hotel-room-type-allocations")
@CrossOrigin(origins = "*")
public class HotelRoomTypeAllocationController {
    
    @Autowired
    private HotelRoomTypeAllocationService hotelRoomTypeAllocationService;

    @Autowired
    private HotelRepository hotelRepository;

    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        return tenantId != null ? tenantId : 1;
    }

    // =====================================================================
    // 合规接口：使用 hotelCode 作为关联条件（符合CODE关联规范）
    // =====================================================================

    /**
     * 根据 hotelCode 获取分配列表
     * 关联查询原则： tenantId + hotelCode
     */
    @GetMapping("/by-code/hotel/{hotelCode}")
    public ResponseEntity<List<HotelRoomTypeAllocation>> getAllocationsByHotelCode(
            @PathVariable String hotelCode) {
        List<HotelRoomTypeAllocation> allocations = hotelRoomTypeAllocationService.getAllocationsByHotelCode(hotelCode);
        return ResponseEntity.ok(allocations);
    }

    /**
     * 根据 hotelCode 和 roomTypeCode 获取单条分配
     * 关联查询原则： tenantId + hotelCode + roomTypeCode
     */
    @GetMapping("/by-code/hotel/{hotelCode}/room-type/{roomTypeCode}")
    public ResponseEntity<HotelRoomTypeAllocation> getAllocationByHotelCodeAndRoomTypeCode(
            @PathVariable String hotelCode, @PathVariable String roomTypeCode) {
        HotelRoomTypeAllocation allocation = hotelRoomTypeAllocationService.getAllocationByHotelCodeAndRoomTypeCode(hotelCode, roomTypeCode);
        if (allocation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allocation);
    }

    /**
     * 根据 hotelCode 获取已分配的房型列表
     * 关联查询原则： tenantId + hotelCode
     */
    @GetMapping("/by-code/hotel/{hotelCode}/allocated")
    public ResponseEntity<List<HotelRoomTypeAllocation>> getAllocatedRoomTypesByHotelCode(
            @PathVariable String hotelCode) {
        List<HotelRoomTypeAllocation> allocations = hotelRoomTypeAllocationService.getAllocatedRoomTypesByHotelCode(hotelCode);
        return ResponseEntity.ok(allocations);
    }

    /**
     * 根据 hotelCode 删除分配
     * 关联查询原则： tenantId + hotelCode
     */
    @DeleteMapping("/by-code/hotel/{hotelCode}")
    public ResponseEntity<Void> deleteAllocationsByHotelCode(@PathVariable String hotelCode) {
        hotelRoomTypeAllocationService.deleteAllocationsByHotelCode(hotelCode);
        return ResponseEntity.ok().build();
    }

    // =====================================================================
    // 已废弃接口：使用 hotelId（保留兼容，内部转化为 hotelCode 查询）
    // =====================================================================

    /**
     * @deprecated 请使用 GET /by-code/hotel/{hotelCode}，将在后续版本移除
     */
    @Deprecated
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<HotelRoomTypeAllocation>> getAllocationsByHotelId(@PathVariable Integer hotelId) {
        Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId);
        if (!hotelOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<HotelRoomTypeAllocation> allocations =
                hotelRoomTypeAllocationService.getAllocationsByHotelCode(hotelOpt.get().getHotelCode());
        return ResponseEntity.ok(allocations);
    }

    /**
     * @deprecated 请使用 GET /by-code/hotel/{hotelCode}/room-type/{roomTypeCode}，将在后续版本移除
     */
    @Deprecated
    @GetMapping("/hotel/{hotelId}/room-type/{roomTypeId}")
    public ResponseEntity<HotelRoomTypeAllocation> getAllocationByHotelAndRoomType(
            @PathVariable Integer hotelId, @PathVariable Integer roomTypeId) {
        HotelRoomTypeAllocation allocation = hotelRoomTypeAllocationService.getAllocationByHotelAndRoomType(hotelId, roomTypeId);
        return ResponseEntity.ok(allocation);
    }

    /**
     * @deprecated 请使用 GET /by-code/hotel/{hotelCode}/allocated，将在后续版本移除
     */
    @Deprecated
    @GetMapping("/hotel/{hotelId}/allocated")
    public ResponseEntity<List<HotelRoomTypeAllocation>> getAllocatedRoomTypesByHotelId(@PathVariable Integer hotelId) {
        Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId);
        if (!hotelOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<HotelRoomTypeAllocation> allocations =
                hotelRoomTypeAllocationService.getAllocatedRoomTypesByHotelCode(hotelOpt.get().getHotelCode());
        return ResponseEntity.ok(allocations);
    }
    
    @PostMapping
    public ResponseEntity<HotelRoomTypeAllocation> createAllocation(@RequestBody HotelRoomTypeAllocation allocation) {
        HotelRoomTypeAllocation createdAllocation = hotelRoomTypeAllocationService.createAllocation(allocation);
        return ResponseEntity.ok(createdAllocation);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<HotelRoomTypeAllocation> updateAllocation(@PathVariable Integer id, @RequestBody HotelRoomTypeAllocation allocation) {
        allocation.setId(id);
        HotelRoomTypeAllocation updatedAllocation = hotelRoomTypeAllocationService.updateAllocation(allocation);
        return ResponseEntity.ok(updatedAllocation);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAllocation(@PathVariable Integer id) {
        hotelRoomTypeAllocationService.deleteAllocation(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @deprecated 请使用 DELETE /by-code/hotel/{hotelCode}，将在后续版本移除
     */
    @Deprecated
    @DeleteMapping("/hotel/{hotelId}")
    public ResponseEntity<Void> deleteAllocationsByHotelId(@PathVariable Integer hotelId) {
        Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId);
        if (!hotelOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        hotelRoomTypeAllocationService.deleteAllocationsByHotelCode(hotelOpt.get().getHotelCode());
        return ResponseEntity.ok().build();
    }
}