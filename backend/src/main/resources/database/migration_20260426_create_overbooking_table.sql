-- 超预订管理表（酒店级和房型级共用）
CREATE TABLE IF NOT EXISTS overbooking (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tenant_id INT NOT NULL,
    hotel_code VARCHAR(50) NOT NULL,
    dimension_type VARCHAR(20) NOT NULL COMMENT 'hotel/room_type',
    dimension_code VARCHAR(50) NOT NULL DEFAULT '' COMMENT '酒店级为空，房型级为房型CODE',
    overbook_date DATE NOT NULL,
    overbook_count INT NOT NULL DEFAULT 0 COMMENT '超预订数量',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_overbooking (tenant_id, hotel_code, dimension_type, dimension_code, overbook_date),
    INDEX idx_tenant_hotel (tenant_id, hotel_code),
    INDEX idx_overbook_date (overbook_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='超预订管理表';
