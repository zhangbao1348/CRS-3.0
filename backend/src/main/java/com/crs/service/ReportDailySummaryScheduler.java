package com.crs.service;

import com.crs.entity.Tenant;
import com.crs.repository.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 每日预订聚合汇总报表 T+1 定时同步与对账任务
 */
@Service
public class ReportDailySummaryScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportDailySummaryScheduler.class);

    @Autowired
    private ReportService reportService;

    @Autowired
    private TenantRepository tenantRepository;

    /**
     * 每天凌晨 2:00 执行同步前两天（今天与昨天）的汇总数据，以处理延迟退改或跨天订单的尾差
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduleDailyReservationSummaryAlign() {
        LOGGER.info("开始执行预订报表日聚合数据 T+1 对账与同步任务...");
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(2);
        LocalDate endDate = today;

        try {
            List<Tenant> tenants = tenantRepository.findAll();
            for (Tenant tenant : tenants) {
                if (tenant.getStatus() == null || tenant.getStatus() == Tenant.Status.active) {
                    LOGGER.info("正在同步租户: tenantId={}, 范围: {} 到 {}", tenant.getId(), startDate, endDate);
                    reportService.initializeSummaryData(tenant.getId(), startDate, endDate);
                }
            }
            LOGGER.info("预订报表日聚合对账与同步任务成功完成。");
        } catch (Exception e) {
            LOGGER.error("预订报表日聚合任务执行失败: {}", e.getMessage(), e);
        }
    }
}
