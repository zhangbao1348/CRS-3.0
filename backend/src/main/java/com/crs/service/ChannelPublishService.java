package com.crs.service;

import com.crs.entity.ChannelPublishRecord;
import com.crs.entity.RatePlan;
import com.crs.entity.HotelRoomType;
import com.crs.repository.ChannelPublishRecordRepository;
import com.crs.repository.RatePlanRepository;
import com.crs.repository.HotelRoomTypeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChannelPublishService {

    @Autowired
    private RatePlanRepository ratePlanRepository;

    @Autowired
    private HotelRoomTypeRepository hotelRoomTypeRepository;

    @Autowired
    private ChannelPublishRecordRepository publishRecordRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取酒店的房价码列表及关联房型（用于渠道发布页面）
     */
    public List<Map<String, Object>> getRateCodesWithRoomTypes(Integer hotelId) {
        List<RatePlan> ratePlans = ratePlanRepository.findByHotelIdAndStatus(hotelId, "active");
        List<HotelRoomType> allRoomTypes = hotelRoomTypeRepository.findByHotelIdAndStatus(hotelId, "active");

        // 构建多维度映射：applicableRoomTypes 可能存的是 hotel_room_types.id 或 groupRoomTypeId
        Map<String, HotelRoomType> idToRoomType = new HashMap<>();
        for (HotelRoomType rt : allRoomTypes) {
            // 用自身 ID 映射
            idToRoomType.put(String.valueOf(rt.getId()), rt);
            // 用 groupRoomTypeId 映射（集团下发的房型）
            if (rt.getGroupRoomTypeId() != null) {
                idToRoomType.put(String.valueOf(rt.getGroupRoomTypeId()), rt);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (RatePlan rp : ratePlans) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("rateCode", rp.getRateCode());
            item.put("rateName", rp.getRateName());

            List<String> applicableIds = parseApplicableRoomTypes(rp.getApplicableRoomTypes());
            List<Map<String, String>> roomTypes = new ArrayList<>();
            if (applicableIds.isEmpty()) {
                // 没有指定则关联所有房型
                for (HotelRoomType rt : allRoomTypes) {
                    roomTypes.add(Map.of("code", rt.getRoomTypeCode(), "name", rt.getRoomTypeName()));
                }
            } else {
                for (String id : applicableIds) {
                    HotelRoomType rt = idToRoomType.get(id);
                    if (rt != null) {
                        roomTypes.add(Map.of("code", rt.getRoomTypeCode(), "name", rt.getRoomTypeName()));
                    }
                }
            }
            item.put("roomTypes", roomTypes);
            result.add(item);
        }
        return result;
    }

    /**
     * 获取已发布的记录
     */
    public List<ChannelPublishRecord> getPublishedRecords(Integer tenantId, String hotelCode, String channelCode) {
        return publishRecordRepository.findByTenantIdAndHotelCodeAndChannelCodeAndStatus(
                tenantId, hotelCode, channelCode, "published");
    }

    /**
     * 批量发布（按房价码独立发布各自的房型）
     */
    public int batchPublish(Integer tenantId, String hotelCode, String channelCode,
                            Map<String, List<String>> rateCodeRoomTypesMap) {
        int count = 0;
        Date now = new Date();
        for (Map.Entry<String, List<String>> entry : rateCodeRoomTypesMap.entrySet()) {
            String rateCode = entry.getKey();
            List<String> roomTypeCodes = entry.getValue();
            for (String roomTypeCode : roomTypeCodes) {
                try {
                    ChannelPublishRecord record = new ChannelPublishRecord();
                    record.setTenantId(tenantId);
                    record.setHotelCode(hotelCode);
                    record.setChannelCode(channelCode);
                    record.setRateCode(rateCode);
                    record.setRoomTypeCode(roomTypeCode);
                    record.setStatus("published");
                    record.setPublishedAt(now);
                    publishRecordRepository.save(record);
                    count++;
                } catch (Exception e) {
                    // 唯一键冲突说明已发布，跳过
                }
            }
        }
        return count;
    }

    private List<String> parseApplicableRoomTypes(String json) {
        if (json == null || json.isEmpty() || json.equals("null")) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
