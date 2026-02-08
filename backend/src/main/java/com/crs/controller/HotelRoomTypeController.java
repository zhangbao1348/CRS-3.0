package com.crs.controller;

import com.crs.entity.HotelRoomType;
import com.crs.service.HotelRoomTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 酒店房型控制器
 * 用于处理酒店房型的HTTP请求
 */
@RestController
@RequestMapping("/api/hotel-room-types")
public class HotelRoomTypeController {
    
    private final HotelRoomTypeService hotelRoomTypeService;
    
    public HotelRoomTypeController(HotelRoomTypeService hotelRoomTypeService) {
        this.hotelRoomTypeService = hotelRoomTypeService;
    }
    
    /**
     * 获取酒店的所有房型
     * @param hotelId 酒店ID
     * @return 房型列表
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<?> getHotelRoomTypes(@PathVariable Integer hotelId) {
        try {
            List<HotelRoomType> roomTypes = hotelRoomTypeService.getHotelRoomTypes(hotelId);
            return ResponseEntity.ok(roomTypes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 根据ID获取酒店房型
     * @param id 房型ID
     * @return 房型信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getHotelRoomTypeById(@PathVariable Integer id) {
        try {
            var roomType = hotelRoomTypeService.getHotelRoomTypeById(id)
                    .orElseThrow(() -> new RuntimeException("Hotel room type not found"));
            return ResponseEntity.ok(roomType);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 创建酒店房型
     * @param hotelRoomType 房型信息
     * @return 创建的房型信息
     */
    @PostMapping
    public ResponseEntity<?> createHotelRoomType(@RequestBody HotelRoomType hotelRoomType) {
        try {
            HotelRoomType createdRoomType = hotelRoomTypeService.createHotelRoomType(hotelRoomType);
            return ResponseEntity.ok(createdRoomType);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 更新酒店房型
     * @param id 房型ID
     * @param hotelRoomType 房型信息
     * @return 更新后的房型信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHotelRoomType(
            @PathVariable Integer id,
            @RequestBody HotelRoomType hotelRoomType) {
        try {
            HotelRoomType updatedRoomType = hotelRoomTypeService.updateHotelRoomType(id, hotelRoomType);
            return ResponseEntity.ok(updatedRoomType);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 删除酒店房型
     * @param id 房型ID
     * @return 删除响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHotelRoomType(@PathVariable Integer id) {
        try {
            hotelRoomTypeService.deleteHotelRoomType(id);
            return ResponseEntity.ok(Map.of("message", "Hotel room type deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
