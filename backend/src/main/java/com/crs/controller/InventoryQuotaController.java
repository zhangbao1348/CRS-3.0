package com.crs.controller;

import com.crs.entity.InventoryQuota;
import com.crs.entity.InventoryQuotaLog;
import com.crs.repository.InventoryQuotaLogRepository;
import com.crs.repository.InventoryQuotaRepository;
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
 * InventoryQuotaController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【InventoryQuotaController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/11-库存管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 InventoryQuotaController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/inventory-quota")
public class InventoryQuotaController {

    @Autowired private InventoryQuotaRepository repository;
    @Autowired private InventoryQuotaLogRepository logRepository;

    private Integer tid() { 
        Integer t = TenantContext.getTenantId(); 
        if (t == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return t; 
    }
    private String dec(String s) { try { return URLDecoder.decode(s, StandardCharsets.UTF_8); } catch (Exception e) { return s; } }

    @GetMapping
    public ResponseEntity<?> query(@RequestParam String hotelCode, @RequestParam String dimensionType,
            @RequestParam(defaultValue = "") String dimensionCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return ResponseEntity.ok(Map.of("success", true, "data",
                repository.findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndQuotaDateBetween(
                        tid(), hotelCode, dimensionType, dimensionCode, startDate, endDate)));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody InventoryQuota record,
            @RequestHeader(value = "X-Operator-Name", defaultValue = "system") String rawOp) {
        try {
            String op = dec(rawOp); Integer tenantId = tid();
            record.setTenantId(tenantId);
            if (record.getDimensionCode() == null) record.setDimensionCode("");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Optional<InventoryQuota> existing = repository.findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndQuotaDate(
                    tenantId, record.getHotelCode(), record.getDimensionType(), record.getDimensionCode(), record.getQuotaDate());

            Integer oldLimit = existing.map(InventoryQuota::getQuotaLimit).orElse(null);
            if (existing.isPresent()) {
                existing.get().setQuotaLimit(record.getQuotaLimit());
                repository.save(existing.get());
            } else {
                repository.save(record);
            }

            InventoryQuotaLog log = new InventoryQuotaLog();
            log.setTenantId(tenantId); log.setHotelCode(record.getHotelCode());
            log.setDimensionType(record.getDimensionType()); log.setDimensionCode(record.getDimensionCode());
            log.setOperatorName(op); log.setOperationType("single");
            log.setDetail(String.format("{\"date\":\"%s\",\"old\":%s,\"new\":%s}",
                    sdf.format(record.getQuotaDate()),
                    oldLimit != null ? oldLimit.toString() : "\"未设置\"",
                    record.getQuotaLimit() != null ? record.getQuotaLimit().toString() : "\"未设置\""));
            logRepository.save(log);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }

    @PostMapping("/batch")
    @Transactional
    public ResponseEntity<?> batchSave(@RequestBody List<InventoryQuota> records,
            @RequestHeader(value = "X-Operator-Name", defaultValue = "system") String rawOp) {
        try {
            String op = dec(rawOp); Integer tenantId = tid();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int count = 0; String hotelCode = null, dimType = null, dimCode = "";
            List<String> dateList = new ArrayList<>(); Integer quotaLimit = null;

            for (InventoryQuota record : records) {
                record.setTenantId(tenantId);
                if (record.getDimensionCode() == null) record.setDimensionCode("");
                if (hotelCode == null) hotelCode = record.getHotelCode();
                if (dimType == null) dimType = record.getDimensionType();
                dimCode = record.getDimensionCode();
                if (quotaLimit == null) quotaLimit = record.getQuotaLimit();
                dateList.add(sdf.format(record.getQuotaDate()));

                Optional<InventoryQuota> existing = repository.findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndQuotaDate(
                        tenantId, record.getHotelCode(), record.getDimensionType(), record.getDimensionCode(), record.getQuotaDate());
                if (existing.isPresent()) { existing.get().setQuotaLimit(record.getQuotaLimit()); repository.save(existing.get()); }
                else { repository.save(record); }
                count++;
            }

            if (hotelCode != null) {
                InventoryQuotaLog log = new InventoryQuotaLog();
                log.setTenantId(tenantId); log.setHotelCode(hotelCode);
                log.setDimensionType(dimType); log.setDimensionCode(dimCode);
                log.setOperatorName(op); log.setOperationType("batch");
                log.setDetail(String.format("{\"dates\":\"%s ~ %s（共%d天）\",\"quotaLimit\":%s}",
                        dateList.get(0), dateList.get(dateList.size() - 1), dateList.size(),
                        quotaLimit != null ? quotaLimit.toString() : "\"未设置\""));
                logRepository.save(log);
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "保存成功，共 " + count + " 条"));
        } catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(@RequestParam String hotelCode, @RequestParam String dimensionType,
            @RequestParam(defaultValue = "") String dimensionCode) {
        Integer tenantId = tid();
        if (dimensionCode == null || dimensionCode.isEmpty()) {
            return ResponseEntity.ok(Map.of("success", true, "data",
                    logRepository.findByTenantIdAndHotelCodeAndDimensionTypeOrderByOperationTimeDesc(
                            tenantId, hotelCode, dimensionType)));
        }
        return ResponseEntity.ok(Map.of("success", true, "data",
                logRepository.findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeOrderByOperationTimeDesc(
                        tenantId, hotelCode, dimensionType, dimensionCode)));
    }
}
