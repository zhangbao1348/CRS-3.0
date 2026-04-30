package com.crs.controller;

import com.crs.entity.Overbooking;
import com.crs.entity.OverbookingLog;
import com.crs.repository.OverbookingLogRepository;
import com.crs.repository.OverbookingRepository;
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

@RestController
@RequestMapping("/api/overbooking")
public class OverbookingController {

    @Autowired private OverbookingRepository repository;
    @Autowired private OverbookingLogRepository logRepository;

    private Integer getTenantId() { Integer t = TenantContext.getTenantId(); return t != null ? t : 1; }
    private String decode(String s) { try { return URLDecoder.decode(s, StandardCharsets.UTF_8); } catch (Exception e) { return s; } }

    @GetMapping
    public ResponseEntity<?> query(@RequestParam String hotelCode,
            @RequestParam(defaultValue = "hotel") String dimensionType,
            @RequestParam(defaultValue = "") String dimensionCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return ResponseEntity.ok(Map.of("success", true, "data",
                repository.findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndOverbookDateBetween(
                        getTenantId(), hotelCode, dimensionType, dimensionCode, startDate, endDate)));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Overbooking record,
            @RequestHeader(value = "X-Operator-Name", defaultValue = "system") String rawOp) {
        try {
            String op = decode(rawOp); Integer tenantId = getTenantId();
            record.setTenantId(tenantId);
            if (record.getDimensionCode() == null) record.setDimensionCode("");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Optional<Overbooking> existing = repository.findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndOverbookDate(
                    tenantId, record.getHotelCode(), record.getDimensionType(), record.getDimensionCode(), record.getOverbookDate());

            int oldCount = existing.map(Overbooking::getOverbookCount).orElse(0);
            if (existing.isPresent()) { existing.get().setOverbookCount(record.getOverbookCount()); repository.save(existing.get()); }
            else { repository.save(record); }

            OverbookingLog log = new OverbookingLog();
            log.setTenantId(tenantId); log.setHotelCode(record.getHotelCode());
            log.setDimensionType(record.getDimensionType()); log.setDimensionCode(record.getDimensionCode());
            log.setOperatorName(op); log.setOperationType("single");
            log.setDetail(String.format("{\"date\":\"%s\",\"old\":%d,\"new\":%d}", sdf.format(record.getOverbookDate()), oldCount, record.getOverbookCount()));
            logRepository.save(log);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }

    @PostMapping("/batch")
    @Transactional
    public ResponseEntity<?> batchSave(@RequestBody List<Overbooking> records,
            @RequestHeader(value = "X-Operator-Name", defaultValue = "system") String rawOp) {
        try {
            String op = decode(rawOp); Integer tenantId = getTenantId();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int count = 0; String hotelCode = null, dimType = null, dimCode = "";
            List<String> dateList = new ArrayList<>(); Integer overbookCount = null;

            for (Overbooking record : records) {
                record.setTenantId(tenantId);
                if (record.getDimensionCode() == null) record.setDimensionCode("");
                if (hotelCode == null) hotelCode = record.getHotelCode();
                if (dimType == null) dimType = record.getDimensionType();
                dimCode = record.getDimensionCode();
                if (overbookCount == null) overbookCount = record.getOverbookCount();
                dateList.add(sdf.format(record.getOverbookDate()));

                Optional<Overbooking> existing = repository.findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndOverbookDate(
                        tenantId, record.getHotelCode(), record.getDimensionType(), record.getDimensionCode(), record.getOverbookDate());
                if (existing.isPresent()) { existing.get().setOverbookCount(record.getOverbookCount()); repository.save(existing.get()); }
                else { repository.save(record); }
                count++;
            }

            if (hotelCode != null) {
                OverbookingLog log = new OverbookingLog();
                log.setTenantId(tenantId); log.setHotelCode(hotelCode);
                log.setDimensionType(dimType); log.setDimensionCode(dimCode);
                log.setOperatorName(op); log.setOperationType("batch");
                log.setDetail(String.format("{\"dates\":\"%s ~ %s（共%d天）\",\"count\":%d}",
                        dateList.get(0), dateList.get(dateList.size() - 1), dateList.size(), overbookCount));
                logRepository.save(log);
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "保存成功，共 " + count + " 条"));
        } catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage())); }
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(@RequestParam String hotelCode,
            @RequestParam(defaultValue = "hotel") String dimensionType,
            @RequestParam(defaultValue = "") String dimensionCode) {
        Integer tenantId = getTenantId();
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
