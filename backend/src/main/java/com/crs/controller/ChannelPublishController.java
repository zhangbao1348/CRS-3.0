package com.crs.controller;

import com.crs.entity.ChannelPublishRecord;
import com.crs.service.ChannelPublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 渠道发布控制器
 */
@RestController
@RequestMapping("/api/channel-publish")
public class ChannelPublishController {

    @Autowired
    private ChannelPublishService channelPublishService;

    /**
     * 获取酒店的房价码及关联房型列表
     */
    @GetMapping("/rate-codes")
    public ResponseEntity<List<Map<String, Object>>> getRateCodesWithRoomTypes(
            @RequestParam Integer hotelId) {
        try {
            List<Map<String, Object>> result = channelPublishService.getRateCodesWithRoomTypes(hotelId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 获取已发布记录
     */
    @GetMapping("/records")
    public ResponseEntity<List<ChannelPublishRecord>> getPublishedRecords(
            @RequestParam(defaultValue = "1") Integer tenantId,
            @RequestParam String hotelCode,
            @RequestParam String channelCode) {
        try {
            List<ChannelPublishRecord> records = channelPublishService.getPublishedRecords(tenantId, hotelCode, channelCode);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 批量发布
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchPublish(@RequestBody Map<String, Object> request) {
        try {
            Integer tenantId = (Integer) request.getOrDefault("tenantId", 1);
            String hotelCode = (String) request.get("hotelCode");
            String channelCode = (String) request.get("channelCode");
            // rateCodeRoomTypesMap: { "BAR": ["ST1","ST3"], "BAR_B1": ["ST2"] }
            Map<String, List<String>> rateCodeRoomTypesMap = (Map<String, List<String>>) request.get("rateCodeRoomTypesMap");

            int count = channelPublishService.batchPublish(tenantId, hotelCode, channelCode, rateCodeRoomTypesMap);
            return ResponseEntity.ok(Map.of("success", true, "count", count));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
