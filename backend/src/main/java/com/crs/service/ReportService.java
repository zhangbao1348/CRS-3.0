package com.crs.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportService {

    /**
     * 初始化/重构特定租户在特定时间段内的每日预订聚合汇总数据
     */
    void initializeSummaryData(Integer tenantId, LocalDate startDate, LocalDate endDate);

    /**
     * 查询多维嵌套订单报表（支持同环比计算、灵活维度组合过滤与分组）
     */
    Map<String, Object> queryReservationReport(
        Integer tenantId,
        LocalDate startDate,
        LocalDate endDate,
        String hotelCode,
        String channelCode,
        String marketCode,
        String rateCategoryCode,
        String ratePlanCode,
        String orderStatus,
        String groupBy1,
        String groupBy2,
        String paymentMethod,
        Boolean memberBooking,
        Boolean canEarnPoints,
        Boolean onlineBooking,
        Boolean enableCompare,
        LocalDate compareStartDate,
        LocalDate compareEndDate
    );

    /**
     * 查询出租率报表 (按天呈现, 支持按酒店/按房型维度)
     */
    List<Map<String, Object>> queryOccupancyReport(
        Integer tenantId,
        String hotelCode,
        LocalDate monthDate,
        String statisticMethod
    );

    /**
     * 查询营收分析报表 (按天呈现, 支持按酒店/按房型维度)
     */
    List<Map<String, Object>> queryRevenueReport(
        Integer tenantId,
        String hotelCode,
        LocalDate monthDate,
        String statisticMethod
    );
}
