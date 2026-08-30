package com.crs.modules.report.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ReportQueryPolicyTest {

    @Test
    void acceptsInclusiveRangeAtBudgetBoundary() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        assertDoesNotThrow(() -> ReportQueryPolicy.validateRange(start, start.plusDays(365), "本期"));
    }

    @Test
    void rejectsInvalidOrUnboundedRanges() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        assertThrows(IllegalArgumentException.class,
                () -> ReportQueryPolicy.validateRange(start, start.minusDays(1), "本期"));
        assertThrows(IllegalArgumentException.class,
                () -> ReportQueryPolicy.validateRange(start, start.plusDays(366), "本期"));
        assertThrows(IllegalArgumentException.class,
                () -> ReportQueryPolicy.validateRange(null, start, "本期"));
    }
}
