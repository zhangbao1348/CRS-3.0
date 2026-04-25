package com.crs.controller;

import com.crs.entity.Hotel;
import com.crs.repository.HotelRepository;
import com.crs.service.HotelService;
import com.crs.util.CodeValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    private final HotelRepository hotelRepository;
    
    public HotelController(HotelService hotelService, HotelRepository hotelRepository) {
        this.hotelService = hotelService;
        this.hotelRepository = hotelRepository;
    }
    
    /**
     * 获取酒店列表（只返回状态为active的酒店）
     * @param tenantId 租户ID（可选）
     * @return 酒店列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getHotels(@RequestParam(required = false) Integer tenantId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Hotel> hotels;
            if (tenantId != null && tenantId > 0) {
                hotels = hotelService.getHotelsByTenantIdAndStatus(tenantId, Hotel.Status.active);
            } else {
                hotels = hotelService.getHotelsByStatus(Hotel.Status.active);
            }
            response.put("success", true);
            response.put("data", hotels);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取酒店列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 根据ID获取酒店详情
     * @param id 酒店ID
     * @return 酒店详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getHotelById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            var hotel = hotelService.getHotelById(id)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));
            response.put("success", true);
            response.put("data", hotel);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "获取酒店详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 根据租户ID获取酒店列表
     * @param tenantId 租户ID
     * @return 酒店列表
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<Map<String, Object>> getHotelsByTenantId(@PathVariable Integer tenantId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Hotel> hotels = hotelService.getHotelsByTenantId(tenantId);
            response.put("success", true);
            response.put("data", hotels);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取酒店列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 创建酒店
     * @param hotel 酒店信息
     * @return 创建的酒店信息
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createHotel(@RequestBody Hotel hotel) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (hotel.getHotelCode() != null && !CodeValidator.isValid(hotel.getHotelCode())) {
                response.put("success", false);
                response.put("message", CodeValidator.ERROR_MESSAGE);
                return ResponseEntity.badRequest().body(response);
            }
            var createdHotel = hotelService.createHotel(hotel);
            response.put("success", true);
            response.put("data", createdHotel);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "创建酒店失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 更新酒店
     * @param id 酒店ID
     * @param hotel 酒店信息
     * @return 更新后的酒店信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateHotel(@PathVariable Integer id, @RequestBody Hotel hotel) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (hotel.getHotelCode() != null && !CodeValidator.isValid(hotel.getHotelCode())) {
                response.put("success", false);
                response.put("message", CodeValidator.ERROR_MESSAGE);
                return ResponseEntity.badRequest().body(response);
            }
            var updatedHotel = hotelService.updateHotel(id, hotel);
            response.put("success", true);
            response.put("data", updatedHotel);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "更新酒店失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 删除酒店
     * @param id 酒店ID
     * @return 删除响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteHotel(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            hotelService.deleteHotel(id);
            response.put("success", true);
            response.put("message", "Hotel deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "删除酒店失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 根据状态获取酒店列表
     * @param status 状态
     * @return 酒店列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getHotelsByStatus(@PathVariable String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            Hotel.Status statusEnum = Hotel.Status.valueOf(status);
            List<Hotel> hotels = hotelService.getHotelsByStatus(statusEnum);
            response.put("success", true);
            response.put("data", hotels);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Invalid status");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 根据城市获取酒店列表
     * @param city 城市
     * @return 酒店列表
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<Map<String, Object>> getHotelsByCity(@PathVariable String city) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Hotel> hotels = hotelService.getHotelsByCity(city);
            response.put("success", true);
            response.put("data", hotels);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取酒店列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 检查酒店CODE数据是否存在
     * @param id 酒店ID
     * @return 检查结果
     */
    @GetMapping("/{id}/check-code")
    public ResponseEntity<Map<String, Object>> checkHotelCode(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            var hotel = hotelService.getHotelById(id)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));
            boolean exists = hotel.getHotelCode() != null && !hotel.getHotelCode().trim().isEmpty();
            response.put("success", true);
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "检查酒店CODE失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ===== CODE-based endpoints =====
    
    /**
     * 根据酒店代码获取酒店详情
     * @param code 酒店代码
     * @return 酒店详情
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<Map<String, Object>> getHotelByCode(@PathVariable String code) {
        Map<String, Object> response = new HashMap<>();
        try {
            var hotel = hotelRepository.findByHotelCode(code)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));
            response.put("success", true);
            response.put("data", hotel);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "获取酒店详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 根据酒店代码更新酒店
     * @param code 酒店代码
     * @param hotel 酒店信息
     * @return 更新后的酒店信息
     */
    @PutMapping("/code/{code}")
    public ResponseEntity<Map<String, Object>> updateHotelByCode(@PathVariable String code, @RequestBody Hotel hotel) {
        Map<String, Object> response = new HashMap<>();
        try {
            var existing = hotelRepository.findByHotelCode(code)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));
            var updatedHotel = hotelService.updateHotel(existing.getId(), hotel);
            response.put("success", true);
            response.put("data", updatedHotel);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "更新酒店失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 根据酒店代码删除酒店
     * @param code 酒店代码
     * @return 删除响应
     */
    @DeleteMapping("/code/{code}")
    public ResponseEntity<Map<String, Object>> deleteHotelByCode(@PathVariable String code) {
        Map<String, Object> response = new HashMap<>();
        try {
            var existing = hotelRepository.findByHotelCode(code)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));
            hotelService.deleteHotel(existing.getId());
            response.put("success", true);
            response.put("message", "Hotel deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "删除酒店失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
