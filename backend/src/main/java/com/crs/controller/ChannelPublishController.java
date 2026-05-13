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
                    Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId)
                            .filter(h -> h.getTenantId() != null && h.getTenantId().equals(currentTenantId));
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
}
