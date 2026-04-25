-- 酒店价格表
-- 存储酒店每日价格数据，按租户+酒店+房价码+房型+日期维度
CREATE TABLE IF NOT EXISTS hotel_prices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tenant_id INT NOT NULL COMMENT '租户ID',
    hotel_code VARCHAR(50) NOT NULL COMMENT '酒店CODE',
    rate_code VARCHAR(50) NOT NULL COMMENT '房价码CODE',
    room_type_code VARCHAR(50) NOT NULL COMMENT '房型CODE',
    price_date DATE NOT NULL COMMENT '日期',
    price_with_tax DECIMAL(10, 2) COMMENT '含税价格',
    price_without_tax DECIMAL(10, 2) COMMENT '不含税价格',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态：active/inactive',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_price (tenant_id, hotel_code, rate_code, room_type_code, price_date),
    INDEX idx_tenant_hotel (tenant_id, hotel_code),
    INDEX idx_hotel_date (hotel_code, price_date),
    INDEX idx_rate_code (rate_code),
    INDEX idx_room_type (room_type_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒店价格表';
