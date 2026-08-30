package com.crs.controller;

import com.crs.entity.RoomTypeFacility;
import com.crs.repository.RoomTypeFacilityRepository;
import com.crs.repository.HotelRepository;
import com.crs.repository.HotelRoomTypeRepository;
import com.crs.util.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * RoomTypeFacilityController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【RoomTypeFacilityController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/12-房型管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 RoomTypeFacilityController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/room-type-facilities")
public class RoomTypeFacilityController {

    private final RoomTypeFacilityRepository repository;
    private final HotelRepository hotelRepository;
    private final HotelRoomTypeRepository hotelRoomTypeRepository;

    public RoomTypeFacilityController(
            RoomTypeFacilityRepository repository,
            HotelRepository hotelRepository,
            HotelRoomTypeRepository hotelRoomTypeRepository) {
        this.repository = repository;
        this.hotelRepository = hotelRepository;
        this.hotelRoomTypeRepository = hotelRoomTypeRepository;
    }

    private Integer currentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalArgumentException("租户上下文缺失");
        }
        return tenantId;
    }

    @GetMapping
    public ResponseEntity<?> getByRoomTypeCode(@RequestParam String hotelCode, @RequestParam String roomTypeCode) {
        Integer tenantId = currentTenantId();
        hotelRoomTypeRepository.findByTenantIdAndHotelCodeAndRoomTypeCode(tenantId, hotelCode, roomTypeCode)
                .orElseThrow(() -> new IllegalArgumentException("房型不存在或无权访问"));
        List<RoomTypeFacility> facilities = repository
                .findByTenantIdAndHotelCodeAndRoomTypeCode(tenantId, hotelCode, roomTypeCode);
        return ResponseEntity.ok(Map.of("success", true, "data", facilities));
    }

    @PostMapping("/batch")
    @Transactional
    public ResponseEntity<?> saveBatch(@RequestBody Map<String, Object> request) {
        try {
            String hotelCode = (String) request.get("hotelCode");
            String roomTypeCode = (String) request.get("roomTypeCode");
            @SuppressWarnings("unchecked")
            List<Map<String, String>> facilities = (List<Map<String, String>>) request.get("facilities");

            Integer tenantId = currentTenantId();
            var hotel = hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("酒店不存在或无权访问"));
            var roomType = hotelRoomTypeRepository
                    .findByTenantIdAndHotelCodeAndRoomTypeCode(tenantId, hotelCode, roomTypeCode)
                    .orElseThrow(() -> new IllegalArgumentException("房型不存在或无权访问"));

            // 先删除该房型的所有设施（租户 + 酒店 CODE + 房型 CODE）。
            repository.deleteByTenantIdAndHotelCodeAndRoomTypeCode(tenantId, hotelCode, roomTypeCode);

            // 批量新增
            if (facilities != null) {
                for (Map<String, String> f : facilities) {
                    RoomTypeFacility entity = new RoomTypeFacility();
                    entity.setTenantId(tenantId);
                    entity.setHotelId(hotel.getId());
                    entity.setRoomTypeId(roomType.getId());
                    entity.setHotelCode(hotelCode);
                    entity.setRoomTypeCode(roomTypeCode);
                    entity.setFacilityType(f.get("facilityType"));
                    entity.setFacilityName(f.get("facilityName"));
                    entity.setFacilityCode(f.get("facilityCode"));
                    entity.setAvailable(true);
                    repository.save(entity);
                }
            }

            return ResponseEntity.ok(Map.of("success", true, "message", "保存成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
