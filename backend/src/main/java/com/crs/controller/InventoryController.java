package com.crs.controller;

import com.crs.entity.Inventory;
import com.crs.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Date;

/**
 * 库存控制器
 * 提供库存管理的REST API端点
 */
@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;
    
    /**
     * 获取所有库存列表
     * @return 库存列表
     */
    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventories() {
        List<Inventory> inventories = inventoryService.getAllInventories();
        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }
    
    /**
     * 根据ID获取库存详情
     * @param id 库存ID
     * @return 库存详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable Integer id) {
        return inventoryService.getInventoryById(id)
                .map(inventory -> new ResponseEntity<>(inventory, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * 根据酒店ID获取库存列表
     * @param hotelId 酒店ID
     * @return 库存列表
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Inventory>> getInventoriesByHotelId(@PathVariable Integer hotelId) {
        List<Inventory> inventories = inventoryService.getInventoriesByHotelId(hotelId);
        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }
    
    /**
     * 根据酒店ID、价格计划ID和房型ID获取库存列表
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @return 库存列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<Inventory>> getInventoriesByParams(
            @RequestParam Integer hotelId,
            @RequestParam Integer ratePlanId,
            @RequestParam Integer roomTypeId) {
        List<Inventory> inventories = inventoryService.getInventoriesByHotelIdAndRatePlanIdAndRoomTypeId(
                hotelId, ratePlanId, roomTypeId);
        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }
    
    /**
     * 根据日期范围获取库存
     * @param hotelId 酒店ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 库存列表
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<Inventory>> getInventoriesByDateRange(
            @RequestParam Integer hotelId,
            @RequestParam Date startDate,
            @RequestParam Date endDate) {
        List<Inventory> inventories = inventoryService.getInventoriesByDateRange(hotelId, startDate, endDate);
        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }
    
    /**
     * 创建库存
     * @param inventory 库存信息
     * @return 创建的库存信息
     */
    @PostMapping
    public ResponseEntity<Inventory> createInventory(@RequestBody Inventory inventory) {
        Inventory createdInventory = inventoryService.createInventory(inventory);
        return new ResponseEntity<>(createdInventory, HttpStatus.CREATED);
    }
    
    /**
     * 批量创建库存
     * @param inventories 库存列表
     * @return 创建的库存列表
     */
    @PostMapping("/batch")
    public ResponseEntity<List<Inventory>> createBatchInventories(@RequestBody List<Inventory> inventories) {
        List<Inventory> createdInventories = inventoryService.createBatchInventories(inventories);
        return new ResponseEntity<>(createdInventories, HttpStatus.CREATED);
    }
    
    /**
     * 更新库存
     * @param id 库存ID
     * @param inventory 库存信息
     * @return 更新后的库存信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(@PathVariable Integer id, @RequestBody Inventory inventory) {
        try {
            Inventory updatedInventory = inventoryService.updateInventory(id, inventory);
            return new ResponseEntity<>(updatedInventory, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * 更新可用房间数量
     * @param id 库存ID
     * @param availableRooms 可用房间数量
     * @return 更新后的库存信息
     */
    @PatchMapping("/{id}/available-rooms")
    public ResponseEntity<Inventory> updateAvailableRooms(@PathVariable Integer id, @RequestParam Integer availableRooms) {
        try {
            Inventory updatedInventory = inventoryService.updateAvailableRooms(id, availableRooms);
            return new ResponseEntity<>(updatedInventory, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * 批量更新库存
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param availableRooms 可用房间数量
     * @return 更新结果
     */
    @PutMapping("/batch-update")
    public ResponseEntity<String> batchUpdateInventory(
            @RequestParam Integer hotelId,
            @RequestParam Integer ratePlanId,
            @RequestParam Integer roomTypeId,
            @RequestParam Date startDate,
            @RequestParam Date endDate,
            @RequestParam Integer availableRooms) {
        int updatedCount = inventoryService.batchUpdateInventory(
                hotelId, ratePlanId, roomTypeId, startDate, endDate, availableRooms);
        return new ResponseEntity<>("Updated " + updatedCount + " inventories", HttpStatus.OK);
    }
    
    /**
     * 删除库存
     * @param id 库存ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Integer id) {
        try {
            inventoryService.deleteInventory(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * 检查库存是否充足
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param date 日期
     * @param requiredRooms 需要的房间数量
     * @return 检查结果
     */
    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkInventoryAvailability(
            @RequestParam Integer hotelId,
            @RequestParam Integer ratePlanId,
            @RequestParam Integer roomTypeId,
            @RequestParam Date date,
            @RequestParam Integer requiredRooms) {
        boolean isAvailable = inventoryService.checkInventoryAvailability(
                hotelId, ratePlanId, roomTypeId, date, requiredRooms);
        return new ResponseEntity<>(isAvailable, HttpStatus.OK);
    }
    
    /**
     * 预留库存
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param date 日期
     * @param reservedRooms 预留的房间数量
     * @return 预留结果
     */
    @PostMapping("/reserve")
    public ResponseEntity<Boolean> reserveInventory(
            @RequestParam Integer hotelId,
            @RequestParam Integer ratePlanId,
            @RequestParam Integer roomTypeId,
            @RequestParam Date date,
            @RequestParam Integer reservedRooms) {
        boolean isReserved = inventoryService.reserveInventory(
                hotelId, ratePlanId, roomTypeId, date, reservedRooms);
        return new ResponseEntity<>(isReserved, HttpStatus.OK);
    }
    
    /**
     * 释放预留库存
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param date 日期
     * @param releasedRooms 释放的房间数量
     * @return 释放结果
     */
    @PostMapping("/release")
    public ResponseEntity<Boolean> releaseInventory(
            @RequestParam Integer hotelId,
            @RequestParam Integer ratePlanId,
            @RequestParam Integer roomTypeId,
            @RequestParam Date date,
            @RequestParam Integer releasedRooms) {
        boolean isReleased = inventoryService.releaseInventory(
                hotelId, ratePlanId, roomTypeId, date, releasedRooms);
        return new ResponseEntity<>(isReleased, HttpStatus.OK);
    }
    
    /**
     * 根据渠道ID获取库存列表
     * @param channelId 渠道ID
     * @return 库存列表
     */
    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<Inventory>> getInventoriesByChannelId(@PathVariable Integer channelId) {
        List<Inventory> inventories = inventoryService.getInventoriesByChannelId(channelId);
        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }
    
    /**
     * 根据酒店ID和渠道ID获取库存列表
     * @param hotelId 酒店ID
     * @param channelId 渠道ID
     * @return 库存列表
     */
    @GetMapping("/hotel/{hotelId}/channel/{channelId}")
    public ResponseEntity<List<Inventory>> getInventoriesByHotelIdAndChannelId(
            @PathVariable Integer hotelId,
            @PathVariable Integer channelId) {
        List<Inventory> inventories = inventoryService.getInventoriesByHotelIdAndChannelId(hotelId, channelId);
        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }
    
    /**
     * 根据酒店ID、渠道ID和日期范围获取库存
     * @param hotelId 酒店ID
     * @param channelId 渠道ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 库存列表
     */
    @GetMapping("/channel/date-range")
    public ResponseEntity<List<Inventory>> getInventoriesByHotelIdAndChannelIdAndDateRange(
            @RequestParam Integer hotelId,
            @RequestParam Integer channelId,
            @RequestParam Date startDate,
            @RequestParam Date endDate) {
        List<Inventory> inventories = inventoryService.getInventoriesByHotelIdAndChannelIdAndDateRange(
                hotelId, channelId, startDate, endDate);
        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }
    
    /**
     * 检查渠道库存是否充足
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param channelId 渠道ID
     * @param date 日期
     * @param requiredRooms 需要的房间数量
     * @return 检查结果
     */
    @GetMapping("/channel/check-availability")
    public ResponseEntity<Boolean> checkChannelInventoryAvailability(
            @RequestParam Integer hotelId,
            @RequestParam Integer ratePlanId,
            @RequestParam Integer roomTypeId,
            @RequestParam Integer channelId,
            @RequestParam Date date,
            @RequestParam Integer requiredRooms) {
        boolean isAvailable = inventoryService.checkChannelInventoryAvailability(
                hotelId, ratePlanId, roomTypeId, channelId, date, requiredRooms);
        return new ResponseEntity<>(isAvailable, HttpStatus.OK);
    }
    
    /**
     * 预留渠道库存
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param channelId 渠道ID
     * @param date 日期
     * @param reservedRooms 预留的房间数量
     * @return 预留结果
     */
    @PostMapping("/channel/reserve")
    public ResponseEntity<Boolean> reserveChannelInventory(
            @RequestParam Integer hotelId,
            @RequestParam Integer ratePlanId,
            @RequestParam Integer roomTypeId,
            @RequestParam Integer channelId,
            @RequestParam Date date,
            @RequestParam Integer reservedRooms) {
        boolean isReserved = inventoryService.reserveChannelInventory(
                hotelId, ratePlanId, roomTypeId, channelId, date, reservedRooms);
        return new ResponseEntity<>(isReserved, HttpStatus.OK);
    }
    
    /**
     * 释放渠道预留库存
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param channelId 渠道ID
     * @param date 日期
     * @param releasedRooms 释放的房间数量
     * @return 释放结果
     */
    @PostMapping("/channel/release")
    public ResponseEntity<Boolean> releaseChannelInventory(
            @RequestParam Integer hotelId,
            @RequestParam Integer ratePlanId,
            @RequestParam Integer roomTypeId,
            @RequestParam Integer channelId,
            @RequestParam Date date,
            @RequestParam Integer releasedRooms) {
        boolean isReleased = inventoryService.releaseChannelInventory(
                hotelId, ratePlanId, roomTypeId, channelId, date, releasedRooms);
        return new ResponseEntity<>(isReleased, HttpStatus.OK);
    }
    
    /**
     * 批量更新渠道库存
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param channelId 渠道ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param availableRooms 可用房间数量
     * @return 更新结果
     */
    @PutMapping("/channel/batch-update")
    public ResponseEntity<String> batchUpdateChannelInventory(
            @RequestParam Integer hotelId,
            @RequestParam Integer ratePlanId,
            @RequestParam Integer roomTypeId,
            @RequestParam Integer channelId,
            @RequestParam Date startDate,
            @RequestParam Date endDate,
            @RequestParam Integer availableRooms) {
        int updatedCount = inventoryService.batchUpdateChannelInventory(
                hotelId, ratePlanId, roomTypeId, channelId, startDate, endDate, availableRooms);
        return new ResponseEntity<>("Updated " + updatedCount + " channel inventories", HttpStatus.OK);
    }
}

