-- 房态管理表
-- 使用 dimension_type + dimension_code 区分7个维度（与 booking_controls 同模式）
CREATE TABLE IF NOT EXISTS room_status (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tenant_id INT NOT NULL,
    hotel_code VARCHAR(50) NOT NULL,
    dimension_type VARCHAR(30) NOT NULL COMMENT 'hotel/room_type/rate/channel/channel_room_type/market/rate_category',
    dimension_code VARCHAR(100) NOT NULL DEFAULT '',
    status_date DATE NOT NULL,
    is_open TINYINT(1) NOT NULL DEFAULT 0 COMMENT '0=关, 1=开',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_room_status (tenant_id, hotel_code, dimension_type, dimension_code, status_date),
    INDEX idx_tenant_hotel (tenant_id, hotel_code),
    INDEX idx_dimension (dimension_type, dimension_code),
    INDEX idx_status_date (status_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房态管理表';
