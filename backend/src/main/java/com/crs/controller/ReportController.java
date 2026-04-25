package com.crs.controller;

import com.crs.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 报表控制器
 * 提供预订报表、入住率报表、收入报表的RESTful API接口
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    
    @Autowired
    private ReportService reportService;
    
    /**
     * 预订报表查询
     * @param params 查询参数（hotel, bookingDateStart, bookingDateEnd, orderStatus, marketCode, channelCode, ratePlan, groupBy1, groupBy2, paymentMethod）
     * @return 报表数据列表
     */
    @PostMapping("/reservation")
    public ResponseEntity<List<Map<String, Object>>> getReservationReport(@RequestBody Map<String, Object> params) {
        List<Map<String, Object>> data = reportService.getReservationReport(params);
        return ResponseEntity.ok(data);
    }
    
    /**
     * 入住率报表查询
     * @param hotelId 酒店ID
     * @param month 月份（格式：YYYY-MM）
     * @param groupBy 分组方式
     * @return 每日入住率数据列表（totalRooms, maintenanceRooms, soldRooms, orderCount, occupancyRate）
     */
    @GetMapping("/occupancy")
    public ResponseEntity<List<Map<String, Object>>> getOccupancyReport(
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String groupBy) {
        List<Map<String, Object>> data = reportService.getOccupancyReport(hotelId, month, groupBy);
        return ResponseEntity.ok(data);
    }
    
    /**
     * 收入报表查询
     * @param hotelId 酒店ID
     * @param month 月份（格式：YYYY-MM）
     * @param groupBy 分组方式
     * @return 每日收入数据列表（totalOrders, avgRate）
     */
    @GetMapping("/revenue")
    public ResponseEntity<List<Map<String, Object>>> getRevenueReport(
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String groupBy) {
        List<Map<String, Object>> data = reportService.getRevenueReport(hotelId, month, groupBy);
        return ResponseEntity.ok(data);
    }
}
