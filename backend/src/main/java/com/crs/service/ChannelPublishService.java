package com.crs.service;

import com.crs.entity.*;
import com.crs.repository.*;
import com.crs.util.TenantContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * ChannelPublishService 服务接口 (Service Interface)
 *
 * <p>本核心模块自动生成详细注释。主要负责处理【ChannelPublishService】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 *
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/13-渠道管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 ChannelPublishService 的功能定义。</li>
 * </ul>
 *
 * @since 2026-05-10
 */
@Service
public class ChannelPublishService {

    @Autowired
    private RatePlanRepository ratePlanRepository;

    @Autowired
    private HotelRoomTypeRepository hotelRoomTypeRepository;

    @Autowired
    private ChannelPublishRecordRepository publishRecordRepository;

    @Autowired
    private ChannelHotelMappingRepository channelHotelMappingRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private TenantChannelRepository tenantChannelRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    /**
     * 获取酒店的房价码列表及关联房型（用于渠道发布页面）
     */
    public List<Map<String, Object>> getRateCodesWithRoomTypes(String hotelCode) {
        Integer tenantId = getCurrentTenantId();
        List<RatePlan> ratePlans = ratePlanRepository.findByTenantIdAndHotelCodeAndStatus(tenantId, hotelCode, "active");
        List<HotelRoomType> allRoomTypes = hotelRoomTypeRepository.findDistinctByTenantIdAndHotelCodeAndStatus(tenantId, hotelCode, "active");

        Map<String, HotelRoomType> codeToRoomType = new HashMap<>();
        for (HotelRoomType rt : allRoomTypes) {
            codeToRoomType.put(rt.getRoomTypeCode(), rt);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (RatePlan rp : ratePlans) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("rateCode", rp.getRateCode());
            item.put("rateName", rp.getRateName());

            List<String> applicableCodes = parseApplicableRoomTypes(rp.getApplicableRoomTypes());
            List<Map<String, String>> roomTypes = new ArrayList<>();
            if (applicableCodes.isEmpty()) {
                for (HotelRoomType rt : allRoomTypes) {
                    roomTypes.add(Map.of("code", rt.getRoomTypeCode(), "name", rt.getRoomTypeName()));
                }
            } else {
                for (String code : applicableCodes) {
                    HotelRoomType rt = codeToRoomType.get(code);
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
    public List<ChannelPublishRecord> getPublishedRecords(String hotelCode, String channelCode) {
        return publishRecordRepository.findByTenantIdAndHotelCodeAndChannelCodeAndStatus(
                getCurrentTenantId(), hotelCode, channelCode, "published");
    }

    /**
     * 批量发布（按房价码独立发布各自的房型）
     */
    @Transactional
    public int batchPublish(String hotelCode, String channelCode,
                            Map<String, List<String>> rateCodeRoomTypesMap) {
        Integer tenantId = getCurrentTenantId();
        // 自动建立渠道酒店映射（确保授权）
        ensureChannelHotelMapping(tenantId, hotelCode, channelCode);

        int count = 0;
        Date now = new Date();
        for (Map.Entry<String, List<String>> entry : rateCodeRoomTypesMap.entrySet()) {
            String rateCode = entry.getKey();
            List<String> roomTypeCodes = entry.getValue();

            // 先清理该房价码下原有的发布记录（覆盖式同步，支持取消勾选）
            publishRecordRepository.deleteByRateCode(tenantId, hotelCode, channelCode, rateCode);

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
                    // 唯一键冲突跳过（理论上由于已删除不会再冲突）
                }
            }
        }
        return count;
    }

    /**
     * 确保渠道与酒店的映射关系存在且激活
     */
    private void ensureChannelHotelMapping(Integer tenantId, String hotelCode, String channelCode) {
        Hotel hotel = hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId).orElse(null);
        TenantChannel channel = tenantChannelRepository.findByTenantIdAndChannelCode(tenantId, channelCode);

        if (hotel != null && channel != null) {
            List<ChannelHotelMapping> mappings = channelHotelMappingRepository.findByTenantIdAndChannelCodeAndHotelCode(tenantId, channelCode, hotelCode);
            if (mappings.isEmpty()) {
                ChannelHotelMapping mapping = new ChannelHotelMapping();
                fillChannelHotelMapping(mapping, tenantId, channel, hotel);
                channelHotelMappingRepository.save(mapping);
            } else {
                // 如果已存在则回填主数据并重新激活，避免缺失必填关联字段
                for (ChannelHotelMapping mapping : mappings) {
                    fillChannelHotelMapping(mapping, tenantId, channel, hotel);
                    channelHotelMappingRepository.save(mapping);
                }
            }
        }
    }

    private void fillChannelHotelMapping(ChannelHotelMapping mapping, Integer tenantId, TenantChannel channel, Hotel hotel) {
        mapping.setTenantId(tenantId);
        mapping.setChannelId(channel.getId());
        mapping.setChannelCode(channel.getChannelCode());
        mapping.setChannelName(channel.getChannelName());
        mapping.setHotelId(hotel.getId());
        mapping.setHotelCode(hotel.getHotelCode());
        mapping.setHotelName(hotel.getChineseName());
        mapping.setChannelHotelCode(channel.getChannelCode() + "_" + hotel.getHotelCode());
        mapping.setStatus("active");
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

    /**
     * 获取集团房价码的渠道发布记录
     * 关联PRD文档：.kiro/specs/prd/08-集团管理.md
     */
    public List<ChannelPublishRecord> getPublishedRecordsByRateCode(String rateCode) {
        Integer tenantId = getCurrentTenantId();
        return publishRecordRepository.findByTenantIdAndRateCode(tenantId, rateCode);
    }

    /**
     * 批量保存/更新集团房价码的渠道发布配置
     * 关联PRD文档：.kiro/specs/prd/08-集团管理.md
     */
    @Transactional
    public int saveGroupRateCodePublish(String rateCode, List<Map<String, Object>> configs) {
        Integer tenantId = getCurrentTenantId();
        int count = 0;
        Date now = new Date();

        for (Map<String, Object> config : configs) {
            @SuppressWarnings("unchecked")
            List<String> channels = (List<String>) config.get("channels");
            @SuppressWarnings("unchecked")
            List<String> hotels = (List<String>) config.get("hotels");
            @SuppressWarnings("unchecked")
            List<String> roomTypes = (List<String>) config.get("roomTypes");

            if (channels == null || hotels == null || roomTypes == null) {
                continue;
            }

            for (String channelCode : channels) {
                for (String hotelCode : hotels) {
                    // 1. 确保渠道与酒店映射存在且激活
                    ensureChannelHotelMapping(tenantId, hotelCode, channelCode);

                    // 2. 清理原有发布记录（覆盖式更新房型）
                    publishRecordRepository.deleteByRateCode(tenantId, hotelCode, channelCode, rateCode);

                    // 3. 逐个房型保存
                    for (String roomTypeCode : roomTypes) {
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
                            // 忽略唯一键冲突
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * 取消房价码在特定酒店与渠道的发布
     * 关联PRD文档：.kiro/specs/prd/08-集团管理.md
     */
    @Transactional
    public void cancelGroupRateCodePublish(String rateCode, String hotelCode, String channelCode) {
        Integer tenantId = getCurrentTenantId();
        publishRecordRepository.deleteByRateCode(tenantId, hotelCode, channelCode, rateCode);
    }
}
