package com.crs.controller;

import com.crs.entity.RoomTypeFacility;
import com.crs.repository.RoomTypeFacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/room-type-facilities")
public class RoomTypeFacilityController {

    @Autowired
    private RoomTypeFacilityRepository repository;

    @GetMapping
    public ResponseEntity<?> getByRoomTypeId(@RequestParam Integer roomTypeId) {
        List<RoomTypeFacility> facilities = repository.findByRoomTypeId(roomTypeId);
        return ResponseEntity.ok(Map.of("success", true, "data", facilities));
    }

    @PostMapping("/batch")
    @Transactional
    public ResponseEntity<?> saveBatch(@RequestBody Map<String, Object> request) {
        try {
            Integer roomTypeId = (Integer) request.get("roomTypeId");
            Integer hotelId = (Integer) request.get("hotelId");
            String hotelCode = (String) request.get("hotelCode");
            String roomTypeCode = (String) request.get("roomTypeCode");
            @SuppressWarnings("unchecked")
            List<Map<String, String>> facilities = (List<Map<String, String>>) request.get("facilities");

            // 先删除该房型的所有设施
            repository.deleteByRoomTypeId(roomTypeId);

            // 批量新增
            if (facilities != null) {
                for (Map<String, String> f : facilities) {
                    RoomTypeFacility entity = new RoomTypeFacility();
                    entity.setRoomTypeId(roomTypeId);
                    entity.setHotelId(hotelId);
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
