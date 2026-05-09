-- Migration: 删除 rate_plans 和 group_rate_codes 中多余的关联 ID 列，仅保留 CODE 列
-- 为 rate_plans 添加 tenant_id 列
-- Date: 2026-05-07

-- ===================== rate_plans 表 =====================

-- 1. 添加 tenant_id 列
ALTER TABLE rate_plans ADD COLUMN tenant_id INT NULL;

-- 2. 根据 hotel_code 回填 tenant_id（从 hotels 表获取）
UPDATE rate_plans rp
    INNER JOIN hotels h ON rp.hotel_code = h.hotel_code
SET rp.tenant_id = h.tenant_id
WHERE rp.tenant_id IS NULL AND rp.hotel_code IS NOT NULL;

-- 3. 对于没有 hotel_code 的记录，根据 hotel_id 回填
UPDATE rate_plans rp
    INNER JOIN hotels h ON rp.hotel_id = h.id
SET rp.tenant_id = h.tenant_id
WHERE rp.tenant_id IS NULL;

-- 4. 删除多余的关联 ID 列
ALTER TABLE rate_plans DROP COLUMN source_group_rate_code_id;
ALTER TABLE rate_plans DROP COLUMN market_code_id;
ALTER TABLE rate_plans DROP COLUMN source_code_id;
ALTER TABLE rate_plans DROP COLUMN parent_rate_code_id;

-- ===================== group_rate_codes 表 =====================

-- 5. 删除多余的关联 ID 列
ALTER TABLE group_rate_codes DROP COLUMN market_code_id;
ALTER TABLE group_rate_codes DROP COLUMN source_code_id;
ALTER TABLE group_rate_codes DROP COLUMN parent_rate_code_id;
