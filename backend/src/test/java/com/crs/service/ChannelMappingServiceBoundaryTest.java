package com.crs.service;

import com.crs.entity.ChannelHotelMapping;
import com.crs.entity.ChannelRateCodeMapping;
import com.crs.entity.ChannelRoomTypeMapping;
import com.crs.entity.Hotel;
import com.crs.entity.HotelRoomType;
import com.crs.entity.RatePlan;
import com.crs.entity.TenantChannel;
import com.crs.repository.ChannelHotelMappingRepository;
import com.crs.repository.ChannelRateCodeMappingRepository;
import com.crs.repository.ChannelRoomTypeMappingRepository;
import com.crs.repository.HotelRepository;
import com.crs.repository.HotelRoomTypeRepository;
import com.crs.repository.RatePlanRepository;
import com.crs.repository.TenantChannelRepository;
import com.crs.service.impl.ChannelMappingServiceImpl;
import com.crs.util.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ChannelMappingServiceBoundaryTest {

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void createMappingsUseTrustedTenantMasterDataAndIgnoreClientIdentity() {
        TenantContext.setTenantId(7);
        Hotel hotel = new Hotel();
        hotel.setId(31);
        hotel.setTenantId(7);
        hotel.setHotelCode("HTL-31");
        hotel.setChineseName("可信酒店");

        TenantChannel channel = new TenantChannel();
        channel.setId(41);
        channel.setTenantId(7);
        channel.setChannelCode("OTA-A");
        channel.setChannelName("可信渠道");

        HotelRoomType roomType = new HotelRoomType();
        roomType.setId(51);
        roomType.setTenantId(7);
        roomType.setHotelCode("HTL-31");
        roomType.setRoomTypeCode("KING");
        roomType.setRoomTypeName("可信房型");

        RatePlan ratePlan = new RatePlan();
        ratePlan.setId(61);
        ratePlan.setTenantId(7);
        ratePlan.setHotelCode("HTL-31");
        ratePlan.setRateCode("RACK");
        ratePlan.setRateName("可信房价");

        ChannelMappingServiceImpl service = new ChannelMappingServiceImpl(
                repository(ChannelHotelMappingRepository.class, hotel, channel, roomType, ratePlan),
                repository(ChannelRoomTypeMappingRepository.class, hotel, channel, roomType, ratePlan),
                repository(ChannelRateCodeMappingRepository.class, hotel, channel, roomType, ratePlan),
                repository(HotelRepository.class, hotel, channel, roomType, ratePlan),
                repository(TenantChannelRepository.class, hotel, channel, roomType, ratePlan),
                repository(HotelRoomTypeRepository.class, hotel, channel, roomType, ratePlan),
                repository(RatePlanRepository.class, hotel, channel, roomType, ratePlan));

        ChannelHotelMapping hotelRequest = new ChannelHotelMapping();
        hotelRequest.setId(999);
        hotelRequest.setTenantId(999);
        hotelRequest.setChannelCode("OTA-A");
        hotelRequest.setChannelName("伪造渠道");
        hotelRequest.setHotelCode("HTL-31");
        hotelRequest.setHotelName("伪造酒店");
        hotelRequest.setChannelHotelCode(" OTA-HTL-31 ");
        ChannelHotelMapping savedHotel = service.createHotelMapping(hotelRequest);
        assertNull(savedHotel.getId());
        assertEquals(7, savedHotel.getTenantId());
        assertEquals("可信渠道", savedHotel.getChannelName());
        assertEquals("可信酒店", savedHotel.getHotelName());
        assertEquals("OTA-HTL-31", savedHotel.getChannelHotelCode());

        ChannelRoomTypeMapping roomRequest = new ChannelRoomTypeMapping();
        roomRequest.setId(998);
        roomRequest.setTenantId(998);
        roomRequest.setChannelCode("OTA-A");
        roomRequest.setHotelCode("HTL-31");
        roomRequest.setRoomTypeCode("KING");
        roomRequest.setRoomTypeName("伪造房型");
        roomRequest.setChannelRoomTypeCode("OTA-KING");
        ChannelRoomTypeMapping savedRoom = service.createRoomTypeMapping(roomRequest);
        assertNull(savedRoom.getId());
        assertEquals("可信房型", savedRoom.getRoomTypeName());

        ChannelRateCodeMapping rateRequest = new ChannelRateCodeMapping();
        rateRequest.setId(997);
        rateRequest.setTenantId(997);
        rateRequest.setChannelCode("OTA-A");
        rateRequest.setHotelCode("HTL-31");
        rateRequest.setRateCode("RACK");
        rateRequest.setRateCodeName("伪造房价");
        rateRequest.setChannelRateCode("OTA-RACK");
        rateRequest.setMarkup(new BigDecimal("8.50"));
        ChannelRateCodeMapping savedRate = service.createRateCodeMapping(rateRequest);
        assertNull(savedRate.getId());
        assertEquals("可信房价", savedRate.getRateCodeName());
        assertEquals(new BigDecimal("8.50"), savedRate.getMarkup());
        assertNotEquals(997, savedRate.getTenantId());
    }

    @SuppressWarnings("unchecked")
    private <T> T repository(Class<T> type, Hotel hotel, TenantChannel channel,
                             HotelRoomType roomType, RatePlan ratePlan) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (proxy, method, args) -> {
            return switch (method.getName()) {
                case "save" -> args[0];
                case "findByHotelCodeAndTenantId" -> Optional.of(hotel);
                case "findByTenantIdAndChannelCode" -> channel;
                case "findByTenantIdAndHotelCodeAndRoomTypeCode" -> Optional.of(roomType);
                case "findByTenantIdAndHotelCodeAndRateCode" -> Optional.of(ratePlan);
                case "findByTenantIdAndChannelCodeAndHotelCode",
                     "findByTenantIdAndChannelCodeAndHotelCodeAndRoomTypeCode",
                     "findByTenantIdAndChannelCodeAndHotelCodeAndRateCode" -> List.of();
                case "toString" -> type.getSimpleName();
                default -> defaultValue(method.getReturnType());
            };
        });
    }

    private Object defaultValue(Class<?> returnType) {
        if (returnType == boolean.class) return false;
        if (returnType == long.class) return 0L;
        if (returnType == int.class) return 0;
        if (List.class.isAssignableFrom(returnType)) return List.of();
        if (Optional.class.isAssignableFrom(returnType)) return Optional.empty();
        return null;
    }
}
