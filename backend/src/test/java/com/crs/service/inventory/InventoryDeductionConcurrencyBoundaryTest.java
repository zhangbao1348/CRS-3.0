package com.crs.service.inventory;

import com.crs.entity.PmsInventory;
import com.crs.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InventoryDeductionConcurrencyBoundaryTest {

    @Test
    void shouldLockAllHotelDateRowsBeforeAtomicRoomTypeDeduction() {
        List<String> operations = new ArrayList<>();
        PmsInventoryRepository pmsRepository = proxy(PmsInventoryRepository.class, (method, args) -> {
            if (method.getName().equals("findHotelDateInventoryForUpdate")) {
                operations.add("lock-hotel-date");
                return List.of(inventory("RT1", 3), inventory("RT2", 2));
            }
            return defaultValue(method);
        });
        JdbcTemplate jdbcTemplate = jdbc(operations, 1);
        InventoryDeductionService service = service(pmsRepository, jdbcTemplate);

        service.deductInventory(context(2));

        assertFalse(operations.isEmpty());
        assertEquals("lock-hotel-date", operations.get(0));
        assertEquals("update-pms", operations.get(1));
    }

    @Test
    void shouldRejectHotelLevelShortageWithoutWritingRoomTypeRow() {
        List<String> operations = new ArrayList<>();
        PmsInventoryRepository pmsRepository = proxy(PmsInventoryRepository.class, (method, args) -> {
            if (method.getName().equals("findHotelDateInventoryForUpdate")) {
                operations.add("lock-hotel-date");
                return List.of(inventory("RT1", 1), inventory("RT2", 0));
            }
            return defaultValue(method);
        });
        InventoryDeductionService service = service(pmsRepository, jdbc(operations, 1));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.deductInventory(context(2)));

        assertTrue(exception.getMessage().contains("酒店库存不足"));
        assertEquals(List.of("lock-hotel-date"), operations);
    }

    private InventoryDeductionService service(PmsInventoryRepository pmsRepository, JdbcTemplate jdbcTemplate) {
        return new InventoryDeductionServiceImpl(
                pmsRepository,
                proxy(InventoryQuotaRepository.class, InventoryDeductionConcurrencyBoundaryTest::emptyOptionalHandler),
                proxy(OverbookingRepository.class, InventoryDeductionConcurrencyBoundaryTest::emptyOptionalHandler),
                proxy(RoomStatusRepository.class, InventoryDeductionConcurrencyBoundaryTest::emptyOptionalHandler),
                proxy(BookingControlRepository.class, InventoryDeductionConcurrencyBoundaryTest::emptyOptionalHandler),
                jdbcTemplate);
    }

    private JdbcTemplate jdbc(List<String> operations, int pmsResult) {
        return new JdbcTemplate() {
            @Override
            public int update(String sql, Object... args) {
                if (sql.startsWith("UPDATE pms_inventory")) {
                    operations.add("update-pms");
                    return pmsResult;
                }
                operations.add("update-quota");
                return 0;
            }
        };
    }

    private InventoryDeductionContext context(int roomCount) {
        InventoryDeductionContext context = new InventoryDeductionContext();
        context.setTenantId(1);
        context.setHotelCode("H001");
        context.setRoomTypeCode("RT1");
        context.setRateCode("BAR");
        context.setChannelCode("DIRECT");
        context.setCheckInDate(LocalDate.of(2026, 9, 1));
        context.setCheckOutDate(LocalDate.of(2026, 9, 2));
        context.setRoomCount(roomCount);
        context.setReservationCode("RES-TEST-001");
        return context;
    }

    private PmsInventory inventory(String roomTypeCode, int available) {
        PmsInventory inventory = new PmsInventory();
        inventory.setRoomTypeCode(roomTypeCode);
        inventory.setAvailableRooms(available);
        inventory.setOverbookCount(0);
        return inventory;
    }

    private static Object emptyOptionalHandler(Method method, Object[] args) {
        return defaultValue(method);
    }

    @FunctionalInterface
    private interface Handler {
        Object invoke(Method method, Object[] args) throws Throwable;
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
