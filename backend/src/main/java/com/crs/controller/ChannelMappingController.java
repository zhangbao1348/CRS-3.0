package com.crs.controller;

import com.crs.entity.ChannelHotelMapping;
import com.crs.entity.ChannelRoomTypeMapping;
import com.crs.entity.ChannelRateCodeMapping;
import com.crs.service.ChannelMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 渠道映射控制器
 * 提供渠道酒店/房型/房价映射管理的RESTful API接口
 */
@RestController
@RequestMapping("/api/channel-mappings")
public class ChannelMappingController {
    
    @Autowired
    private ChannelMappingService channelMappingService;
    
    // ===== 酒店映射 =====
    
    /**
     * 获取酒店映射列表
     * @param channelId 渠道ID（可选）
     * @param hotelId 酒店ID（可选）
     * @return 酒店映射列表
     */
    @GetMapping("/hotels")
    public ResponseEntity<List<ChannelHotelMapping>> getHotelMappings(
            @RequestParam(required = false) Integer channelId,
            @RequestParam(required = false) Integer hotelId) {
        List<ChannelHotelMapping> mappings = channelMappingService.getHotelMappings(channelId, hotelId);
        return ResponseEntity.ok(mappings);
    }
    
    /**
     * 创建酒店映射
     * @param mapping 酒店映射
     * @return 创建的酒店映射
     */
    @PostMapping("/hotels")
    public ResponseEntity<?> createHotelMapping(@RequestBody ChannelHotelMapping mapping) {
        try {
            ChannelHotelMapping created = channelMappingService.createHotelMapping(mapping);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 更新酒店映射
     * @param id 映射ID
     * @param mapping 酒店映射
     * @return 更新后的酒店映射
     */
    @PutMapping("/hotels/{id}")
    public ResponseEntity<?> updateHotelMapping(@PathVariable Integer id, @RequestBody ChannelHotelMapping mapping) {
        try {
            ChannelHotelMapping updated = channelMappingService.updateHotelMapping(id, mapping);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 删除酒店映射
     * @param id 映射ID
     * @return 删除结果
     */
    @DeleteMapping("/hotels/{id}")
    public ResponseEntity<?> deleteHotelMapping(@PathVariable Integer id) {
        try {
            channelMappingService.deleteHotelMapping(id);
            return ResponseEntity.ok("酒店映射删除成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 切换酒店映射状态
     * @param id 映射ID
     * @return 更新后的酒店映射
     */
    @PutMapping("/hotels/{id}/toggle-status")
    public ResponseEntity<?> toggleHotelMappingStatus(@PathVariable Integer id) {
        try {
            ChannelHotelMapping updated = channelMappingService.toggleHotelMappingStatus(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // ===== 房型映射 =====
    
    /**
     * 获取房型映射列表
     * @param channelId 渠道ID（可选）
     * @param hotelId 酒店ID（可选）
     * @return 房型映射列表
     */
    @GetMapping("/room-types")
    public ResponseEntity<List<ChannelRoomTypeMapping>> getRoomTypeMappings(
            @RequestParam(required = false) Integer channelId,
            @RequestParam(required = false) Integer hotelId) {
        List<ChannelRoomTypeMapping> mappings = channelMappingService.getRoomTypeMappings(channelId, hotelId);
        return ResponseEntity.ok(mappings);
    }
    
    /**
     * 创建房型映射
     * @param mapping 房型映射
     * @return 创建的房型映射
     */
    @PostMapping("/room-types")
    public ResponseEntity<?> createRoomTypeMapping(@RequestBody ChannelRoomTypeMapping mapping) {
        try {
            ChannelRoomTypeMapping created = channelMappingService.createRoomTypeMapping(mapping);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 更新房型映射
     * @param id 映射ID
     * @param mapping 房型映射
     * @return 更新后的房型映射
     */
    @PutMapping("/room-types/{id}")
    public ResponseEntity<?> updateRoomTypeMapping(@PathVariable Integer id, @RequestBody ChannelRoomTypeMapping mapping) {
        try {
            ChannelRoomTypeMapping updated = channelMappingService.updateRoomTypeMapping(id, mapping);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 删除房型映射
     * @param id 映射ID
     * @return 删除结果
     */
    @DeleteMapping("/room-types/{id}")
    public ResponseEntity<?> deleteRoomTypeMapping(@PathVariable Integer id) {
        try {
            channelMappingService.deleteRoomTypeMapping(id);
            return ResponseEntity.ok("房型映射删除成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 切换房型映射状态
     * @param id 映射ID
     * @return 更新后的房型映射
     */
    @PutMapping("/room-types/{id}/toggle-status")
    public ResponseEntity<?> toggleRoomTypeMappingStatus(@PathVariable Integer id) {
        try {
            ChannelRoomTypeMapping updated = channelMappingService.toggleRoomTypeMappingStatus(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // ===== 房价映射 =====
    
    /**
     * 获取房价映射列表
     * @param channelId 渠道ID（可选）
     * @param hotelId 酒店ID（可选）
     * @return 房价映射列表
     */
    @GetMapping("/rate-codes")
    public ResponseEntity<List<ChannelRateCodeMapping>> getRateCodeMappings(
            @RequestParam(required = false) Integer channelId,
            @RequestParam(required = false) Integer hotelId) {
        List<ChannelRateCodeMapping> mappings = channelMappingService.getRateCodeMappings(channelId, hotelId);
        return ResponseEntity.ok(mappings);
    }
    
    /**
     * 创建房价映射
     * @param mapping 房价映射
     * @return 创建的房价映射
     */
    @PostMapping("/rate-codes")
    public ResponseEntity<?> createRateCodeMapping(@RequestBody ChannelRateCodeMapping mapping) {
        try {
            ChannelRateCodeMapping created = channelMappingService.createRateCodeMapping(mapping);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 更新房价映射
     * @param id 映射ID
     * @param mapping 房价映射
     * @return 更新后的房价映射
     */
    @PutMapping("/rate-codes/{id}")
    public ResponseEntity<?> updateRateCodeMapping(@PathVariable Integer id, @RequestBody ChannelRateCodeMapping mapping) {
        try {
            ChannelRateCodeMapping updated = channelMappingService.updateRateCodeMapping(id, mapping);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 删除房价映射
     * @param id 映射ID
     * @return 删除结果
     */
    @DeleteMapping("/rate-codes/{id}")
    public ResponseEntity<?> deleteRateCodeMapping(@PathVariable Integer id) {
        try {
            channelMappingService.deleteRateCodeMapping(id);
            return ResponseEntity.ok("房价映射删除成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 切换房价映射状态
     * @param id 映射ID
     * @return 更新后的房价映射
     */
    @PutMapping("/rate-codes/{id}/toggle-status")
    public ResponseEntity<?> toggleRateCodeMappingStatus(@PathVariable Integer id) {
        try {
            ChannelRateCodeMapping updated = channelMappingService.toggleRateCodeMappingStatus(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
