package com.crs.modules.pms.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/** 厂商无关的 PMS 绝对库存快照契约。 */
public record PmsInventoryWebhookRequest(
        String hotelCode,
        String roomTypeCode,
        @JsonFormat(pattern = "yyyy-MM-dd") Date inventoryDate,
        Integer physicalRooms,
        Integer availableRooms,
        Integer maintenanceRooms,
        Integer overbookCount) {
}
