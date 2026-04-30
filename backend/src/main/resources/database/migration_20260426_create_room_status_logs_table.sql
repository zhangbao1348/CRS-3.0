-- 房态操作日志表
CREATE TABLE IF NOT EXISTS room_status_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tenant_id INT NOT NULL,
    hotel_code VARCHAR(50) NOT NULL,
    dimension_type VARCHAR(30) NOT NULL,
    dimension_code VARCHAR(100) NOT NULL DEFAULT '',
    operator_name VARCHAR(100) NOT NULL,
    operation_type VARCHAR(20) NOT NULL COMMENT 'single/batch',
    operation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    detail TEXT COMMENT '操作明细JSON',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tenant_hotel (tenant_id, hotel_code),
    INDEX idx_dimension (dimension_type, dimension_code),
    INDEX idx_operation_time (operation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房态操作日志表';
