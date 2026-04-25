-- 在 rate_codes 表中添加 source_group_rate_code_id 字段
USE CRS;

-- 添加 source_group_rate_code_id 字段
ALTER TABLE rate_codes 
ADD COLUMN source_group_rate_code_id INT AFTER hotel_id;

-- 添加索引
ALTER TABLE rate_codes 
ADD INDEX idx_source_group_rate_code_id (source_group_rate_code_id);

-- 显示完成信息
SELECT 'rate_codes 表添加 source_group_rate_code_id 字段完成！' AS result;
