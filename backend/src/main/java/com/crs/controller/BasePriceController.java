package com.crs.controller;

import com.crs.entity.BasePrice;
import com.crs.service.BasePriceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * 基础价格控制器 (BasePriceController)
 * 已根据【CODE关联规范】重构，所有基于酒店维度的查询均使用 hotelCode。
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
     * 根据内部 ID 获取基础价格详情 (仅用于系统内精确定位)
     * @param id 基础价格记录 ID
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
     * 根据酒店编码获取基础价格列表 (符合规范)
     * @param hotelCode 酒店编码
     * @return 基础价格列表
     */
    @GetMapping("/hotel/{hotelCode}")
    public ResponseEntity<?> getBasePricesByHotelCode(@PathVariable String hotelCode) {
        List<BasePrice> basePrices = basePriceService.getBasePricesByHotelCode(hotelCode);
        return ResponseEntity.ok(basePrices);
    }
    
    /**
     * 根据日期范围和酒店编码获取基础价格
     * @param hotelCode 酒店编码
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 基础价格列表
     */
    @GetMapping("/date-range")
    public ResponseEntity<?> getBasePricesByDateRange(
            @RequestParam String hotelCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            List<BasePrice> basePrices = basePriceService.getBasePricesByDateRange(hotelCode, startDate, endDate);
            return ResponseEntity.ok(basePrices);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid parameters or date format"));
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
     * @param id 基础价格记录 ID
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
     * @param id 基础价格记录 ID
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
