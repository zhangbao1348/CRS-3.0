package com.crs.controller;

import com.crs.entity.ChannelPublishRecord;
import com.crs.entity.Hotel;
import com.crs.repository.HotelRepository;
import com.crs.service.ChannelPublishService;
import com.crs.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 渠道发布控制器
 */
@RestController
@RequestMapping("/api/channel-publish")
public class ChannelPublishController {

    @Autowired
    private ChannelPublishService channelPublishService;

    @Autowired
    private HotelRepository hotelRepository;

    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    /**
     * 获取酒店的房价码及关联房型列表
     */
    @GetMapping("/rate-codes")
    public ResponseEntity<List<Map<String, Object>>> getRateCodesWithRoomTypes(
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(required = false) String hotelCode) {
        try {
            Integer currentTenantId = getCurrentTenantId();
            String resolvedHotelCode = hotelCode;
            if (resolvedHotelCode == null || resolvedHotelCode.trim().isEmpty()) {
                if (hotelId != null) {
                    Optional<Hotel> hotelOpt = hotelRepository.findByIdAndTenantId(hotelId, currentTenantId);
                    if (hotelOpt.isPresent()) {
                        resolvedHotelCode = hotelOpt.get().getHotelCode();
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                    }
                } else {
                    return ResponseEntity.badRequest().body(null);
                }
            } else {
                Optional<Hotel> hotelOpt = hotelRepository.findByHotelCodeAndTenantId(resolvedHotelCode, currentTenantId);
                if (hotelOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            }
            List<Map<String, Object>> result = channelPublishService.getRateCodesWithRoomTypes(resolvedHotelCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 获取已发布记录
     */
    @GetMapping("/records")
    public ResponseEntity<List<ChannelPublishRecord>> getPublishedRecords(
            @RequestParam String hotelCode,
            @RequestParam String channelCode) {
        try {
            List<ChannelPublishRecord> records = channelPublishService.getPublishedRecords(hotelCode, channelCode);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 批量发布
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchPublish(@RequestBody Map<String, Object> request) {
        try {
            String hotelCode = (String) request.get("hotelCode");
            String channelCode = (String) request.get("channelCode");
            // rateCodeRoomTypesMap: { "BAR": ["ST1","ST3"], "BAR_B1": ["ST2"] }
            @SuppressWarnings("unchecked")
            Map<String, List<String>> rateCodeRoomTypesMap = (Map<String, List<String>>) request.get("rateCodeRoomTypesMap");

            int count = channelPublishService.batchPublish(hotelCode, channelCode, rateCodeRoomTypesMap);
            return ResponseEntity.ok(Map.of("success", true, "count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 获取指定集团房价码的已发布渠道记录
     * 关联PRD文档：.kiro/specs/prd/08-集团管理.md
     */
    @GetMapping("/group-rate-code/records")
    public ResponseEntity<List<ChannelPublishRecord>> getPublishedRecordsByRateCode(@RequestParam String rateCode) {
        try {
            List<ChannelPublishRecord> records = channelPublishService.getPublishedRecordsByRateCode(rateCode);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 批量保存/更新集团房价码渠道发布配置
     * 关联PRD文档：.kiro/specs/prd/08-集团管理.md
     */
    @PostMapping("/group-rate-code/save")
    public ResponseEntity<Map<String, Object>> saveGroupRateCodePublish(@RequestBody Map<String, Object> request) {
        try {
            String rateCode = (String) request.get("rateCode");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> configs = (List<Map<String, Object>>) request.get("configs");
            int count = channelPublishService.saveGroupRateCodePublish(rateCode, configs);
            return ResponseEntity.ok(Map.of("success", true, "count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 取消房价码在特定酒店与渠道的发布
     * 关联PRD文档：.kiro/specs/prd/08-集团管理.md
     */
    @PostMapping("/group-rate-code/cancel")
    public ResponseEntity<Map<String, Object>> cancelGroupRateCodePublish(@RequestBody Map<String, String> request) {
        try {
            String rateCode = request.get("rateCode");
            String hotelCode = request.get("hotelCode");
            String channelCode = request.get("channelCode");
            channelPublishService.cancelGroupRateCodePublish(rateCode, hotelCode, channelCode);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
