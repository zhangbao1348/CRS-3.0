package com.crs;

import com.crs.service.ReportService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
@Disabled("手工报表数据初始化工具，禁止在自动化测试中修改数据库")
public class ReportInitTest {

    @Autowired
    private ReportService reportService;

    @Test
    public void initReportData() {
        System.out.println(">>> 开始初始化报表汇总数据...");
        reportService.initializeSummaryData(1, LocalDate.of(2025, 5, 1), LocalDate.of(2026, 7, 30));
        System.out.println(">>> 报表汇总数据初始化完成！");
    }
}
