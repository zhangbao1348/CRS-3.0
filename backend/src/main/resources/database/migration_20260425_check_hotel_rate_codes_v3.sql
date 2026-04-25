-- 详细查询酒店房价码关联情况

USE CRS;

-- 查看hotels表结构
DESCRIBE hotels;

-- 查看rate_codes表结构
DESCRIBE rate_codes;

-- 查询酒店房价码分配与酒店的关联
SELECT '酒店房价码分配详情:' AS info;
SELECT 
    hra.id, 
    hra.hotel_code, 
    hra.rate_code, 
    hra.tenant_id, 
    hra.allocated,
    h.hotel_code AS hotel_code_verify,
    h.id AS hotel_id,
    grc.rate_code AS group_rate_code,
    grc.rate_name
FROM hotel_rate_code_allocations hra
LEFT JOIN hotels h ON hra.hotel_code = h.hotel_code
LEFT JOIN group_rate_codes grc ON hra.rate_code = grc.rate_code;

-- 查询rate_codes表的完整数据
SELECT 'rate_codes表数据:' AS info;
SELECT 
    id, 
    rate_code, 
    rate_name, 
    hotel_id, 
    hotel_code, 
    tenant_code,
    source_group_rate_code_id
FROM rate_codes
LIMIT 30;

-- 查询酒店信息
SELECT '酒店信息:' AS info;
SELECT id, hotel_code, hotel_name FROM hotels LIMIT 10;
