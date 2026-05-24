package com.crs.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.crs.entity.Reservation;
import com.crs.repository.ReservationRepository;

/**
 * 待支付订单超时自动取消任务。
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
                try {
                    reservationService.cancelReservationBySystem(
                            reservation.getId(),
                            SYSTEM_OPERATOR,
                            TIMEOUT_REASON);
                    cancelledCount++;
                    cancelledThisBatch++;
                } catch (RuntimeException ex) {
                    LOGGER.warn("支付超时自动取消订单失败: reservationCode={}, reason={}",
                            reservation.getReservationCode(),
                            ex.getMessage());
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
}
