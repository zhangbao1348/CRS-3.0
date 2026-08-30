package com.crs.service;

import com.crs.entity.HotelRoomType;
import com.crs.repository.HotelRepository;
import com.crs.repository.HotelRoomTypeRepository;
import com.crs.repository.GroupRoomTypeHotelRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class HotelRoomTypeServiceTenantBoundaryTest {

    @AfterEach
    void clearTenantContext() {
        TenantContext.clear();
    }

    @Test
    void shouldReadOnlyRoomTypeOwnedByCurrentTenant() {
        HotelRoomType ownRoomType = roomType(10, 1);
        HotelRoomType otherTenantRoomType = roomType(20, 2);
        HotelRoomTypeService service = service(ownRoomType, otherTenantRoomType);
        TenantContext.setTenantId(1);

        assertTrue(service.getHotelRoomTypeById(10).isPresent());
        assertTrue(service.getHotelRoomTypeById(20).isEmpty());
    }

    @Test
    void shouldRejectCrossTenantDeleteBeforeMutation() {
        AtomicInteger deleteCount = new AtomicInteger();
        HotelRoomTypeService service = service(deleteCount, roomType(20, 2));
        TenantContext.setTenantId(1);

        assertThrows(RuntimeException.class, () -> service.deleteHotelRoomType(20));
        assertEquals(0, deleteCount.get());
    }

    @Test
    void shouldDeleteOwnedRoomType() {
        AtomicInteger deleteCount = new AtomicInteger();
        HotelRoomTypeService service = service(deleteCount, roomType(10, 1));
        TenantContext.setTenantId(1);

        service.deleteHotelRoomType(10);

        assertEquals(1, deleteCount.get());
    }

    private HotelRoomTypeService service(HotelRoomType... roomTypes) {
        return service(new AtomicInteger(), roomTypes);
    }

    private HotelRoomTypeService service(AtomicInteger deleteCount, HotelRoomType... roomTypes) {
        Map<Integer, HotelRoomType> roomTypesById = new HashMap<>();
        for (HotelRoomType roomType : roomTypes) {
            roomTypesById.put(roomType.getId(), roomType);
        }

        HotelRoomTypeRepository roomTypeRepository = (HotelRoomTypeRepository) Proxy.newProxyInstance(
                HotelRoomTypeRepository.class.getClassLoader(),
                new Class<?>[]{HotelRoomTypeRepository.class},
                (proxy, method, args) -> {
                    if ("findByIdAndTenantId".equals(method.getName())) {
                        HotelRoomType roomType = roomTypesById.get((Integer) args[0]);
                        Integer tenantId = (Integer) args[1];
                        return Optional.ofNullable(roomType)
                                .filter(candidate -> tenantId.equals(candidate.getTenantId()));
                    }
                    if ("delete".equals(method.getName())) {
                        deleteCount.incrementAndGet();
                        return null;
                    }
                    if ("toString".equals(method.getName())) {
                        return "HotelRoomTypeRepositoryTestStub";
                    }
                    throw new UnsupportedOperationException("未支持的测试方法: " + method.getName());
                });

        HotelRepository hotelRepository = (HotelRepository) Proxy.newProxyInstance(
                HotelRepository.class.getClassLoader(),
                new Class<?>[]{HotelRepository.class},
                (proxy, method, args) -> {
                    throw new UnsupportedOperationException("房型 ID 归属校验不应回退到酒店编码校验");
                });

        GroupRoomTypeHotelRepository allocationRepository = (GroupRoomTypeHotelRepository) Proxy.newProxyInstance(
                GroupRoomTypeHotelRepository.class.getClassLoader(),
                new Class<?>[]{GroupRoomTypeHotelRepository.class},
                (proxy, method, args) -> {
                    if ("findByTenantIdAndGroupRoomTypeCodeAndHotelCode".equals(method.getName())) {
                        return Optional.empty();
                    }
                    if ("toString".equals(method.getName())) {
                        return "GroupRoomTypeHotelRepositoryTestStub";
                    }
                    throw new UnsupportedOperationException("未支持的测试方法: " + method.getName());
                });

        return new HotelRoomTypeService(roomTypeRepository, hotelRepository, allocationRepository);
    }

    private HotelRoomType roomType(Integer id, Integer tenantId) {
        HotelRoomType roomType = new HotelRoomType();
        roomType.setId(id);
        roomType.setTenantId(tenantId);
        return roomType;
    }
}
