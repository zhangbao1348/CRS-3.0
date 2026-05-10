package com.crs.controller;

import com.crs.entity.BookingControl;
import com.crs.entity.BookingControlLog;
import com.crs.repository.BookingControlLogRepository;
import com.crs.repository.BookingControlRepository;
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
 * BookingControlController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【BookingControlController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/11-库存管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 BookingControlController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/booking-controls")
public class BookingControlController {

    @Autowired
    private BookingControlRepository repository;

    @Autowired
    private BookingControlLogRepository logRepository;

    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        return tenantId != null ? tenantId : 1;
    }

    private String decodeOperator(String encoded) {
        try { return URLDecoder.decode(encoded, StandardCharsets.UTF_8); }
        catch (Exception e) { return encoded; }
    }

    @GetMapping
    public ResponseEntity<?> query(
            @RequestParam String hotelCode,
            @RequestParam String dimensionType,
            @RequestParam(defaultValue = "") String dimensionCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        Integer tenantId = getCurrentTenantId();
        List<BookingControl> list = repository
                .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndControlDateBetween(
                        tenantId, hotelCode, dimensionType, dimensionCode, startDate, endDate);
        return ResponseEntity.ok(Map.of("success", true, "data", list));
    }

    @PostMapping
    public ResponseEntity<?> save(
            @RequestBody BookingControl control,
            @RequestHeader(value = "X-Operator-Name", defaultValue = "system") String rawOperator) {
        try {
            String operator = decodeOperator(rawOperator);
            Integer tenantId = getCurrentTenantId();
            control.setTenantId(tenantId);
            if (control.getDimensionCode() == null) control.setDimensionCode("");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Optional<BookingControl> existing = repository
                    .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndControlDate(
                            tenantId, control.getHotelCode(), control.getDimensionType(),
                            control.getDimensionCode(), control.getControlDate());

            String detail;
            if (existing.isPresent()) {
                BookingControl old = existing.get();
                // 记录变更明细（旧值 → 新值）
                detail = String.format(
                    "{\"date\":\"%s\",\"changes\":{\"cancellationPolicyCode\":\"%s→%s\",\"advanceBookingDays\":\"%d→%d\",\"minStay\":\"%d→%d\",\"maxStay\":\"%d→%d\"}}",
                    sdf.format(control.getControlDate()),
                    old.getCancellationPolicyCode(), control.getCancellationPolicyCode(),
                    old.getAdvanceBookingDays(), control.getAdvanceBookingDays(),
                    old.getMinStay(), control.getMinStay(),
                    old.getMaxStay(), control.getMaxStay());
                old.setCancellationPolicyCode(control.getCancellationPolicyCode());
                old.setAdvanceBookingDays(control.getAdvanceBookingDays());
                old.setMinStay(control.getMinStay());
                old.setMaxStay(control.getMaxStay());
                repository.save(old);
            } else {
                detail = String.format(
                    "{\"date\":\"%s\",\"changes\":{\"cancellationPolicyCode\":\"→%s\",\"advanceBookingDays\":\"→%d\",\"minStay\":\"→%d\",\"maxStay\":\"→%d\"}}",
                    sdf.format(control.getControlDate()),
                    control.getCancellationPolicyCode(), control.getAdvanceBookingDays(),
                    control.getMinStay(), control.getMaxStay());
                repository.save(control);
            }

            // 写日志
            BookingControlLog log = new BookingControlLog();
            log.setTenantId(tenantId);
            log.setHotelCode(control.getHotelCode());
            log.setDimensionType(control.getDimensionType());
            log.setDimensionCode(control.getDimensionCode());
            log.setOperatorName(operator);
            log.setOperationType("single");
            log.setDetail(detail);
            logRepository.save(log);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/batch")
    @Transactional
    public ResponseEntity<?> batchSave(
            @RequestBody List<BookingControl> controls,
            @RequestHeader(value = "X-Operator-Name", defaultValue = "system") String rawOperator) {
        try {
            String operator = decodeOperator(rawOperator);
            Integer tenantId = getCurrentTenantId();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int count = 0;
            String hotelCode = null, dimType = null, dimCode = "";
            List<String> dateList = new ArrayList<>();

            for (BookingControl control : controls) {
                control.setTenantId(tenantId);
                if (control.getDimensionCode() == null) control.setDimensionCode("");
                if (hotelCode == null) hotelCode = control.getHotelCode();
                if (dimType == null) dimType = control.getDimensionType();
                dimCode = control.getDimensionCode();
                dateList.add(sdf.format(control.getControlDate()));

                Optional<BookingControl> existing = repository
                        .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndControlDate(
                                tenantId, control.getHotelCode(), control.getDimensionType(),
                                control.getDimensionCode(), control.getControlDate());

                if (existing.isPresent()) {
                    BookingControl e = existing.get();
                    e.setCancellationPolicyCode(control.getCancellationPolicyCode());
                    e.setAdvanceBookingDays(control.getAdvanceBookingDays());
                    e.setMinStay(control.getMinStay());
                    e.setMaxStay(control.getMaxStay());
                    repository.save(e);
                } else {
                    repository.save(control);
                }
                count++;
            }

            // 写批量日志
            if (hotelCode != null && !controls.isEmpty()) {
                BookingControl first = controls.get(0);
                String detail = String.format(
                    "{\"dates\":\"%s ~ %s（共%d天）\",\"values\":{\"cancellationPolicyCode\":\"%s\",\"advanceBookingDays\":%d,\"minStay\":%d,\"maxStay\":%d}}",
                    dateList.get(0), dateList.get(dateList.size() - 1), dateList.size(),
                    first.getCancellationPolicyCode(), first.getAdvanceBookingDays(),
                    first.getMinStay(), first.getMaxStay());

                BookingControlLog log = new BookingControlLog();
                log.setTenantId(tenantId);
                log.setHotelCode(hotelCode);
                log.setDimensionType(dimType);
                log.setDimensionCode(dimCode);
                log.setOperatorName(operator);
                log.setOperationType("batch");
                log.setDetail(detail);
                logRepository.save(log);
            }

            return ResponseEntity.ok(Map.of("success", true, "message", "保存成功，共 " + count + " 条"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(
            @RequestParam String hotelCode,
            @RequestParam String dimensionType,
            @RequestParam(defaultValue = "") String dimensionCode) {
        Integer tenantId = getCurrentTenantId();
        List<BookingControlLog> logs = logRepository
                .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeOrderByOperationTimeDesc(
                        tenantId, hotelCode, dimensionType, dimensionCode);
        return ResponseEntity.ok(Map.of("success", true, "data", logs));
    }
}
