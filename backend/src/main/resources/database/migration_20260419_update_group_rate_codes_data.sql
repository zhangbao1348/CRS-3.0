-- 更新 group_rate_codes 表中的冗余字段
-- 从相关表中查找并填充 code 字段

USE CRS;

-- 1. 更新 market_code 字段
UPDATE group_rate_codes grc
LEFT JOIN market_codes mc ON grc.market_code_id = mc.id
SET grc.market_code = mc.code
WHERE grc.market_code_id IS NOT NULL AND grc.market_code IS NULL;

-- 2. 更新 source_code 字段
UPDATE group_rate_codes grc
LEFT JOIN source_codes sc ON grc.source_code_id = sc.id
SET grc.source_code = sc.code
WHERE grc.source_code_id IS NOT NULL AND grc.source_code IS NULL;

-- 3. 更新 tenant_code 字段（如果有）
-- 假设 tenant 表有 code 字段，这里先跳过

-- 显示更新结果
SELECT 
    'group_rate_codes 表更新完成' AS message,
    COUNT(*) AS total_records,
    COUNT(CASE WHEN market_code IS NOT NULL THEN 1 END) AS has_market_code,
    COUNT(CASE WHEN source_code IS NOT NULL THEN 1 END) AS has_source_code
FROM group_rate_codes;
