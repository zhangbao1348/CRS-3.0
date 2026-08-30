CREATE TABLE pms_webhook_receipts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id INT NOT NULL,
    event_id VARCHAR(100) NOT NULL,
    request_hash CHAR(64) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    trace_id VARCHAR(64) NULL,
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_pms_webhook_tenant_event UNIQUE (tenant_id, event_id),
    INDEX idx_pms_webhook_processed_at (processed_at),
    INDEX idx_pms_webhook_trace_id (trace_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
