-- 修改 hotel_rate_code_allocations 表结构，使用业务代码
USE CRS;

-- 删除旧表
DROP TABLE IF EXISTS hotel_rate_code_allocations;

-- 创建新表
CREATE TABLE hotel_rate_code_allocations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    hotel_code VARCHAR(50) NOT NULL,
    rate_code VARCHAR(50) NOT NULL,
    group_rate_code_id INT NOT NULL,
    allocated BIT(1) NOT NULL DEFAULT 0,
    basic_info_editable BIT(1) NOT NULL DEFAULT 0,
    price_info_editable BIT(1) NOT NULL DEFAULT 0,
    booking_limit_editable BIT(1) NOT NULL DEFAULT 0,
    guarantee_rule_editable BIT(1) NOT NULL DEFAULT 0,
    promotion_editable BIT(1) NOT NULL DEFAULT 0,
    INDEX idx_hotel_code (hotel_code),
    INDEX idx_rate_code (rate_code),
    INDEX idx_group_rate_code_id (group_rate_code_id),
    UNIQUE KEY uk_hotel_rate_code (hotel_code, rate_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒店房价码分配表';

SELECT 'hotel_rate_code_allocations 表结构修改完成！' AS result;
