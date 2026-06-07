package com.crs.service;

import com.crs.entity.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 实时同步订单变更事件至日聚合表的监听器 (ReportDailySummaryListener)
 */
@Component
public class ReportDailySummaryListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportDailySummaryListener.class);

    @Autowired
    private ReportService reportService;

    @EventListener
    public void handleReservationChanged(ReservationChangedEvent event) {
        Reservation res = event.reservation();
        try {
            // 将订单创建日期转为 LocalDate 作为聚合维度日期
            LocalDate reportDate = res.getCreatedAt().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // 直接触发当天该租户的报表预聚合重算
            reportService.initializeSummaryData(res.getTenantId(), reportDate, reportDate);

            LOGGER.debug("报表实时天级数据重算成功: tenantId={}, reportDate={}", 
                    res.getTenantId(), reportDate);

        } catch (Exception e) {
            LOGGER.error("报表实时天级数据重算失败: tenantId={}, error={}", 
                    res.getTenantId(), e.getMessage(), e);
        }
    }
}
