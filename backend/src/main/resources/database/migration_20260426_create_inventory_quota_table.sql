-- 房量控制表（5个维度共用）
CREATE TABLE IF NOT EXISTS inventory_quota (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tenant_id INT NOT NULL,
    hotel_code VARCHAR(50) NOT NULL,
    dimension_type VARCHAR(30) NOT NULL COMMENT 'rate/channel/market/channel_room_type/rate_category',
    dimension_code VARCHAR(100) NOT NULL DEFAULT '',
    quota_date DATE NOT NULL,
    quota_limit INT COMMENT '库存限制（NULL表示未设置）',
    sold_count INT NOT NULL DEFAULT 0 COMMENT '已售数量',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_inventory_quota (tenant_id, hotel_code, dimension_type, dimension_code, quota_date),
    INDEX idx_tenant_hotel (tenant_id, hotel_code),
    INDEX idx_dimension (dimension_type, dimension_code),
    INDEX idx_quota_date (quota_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房量控制表';

-- 房量控制操作日志表
CREATE TABLE IF NOT EXISTS inventory_quota_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tenant_id INT NOT NULL,
    hotel_code VARCHAR(50) NOT NULL,
    dimension_type VARCHAR(30) NOT NULL,
    dimension_code VARCHAR(100) NOT NULL DEFAULT '',
    operator_name VARCHAR(100) NOT NULL,
    operation_type VARCHAR(20) NOT NULL,
    operation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    detail TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tenant_hotel (tenant_id, hotel_code),
    INDEX idx_operation_time (operation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房量控制操作日志表';
