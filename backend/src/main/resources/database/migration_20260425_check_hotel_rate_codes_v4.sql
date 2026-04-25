-- 最终查询酒店房价码完整情况

USE CRS;

-- 查询rate_codes表的完整数据
SELECT 'rate_codes表数据:' AS info;
SELECT 
    id, 
    rate_code, 
    name, 
    hotel_id, 
    hotel_code, 
    source_group_rate_code_id,
    source_group_rate_code,
    status
FROM rate_codes
ORDER BY hotel_id, id
LIMIT 30;

-- 查看是否有JJSH001酒店的rate_codes数据
SELECT 'JJSH001酒店的房价码数据:' AS info;
SELECT 
    id, 
    rate_code, 
    name, 
    hotel_id, 
    hotel_code, 
    source_group_rate_code_id,
    source_group_rate_code
FROM rate_codes
WHERE hotel_code = 'JJSH001';

-- 查询所有酒店及其房价码数量
SELECT '各酒店房价码统计:' AS info;
SELECT 
    h.hotel_code,
    h.chinese_name,
    COUNT(rc.id) AS rate_code_count
FROM hotels h
LEFT JOIN rate_codes rc ON h.hotel_code = rc.hotel_code
GROUP BY h.hotel_code, h.chinese_name
ORDER BY h.hotel_code;
