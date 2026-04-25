package com.crs.service;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 报表服务
 * 提供预订报表、入住率报表、收入报表的查询功能
 */
@Service
public class ReportService {
    
    /**
     * 查询预订报表
     * @param params 查询参数（hotel, bookingDateStart, bookingDateEnd, orderStatus, marketCode, channelCode, ratePlan, groupBy1, groupBy2, paymentMethod）
     * @return 报表数据列表
     */
    public List<Map<String, Object>> getReservationReport(Map<String, Object> params) {
        // TODO: 实现真实的预订报表查询逻辑
        // 当前返回空列表，前端在DEMO_MODE下使用Mock数据
        return new ArrayList<>();
    }
    
    /**
     * 查询入住率报表
     * @param hotelId 酒店ID
     * @param month 月份（格式：YYYY-MM）
     * @param groupBy 分组方式
     * @return 每日入住率数据列表
     */
    public List<Map<String, Object>> getOccupancyReport(Integer hotelId, String month, String groupBy) {
        // TODO: 实现真实的入住率报表查询逻辑
        // 当前返回空列表，前端在DEMO_MODE下使用Mock数据
        return new ArrayList<>();
    }
    
    /**
     * 查询收入报表
     * @param hotelId 酒店ID
     * @param month 月份（格式：YYYY-MM）
     * @param groupBy 分组方式
     * @return 每日收入数据列表
     */
    public List<Map<String, Object>> getRevenueReport(Integer hotelId, String month, String groupBy) {
        // TODO: 实现真实的收入报表查询逻辑
        // 当前返回空列表，前端在DEMO_MODE下使用Mock数据
        return new ArrayList<>();
    }
}
