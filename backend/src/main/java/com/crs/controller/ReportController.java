package com.crs.controller;

import com.crs.service.ReportService;
import com.crs.modules.report.domain.ReportQueryPolicy;
import com.crs.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表数据接口控制器 (ReportController)
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    /**
     * 查询订单多维聚合报表 (支持同环比、任意维度组合)
     */
    @GetMapping("/reservation")
    public ResponseEntity<?> queryReservationReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String hotelCode,
            @RequestParam(required = false) String channelCode,
            @RequestParam(required = false) String marketCode,
            @RequestParam(required = false) String rateCategoryCode,
            @RequestParam(required = false) String ratePlanCode,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) String groupBy1,
            @RequestParam(required = false) String groupBy2,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) Boolean memberBooking,
            @RequestParam(required = false) Boolean canEarnPoints,
            @RequestParam(required = false) Boolean onlineBooking,
            @RequestParam(defaultValue = "false") Boolean enableCompare,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate compareStartDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate compareEndDate) {

        try {
            Integer tenantId = getCurrentTenantId();
            ReportQueryPolicy.validateRange(startDate, endDate, "本期");
            if (Boolean.TRUE.equals(enableCompare)) {
                ReportQueryPolicy.validateRange(compareStartDate, compareEndDate, "对比期");
            }
            
            // 前后端订单状态映射转换
            String mappedStatus = null;
            if (orderStatus != null && !orderStatus.trim().isEmpty()) {
                switch (orderStatus) {
                    case "confirmed":
                        mappedStatus = "confirmed";
                        break;
                    case "canceled":
                        mappedStatus = "cancelled";
                        break;
                    case "checkIn":
                        mappedStatus = "checked_in";
                        break;
                    case "checkOut":
                        mappedStatus = "completed";
                        break;
                    default:
                        mappedStatus = orderStatus;
                }
            }

            Map<String, Object> data = reportService.queryReservationReport(
                    tenantId, startDate, endDate, hotelCode, channelCode, marketCode,
                    rateCategoryCode, ratePlanCode, mappedStatus, groupBy1, groupBy2, paymentMethod,
                    memberBooking, canEarnPoints, onlineBooking,
                    enableCompare, compareStartDate, compareEndDate
            );
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 手动触发指定时间段内汇总数据的同步与初始化
     */
    @PostMapping("/reservation/initialize")
    @PreAuthorize("hasAnyRole('super_admin', 'group_admin')")
    public ResponseEntity<?> initializeSummaryData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Integer tenantId = getCurrentTenantId();
            ReportQueryPolicy.validateRange(startDate, endDate, "汇总同步");
            reportService.initializeSummaryData(tenantId, startDate, endDate);
            return ResponseEntity.ok(Map.of("success", true, "message", "Summary aggregation initialized successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * 查询出租率报表 (按天呈现, 支持按酒店/按房型维度)
     */
    @GetMapping("/occupancy")
    public ResponseEntity<?> queryOccupancyReport(
            @RequestParam(required = false) String hotelCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month,
            @RequestParam(defaultValue = "按酒店纬度") String statisticMethod) {
        try {
            Integer tenantId = getCurrentTenantId();
            List<Map<String, Object>> data = reportService.queryOccupancyReport(tenantId, hotelCode, month, statisticMethod);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * 查询营收分析报表 (按天呈现, 支持按酒店/按房型维度)
     */
    @GetMapping("/revenue")
    public ResponseEntity<?> queryRevenueReport(
            @RequestParam(required = false) String hotelCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month,
            @RequestParam(defaultValue = "按酒店纬度") String statisticMethod) {
        try {
            Integer tenantId = getCurrentTenantId();
            List<Map<String, Object>> data = reportService.queryRevenueReport(tenantId, hotelCode, month, statisticMethod);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
