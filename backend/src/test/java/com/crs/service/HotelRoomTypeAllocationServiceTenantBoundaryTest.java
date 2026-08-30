package com.crs.service;

import com.crs.entity.HotelRoomTypeAllocation;
import com.crs.repository.HotelRoomTypeAllocationRepository;
import com.crs.util.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HotelRoomTypeAllocationServiceTenantBoundaryTest {

    @AfterEach
    void clearTenantContext() {
        TenantContext.clear();
    }

    @Test
    void shouldRejectCrossTenantUpdateWithoutSaving() {
        AtomicInteger saveCount = new AtomicInteger();
        HotelRoomTypeAllocationService service = service(saveCount, new AtomicInteger(), allocation(20, 2));
        TenantContext.setTenantId(1);

        HotelRoomTypeAllocation request = allocation(20, 1);
        request.setAllocated(true);

        assertThrows(IllegalArgumentException.class, () -> service.updateAllocation(request));
        assertEquals(0, saveCount.get());
    }

    @Test
    void shouldRejectCrossTenantDeleteWithoutDeleting() {
        AtomicInteger deleteCount = new AtomicInteger();
        HotelRoomTypeAllocationService service = service(new AtomicInteger(), deleteCount, allocation(20, 2));
        TenantContext.setTenantId(1);

        assertThrows(IllegalArgumentException.class, () -> service.deleteAllocation(20));
        assertEquals(0, deleteCount.get());
    }

    @Test
    void shouldUpdateOnlyMutableFlagsOnOwnedAllocation() {
        AtomicInteger saveCount = new AtomicInteger();
        HotelRoomTypeAllocation existing = allocation(10, 1);
        existing.setHotelCode("H-OWN");
        existing.setRoomTypeCode("RT-OWN");
        HotelRoomTypeAllocationService service = service(saveCount, new AtomicInteger(), existing);
        TenantContext.setTenantId(1);

        HotelRoomTypeAllocation request = allocation(10, 1);
        request.setHotelCode("H-MOVED");
        request.setRoomTypeCode("RT-MOVED");
        request.setAllocated(true);
        request.setRoomInfoEditable(true);
        HotelRoomTypeAllocation updated = service.updateAllocation(request);

        assertEquals("H-OWN", updated.getHotelCode());
        assertEquals("RT-OWN", updated.getRoomTypeCode());
        assertEquals(true, updated.getAllocated());
        assertEquals(true, updated.getRoomInfoEditable());
        assertEquals(1, saveCount.get());
    }

    private HotelRoomTypeAllocationService service(
            AtomicInteger saveCount,
            AtomicInteger deleteCount,
            HotelRoomTypeAllocation... allocations) {
        Map<Integer, HotelRoomTypeAllocation> byId = new HashMap<>();
        for (HotelRoomTypeAllocation allocation : allocations) {
            byId.put(allocation.getId(), allocation);
        }

        HotelRoomTypeAllocationRepository repository = (HotelRoomTypeAllocationRepository) Proxy.newProxyInstance(
                HotelRoomTypeAllocationRepository.class.getClassLoader(),
                new Class<?>[]{HotelRoomTypeAllocationRepository.class},
                (proxy, method, args) -> {
                    if ("findByIdAndTenantId".equals(method.getName())) {
                        HotelRoomTypeAllocation allocation = byId.get((Integer) args[0]);
                        Integer tenantId = (Integer) args[1];
                        return Optional.ofNullable(allocation)
                                .filter(candidate -> tenantId.equals(candidate.getTenantId()));
                    }
                    if ("save".equals(method.getName())) {
                        saveCount.incrementAndGet();
                        return args[0];
                    }
                    if ("delete".equals(method.getName())) {
                        deleteCount.incrementAndGet();
                        return null;
                    }
                    if ("toString".equals(method.getName())) {
                        return "HotelRoomTypeAllocationRepositoryTestStub";
                    }
                    throw new UnsupportedOperationException("未支持的测试方法: " + method.getName());
                });
        return new HotelRoomTypeAllocationService(repository);
    }

    private HotelRoomTypeAllocation allocation(Integer id, Integer tenantId) {
        HotelRoomTypeAllocation allocation = new HotelRoomTypeAllocation();
        allocation.setId(id);
        allocation.setTenantId(tenantId);
        allocation.setAllocated(false);
        allocation.setRoomInfoEditable(false);
        return allocation;
    }
}
