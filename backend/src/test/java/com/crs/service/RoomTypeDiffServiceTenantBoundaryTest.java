package com.crs.service;

import com.crs.entity.RoomTypeDiff;
import com.crs.repository.RoomTypeDiffRepository;
import com.crs.util.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomTypeDiffServiceTenantBoundaryTest {

    @AfterEach
    void clearTenantContext() {
        TenantContext.clear();
    }

    @Test
    void shouldReadOnlyDiffOwnedByCurrentTenant() {
        RoomTypeDiffService service = service(new AtomicInteger(), diff(10, 1), diff(20, 2));
        TenantContext.setTenantId(1);

        assertTrue(service.getRoomTypeDiffById(10).isPresent());
        assertTrue(service.getRoomTypeDiffById(20).isEmpty());
    }

    @Test
    void shouldRejectCrossTenantWeekdayUpdateBeforeMutation() {
        AtomicInteger saveCount = new AtomicInteger();
        RoomTypeDiffService service = service(saveCount, diff(20, 2));
        TenantContext.setTenantId(1);

        assertThrows(RuntimeException.class,
                () -> service.updateRoomTypeDiffWeekdays(20, List.of("1", "2")));
        assertEquals(0, saveCount.get());
    }

    @Test
    void shouldUpdateOwnedDiffWeekdays() {
        AtomicInteger saveCount = new AtomicInteger();
        RoomTypeDiffService service = service(saveCount, diff(10, 1));
        TenantContext.setTenantId(1);

        RoomTypeDiff updated = service.updateRoomTypeDiffWeekdays(10, List.of("1", "3", "5"));

        assertEquals("1,3,5", updated.getWeekdays());
        assertEquals(1, saveCount.get());
    }

    private RoomTypeDiffService service(AtomicInteger saveCount, RoomTypeDiff... diffs) {
        Map<Integer, RoomTypeDiff> diffsById = new HashMap<>();
        for (RoomTypeDiff diff : diffs) {
            diffsById.put(diff.getId(), diff);
        }

        RoomTypeDiffRepository repository = (RoomTypeDiffRepository) Proxy.newProxyInstance(
                RoomTypeDiffRepository.class.getClassLoader(),
                new Class<?>[]{RoomTypeDiffRepository.class},
                (proxy, method, args) -> {
                    if ("findByIdAndTenantId".equals(method.getName())) {
                        RoomTypeDiff diff = diffsById.get((Integer) args[0]);
                        Integer tenantId = (Integer) args[1];
                        return Optional.ofNullable(diff)
                                .filter(candidate -> tenantId.equals(candidate.getTenantId()));
                    }
                    if ("save".equals(method.getName())) {
                        saveCount.incrementAndGet();
                        return args[0];
                    }
                    if ("toString".equals(method.getName())) {
                        return "RoomTypeDiffRepositoryTestStub";
                    }
                    throw new UnsupportedOperationException("未支持的测试方法: " + method.getName());
                });

        return new RoomTypeDiffService(repository);
    }

    private RoomTypeDiff diff(Integer id, Integer tenantId) {
        RoomTypeDiff diff = new RoomTypeDiff();
        diff.setId(id);
        diff.setTenantId(tenantId);
        return diff;
    }
}
