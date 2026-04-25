-- 查询酒店ID与rate_codes的匹配问题

USE CRS;

-- 查询所有酒店的ID和CODE
SELECT '酒店ID和CODE:' AS info;
SELECT id, hotel_code, chinese_name FROM hotels ORDER BY id;

-- 查询rate_codes表的hotel_id和对应的酒店
SELECT 'rate_codes表的hotel_id:' AS info;
SELECT DISTINCT hotel_id FROM rate_codes ORDER BY hotel_id;
