package com.crs.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.crs.entity.Reservation;
import com.crs.repository.ReservationRepository;
import com.crs.repository.SystemTraceLogRepository;
import com.crs.entity.SystemTraceLog;

/**
 * 待支付订单超时自动取消任务。
 * 
 * <p>本定时任务在执行自动取消时，会为每笔订单分配独立的链路追踪 ID 并记录决策快照。</p>
 */
@Service
public class ReservationPaymentTimeoutJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationPaymentTimeoutJob.class);
    private static final String TIMEOUT_STATUS = "pending_payment";
    private static final String UNPAID_STATUS = "unpaid";
    private static final String SYSTEM_OPERATOR = "system:payment-timeout";
    private static final String TIMEOUT_REASON = "PAYMENT_TIMEOUT";

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    @Autowired
    private SystemTraceLogRepository systemTraceLogRepository;

    public ReservationPaymentTimeoutJob(
            ReservationRepository reservationRepository,
            ReservationService reservationService) {
        this.reservationRepository = reservationRepository;
        this.reservationService = reservationService;
    }

    @Scheduled(fixedDelayString = "${crs.reservation.payment-timeout-scan-ms:60000}")
    public void cancelTimedOutPendingPayments() {
        int cancelledCount = 0;

        while (true) {
            int cancelledThisBatch = 0;
            List<Reservation> timedOutReservations = reservationRepository
                    .findTop100ByReservationStatusAndPaymentStatusAndPaymentDeadlineLessThanEqualAndStatusOrderByPaymentDeadlineAsc(
                            TIMEOUT_STATUS,
                            UNPAID_STATUS,
                            new Date(),
                            Reservation.Status.active);

            if (timedOutReservations.isEmpty()) {
                break;
            }

            for (Reservation reservation : timedOutReservations) {
                // 为每笔订单生成独立的 traceId，确保能独立检索
                com.crs.util.TraceContext.setTraceId(null); 
                com.crs.util.TraceContext.recordDecision("reservationCode", reservation.getReservationCode());
                com.crs.util.TraceContext.recordDecision("paymentDeadline", reservation.getPaymentDeadline());
                com.crs.util.TraceContext.recordDecision("currentScanTime", new Date());
                
                com.crs.util.TenantContext.setTenantId(reservation.getTenantId());
                
                long start = System.currentTimeMillis();
                Throwable exception = null;
                try {
                    reservationService.cancelReservationBySystem(
                            reservation.getId(),
                            SYSTEM_OPERATOR,
                            TIMEOUT_REASON);
                    cancelledCount++;
                    cancelledThisBatch++;
                } catch (RuntimeException ex) {
                    exception = ex;
                    LOGGER.warn("支付超时自动取消订单失败: reservationCode={}, reason={}",
                            reservation.getReservationCode(),
                            ex.getMessage());
                } finally {
                    long duration = System.currentTimeMillis() - start;
                    saveSystemTraceLogForJob("cancelTimedOutReservation", reservation.getReservationCode(), exception, duration);
                    
                    com.crs.util.TenantContext.clear();
                    com.crs.util.TraceContext.clear();
                }
            }

            if (timedOutReservations.size() < 100 || cancelledThisBatch == 0) {
                break;
            }
        }

        if (cancelledCount > 0) {
            LOGGER.info("支付超时自动取消任务完成，本次共取消 {} 笔订单", cancelledCount);
        }
    }

    private void saveSystemTraceLogForJob(String action, String reservationCode, Throwable exception, long duration) {
        try {
            SystemTraceLog traceLog = new SystemTraceLog();
            traceLog.setTraceId(com.crs.util.TraceContext.getTraceId());
            traceLog.setCreatedAt(new Date());
            traceLog.setSourceType("SCHEDULED_JOB");
            traceLog.setOperationName("ReservationPaymentTimeoutJob." + action);
            traceLog.setReferenceCode(reservationCode);
            traceLog.setRelatedPrdLink(".kiro/specs/prd/14-订单管理.md");

            if (exception != null) {
                traceLog.setStatus("ERROR");
                traceLog.setErrorClass(exception.getClass().getName());
                traceLog.setErrorMethod(action);
                
                StackTraceElement[] stack = exception.getStackTrace();
                if (stack != null && stack.length > 0) {
                    for (StackTraceElement ste : stack) {
                        if (ste.getClassName().startsWith("com.crs.")) {
                            traceLog.setErrorClass(ste.getClassName());
                            traceLog.setErrorMethod(ste.getMethodName());
                            traceLog.setErrorLine(ste.getLineNumber());
                            break;
                        }
                    }
                }
                
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                exception.printStackTrace(pw);
                traceLog.setErrorStack(sw.toString());
            } else {
                traceLog.setStatus("SUCCESS");
            }

            // 封装完整的决策快照数据包 (DecisionSnapshot)
            Map<String, Object> finalSnapshot = new java.util.HashMap<>();
            finalSnapshot.put("decisions", com.crs.util.TraceContext.getDecisions());
            finalSnapshot.put("duration", duration + "ms");
            
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            traceLog.setDecisionSnapshot(mapper.writeValueAsString(finalSnapshot));
            
            systemTraceLogRepository.save(traceLog);
        } catch (Exception e) {
            LOGGER.error("Failed to save scheduled job trace log", e);
        }
    }
}
