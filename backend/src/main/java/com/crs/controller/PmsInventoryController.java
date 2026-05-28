package com.crs.controller;

import com.crs.entity.PmsInventory;
import com.crs.entity.PmsSyncLog;
import com.crs.repository.PmsInventoryRepository;
import com.crs.repository.PmsSyncLogRepository;
import com.crs.util.TenantContext;
import com.crs.service.inventory.InventoryDeductionService;
import com.crs.service.inventory.AvailabilityContext;
import com.crs.service.inventory.AvailabilityResult;
import com.crs.entity.RoomType;
import com.crs.repository.RoomTypeRepository;
import java.time.LocalDate;
import java.time.ZoneId;
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
    @Autowired private InventoryDeductionService inventoryDeductionService;
    @Autowired private RoomTypeRepository roomTypeRepository;
    @Autowired private com.crs.repository.HotelRoomTypeRepository hotelRoomTypeRepository;
    @Autowired private com.crs.repository.RatePlanRepository ratePlanRepository;

    private Integer tid() {
        Integer t = TenantContext.getTenantId();
        if (t == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return t;
    }

    /**
     * 查询PMS库存数据
     * 支持按房型筛选，不传roomTypeCode则查所有房型。如果传入channelCode，则结合渠道及渠道房型配额实时计算最终实际可售房量。
     */
    @GetMapping
    public ResponseEntity<?> query(
            @RequestParam String hotelCode,
            @RequestParam(required = false) String roomTypeCode,
            @RequestParam(required = false) String channelCode,
            @RequestParam(required = false) String rateCode,
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

        // 如果传入了 channelCode，则并行计算每日的渠道实时最终可售数
        Map<String, Map<String, AvailabilityResult.DailyAvailability>> roomTypeDateAvailMap = new HashMap<>();
        // 新增：房型 -> 价格计划 -> 日期 -> 专属算力结果的映射
        Map<String, Map<String, Map<String, AvailabilityResult.DailyAvailability>>> roomTypeRateDateAvailMap = new HashMap<>();

        if (channelCode != null && !channelCode.isEmpty()) {
            try {
                List<com.crs.entity.HotelRoomType> hotelRoomTypes = 
                        hotelRoomTypeRepository.findDistinctByTenantIdAndHotelCodeAndStatus(tenantId, hotelCode, "active");
                List<com.crs.entity.RatePlan> activeRatePlans = 
                        ratePlanRepository.findByTenantIdAndHotelCodeAndStatus(tenantId, hotelCode, "active");
                LocalDate checkInLocal = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate checkOutLocal = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1);

                for (com.crs.entity.HotelRoomType rt : hotelRoomTypes) {
                    AvailabilityContext availCtx = new AvailabilityContext();
                    availCtx.setTenantId(tenantId);
                    availCtx.setHotelCode(hotelCode);
                    availCtx.setRoomTypeCode(rt.getRoomTypeCode());
                    availCtx.setChannelCode(channelCode);
                    availCtx.setCheckInDate(checkInLocal);
                    availCtx.setCheckOutDate(checkOutLocal);
                    availCtx.setRequestedRooms(1);

                    // 融入细粒度配额
                    if (rateCode != null && !rateCode.isEmpty() && !"all".equalsIgnoreCase(rateCode)) {
                        availCtx.setRateCode(rateCode);
                        Optional<com.crs.entity.RatePlan> rpOpt = ratePlanRepository.findByTenantIdAndHotelCodeAndRateCode(tenantId, hotelCode, rateCode);
                        if (rpOpt.isPresent()) {
                            com.crs.entity.RatePlan rp = rpOpt.get();
                            availCtx.setRateCategoryCode(rp.getRateCategory());
                            availCtx.setMarketCode(rp.getMarketCode());
                        }
                    } else {
                        availCtx.setRateCode("");
                    }

                    List<AvailabilityResult.DailyAvailability> dailyDetails = 
                            inventoryDeductionService.checkDailyRangeAvailability(availCtx);
                    Map<String, AvailabilityResult.DailyAvailability> dateAvail = new HashMap<>();
                    if (dailyDetails != null) {
                        for (AvailabilityResult.DailyAvailability daily : dailyDetails) {
                            String dStr = daily.getDate().toString();
                            dateAvail.put(dStr, daily);
                        }
                    }
                    roomTypeDateAvailMap.put(rt.getRoomTypeCode(), dateAvail);

                    // 如果是“全部价格计划”或未指定，则为该房型计算每一个激活价格计划的专属产品可用库存
                    if (rateCode == null || rateCode.isEmpty() || "all".equalsIgnoreCase(rateCode)) {
                        Map<String, Map<String, AvailabilityResult.DailyAvailability>> rateDateAvail = new HashMap<>();
                        for (com.crs.entity.RatePlan rp : activeRatePlans) {
                            AvailabilityContext rpCtx = new AvailabilityContext();
                            rpCtx.setTenantId(tenantId);
                            rpCtx.setHotelCode(hotelCode);
                            rpCtx.setRoomTypeCode(rt.getRoomTypeCode());
                            rpCtx.setChannelCode(channelCode);
                            rpCtx.setRateCode(rp.getRateCode());
                            rpCtx.setRateCategoryCode(rp.getRateCategory());
                            rpCtx.setMarketCode(rp.getMarketCode());
                            rpCtx.setCheckInDate(checkInLocal);
                            rpCtx.setCheckOutDate(checkOutLocal);
                            rpCtx.setRequestedRooms(1);

                            List<AvailabilityResult.DailyAvailability> rpDailyDetails = 
                                    inventoryDeductionService.checkDailyRangeAvailability(rpCtx);
                            Map<String, AvailabilityResult.DailyAvailability> rpDateAvail = new HashMap<>();
                            if (rpDailyDetails != null) {
                                for (AvailabilityResult.DailyAvailability daily : rpDailyDetails) {
                                    rpDateAvail.put(daily.getDate().toString(), daily);
                                }
                            }
                            rateDateAvail.put(rp.getRateCode(), rpDateAvail);
                        }
                        roomTypeRateDateAvailMap.put(rt.getRoomTypeCode(), rateDateAvail);
                    }
                }
            } catch (Exception e) {
                System.err.println("[PmsInventoryController] 渠道及产品实时可订计算失败: " + e.getMessage());
            }
        }

        // 在返回数据中加入计算的soldCount
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Map<String, Object>> result = data.stream().map(item -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", item.getId());
            map.put("roomTypeCode", item.getRoomTypeCode());
            String dateStr = sdf.format(item.getInventoryDate());
            map.put("inventoryDate", dateStr);
            map.put("physicalRooms", item.getPhysicalRooms());

            // 覆盖物理库存：如果有该日期的选定渠道的可用数，直接覆盖
            int finalAvail = item.getAvailableRooms();
            AvailabilityResult.DailyAvailability dailyAvail = null;
            if (channelCode != null && !channelCode.isEmpty()) {
                Map<String, AvailabilityResult.DailyAvailability> dateAvail = roomTypeDateAvailMap.get(item.getRoomTypeCode());
                if (dateAvail != null && dateAvail.containsKey(dateStr)) {
                    dailyAvail = dateAvail.get(dateStr);
                    if (dailyAvail != null && dailyAvail.getMinAvailable() != null) {
                        finalAvail = dailyAvail.getMinAvailable();
                    }
                }
            }
            map.put("availableRooms", finalAvail);
            map.put("availabilityDetail", dailyAvail);

            // 构建并注入每个激活价格计划的专属可用剩余数 productAvailability Map
            Map<String, Map<String, Object>> productAvailability = new LinkedHashMap<>();
            if (channelCode != null && !channelCode.isEmpty() && (rateCode == null || rateCode.isEmpty() || "all".equalsIgnoreCase(rateCode))) {
                Map<String, Map<String, AvailabilityResult.DailyAvailability>> rateDateAvail = roomTypeRateDateAvailMap.get(item.getRoomTypeCode());
                if (rateDateAvail != null) {
                    for (Map.Entry<String, Map<String, AvailabilityResult.DailyAvailability>> entry : rateDateAvail.entrySet()) {
                        String rCode = entry.getKey();
                        Map<String, AvailabilityResult.DailyAvailability> rpDateAvail = entry.getValue();
                        if (rpDateAvail != null && rpDateAvail.containsKey(dateStr)) {
                            AvailabilityResult.DailyAvailability rpDaily = rpDateAvail.get(dateStr);
                            if (rpDaily != null) {
                                Map<String, Object> inner = new LinkedHashMap<>();
                                inner.put("availableRooms", rpDaily.getMinAvailable() != null ? rpDaily.getMinAvailable() : 0);
                                inner.put("availabilityDetail", rpDaily);
                                productAvailability.put(rCode, inner);
                            }
                        }
                    }
                }
            }
            map.put("productAvailability", productAvailability);

            map.put("maintenanceRooms", item.getMaintenanceRooms());
            map.put("overbookCount", item.getOverbookCount());
            map.put("soldCount", item.getSoldCount());
            return map;
        }).collect(Collectors.toList());

        // 补充：有渠道算力结果但 pms_inventory 表中无实体记录的日期，也需返回给前端展示算力推导
        if (channelCode != null && !channelCode.isEmpty() && !roomTypeDateAvailMap.isEmpty()) {
            // 统计 data 中已经覆盖了的 roomTypeCode+date 集合
            Set<String> coveredKeys = result.stream()
                    .map(m -> m.get("roomTypeCode") + "_" + m.get("inventoryDate"))
                    .collect(Collectors.toSet());

            for (Map.Entry<String, Map<String, AvailabilityResult.DailyAvailability>> rtEntry : roomTypeDateAvailMap.entrySet()) {
                String rtCode = rtEntry.getKey();
                // 如果有房型过滤，只补充匹配的房型
                if (roomTypeCode != null && !roomTypeCode.isEmpty() && !roomTypeCode.equals(rtCode)) {
                    continue;
                }
                for (Map.Entry<String, AvailabilityResult.DailyAvailability> dateEntry : rtEntry.getValue().entrySet()) {
                    String dateStr = dateEntry.getKey();
                    AvailabilityResult.DailyAvailability dailyAvail = dateEntry.getValue();
                    if (coveredKeys.contains(rtCode + "_" + dateStr)) {
                        continue; // 已有 PMS 实体记录，跳过
                    }
                    // 构造虚拟行（pmsAvailable 来自算力模型，物理库存字段置空）
                    Map<String, Object> virtualRow = new LinkedHashMap<>();
                    virtualRow.put("id", null);
                    virtualRow.put("roomTypeCode", rtCode);
                    virtualRow.put("inventoryDate", dateStr);
                    virtualRow.put("physicalRooms", dailyAvail.getPmsAvailable()); // 用算力中的pmsAvailable估算
                    int finalAvail = dailyAvail.getMinAvailable() != null ? dailyAvail.getMinAvailable() : 0;
                    virtualRow.put("availableRooms", finalAvail);
                    virtualRow.put("availabilityDetail", dailyAvail);

                    // 同样为虚拟行注入 productAvailability Map
                    Map<String, Map<String, Object>> virtProductAvail = new LinkedHashMap<>();
                    Map<String, Map<String, AvailabilityResult.DailyAvailability>> rateDateAvail = roomTypeRateDateAvailMap.get(rtCode);
                    if (rateDateAvail != null) {
                        for (Map.Entry<String, Map<String, AvailabilityResult.DailyAvailability>> entry : rateDateAvail.entrySet()) {
                            String rCode = entry.getKey();
                            Map<String, AvailabilityResult.DailyAvailability> rpDateAvail = entry.getValue();
                            if (rpDateAvail != null && rpDateAvail.containsKey(dateStr)) {
                                AvailabilityResult.DailyAvailability rpDaily = rpDateAvail.get(dateStr);
                                if (rpDaily != null) {
                                    Map<String, Object> inner = new LinkedHashMap<>();
                                    inner.put("availableRooms", rpDaily.getMinAvailable() != null ? rpDaily.getMinAvailable() : 0);
                                    inner.put("availabilityDetail", rpDaily);
                                    virtProductAvail.put(rCode, inner);
                                }
                            }
                        }
                    }
                    virtualRow.put("productAvailability", virtProductAvail);

                    virtualRow.put("maintenanceRooms", 0);
                    virtualRow.put("overbookCount", 0);
                    virtualRow.put("soldCount", 0);
                    result.add(virtualRow);
                }
            }
        }

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
    }}
