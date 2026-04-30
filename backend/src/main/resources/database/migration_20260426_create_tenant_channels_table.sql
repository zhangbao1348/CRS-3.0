-- 创建租户可对接渠道表
-- 用于渠道管理 > 渠道列表页面
-- 每个租户/集团维护自己的可对接渠道列表及对接状态

USE CRS;

CREATE TABLE IF NOT EXISTS tenant_channels (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id INT NOT NULL COMMENT '租户ID（等同于集团ID）',
    channel_name VARCHAR(100) NOT NULL COMMENT '渠道名称（如：携程、美团、飞猪）',
    channel_code VARCHAR(50) NOT NULL COMMENT '渠道代码（如：CTRIP、MEITUAN、FLIGGY）',
    connected TINYINT(1) DEFAULT 0 COMMENT '是否已对接：0-未对接，1-已对接',
    logo_url VARCHAR(500) COMMENT '渠道LOGO地址',
    switch_channel VARCHAR(50) COMMENT '通道（如：德比debi、畅联changlian）',
    access_key VARCHAR(200) COMMENT '对接key',
    access_secret VARCHAR(500) COMMENT '对接秘钥',
    sort_order INT DEFAULT 0 COMMENT '排序序号',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_channel_code (channel_code),
    INDEX idx_connected (connected),
    INDEX idx_status (status),
    UNIQUE KEY uk_tenant_channel (tenant_id, channel_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户可对接渠道表';

-- 为 tenant_id=1 插入默认渠道数据（与前端 ChannelList.jsx 一致）
INSERT IGNORE INTO tenant_channels (tenant_id, channel_name, channel_code, connected, logo_url, switch_channel, sort_order, status) VALUES
-- 已连接渠道
(1, '携程', 'CTRIP', 1, '/images/channels/ctrip.webp', 'debi', 1, 'active'),
(1, '飞猪', 'FLIGGY', 1, '/images/channels/feizhu.jpeg', 'debi', 2, 'active'),
(1, '红色加力', 'RED_POWER', 1, '/images/channels/hongsejiali.png', 'changlian', 3, 'active'),
(1, '美团', 'MEITUAN', 1, '/images/channels/meituan.webp', 'debi', 4, 'active'),
-- 可连接渠道
(1, 'Booking.com', 'BOOKING', 0, NULL, NULL, 5, 'active'),
(1, 'Agoda', 'AGODA', 0, NULL, NULL, 6, 'active'),
(1, 'Expedia', 'EXPEDIA', 0, NULL, NULL, 7, 'active'),
(1, 'Hotels.com', 'HOTELS_COM', 0, NULL, NULL, 8, 'active');

SELECT '租户可对接渠道表创建完成！' AS result;
