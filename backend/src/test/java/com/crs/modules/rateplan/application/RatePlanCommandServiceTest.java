package com.crs.modules.rateplan.application;

import com.crs.entity.Hotel;
import com.crs.entity.RatePlan;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.repository.HotelRepository;
import com.crs.repository.RatePlanRepository;
import com.crs.shared.api.ApiException;
import com.crs.util.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class RatePlanCommandServiceTest {

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void createShouldIgnoreClientIdentityTenantAndAuditFields() {
        AtomicReference<RatePlan> saved = new AtomicReference<>();
        RatePlanRepository rateRepository = proxy(RatePlanRepository.class, (method, args) -> switch (method.getName()) {
            case "findByTenantIdAndHotelCodeAndRateCode" -> Optional.empty();
            case "save" -> {
                saved.set((RatePlan) args[0]);
                yield args[0];
            }
            default -> defaultValue(method);
        });
        RatePlanCommandService service = service(rateRepository);
        RatePlan request = request("BAR", "基础价");
        Date forgedCreatedAt = new Date(1L);
        request.setId(999);
        request.setTenantId(88);
        request.setCreatedAt(forgedCreatedAt);
        TenantContext.setTenantId(1);

        RatePlan created = service.create(request);

        assertSame(saved.get(), created);
        assertNotSame(request, created);
        assertNull(created.getId());
        assertEquals(1, created.getTenantId());
        assertNotEquals(forgedCreatedAt, created.getCreatedAt());
        assertEquals("BAR", created.getRateCode());
    }

    @Test
    void updateShouldPreserveIdentityTenantCreatedAtAndImmutableCodeType() {
        RatePlan existing = request("BAR", "基础价");
        existing.setId(7);
        existing.setTenantId(1);
        existing.setRateType("basic");
        Date originalCreatedAt = new Date(1234L);
        existing.setCreatedAt(originalCreatedAt);
        AtomicReference<RatePlan> saved = new AtomicReference<>();
        RatePlanRepository rateRepository = proxy(RatePlanRepository.class, (method, args) -> switch (method.getName()) {
            case "findByIdAndTenantId" -> Optional.of(existing);
            case "findByTenantIdAndHotelCodeAndRateCode" -> Optional.of(existing);
            case "save" -> {
                saved.set((RatePlan) args[0]);
                yield args[0];
            }
            default -> defaultValue(method);
        });
        RatePlanCommandService service = service(rateRepository);
        RatePlan request = request("BAR", "更新名称");
        request.setRateType("basic");
        request.setId(999);
        request.setTenantId(88);
        request.setCreatedAt(new Date(9L));
        TenantContext.setTenantId(1);

        RatePlan updated = service.update(7, request);

        assertSame(existing, saved.get());
        assertSame(existing, updated);
        assertEquals(7, updated.getId());
        assertEquals(1, updated.getTenantId());
        assertEquals(originalCreatedAt, updated.getCreatedAt());
        assertEquals("BAR", updated.getRateCode());
        assertEquals("basic", updated.getRateType());
        assertEquals("更新名称", updated.getRateName());
    }

    @Test
    void updateShouldRejectCodeMutationBeforeSave() {
        RatePlan existing = request("BAR", "基础价");
        existing.setId(7);
        existing.setTenantId(1);
        existing.setRateType("basic");
        RatePlanRepository rateRepository = proxy(RatePlanRepository.class, (method, args) ->
                method.getName().equals("findByIdAndTenantId") ? Optional.of(existing) : defaultValue(method));
        RatePlanCommandService service = service(rateRepository);
        RatePlan request = request("FORGED", "基础价");
        request.setRateType("basic");
        TenantContext.setTenantId(1);

        ApiException exception = assertThrows(ApiException.class, () -> service.update(7, request));

        assertEquals("RATE_CODE_IMMUTABLE", exception.getCode());
    }

    private RatePlanCommandService service(RatePlanRepository rateRepository) {
        GroupRateCodeRepository groupRepository = proxy(GroupRateCodeRepository.class,
                (method, args) -> defaultValue(method));
        HotelRepository hotelRepository = proxy(HotelRepository.class, (method, args) -> {
            if (method.getName().equals("findByHotelCodeAndTenantId")) {
                Hotel hotel = new Hotel();
                hotel.setTenantId(1);
                hotel.setHotelCode("H001");
                return Optional.of(hotel);
            }
            return defaultValue(method);
        });
        return new RatePlanCommandService(rateRepository, groupRepository, hotelRepository);
    }

    private RatePlan request(String code, String name) {
        RatePlan plan = new RatePlan();
        plan.setHotelCode("H001");
        plan.setRateCode(code);
        plan.setRateName(name);
        plan.setRateType("basic");
        plan.setStatus("active");
        return plan;
    }

    @FunctionalInterface
    private interface Handler { Object invoke(Method method, Object[] args) throws Throwable; }

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
