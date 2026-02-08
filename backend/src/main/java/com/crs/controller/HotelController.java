package com.crs.controller;

import com.crs.entity.Hotel;
import com.crs.service.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 酒店控制器
 * 用于处理HTTP请求并调用酒店服务
 */
@RestController
@RequestMapping("/api/hotels")
public class HotelController {
    
    private final HotelService hotelService;
    
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }
    
    /**
     * 获取酒店列表（只返回状态为active的酒店）
     * @return 酒店列表
     */
    @GetMapping
    public ResponseEntity<?> getHotels() {
        List<Hotel> hotels = hotelService.getHotelsByStatus(Hotel.Status.active);
        return ResponseEntity.ok(hotels);
    }
    
    /**
     * 根据ID获取酒店详情
     * @param id 酒店ID
     * @return 酒店详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getHotelById(@PathVariable Integer id) {
        try {
            var hotel = hotelService.getHotelById(id)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));
            return ResponseEntity.ok(hotel);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 根据集团ID获取酒店列表
     * @param groupId 集团ID
     * @return 酒店列表
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getHotelsByGroupId(@PathVariable Integer groupId) {
        List<Hotel> hotels = hotelService.getHotelsByGroupId(groupId);
        return ResponseEntity.ok(hotels);
    }
    
    /**
     * 创建酒店
     * @param hotel 酒店信息
     * @return 创建的酒店信息
     */
    @PostMapping
    public ResponseEntity<?> createHotel(@RequestBody Hotel hotel) {
        try {
            var createdHotel = hotelService.createHotel(hotel);
            return ResponseEntity.ok(createdHotel);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 更新酒店
     * @param id 酒店ID
     * @param hotel 酒店信息
     * @return 更新后的酒店信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHotel(@PathVariable Integer id, @RequestBody Hotel hotel) {
        try {
            var updatedHotel = hotelService.updateHotel(id, hotel);
            return ResponseEntity.ok(updatedHotel);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 删除酒店
     * @param id 酒店ID
     * @return 删除响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHotel(@PathVariable Integer id) {
        try {
            hotelService.deleteHotel(id);
            return ResponseEntity.ok(Map.of("message", "Hotel deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 根据状态获取酒店列表
     * @param status 状态
     * @return 酒店列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getHotelsByStatus(@PathVariable String status) {
        try {
            Hotel.Status statusEnum = Hotel.Status.valueOf(status);
            List<Hotel> hotels = hotelService.getHotelsByStatus(statusEnum);
            return ResponseEntity.ok(hotels);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status"));
        }
    }
    
    /**
     * 根据城市获取酒店列表
     * @param city 城市
     * @return 酒店列表
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<?> getHotelsByCity(@PathVariable String city) {
        List<Hotel> hotels = hotelService.getHotelsByCity(city);
        return ResponseEntity.ok(hotels);
    }
}
