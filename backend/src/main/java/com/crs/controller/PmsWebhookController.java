package com.crs.controller;

import com.crs.modules.pms.api.PmsInventoryWebhookRequest;
import com.crs.modules.pms.application.PmsInventoryIngestService;
import com.crs.modules.pms.application.PmsWebhookAuthenticator;
import com.crs.shared.api.ApiException;
import com.crs.util.TraceContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

/** 厂商适配层入口：验证原始报文签名后再转换为内部库存契约。 */
@RestController
@RequestMapping("/webhooks/pms")
public class PmsWebhookController {
    private final ObjectMapper objectMapper;
    private final PmsWebhookAuthenticator authenticator;
    private final PmsInventoryIngestService ingestService;

    public PmsWebhookController(ObjectMapper objectMapper,
                                PmsWebhookAuthenticator authenticator,
                                PmsInventoryIngestService ingestService) {
        this.objectMapper = objectMapper;
        this.authenticator = authenticator;
        this.ingestService = ingestService;
    }

    @PostMapping("/inventory")
    public Map<String, Object> inventory(
            @RequestHeader("X-PMS-Timestamp") String timestamp,
            @RequestHeader("X-PMS-Event-Id") String eventId,
            @RequestHeader("X-PMS-Tenant-Id") Integer tenantId,
            @RequestHeader("X-PMS-Signature") String signature,
            @RequestBody String body) {
        authenticator.verify(timestamp, eventId, tenantId, body, signature);
        PmsInventoryWebhookRequest request;
        try {
            request = objectMapper.readValue(body, PmsInventoryWebhookRequest.class);
        } catch (Exception exception) {
            throw ApiException.badRequest("PMS_PAYLOAD_INVALID", "PMS 报文格式不合法");
        }
        String requestHash = sha256(body);
        var result = ingestService.ingest(tenantId, eventId, requestHash, TraceContext.getTraceId(), request);
        return Map.of("success", true, "duplicate", result.duplicate(), "processedAt", result.processedAt());
    }

    private String sha256(String body) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(body.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("无法计算 PMS 请求摘要", exception);
        }
    }
}
