package com.crs.modules.channel.domain;

/**
 * 渠道厂商适配端口。Real_Time_API 使用 CRS 自有开放接口；需要主动推送的 OTA
 * 必须实现本端口，禁止业务服务直接依赖厂商 SDK。
 */
public interface ChannelAdapter {
    String channelCode();
    DispatchResult publish(String idempotencyKey, String payloadJson);
    DispatchResult unpublish(String idempotencyKey, String payloadJson);

    record DispatchResult(boolean success, boolean retryable, String externalReference, String message) {
    }
}
