package com.crs.modules.pms.application;

import com.crs.entity.PmsInventory;
import com.crs.entity.PmsWebhookReceipt;
import com.crs.modules.pms.api.PmsInventoryWebhookRequest;
import com.crs.repository.HotelRepository;
import com.crs.repository.HotelRoomTypeRepository;
import com.crs.repository.PmsInventoryRepository;
import com.crs.repository.PmsWebhookReceiptRepository;
import com.crs.shared.api.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/** 在一个事务中完成主数据校验、绝对库存更新和幂等收件记录。 */
@Service
public class PmsInventoryIngestService {
    private final PmsWebhookReceiptRepository receiptRepository;
    private final PmsInventoryRepository inventoryRepository;
    private final HotelRepository hotelRepository;
    private final HotelRoomTypeRepository roomTypeRepository;

    public PmsInventoryIngestService(PmsWebhookReceiptRepository receiptRepository,
                                     PmsInventoryRepository inventoryRepository,
                                     HotelRepository hotelRepository,
                                     HotelRoomTypeRepository roomTypeRepository) {
        this.receiptRepository = receiptRepository;
        this.inventoryRepository = inventoryRepository;
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    @Transactional
    public IngestResult ingest(Integer tenantId, String eventId, String requestHash,
                               String traceId, PmsInventoryWebhookRequest request) {
        validateEnvelope(tenantId, eventId, requestHash);
        var previous = receiptRepository.findByTenantIdAndEventId(tenantId, eventId);
        if (previous.isPresent()) {
            if (!previous.get().getRequestHash().equals(requestHash)) {
                throw ApiException.badRequest("PMS_EVENT_PAYLOAD_CONFLICT", "相同 PMS 事件号对应了不同报文");
            }
            return new IngestResult(true, previous.get().getProcessedAt());
        }
        validateInventory(tenantId, request);
        PmsInventory inventory = inventoryRepository
                .findByTenantIdAndHotelCodeAndRoomTypeCodeAndInventoryDate(
                        tenantId, request.hotelCode(), request.roomTypeCode(), request.inventoryDate())
                .orElseGet(PmsInventory::new);
        inventory.setTenantId(tenantId);
        inventory.setHotelCode(request.hotelCode());
        inventory.setRoomTypeCode(request.roomTypeCode());
        inventory.setInventoryDate(request.inventoryDate());
        inventory.setPhysicalRooms(request.physicalRooms());
        inventory.setAvailableRooms(request.availableRooms());
        inventory.setMaintenanceRooms(request.maintenanceRooms());
        inventory.setOverbookCount(request.overbookCount());
        inventoryRepository.save(inventory);

        PmsWebhookReceipt receipt = new PmsWebhookReceipt();
        receipt.setTenantId(tenantId);
        receipt.setEventId(eventId);
        receipt.setRequestHash(requestHash);
        receipt.setEventType("inventory.snapshot");
        receipt.setStatus("processed");
        receipt.setTraceId(traceId);
        receipt.setProcessedAt(new Date());
        receiptRepository.save(receipt);
        return new IngestResult(false, receipt.getProcessedAt());
    }

    private void validateEnvelope(Integer tenantId, String eventId, String requestHash) {
        if (tenantId == null || eventId == null || eventId.isBlank() || requestHash == null) {
            throw ApiException.badRequest("PMS_ENVELOPE_INVALID", "PMS 事件头不完整");
        }
    }

    private void validateInventory(Integer tenantId, PmsInventoryWebhookRequest request) {
        if (request == null || request.hotelCode() == null || request.roomTypeCode() == null
                || request.inventoryDate() == null) {
            throw ApiException.badRequest("PMS_INVENTORY_INVALID", "PMS 库存主键字段不完整");
        }
        hotelRepository.findByHotelCodeAndTenantId(request.hotelCode(), tenantId)
                .orElseThrow(() -> ApiException.notFound("PMS_HOTEL_NOT_FOUND", "PMS 酒店不存在"));
        roomTypeRepository.findByTenantIdAndHotelCodeAndRoomTypeCode(
                        tenantId, request.hotelCode(), request.roomTypeCode())
                .orElseThrow(() -> ApiException.notFound("PMS_ROOM_TYPE_NOT_FOUND", "PMS 房型不存在"));
        int physical = nonNegative(request.physicalRooms(), "physicalRooms");
        int available = nonNegative(request.availableRooms(), "availableRooms");
        int maintenance = nonNegative(request.maintenanceRooms(), "maintenanceRooms");
        int overbook = nonNegative(request.overbookCount(), "overbookCount");
        if (available + maintenance > physical + overbook) {
            throw ApiException.badRequest("PMS_INVENTORY_CONFLICT", "可售与维修房量超过物理房量及超订上限");
        }
    }

    private int nonNegative(Integer value, String field) {
        if (value == null || value < 0) {
            throw ApiException.badRequest("PMS_INVENTORY_INVALID", field + " 不能为空或为负数");
        }
        return value;
    }

    public record IngestResult(boolean duplicate, Date processedAt) {
    }
}
