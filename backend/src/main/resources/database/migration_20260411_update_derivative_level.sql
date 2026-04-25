-- 更新现有集团房价码的衍生层级和类型
-- 执行时间：2026-04-11

USE crs;

-- 更新前20条为基础房价码
UPDATE group_rate_codes 
SET derivative_level = 'basic', rate_type = 'basic'
WHERE id <= 20;

-- 更新21-30条为一级衍生房价码（父级为基础房价码）
UPDATE group_rate_codes 
SET derivative_level = 'level1', rate_type = 'derivative', parent_rate_code_id = 1
WHERE id BETWEEN 21 AND 30;

-- 更新31-40条为二级衍生房价码（父级为一级衍生房价码）
UPDATE group_rate_codes 
SET derivative_level = 'level2', rate_type = 'derivative', parent_rate_code_id = 21
WHERE id BETWEEN 31 AND 40;

-- 验证更新结果
SELECT id, rate_code, rate_name, rate_type, derivative_level, parent_rate_code_id
FROM group_rate_codes
ORDER BY id;
