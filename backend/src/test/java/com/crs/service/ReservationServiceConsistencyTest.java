package com.crs.service;

import com.crs.entity.*;
import com.crs.repository.*;
import com.crs.service.inventory.*;
import com.crs.util.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceConsistencyTest {

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void cancelShouldLockOrderAndReleaseInventoryExactlyOnce() throws Exception {
        Reservation reservation = reservation("confirmed", "unpaid");
        AtomicInteger lockCount = new AtomicInteger();
        AtomicInteger releaseCount = new AtomicInteger();
        ReservationService service = service(reservation, lockCount, releaseCount, new AtomicReference<>());
        TenantContext.setTenantId(1);

        Reservation cancelled = service.cancelReservation(10, "operator", "客人取消");

        assertEquals(1, lockCount.get());
        assertEquals(1, releaseCount.get());
        assertEquals("cancelled", cancelled.getReservationStatus());
        assertEquals(Reservation.Status.cancelled, cancelled.getStatus());
    }

    @Test
    void paidOrderShouldRefundBeforeInventoryReleaseAndCancel() throws Exception {
        Reservation reservation = reservation("confirmed", "paid");
        reservation.setTotalPrice(new BigDecimal("120.00"));
        AtomicInteger releaseCount = new AtomicInteger();
        AtomicReference<ReservationPayment> savedPayment = new AtomicReference<>();
        ReservationService service = service(
                reservation, new AtomicInteger(), releaseCount, savedPayment);
        TenantContext.setTenantId(1);

        Reservation cancelled = service.cancelReservation(10, "operator", "客人取消");

        assertEquals(1, releaseCount.get());
        assertEquals("cancelled", cancelled.getReservationStatus());
        assertEquals("refunded", cancelled.getPaymentStatus());
        assertNotNull(savedPayment.get());
        assertEquals("refund", savedPayment.get().getPaymentType());
        assertEquals("refunded", savedPayment.get().getStatus());
        assertEquals(new BigDecimal("120.00"), savedPayment.get().getPaymentAmount());
    }

    @Test
    void genericStatusEndpointMustNotBypassCancellationWorkflow() throws Exception {
        Reservation reservation = reservation("confirmed", "unpaid");
        AtomicInteger releaseCount = new AtomicInteger();
        ReservationService service = service(
                reservation, new AtomicInteger(), releaseCount, new AtomicReference<>());
        TenantContext.setTenantId(1);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.updateReservationStatus(10, "cancelled", "operator"));

        assertTrue(exception.getMessage().contains("取消订单接口"));
        assertEquals(0, releaseCount.get());
        assertEquals("confirmed", reservation.getReservationStatus());
    }

    @Test
    void paymentShouldPersistTenantOnFinancialRecord() throws Exception {
        Reservation reservation = reservation("pending_payment", "unpaid");
        reservation.setTotalPrice(new BigDecimal("120.00"));
        AtomicReference<ReservationPayment> savedPayment = new AtomicReference<>();
        ReservationService service = service(
                reservation, new AtomicInteger(), new AtomicInteger(), savedPayment);
        TenantContext.setTenantId(1);

        Reservation paid = service.payReservation(
                reservation.getReservationCode(), "alipay", new BigDecimal("120.00"),
                "TX-UNIQUE-001", "channel:test");

        assertNotNull(savedPayment.get());
        assertEquals(1, savedPayment.get().getTenantId());
        assertEquals(reservation.getId(), savedPayment.get().getReservationId());
        assertEquals("paid", paid.getPaymentStatus());
        assertEquals("confirmed", paid.getReservationStatus());
        assertNull(paid.getPaymentDeadline());
    }

    private ReservationService service(
            Reservation reservation,
            AtomicInteger lockCount,
            AtomicInteger releaseCount,
            AtomicReference<ReservationPayment> savedPayment) throws Exception {
        ReservationRepository reservationRepository = proxy(ReservationRepository.class, (method, args) -> switch (method.getName()) {
            case "findByIdAndTenantIdForUpdate" -> {
                lockCount.incrementAndGet();
                assertEquals(1, args[1]);
                yield Optional.of(reservation);
            }
            case "findByTenantIdAndReservationCodeForUpdate" -> Optional.of(reservation);
            case "save" -> args[0];
            default -> defaultValue(method);
        });
        ReservationPaymentRepository paymentRepository = proxy(ReservationPaymentRepository.class, (method, args) -> switch (method.getName()) {
            case "findByReservationIdOrderByCreatedAtDesc" -> List.of();
            case "save" -> {
                savedPayment.set((ReservationPayment) args[0]);
                yield args[0];
            }
            default -> defaultValue(method);
        });
        ReservationHistoryRepository historyRepository = proxy(ReservationHistoryRepository.class,
                (method, args) -> "save".equals(method.getName()) ? args[0] : defaultValue(method));
        InventoryDeductionService inventory = new InventoryDeductionService() {
            @Override public AvailabilityResult checkAvailability(AvailabilityContext context) { return null; }
            @Override public List<AvailabilityResult.DailyAvailability> checkDailyRangeAvailability(AvailabilityContext context) { return List.of(); }
            @Override public void deductInventory(InventoryDeductionContext context) { }
            @Override public void releaseInventory(InventoryReleaseContext context) { releaseCount.incrementAndGet(); }
        };

        ReservationService service = new ReservationService(
                reservationRepository,
                proxy(ReservationDailyPriceRepository.class, ReservationServiceConsistencyTest::defaultHandler),
                proxy(ReservationGuestRepository.class, ReservationServiceConsistencyTest::defaultHandler),
                paymentRepository,
                proxy(ReservationPromotionRepository.class, ReservationServiceConsistencyTest::defaultHandler),
                historyRepository,
                inventory);
        Field publisher = ReservationService.class.getDeclaredField("eventPublisher");
        publisher.setAccessible(true);
        publisher.set(service, (ApplicationEventPublisher) event -> { });
        return service;
    }

    private Reservation reservation(String reservationStatus, String paymentStatus) {
        Reservation reservation = new Reservation();
        reservation.setId(10);
        reservation.setTenantId(1);
        reservation.setReservationCode("RES-TEST-001");
        reservation.setReservationStatus(reservationStatus);
        reservation.setPaymentStatus(paymentStatus);
        reservation.setStatus(Reservation.Status.active);
        reservation.setHotelCode("H001");
        reservation.setRoomTypeCode("RT1");
        reservation.setRatePlanCode("BAR");
        reservation.setChannelCode("DIRECT");
        reservation.setRoomCount(1);
        LocalDate today = LocalDate.now();
        reservation.setCheckInDate(Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        reservation.setCheckOutDate(Date.from(today.plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return reservation;
    }

    @FunctionalInterface
    private interface Handler {
        Object invoke(Method method, Object[] args) throws Throwable;
    }

    private static Object defaultHandler(Method method, Object[] args) {
        return defaultValue(method);
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, Handler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (proxy, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        return switch (method.getName()) {
                            case "toString" -> type.getSimpleName() + "Proxy";
                            case "hashCode" -> System.identityHashCode(proxy);
                            case "equals" -> proxy == args[0];
                            default -> null;
                        };
                    }
                    return handler.invoke(method, args == null ? new Object[0] : args);
                });
    }

    private static Object defaultValue(Method method) {
        Class<?> type = method.getReturnType();
        if (type == void.class) return null;
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (Optional.class.isAssignableFrom(type)) return Optional.empty();
        if (List.class.isAssignableFrom(type)) return List.of();
        return null;
    }
}
