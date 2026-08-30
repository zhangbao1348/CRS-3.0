package com.crs.service;

import com.crs.entity.CancellationPolicy;
import com.crs.repository.CancellationPolicyRepository;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.service.impl.CancellationPolicyServiceImpl;
import com.crs.util.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class CancellationPolicyServiceTenantBoundaryTest {

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void updateShouldPersistEveryEditablePolicyField() {
        CancellationPolicy existing = policy(10, 7, "OLD");
        CancellationPolicy incoming = policy(null, null, "OLD");
        incoming.setName("新政策");
        incoming.setType("限时扣费");
        incoming.setCancellationDays(2);
        incoming.setCancellationTime("16:00");
        incoming.setCancellationFeeType("首晚");
        incoming.setDescription("新描述");
        incoming.setStatus("inactive");
        incoming.setIsDefault(1);

        CancellationPolicyRepository policyRepository = proxy(CancellationPolicyRepository.class, (method, args) -> switch (method) {
            case "findByIdAndTenantId" -> Optional.of(existing);
            case "existsByTenantIdAndCode" -> false;
            case "save" -> args[0];
            default -> defaultValue(method);
        });
        GroupRateCodeRepository groupRepository = proxy(GroupRateCodeRepository.class,
                (method, args) -> defaultValue(method));
        CancellationPolicyService service = new CancellationPolicyServiceImpl(policyRepository, groupRepository);
        TenantContext.setTenantId(7);

        CancellationPolicy updated = service.update(10, incoming);

        assertEquals("OLD", updated.getCode());
        assertEquals("新政策", updated.getName());
        assertEquals("限时扣费", updated.getType());
        assertEquals(2, updated.getCancellationDays());
        assertEquals("16:00", updated.getCancellationTime());
        assertEquals("首晚", updated.getCancellationFeeType());
        assertEquals("新描述", updated.getDescription());
        assertEquals("inactive", updated.getStatus());
        assertEquals(1, updated.getIsDefault());
        assertEquals(7, updated.getTenantId());
    }

    @Test
    void updateShouldRejectPolicyCodeMutation() {
        CancellationPolicy existing = policy(10, 7, "OLD");
        CancellationPolicy incoming = policy(null, null, "NEW");
        CancellationPolicyRepository policyRepository = proxy(CancellationPolicyRepository.class, (method, args) -> switch (method) {
            case "findByIdAndTenantId" -> Optional.of(existing);
            default -> defaultValue(method);
        });
        CancellationPolicyService service = new CancellationPolicyServiceImpl(
                policyRepository,
                proxy(GroupRateCodeRepository.class, (method, args) -> defaultValue(method)));
        TenantContext.setTenantId(7);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.update(10, incoming));

        assertTrue(exception.getMessage().contains("代码保存后不可修改"));
    }

    @Test
    void deleteShouldCountReferencesInsideCurrentTenantOnly() {
        CancellationPolicy existing = policy(10, 7, "CXL");
        AtomicBoolean deleted = new AtomicBoolean(false);
        CancellationPolicyRepository policyRepository = proxy(CancellationPolicyRepository.class, (method, args) -> switch (method) {
            case "findByIdAndTenantId" -> Optional.of(existing);
            case "delete" -> {
                deleted.set(true);
                yield null;
            }
            default -> defaultValue(method);
        });
        GroupRateCodeRepository groupRepository = proxy(GroupRateCodeRepository.class, (method, args) -> {
            if (method.equals("countByGroupIdAndCancellationRule")) {
                assertEquals(7, args[0]);
                assertEquals("CXL", args[1]);
                return 2L;
            }
            return defaultValue(method);
        });
        CancellationPolicyService service = new CancellationPolicyServiceImpl(policyRepository, groupRepository);
        TenantContext.setTenantId(7);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.delete(10));

        assertTrue(exception.getMessage().contains("2"));
        assertFalse(deleted.get());
    }

    private static CancellationPolicy policy(Integer id, Integer tenantId, String code) {
        CancellationPolicy policy = new CancellationPolicy();
        policy.setId(id);
        policy.setTenantId(tenantId);
        policy.setCode(code);
        policy.setName("原政策");
        policy.setType("免费取消");
        return policy;
    }

    @FunctionalInterface
    private interface MethodHandler {
        Object invoke(String method, Object[] args);
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, MethodHandler handler) {
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
                    return handler.invoke(method.getName(), args == null ? new Object[0] : args);
                });
    }

    private static Object defaultValue(String method) {
        if (method.startsWith("count")) {
            return 0L;
        }
        if (method.startsWith("exists")) {
            return false;
        }
        if (method.startsWith("find")) {
            return Optional.empty();
        }
        return null;
    }
}
