package com.crs.service;

import com.crs.entity.GroupRateCode;
import com.crs.entity.Hotel;
import com.crs.repository.*;
import com.crs.util.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class HotelAndGroupRateCodeCreateBoundaryTest {

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void hotelCreateShouldAlwaysInsertInsideCurrentTenant() {
        AtomicReference<Hotel> saved = new AtomicReference<>();
        HotelRepository hotelRepository = proxy(HotelRepository.class, (method, args) -> switch (method.getName()) {
            case "existsByHotelCodeAndTenantId" -> false;
            case "save" -> {
                saved.set((Hotel) args[0]);
                yield args[0];
            }
            default -> defaultValue(method);
        });
        HotelService service = new HotelService(
                hotelRepository,
                proxy(HotelPriceRepository.class, HotelAndGroupRateCodeCreateBoundaryTest::emptyHandler),
                proxy(TaxSettingRepository.class, HotelAndGroupRateCodeCreateBoundaryTest::emptyHandler));
        Hotel request = new Hotel();
        request.setId(999);
        request.setTenantId(88);
        request.setHotelCode("H001");
        request.setCreatedAt(new Date(1L));
        TenantContext.setTenantId(1);

        Hotel created = service.createHotel(request);

        assertSame(saved.get(), created);
        assertNull(created.getId());
        assertEquals(1, created.getTenantId());
        assertNotEquals(new Date(1L), created.getCreatedAt());
    }

    @Test
    void groupRateCodeCreateShouldIgnoreClientIdentityAndRejectMissingParent() {
        AtomicReference<GroupRateCode> saved = new AtomicReference<>();
        GroupRateCodeRepository repository = proxy(GroupRateCodeRepository.class, (method, args) -> switch (method.getName()) {
            case "findByRateCodeAndGroupId" -> null;
            case "save" -> {
                saved.set((GroupRateCode) args[0]);
                yield args[0];
            }
            default -> defaultValue(method);
        });
        GroupRateCodeService service = new GroupRateCodeService(
                repository, proxy(RatePlanRepository.class, HotelAndGroupRateCodeCreateBoundaryTest::emptyHandler));
        GroupRateCode request = groupRateCode("BAR", null);
        request.setId(999);
        request.setGroupId(88);
        request.setCreatedAt(new Date(1L));
        TenantContext.setTenantId(1);

        GroupRateCode created = service.createGroupRateCode(request);

        assertSame(saved.get(), created);
        assertNull(created.getId());
        assertEquals(1, created.getGroupId());
        assertNotEquals(new Date(1L), created.getCreatedAt());

        GroupRateCode missingParent = groupRateCode("DERIVED", "MISSING");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createGroupRateCode(missingParent));
        assertTrue(exception.getMessage().contains("父级房价码不存在"));
    }

    @Test
    void groupRateCodeUpdateShouldRejectImmutableCodeAndType() {
        GroupRateCode existing = groupRateCode("BAR", null);
        existing.setId(7);
        existing.setGroupId(1);
        GroupRateCodeRepository repository = proxy(GroupRateCodeRepository.class, (method, args) -> switch (method.getName()) {
            case "findByIdAndGroupId" -> Optional.of(existing);
            case "findByRateCodeAndGroupId" -> existing;
            default -> defaultValue(method);
        });
        GroupRateCodeService service = new GroupRateCodeService(
                repository, proxy(RatePlanRepository.class, HotelAndGroupRateCodeCreateBoundaryTest::emptyHandler));
        TenantContext.setTenantId(1);

        GroupRateCode forgedCode = groupRateCode("OTHER", null);
        IllegalArgumentException codeError = assertThrows(IllegalArgumentException.class,
                () -> service.updateGroupRateCode(7, forgedCode));
        assertTrue(codeError.getMessage().contains("代码保存后不可修改"));

        GroupRateCode forgedType = groupRateCode("BAR", null);
        forgedType.setRateType("derived");
        IllegalArgumentException typeError = assertThrows(IllegalArgumentException.class,
                () -> service.updateGroupRateCode(7, forgedType));
        assertTrue(typeError.getMessage().contains("类型保存后不可修改"));
    }

    private GroupRateCode groupRateCode(String code, String parent) {
        GroupRateCode rateCode = new GroupRateCode();
        rateCode.setRateCode(code);
        rateCode.setRateName(code);
        rateCode.setRateType("basic");
        rateCode.setParentRateCode(parent);
        rateCode.setStatus("active");
        return rateCode;
    }

    private static Object emptyHandler(Method method, Object[] args) { return defaultValue(method); }

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
