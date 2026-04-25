-- 更新 rate_plans 表中的 source_group_rate_code 字段
-- 现在 group_rate_codes 表已经有了 code 字段

USE CRS;

-- 更新 source_group_rate_code 字段
UPDATE rate_plans rp
LEFT JOIN group_rate_codes grc ON rp.source_group_rate_code_id = grc.id
SET rp.source_group_rate_code = grc.rate_code
WHERE rp.source_group_rate_code_id IS NOT NULL AND rp.source_group_rate_code IS NULL;

-- 显示更新结果
SELECT 
    'source_group_rate_code 字段更新完成' AS message,
    COUNT(*) AS total_records,
    COUNT(CASE WHEN source_group_rate_code IS NOT NULL THEN 1 END) AS has_source_group_rate_code
FROM rate_plans;
