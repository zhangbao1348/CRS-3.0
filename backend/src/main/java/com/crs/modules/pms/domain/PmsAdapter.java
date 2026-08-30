package com.crs.modules.pms.domain;

/**
 * CRS 到 PMS 的厂商无关端口。具体 Qtels 或其他厂商实现必须保留幂等键，
 * 未配置适配器时调用方应保持 pending/failed，不得伪造成功。
 */
public interface PmsAdapter {
    DispatchResult pushRatePlan(String idempotencyKey, String payloadJson);
    DispatchResult createReservation(String idempotencyKey, String payloadJson);
    DispatchResult recordPayment(String idempotencyKey, String payloadJson);
    DispatchResult cancelReservation(String idempotencyKey, String payloadJson);

    record DispatchResult(boolean success, boolean retryable, String pmsReference, String message) {
    }
}
