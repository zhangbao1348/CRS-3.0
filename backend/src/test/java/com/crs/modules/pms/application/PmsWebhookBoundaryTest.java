package com.crs.modules.pms.application;

import com.crs.entity.Hotel;
import com.crs.entity.HotelRoomType;
import com.crs.entity.PmsWebhookReceipt;
import com.crs.modules.pms.api.PmsInventoryWebhookRequest;
import com.crs.repository.HotelRepository;
import com.crs.repository.HotelRoomTypeRepository;
import com.crs.repository.PmsInventoryRepository;
import com.crs.repository.PmsWebhookReceiptRepository;
import com.crs.shared.api.ApiException;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PmsWebhookBoundaryTest {
    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    @Test
    void authenticatorFailsClosedAndAcceptsCurrentValidSignature() throws Exception {
        PmsWebhookAuthenticator disabled = new PmsWebhookAuthenticator(false, "");
        ApiException unavailable = assertThrows(ApiException.class,
                () -> disabled.verify("1", "event", 1, "{}", "00"));
        assertEquals("PMS_WEBHOOK_NOT_CONFIGURED", unavailable.getCode());

        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String eventId = "evt-1";
        String body = "{\"hotelCode\":\"H1\"}";
        String payload = timestamp + "." + eventId + ".1." + body;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String signature = HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        new PmsWebhookAuthenticator(true, SECRET).verify(timestamp, eventId, 1, body, signature);
    }

    @Test
    void duplicateEventDoesNotApplyInventoryTwiceAndConflictingPayloadIsRejected() {
        AtomicReference<PmsWebhookReceipt> receiptStore = new AtomicReference<>();
        AtomicInteger inventoryWrites = new AtomicInteger();
        Hotel hotel = new Hotel();
        HotelRoomType roomType = new HotelRoomType();

        PmsWebhookReceiptRepository receipts = proxy(PmsWebhookReceiptRepository.class, (method, args) -> {
            if (method.equals("findByTenantIdAndEventId")) return Optional.ofNullable(receiptStore.get());
            if (method.equals("save")) {
                receiptStore.set((PmsWebhookReceipt) args[0]);
                return args[0];
            }
            return defaultValue(method);
        });
        PmsInventoryRepository inventories = proxy(PmsInventoryRepository.class, (method, args) -> {
            if (method.equals("findByTenantIdAndHotelCodeAndRoomTypeCodeAndInventoryDate")) return Optional.empty();
            if (method.equals("save")) {
                inventoryWrites.incrementAndGet();
                return args[0];
            }
            return defaultValue(method);
        });
        HotelRepository hotels = proxy(HotelRepository.class, (method, args) ->
                method.equals("findByHotelCodeAndTenantId") ? Optional.of(hotel) : defaultValue(method));
        HotelRoomTypeRepository roomTypes = proxy(HotelRoomTypeRepository.class, (method, args) ->
                method.equals("findByTenantIdAndHotelCodeAndRoomTypeCode") ? Optional.of(roomType) : defaultValue(method));

        PmsInventoryIngestService service = new PmsInventoryIngestService(receipts, inventories, hotels, roomTypes);
        PmsInventoryWebhookRequest request = new PmsInventoryWebhookRequest(
                "H1", "KING", new Date(), 10, 7, 1, 0);
        assertFalse(service.ingest(1, "evt-1", "hash-a", "trace-a", request).duplicate());
        assertTrue(service.ingest(1, "evt-1", "hash-a", "trace-b", request).duplicate());
        assertEquals(1, inventoryWrites.get());
        ApiException conflict = assertThrows(ApiException.class,
                () -> service.ingest(1, "evt-1", "hash-b", "trace-c", request));
        assertEquals("PMS_EVENT_PAYLOAD_CONFLICT", conflict.getCode());
    }

    @SuppressWarnings("unchecked")
    private <T> T proxy(Class<T> type, Invocation invocation) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (proxy, method, args) -> invocation.invoke(method.getName(), args));
    }

    private Object defaultValue(String method) {
        if (method.startsWith("find")) return Optional.empty();
        return null;
    }

    @FunctionalInterface
    private interface Invocation {
        Object invoke(String method, Object[] args);
    }
}
