-- 预订控制表
-- 统一存储所有维度（酒店/房价码/渠道/房价大类/市场码）的预订控制规则
CREATE TABLE IF NOT EXISTS booking_controls (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id INT NOT NULL COMMENT '租户ID',
    hotel_code VARCHAR(50) NOT NULL COMMENT '酒店CODE',
    dimension_type VARCHAR(20) NOT NULL COMMENT '维度类型：hotel/rate/channel/rate_category/market',
    dimension_code VARCHAR(50) NOT NULL DEFAULT '' COMMENT '维度值：酒店维度为空，其他为对应CODE',
    control_date DATE NOT NULL COMMENT '日期',
    cancellation_rule VARCHAR(20) DEFAULT 'free' COMMENT '取消规则：free/timed/non_refundable',
    advance_booking_days INT DEFAULT 0 COMMENT '提前预订天数',
    min_stay INT DEFAULT 1 COMMENT '最小连住天数',
    max_stay INT DEFAULT 30 COMMENT '最大连住天数',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_booking_control (tenant_id, hotel_code, dimension_type, dimension_code, control_date),
    INDEX idx_tenant_hotel (tenant_id, hotel_code),
    INDEX idx_dimension (dimension_type, dimension_code),
    INDEX idx_control_date (control_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预订控制表';
