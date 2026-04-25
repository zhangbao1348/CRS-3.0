-- 为担保政策和取消政策表添加tenant_id字段
-- 执行时间: 2026-04-11

USE CRS;

-- ============================================
-- 1. guarantee_policies 表
-- ============================================

-- 先删除原有的唯一索引（忽略错误）
ALTER TABLE guarantee_policies DROP INDEX code;

-- 添加 tenant_id 字段
ALTER TABLE guarantee_policies ADD COLUMN tenant_id INT AFTER group_id;

-- 添加索引
CREATE INDEX idx_guarantee_tenant_id ON guarantee_policies(tenant_id);

-- 创建新的复合唯一索引 (tenant_id, code)
ALTER TABLE guarantee_policies ADD UNIQUE KEY uk_guarantee_tenant_code (tenant_id, code);

-- ============================================
-- 2. cancellation_policies 表
-- ============================================

-- 先删除原有的唯一索引（忽略错误）
ALTER TABLE cancellation_policies DROP INDEX code;

-- 添加 tenant_id 字段
ALTER TABLE cancellation_policies ADD COLUMN tenant_id INT AFTER group_id;

-- 添加索引
CREATE INDEX idx_cancellation_tenant_id ON cancellation_policies(tenant_id);

-- 创建新的复合唯一索引 (tenant_id, code)
ALTER TABLE cancellation_policies ADD UNIQUE KEY uk_cancellation_tenant_code (tenant_id, code);
