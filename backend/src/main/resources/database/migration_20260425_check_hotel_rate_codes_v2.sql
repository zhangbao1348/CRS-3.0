-- 查询酒店房价码分配情况

USE CRS;

-- 统计各表的数据量
SELECT '数据统计:' AS info;
SELECT 'group_rate_codes' AS table_name, COUNT(*) AS count FROM group_rate_codes
UNION ALL
SELECT 'hotel_rate_code_allocations', COUNT(*) FROM hotel_rate_code_allocations
UNION ALL
SELECT 'rate_codes', COUNT(*) FROM rate_codes
UNION ALL
SELECT 'hotels', COUNT(*) FROM hotels;

-- 查询集团房价码表
SELECT '集团房价码数据:' AS info;
SELECT id, rate_code, rate_name, tenant_code, group_id, status 
FROM group_rate_codes LIMIT 20;

-- 查询酒店房价码分配表
SELECT '酒店房价码分配数据:' AS info;
SELECT id, hotel_code, rate_code, tenant_id, allocated 
FROM hotel_rate_code_allocations LIMIT 20;

-- 查询酒店列表
SELECT '酒店列表:' AS info;
SELECT id, hotel_code, name, tenant_code FROM hotels LIMIT 10;

-- 查询rate_codes表
SELECT '酒店房价码数据:' AS info;
SELECT id, rate_code, rate_name, hotel_id, hotel_code, tenant_code 
FROM rate_codes LIMIT 20;
