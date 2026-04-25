-- 更新 rate_plans 表中的冗余字段
-- 从相关表中查找并填充 code 字段

USE CRS;

-- 1. 更新 market_code 字段
UPDATE rate_plans rp
LEFT JOIN market_codes mc ON rp.market_code_id = mc.id
SET rp.market_code = mc.code
WHERE rp.market_code_id IS NOT NULL AND rp.market_code IS NULL;

-- 2. 更新 source_code 字段
UPDATE rate_plans rp
LEFT JOIN source_codes sc ON rp.source_code_id = sc.id
SET rp.source_code = sc.code
WHERE rp.source_code_id IS NOT NULL AND rp.source_code IS NULL;

-- 3. 更新 source_group_rate_code 字段
UPDATE rate_plans rp
LEFT JOIN group_rate_codes grc ON rp.source_group_rate_code_id = grc.id
SET rp.source_group_rate_code = grc.rate_code
WHERE rp.source_group_rate_code_id IS NOT NULL AND rp.source_group_rate_code IS NULL;

-- 4. 更新 parent_rate_code 字段（如果有）
UPDATE rate_plans rp
LEFT JOIN rate_codes rc ON rp.parent_rate_code_id = rc.id
SET rp.parent_rate_code = rc.rate_code
WHERE rp.parent_rate_code_id IS NOT NULL AND rp.parent_rate_code IS NULL;

-- 5. 更新 hotel_code 字段（从 hotels 表）
UPDATE rate_plans rp
LEFT JOIN hotels h ON rp.hotel_id = h.id
SET rp.hotel_code = h.hotel_code
WHERE rp.hotel_id IS NOT NULL AND rp.hotel_code IS NULL;

-- 显示更新结果
SELECT 
    'rate_plans 表更新完成' AS message,
    COUNT(*) AS total_records,
    COUNT(CASE WHEN market_code IS NOT NULL THEN 1 END) AS has_market_code,
    COUNT(CASE WHEN source_code IS NOT NULL THEN 1 END) AS has_source_code,
    COUNT(CASE WHEN source_group_rate_code IS NOT NULL THEN 1 END) AS has_source_group_rate_code,
    COUNT(CASE WHEN hotel_code IS NOT NULL THEN 1 END) AS has_hotel_code
FROM rate_plans;
