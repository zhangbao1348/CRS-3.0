package com.crs.entity;

import jakarta.persistence.*;
import java.util.Date;

/** PMS 入站事件收件箱；唯一键保证同租户事件只应用一次。 */
@Entity
@Table(name = "pms_webhook_receipts")
public class PmsWebhookReceipt {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;
    @Column(name = "event_id", nullable = false, length = 100)
    private String eventId;
    @Column(name = "request_hash", nullable = false, length = 64)
    private String requestHash;
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    @Column(name = "trace_id", length = 64)
    private String traceId;
    @Column(name = "processed_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date processedAt = new Date();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getRequestHash() { return requestHash; }
    public void setRequestHash(String requestHash) { this.requestHash = requestHash; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public Date getProcessedAt() { return processedAt; }
    public void setProcessedAt(Date processedAt) { this.processedAt = processedAt; }
}
