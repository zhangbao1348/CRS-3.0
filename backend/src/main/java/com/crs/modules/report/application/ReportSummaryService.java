package com.crs.modules.report.application;

import com.crs.repository.ReportDailyReservationSummaryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 报表每日预聚合写模型。
 *
 * <p>只负责显式同步任务，不参与在线查询，避免 GET 报表产生隐式删除和重建。</p>
 */
@Service
public class ReportSummaryService {

    private final ReportDailyReservationSummaryRepository summaryRepository;
    private final JdbcTemplate jdbcTemplate;

    public ReportSummaryService(
            ReportDailyReservationSummaryRepository summaryRepository,
            JdbcTemplate jdbcTemplate) {
        this.summaryRepository = summaryRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(timeout = 60)
    public void rebuild(Integer tenantId, LocalDate startDate, LocalDate endDate) {
        summaryRepository.deleteByTenantIdAndReportDateBetween(tenantId, startDate, endDate);

        String aggregateSql =
                "SELECT r.tenant_id, DATE(r.created_at) AS report_date, " +
                "COALESCE(r.hotel_code, 'UNKNOWN') AS hotel_code, " +
                "COALESCE(r.channel_code, 'UNKNOWN') AS channel_code, " +
                "COALESCE(r.market_code, 'UNKNOWN') AS market_code, " +
                "COALESCE(rp.rate_category, 'UNCLASSIFIED') AS rate_category_code, " +
                "COALESCE(r.rate_plan_code, 'UNKNOWN') AS rate_plan_code, " +
                "COALESCE(r.room_type_code, 'UNKNOWN') AS room_type_code, " +
                "COALESCE(r.reservation_status, 'unknown') AS reservation_status, " +
                "COUNT(r.id) AS order_count, SUM(r.room_count * r.nights) AS room_nights, " +
                "SUM(r.total_price) AS total_revenue, " +
                "SUM(CASE WHEN r.rate_plan_code = 'BAR' THEN r.room_count * r.nights * 500 ELSE 0 END) AS points_redeemed " +
                "FROM reservation r " +
                "LEFT JOIN rate_plans rp ON r.rate_plan_code = rp.rate_code " +
                "AND r.hotel_code = rp.hotel_code AND r.tenant_id = rp.tenant_id " +
                "WHERE r.tenant_id = ? AND r.created_at >= ? AND r.created_at < ? " +
                "AND r.reservation_status IN ('confirmed', 'checked_in', 'completed', 'cancelled') " +
                "GROUP BY r.tenant_id, DATE(r.created_at), COALESCE(r.hotel_code, 'UNKNOWN'), " +
                "COALESCE(r.channel_code, 'UNKNOWN'), COALESCE(r.market_code, 'UNKNOWN'), " +
                "COALESCE(rp.rate_category, 'UNCLASSIFIED'), COALESCE(r.rate_plan_code, 'UNKNOWN'), " +
                "COALESCE(r.room_type_code, 'UNKNOWN'), COALESCE(r.reservation_status, 'unknown')";

        var startTimestamp = java.sql.Timestamp.valueOf(startDate.atStartOfDay());
        var endTimestamp = java.sql.Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                aggregateSql, tenantId, startTimestamp, endTimestamp);

        String insertSql =
                "INSERT INTO report_daily_reservation_summary (" +
                "tenant_id, report_date, hotel_code, channel_code, market_code, " +
                "rate_category_code, rate_plan_code, room_type_code, reservation_status, " +
                "order_count, room_nights, total_revenue, points_redeemed, created_at, updated_at" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        List<Object[]> batchArguments = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            batchArguments.add(new Object[]{
                    row.get("tenant_id"), row.get("report_date"), row.get("hotel_code"),
                    row.get("channel_code"), row.get("market_code"), row.get("rate_category_code"),
                    row.get("rate_plan_code"), row.get("room_type_code"), row.get("reservation_status"),
                    row.get("order_count"), row.get("room_nights"), row.get("total_revenue"),
                    row.get("points_redeemed")
            });
        }
        if (!batchArguments.isEmpty()) jdbcTemplate.batchUpdate(insertSql, batchArguments);
    }
}
