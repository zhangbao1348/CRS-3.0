package com.crs.controller;

import com.crs.entity.RoomType;
import com.crs.service.RoomTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 酒店房型控制器
 * 用于处理HTTP请求并调用酒店房型服务
 */
@RestController
@RequestMapping("/api/room-types")
public class RoomTypeController {
    
    private final RoomTypeService roomTypeService;
    
    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }
    
    /**
     * 获取酒店房型列表
     * @return 酒店房型列表
     */
    @GetMapping
    public ResponseEntity<?> getRoomTypes() {
        List<RoomType> roomTypes = roomTypeService.getAllRoomTypes();
        return ResponseEntity.ok(roomTypes);
    }
    
    /**
     * 根据ID获取酒店房型详情
     * @param id 酒店房型ID
     * @return 酒店房型详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomTypeById(@PathVariable Integer id) {
        try {
            var roomType = roomTypeService.getRoomTypeById(id)
                    .orElseThrow(() -> new RuntimeException("Room type not found"));
            return ResponseEntity.ok(roomType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 根据酒店ID获取酒店房型列表
     * @param hotelId 酒店ID
     * @return 酒店房型列表
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<?> getRoomTypesByHotelId(@PathVariable Integer hotelId) {
        List<RoomType> roomTypes = roomTypeService.getRoomTypesByHotelId(hotelId);
        return ResponseEntity.ok(roomTypes);
    }
    
    /**
     * 创建酒店房型
     * @param roomType 酒店房型信息
     * @return 创建的酒店房型信息
     */
    @PostMapping
    public ResponseEntity<?> createRoomType(@RequestBody RoomType roomType) {
        try {
            var createdRoomType = roomTypeService.createRoomType(roomType);
            return ResponseEntity.ok(createdRoomType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 更新酒店房型
     * @param id 酒店房型ID
     * @param roomType 酒店房型信息
     * @return 更新后的酒店房型信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoomType(@PathVariable Integer id, @RequestBody RoomType roomType) {
        try {
            var updatedRoomType = roomTypeService.updateRoomType(id, roomType);
            return ResponseEntity.ok(updatedRoomType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 删除酒店房型
     * @param id 酒店房型ID
     * @return 删除响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoomType(@PathVariable Integer id) {
        try {
            roomTypeService.deleteRoomType(id);
            return ResponseEntity.ok(Map.of("message", "Room type deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
