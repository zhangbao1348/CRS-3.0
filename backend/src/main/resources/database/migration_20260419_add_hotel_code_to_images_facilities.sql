-- 为 hotel_images 和 hotel_facilities 表添加 hotel_code 字段
-- 日期：2026-04-19

USE CRS;

-- 1. 为 hotel_images 表添加 hotel_code 字段
ALTER TABLE hotel_images 
ADD COLUMN hotel_code VARCHAR(50) COMMENT '酒店编码' AFTER hotel_id;

-- 更新现有数据的 hotel_code
UPDATE hotel_images hi
JOIN hotels h ON hi.hotel_id = h.id
SET hi.hotel_code = h.hotel_code;

-- 2. 为 hotel_facilities 表添加 hotel_code 字段
ALTER TABLE hotel_facilities 
ADD COLUMN hotel_code VARCHAR(50) COMMENT '酒店编码' AFTER hotel_id;

-- 更新现有数据的 hotel_code
UPDATE hotel_facilities hf
JOIN hotels h ON hf.hotel_id = h.id
SET hf.hotel_code = h.hotel_code;

-- 添加索引
ALTER TABLE hotel_images ADD INDEX idx_hotel_code (hotel_code);
ALTER TABLE hotel_facilities ADD INDEX idx_hotel_code (hotel_code);

-- 验证更新结果
SELECT 'hotel_images 表更新完成！' AS message, 
       COUNT(*) AS total_images,
       COUNT(hotel_code) AS images_with_hotel_code
FROM hotel_images;

SELECT 'hotel_facilities 表更新完成！' AS message, 
       COUNT(*) AS total_facilities,
       COUNT(hotel_code) AS facilities_with_hotel_code
FROM hotel_facilities;
