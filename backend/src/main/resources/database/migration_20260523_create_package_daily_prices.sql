CREATE TABLE IF NOT EXISTS package_daily_prices (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tenant_id INT NOT NULL COMMENT '租户ID',
    hotel_code VARCHAR(50) NOT NULL COMMENT '酒店CODE',
    package_code VARCHAR(50) NOT NULL COMMENT '包价CODE',
    price_date DATE NOT NULL COMMENT '价格日期',
    sale_price DECIMAL(10, 2) NOT NULL COMMENT '当日价格',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_package_daily_price (tenant_id, hotel_code, package_code, price_date),
    INDEX idx_package_daily_price_query (tenant_id, hotel_code, package_code, price_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒店包价每日价格表';
