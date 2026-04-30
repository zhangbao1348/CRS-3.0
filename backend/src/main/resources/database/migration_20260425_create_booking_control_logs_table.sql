-- 预订控制操作日志表
CREATE TABLE IF NOT EXISTS booking_control_logs (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id INT NOT NULL COMMENT '租户ID',
    hotel_code VARCHAR(50) NOT NULL COMMENT '酒店CODE',
    dimension_type VARCHAR(20) NOT NULL COMMENT '维度类型',
    dimension_code VARCHAR(50) NOT NULL DEFAULT '' COMMENT '维度值',
    operator_name VARCHAR(100) NOT NULL COMMENT '操作人',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型：single/batch',
    operation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    detail TEXT COMMENT '操作明细JSON',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_tenant_hotel (tenant_id, hotel_code),
    INDEX idx_dimension (dimension_type, dimension_code),
    INDEX idx_operation_time (operation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预订控制操作日志表';
