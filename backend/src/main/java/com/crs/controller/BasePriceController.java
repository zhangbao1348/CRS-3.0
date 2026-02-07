package com.crs.controller;

import com.crs.entity.BasePrice;
import com.crs.service.BasePriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * 基础价格控制器
 * 用于处理HTTP请求并调用基础价格服务
 */
@RestController
@RequestMapping("/api/base-prices")
public class BasePriceController {
    
    private final BasePriceService basePriceService;
    
    public BasePriceController(BasePriceService basePriceService) {
        this.basePriceService = basePriceService;
    }
    
    /**
     * 获取基础价格列表
     * @return 基础价格列表
     */
    @GetMapping
    public ResponseEntity<?> getBasePrices() {
        List<BasePrice> basePrices = basePriceService.getAllBasePrices();
        return ResponseEntity.ok(basePrices);
    }
    
    /**
     * 根据ID获取基础价格详情
     * @param id 基础价格ID
     * @return 基础价格详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBasePriceById(@PathVariable Integer id) {
        try {
            var basePrice = basePriceService.getBasePriceById(id)
                    .orElseThrow(() -> new RuntimeException("Base price not found"));
            return ResponseEntity.ok(basePrice);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 根据酒店ID获取基础价格列表
     * @param hotelId 酒店ID
     * @return 基础价格列表
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<?> getBasePricesByHotelId(@PathVariable Integer hotelId) {
        List<BasePrice> basePrices = basePriceService.getBasePricesByHotelId(hotelId);
        return ResponseEntity.ok(basePrices);
    }
    
    /**
     * 根据日期范围获取基础价格
     * @param hotelId 酒店ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 基础价格列表
     */
    @GetMapping("/date-range")
    public ResponseEntity<?> getBasePricesByDateRange(
            @RequestParam Integer hotelId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            Date start = new Date(startDate);
            Date end = new Date(endDate);
            List<BasePrice> basePrices = basePriceService.getBasePricesByDateRange(hotelId, start, end);
            return ResponseEntity.ok(basePrices);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid date format"));
        }
    }
    
    /**
     * 创建基础价格
     * @param basePrice 基础价格信息
     * @return 创建的基础价格信息
     */
    @PostMapping
    public ResponseEntity<?> createBasePrice(@RequestBody BasePrice basePrice) {
        try {
            var createdBasePrice = basePriceService.createBasePrice(basePrice);
            return ResponseEntity.ok(createdBasePrice);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 批量创建基础价格
     * @param basePrices 基础价格列表
     * @return 创建的基础价格列表
     */
    @PostMapping("/batch")
    public ResponseEntity<?> createBatchBasePrices(@RequestBody List<BasePrice> basePrices) {
        try {
            var createdBasePrices = basePriceService.createBatchBasePrices(basePrices);
            return ResponseEntity.ok(createdBasePrices);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 更新基础价格
     * @param id 基础价格ID
     * @param basePrice 基础价格信息
     * @return 更新后的基础价格信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBasePrice(@PathVariable Integer id, @RequestBody BasePrice basePrice) {
        try {
            var updatedBasePrice = basePriceService.updateBasePrice(id, basePrice);
            return ResponseEntity.ok(updatedBasePrice);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 删除基础价格
     * @param id 基础价格ID
     * @return 删除响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBasePrice(@PathVariable Integer id) {
        try {
            basePriceService.deleteBasePrice(id);
            return ResponseEntity.ok(Map.of("message", "Base price deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
