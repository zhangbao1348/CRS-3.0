package com.crs.controller;

import com.crs.entity.RoomStatusLog;
import com.crs.entity.RoomStatusRecord;
import com.crs.repository.RoomStatusLogRepository;
import com.crs.repository.RoomStatusRepository;
import com.crs.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * RoomStatusController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【RoomStatusController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 RoomStatusController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/room-status")
public class RoomStatusController {

    @Autowired
    private RoomStatusRepository repository;
    @Autowired
    private RoomStatusLogRepository logRepository;

    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    private String decode(String s) {
        try { return URLDecoder.decode(s, StandardCharsets.UTF_8); } catch (Exception e) { return s; }
    }

    @GetMapping
    public ResponseEntity<?> query(
            @RequestParam String hotelCode, @RequestParam String dimensionType,
            @RequestParam(defaultValue = "") String dimensionCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<RoomStatusRecord> list = repository
                .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndStatusDateBetween(
                        getCurrentTenantId(), hotelCode, dimensionType, dimensionCode, startDate, endDate);
        return ResponseEntity.ok(Map.of("success", true, "data", list));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody RoomStatusRecord record,
            @RequestHeader(value = "X-Operator-Name", defaultValue = "system") String rawOp) {
        try {
            String operator = decode(rawOp);
            Integer tenantId = getCurrentTenantId();
            record.setTenantId(tenantId);
            if (record.getDimensionCode() == null) record.setDimensionCode("");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Optional<RoomStatusRecord> existing = repository
                    .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndStatusDate(
                            tenantId, record.getHotelCode(), record.getDimensionType(),
                            record.getDimensionCode(), record.getStatusDate());

            String oldVal = existing.isPresent() ? (existing.get().getIsOpen() ? "开" : "关") : "未设置";
            String newVal = record.getIsOpen() ? "开" : "关";

            if (existing.isPresent()) {
                existing.get().setIsOpen(record.getIsOpen());
                repository.save(existing.get());
            } else {
                repository.save(record);
            }

            // 日志
            RoomStatusLog log = new RoomStatusLog();
            log.setTenantId(tenantId);
            log.setHotelCode(record.getHotelCode());
            log.setDimensionType(record.getDimensionType());
            log.setDimensionCode(record.getDimensionCode());
            log.setOperatorName(operator);
            log.setOperationType("single");
            log.setDetail(String.format("{\"date\":\"%s\",\"old\":\"%s\",\"new\":\"%s\"}",
                    sdf.format(record.getStatusDate()), oldVal, newVal));
            logRepository.save(log);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/batch")
    @Transactional
    public ResponseEntity<?> batchSave(@RequestBody List<RoomStatusRecord> records,
            @RequestHeader(value = "X-Operator-Name", defaultValue = "system") String rawOp) {
        try {
            String operator = decode(rawOp);
            Integer tenantId = getCurrentTenantId();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int count = 0;
            String hotelCode = null, dimType = null, dimCode = "";
            List<String> dateList = new ArrayList<>();
            Boolean isOpen = null;

            for (RoomStatusRecord record : records) {
                record.setTenantId(tenantId);
                if (record.getDimensionCode() == null) record.setDimensionCode("");
                if (hotelCode == null) hotelCode = record.getHotelCode();
                if (dimType == null) dimType = record.getDimensionType();
                dimCode = record.getDimensionCode();
                if (isOpen == null) isOpen = record.getIsOpen();
                dateList.add(sdf.format(record.getStatusDate()));

                Optional<RoomStatusRecord> existing = repository
                        .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndStatusDate(
                                tenantId, record.getHotelCode(), record.getDimensionType(),
                                record.getDimensionCode(), record.getStatusDate());
                if (existing.isPresent()) {
                    existing.get().setIsOpen(record.getIsOpen());
                    repository.save(existing.get());
                } else {
                    repository.save(record);
                }
                count++;
            }

            // 日志
            if (hotelCode != null) {
                RoomStatusLog log = new RoomStatusLog();
                log.setTenantId(tenantId);
                log.setHotelCode(hotelCode);
                log.setDimensionType(dimType);
                log.setDimensionCode(dimCode);
                log.setOperatorName(operator);
                log.setOperationType("batch");
                log.setDetail(String.format("{\"dates\":\"%s ~ %s（共%d天）\",\"action\":\"%s\"}",
                        dateList.get(0), dateList.get(dateList.size() - 1), dateList.size(),
                        Boolean.TRUE.equals(isOpen) ? "开房" : "关房"));
                logRepository.save(log);
            }

            return ResponseEntity.ok(Map.of("success", true, "message", "保存成功，共 " + count + " 条"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(@RequestParam String hotelCode, @RequestParam String dimensionType,
            @RequestParam(defaultValue = "") String dimensionCode) {
        List<RoomStatusLog> logs = logRepository
                .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeOrderByOperationTimeDesc(
                        getCurrentTenantId(), hotelCode, dimensionType, dimensionCode);
        return ResponseEntity.ok(Map.of("success", true, "data", logs));
    }
}
