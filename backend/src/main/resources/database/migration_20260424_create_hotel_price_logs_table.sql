-- 酒店价格操作日志表
-- 记录价格的新增、修改、删除操作
CREATE TABLE IF NOT EXISTS hotel_price_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tenant_id INT NOT NULL COMMENT '租户ID',
    hotel_code VARCHAR(50) NOT NULL COMMENT '酒店CODE',
    rate_code VARCHAR(50) NOT NULL COMMENT '房价码CODE',
    operator_name VARCHAR(100) NOT NULL COMMENT '操作人姓名',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型：create/update/delete/batch_update/batch_delete',
    operation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    start_date DATE COMMENT '起始日期',
    end_date DATE COMMENT '结束日期',
    detail TEXT COMMENT '操作明细JSON，格式：[{roomTypeCode, roomTypeName, dates:[], oldPrice, newPrice}]',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_tenant_hotel (tenant_id, hotel_code),
    INDEX idx_rate_code (rate_code),
    INDEX idx_operation_time (operation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒店价格操作日志表';
