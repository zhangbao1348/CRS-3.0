-- 在 rate_codes 表中添加 hotel_id 字段
USE CRS;

-- 添加 hotel_id 字段
ALTER TABLE rate_codes 
ADD COLUMN hotel_id INT NOT NULL AFTER id;

-- 添加索引
ALTER TABLE rate_codes 
ADD INDEX idx_hotel_id (hotel_id);

-- 显示完成信息
SELECT 'rate_codes 表添加 hotel_id 字段完成！' AS result;
