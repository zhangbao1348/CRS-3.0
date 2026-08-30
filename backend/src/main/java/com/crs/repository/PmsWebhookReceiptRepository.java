package com.crs.repository;

import com.crs.entity.PmsWebhookReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PmsWebhookReceiptRepository extends JpaRepository<PmsWebhookReceipt, Long> {
    Optional<PmsWebhookReceipt> findByTenantIdAndEventId(Integer tenantId, String eventId);
}
