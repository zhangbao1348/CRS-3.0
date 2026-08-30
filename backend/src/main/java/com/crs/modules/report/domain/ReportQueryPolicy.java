package com.crs.modules.report.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 报表查询预算与日期边界。
 *
 * <p>关联模块：订单报表、预聚合报表、导出。所有入口复用同一套范围限制，
 * 防止无界时间扫描拖垮在线事务数据库。</p>
 */
public final class ReportQueryPolicy {

    public static final int MAX_ONLINE_RANGE_DAYS = 366;

    private ReportQueryPolicy() {
    }

    /** 校验在线报表或同步任务的日期范围（含首尾两天）。 */
    public static void validateRange(LocalDate startDate, LocalDate endDate, String label) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(label + "日期范围不能为空");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(label + "结束日期不能早于开始日期");
        }
        long inclusiveDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (inclusiveDays > MAX_ONLINE_RANGE_DAYS) {
            throw new IllegalArgumentException(label + "最多查询 " + MAX_ONLINE_RANGE_DAYS + " 天");
        }
    }
}
