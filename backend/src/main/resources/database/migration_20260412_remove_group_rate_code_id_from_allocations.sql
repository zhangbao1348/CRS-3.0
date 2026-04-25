-- 删除 hotel_rate_code_allocations 表中的 group_rate_code_id 字段
USE CRS;

-- 删除 group_rate_code_id 字段
ALTER TABLE hotel_rate_code_allocations 
DROP COLUMN group_rate_code_id;

-- 删除对应的索引（如果存在）
ALTER TABLE hotel_rate_code_allocations 
DROP INDEX idx_group_rate_code_id;

SELECT 'hotel_rate_code_allocations 表删除 group_rate_code_id 字段完成！' AS result;
