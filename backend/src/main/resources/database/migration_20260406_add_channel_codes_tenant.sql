-- 渠道码管理表迁移脚本
-- 创建 channel_codes 表并添加租户隔离支持
-- 同时为 source_codes 表添加 tenant_id 字段

USE CRS;

-- 1. 创建渠道码表（带租户隔离）
CREATE TABLE IF NOT EXISTS channel_codes (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id INT COMMENT '租户ID（等同于集团ID）',
    code VARCHAR(50) NOT NULL COMMENT '渠道码',
    name VARCHAR(100) NOT NULL COMMENT '渠道名称',
    description TEXT COMMENT '描述',
    parent_id INT COMMENT '父节点ID',
    level INT DEFAULT 1 COMMENT '层级',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_code (code),
    INDEX idx_status (status),
    UNIQUE KEY uk_tenant_code (tenant_id, code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='渠道码表';

-- 2. 为 source_codes 表添加 tenant_id 字段
ALTER TABLE source_codes 
ADD COLUMN IF NOT EXISTS tenant_id INT COMMENT '租户ID（等同于集团ID）' AFTER id,
ADD INDEX IF NOT EXISTS idx_tenant_id (tenant_id),
DROP INDEX IF EXISTS code,
ADD UNIQUE KEY IF NOT EXISTS uk_tenant_code (tenant_id, code);

-- 3. 为 tenant_id=1 插入默认渠道码数据
INSERT IGNORE INTO channel_codes (tenant_id, code, name, description, parent_id, level, status) VALUES
-- 一级节点
(1, 'ONLINE', '在线渠道', '在线销售渠道', NULL, 1, 'active'),
(1, 'OFFLINE', '线下渠道', '线下销售渠道', NULL, 1, 'active'),
-- 二级节点
(1, 'OTA', 'OTA渠道', '在线旅行社渠道', 1, 2, 'active'),
(1, 'DIRECT', '直销渠道', '直接销售渠道', 1, 2, 'active'),
(1, 'TRAVEL', '旅行社', '旅行社渠道', 2, 2, 'active'),
(1, 'CORP', '企业协议', '企业协议渠道', 2, 2, 'active'),
-- 三级节点
(1, 'CTRIP', '携程', '携程旅行网', 3, 3, 'active'),
(1, 'MEITUAN', '美团', '美团酒店', 3, 3, 'active'),
(1, 'FLIGGY', '飞猪', '飞猪旅行', 3, 3, 'active'),
(1, 'WEBSITE', '官网', '官方网站', 4, 3, 'active'),
(1, 'APP', 'APP', '手机应用', 4, 3, 'active'),
(1, 'WXMINI', '微信小程序', '微信小程序', 4, 3, 'active');

-- 显示完成信息
SELECT '渠道码表迁移完成！' AS result;
