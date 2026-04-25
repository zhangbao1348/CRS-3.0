-- 为hotel_facilities表添加hotel_code字段
-- 日期：2026-04-19

USE CRS;

-- 添加hotel_code字段
ALTER TABLE hotel_facilities 
ADD COLUMN hotel_code VARCHAR(50) COMMENT '酒店编码'
AFTER hotel_id;

-- 更新现有记录的hotel_code，从hotels表中获取
UPDATE hotel_facilities hf
INNER JOIN hotels h ON hf.hotel_id = h.id
SET hf.hotel_code = h.hotel_code
WHERE hf.hotel_code IS NULL;

-- 验证更新
SELECT 
    h.hotel_code AS '酒店编码',
    h.chinese_name AS '酒店名称',
    COUNT(hf.id) AS '设施数量',
    hf.hotel_code AS '设施表酒店编码'
FROM hotels h
LEFT JOIN hotel_facilities hf ON h.id = hf.hotel_id
WHERE h.id <= 10
GROUP BY h.id, h.hotel_code, h.chinese_name, hf.hotel_code;

SELECT 'hotel_code字段添加成功！' AS message;
