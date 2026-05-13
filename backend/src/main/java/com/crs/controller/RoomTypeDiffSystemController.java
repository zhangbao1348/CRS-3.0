package com.crs.controller;

import com.crs.entity.RoomTypeDiffSystem;
import com.crs.service.RoomTypeDiffSystemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 房型差价体系控制器
 * 用于处理HTTP请求并调用房型差价体系服务
 */
@RestController
@RequestMapping("/api/room-type-diff-systems")
@CrossOrigin(origins = "*")
public class RoomTypeDiffSystemController {
    
    private final RoomTypeDiffSystemService roomTypeDiffSystemService;
    private final com.crs.repository.HotelRepository hotelRepository;
    
    public RoomTypeDiffSystemController(RoomTypeDiffSystemService roomTypeDiffSystemService, com.crs.repository.HotelRepository hotelRepository) {
        this.roomTypeDiffSystemService = roomTypeDiffSystemService;
        this.hotelRepository = hotelRepository;
    }
    
    /**
     * 获取房型差价体系列表
     * @return 房型差价体系列表
     */
    @GetMapping
    public ResponseEntity<?> getRoomTypeDiffSystems() {
        List<RoomTypeDiffSystem> roomTypeDiffSystems = roomTypeDiffSystemService.getAllRoomTypeDiffSystems();
        return ResponseEntity.ok(roomTypeDiffSystems);
    }
    
    /**
     * 根据ID获取房型差价体系详情
     * @param id 房型差价体系ID
     * @return 房型差价体系详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomTypeDiffSystemById(@PathVariable Integer id) {
        try {
            var roomTypeDiffSystem = roomTypeDiffSystemService.getRoomTypeDiffSystemById(id)
                    .orElseThrow(() -> new RuntimeException("Room type diff system not found"));
            return ResponseEntity.ok(roomTypeDiffSystem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 根据酒店ID获取房型差价体系列表
     * @param hotelId 酒店ID
     * @return 房型差价体系列表
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<?> getRoomTypeDiffSystemsByHotelId(@PathVariable Integer hotelId) {
        return hotelRepository.findById(hotelId)
                .map(hotel -> ResponseEntity.ok(roomTypeDiffSystemService.getRoomTypeDiffSystemsByHotelCode(hotel.getHotelCode())))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 创建房型差价体系
     * @param roomTypeDiffSystem 房型差价体系信息
     * @return 创建的房型差价体系信息
     */
    @PostMapping
    public ResponseEntity<?> createRoomTypeDiffSystem(@RequestBody RoomTypeDiffSystem roomTypeDiffSystem) {
        try {
            var createdRoomTypeDiffSystem = roomTypeDiffSystemService.createRoomTypeDiffSystem(roomTypeDiffSystem);
            return ResponseEntity.ok(createdRoomTypeDiffSystem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 更新房型差价体系
     * @param id 房型差价体系ID
     * @param roomTypeDiffSystem 房型差价体系信息
     * @return 更新后的房型差价体系信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoomTypeDiffSystem(@PathVariable Integer id, @RequestBody RoomTypeDiffSystem roomTypeDiffSystem) {
        try {
            var updatedRoomTypeDiffSystem = roomTypeDiffSystemService.updateRoomTypeDiffSystem(id, roomTypeDiffSystem);
            return ResponseEntity.ok(updatedRoomTypeDiffSystem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 删除房型差价体系
     * @param id 房型差价体系ID
     * @return 删除响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoomTypeDiffSystem(@PathVariable Integer id) {
        try {
            roomTypeDiffSystemService.deleteRoomTypeDiffSystem(id);
            return ResponseEntity.ok(Map.of("message", "Room type diff system deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
