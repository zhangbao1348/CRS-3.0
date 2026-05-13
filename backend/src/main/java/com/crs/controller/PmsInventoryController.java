package com.crs.controller;

import com.crs.entity.PmsInventory;
import com.crs.entity.PmsSyncLog;
import com.crs.repository.PmsInventoryRepository;
import com.crs.repository.PmsSyncLogRepository;
import com.crs.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PmsInventoryController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【PmsInventoryController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/11-库存管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 PmsInventoryController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/pms-inventory")
public class PmsInventoryController {

    @Autowired private PmsInventoryRepository repository;
    @Autowired private PmsSyncLogRepository syncLogRepository;

    private Integer tid() {
        Integer t = TenantContext.getTenantId();
        if (t == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return t;
    }

    /**
     * 查询PMS库存数据
     * 支持按房型筛选，不传roomTypeCode则查所有房型
     */
    @GetMapping
    public ResponseEntity<?> query(
            @RequestParam String hotelCode,
            @RequestParam(required = false) String roomTypeCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        Integer tenantId = tid();
        List<PmsInventory> data;
        if (roomTypeCode != null && !roomTypeCode.isEmpty()) {
            data = repository.findByTenantIdAndHotelCodeAndRoomTypeCodeAndInventoryDateBetween(
                    tenantId, hotelCode, roomTypeCode, startDate, endDate);
        } else {
            data = repository.findByTenantIdAndHotelCodeAndInventoryDateBetween(
                    tenantId, hotelCode, startDate, endDate);
        }
        // 在返回数据中加入计算的soldCount
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Map<String, Object>> result = data.stream().map(item -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", item.getId());
            map.put("roomTypeCode", item.getRoomTypeCode());
            map.put("inventoryDate", sdf.format(item.getInventoryDate()));
            map.put("physicalRooms", item.getPhysicalRooms());
            map.put("availableRooms", item.getAvailableRooms());
            map.put("maintenanceRooms", item.getMaintenanceRooms());
            map.put("overbookCount", item.getOverbookCount());
            map.put("soldCount", item.getSoldCount());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }

    /**
     * 保存/更新PMS库存数据（供PMS同步接口调用）
     */
    @PostMapping
    public ResponseEntity<?> save(@RequestBody PmsInventory record) {
        try {
            Integer tenantId = tid();
            record.setTenantId(tenantId);
            Optional<PmsInventory> existing = repository.findByTenantIdAndHotelCodeAndRoomTypeCodeAndInventoryDate(
                    tenantId, record.getHotelCode(), record.getRoomTypeCode(), record.getInventoryDate());
            if (existing.isPresent()) {
                PmsInventory e = existing.get();
                e.setPhysicalRooms(record.getPhysicalRooms());
                e.setAvailableRooms(record.getAvailableRooms());
                e.setMaintenanceRooms(record.getMaintenanceRooms());
                e.setOverbookCount(record.getOverbookCount());
                repository.save(e);
            } else {
                repository.save(record);
            }
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * 批量保存PMS库存数据（供PMS同步接口调用）
     */
    @PostMapping("/batch")
    @Transactional
    public ResponseEntity<?> batchSave(@RequestBody List<PmsInventory> records) {
        try {
            Integer tenantId = tid();
            int count = 0;
            for (PmsInventory record : records) {
                record.setTenantId(tenantId);
                Optional<PmsInventory> existing = repository.findByTenantIdAndHotelCodeAndRoomTypeCodeAndInventoryDate(
                        tenantId, record.getHotelCode(), record.getRoomTypeCode(), record.getInventoryDate());
                if (existing.isPresent()) {
                    PmsInventory e = existing.get();
                    e.setPhysicalRooms(record.getPhysicalRooms());
                    e.setAvailableRooms(record.getAvailableRooms());
                    e.setMaintenanceRooms(record.getMaintenanceRooms());
                    e.setOverbookCount(record.getOverbookCount());
                    repository.save(e);
                } else {
                    repository.save(record);
                }
                count++;
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "保存成功，共 " + count + " 条"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * 查询PMS同步日志
     */
    @GetMapping("/sync-logs")
    public ResponseEntity<?> getSyncLogs(@RequestParam String hotelCode) {
        return ResponseEntity.ok(Map.of("success", true, "data",
                syncLogRepository.findByTenantIdAndHotelCodeOrderBySyncTimeDesc(tid(), hotelCode)));
    }
}
