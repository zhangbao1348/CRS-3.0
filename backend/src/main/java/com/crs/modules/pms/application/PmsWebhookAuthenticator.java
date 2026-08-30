package com.crs.modules.pms.application;

import com.crs.shared.api.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

/** 校验 PMS Webhook 时效与 HMAC-SHA256 签名；未配置时安全拒绝。 */
@Component
public class PmsWebhookAuthenticator {
    private static final long MAX_CLOCK_SKEW_SECONDS = 300;
    private final boolean enabled;
    private final String secret;

    public PmsWebhookAuthenticator(
            @Value("${pms.webhook.enabled:false}") boolean enabled,
            @Value("${pms.webhook.secret:}") String secret) {
        this.enabled = enabled;
        this.secret = secret;
    }

    public void verify(String timestamp, String eventId, Integer tenantId, String body, String signature) {
        if (!enabled || secret == null || secret.length() < 32) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE,
                    "PMS_WEBHOOK_NOT_CONFIGURED", "PMS 入站接口尚未配置");
        }
        long epoch;
        try {
            epoch = Long.parseLong(timestamp);
        } catch (RuntimeException exception) {
            throw ApiException.badRequest("PMS_TIMESTAMP_INVALID", "PMS 时间戳不合法");
        }
        if (Math.abs(Instant.now().getEpochSecond() - epoch) > MAX_CLOCK_SKEW_SECONDS) {
            throw ApiException.forbidden("PMS_TIMESTAMP_EXPIRED", "PMS 请求已过期");
        }
        String payload = timestamp + "." + eventId + "." + tenantId + "." + body;
        byte[] expected = hmac(payload);
        byte[] actual;
        try {
            actual = HexFormat.of().parseHex(signature == null ? "" : signature);
        } catch (IllegalArgumentException exception) {
            throw ApiException.forbidden("PMS_SIGNATURE_INVALID", "PMS 签名无效");
        }
        if (!MessageDigest.isEqual(expected, actual)) {
            throw ApiException.forbidden("PMS_SIGNATURE_INVALID", "PMS 签名无效");
        }
    }

    private byte[] hmac(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new IllegalStateException("无法计算 PMS HMAC", exception);
        }
    }
}
