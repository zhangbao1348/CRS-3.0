-- 查看集团表、酒店表结构和关系

USE CRS;

-- 查看是否有groups表
SELECT '=== 检查表是否存在 ===' AS info;
SHOW TABLES LIKE 'groups';
SHOW TABLES LIKE '%group%';

-- 查看hotels表结构
SELECT '=== hotels表结构 ===' AS info;
DESCRIBE hotels;

-- 查看group_rate_codes表结构
SELECT '=== group_rate_codes表结构 ===' AS info;
DESCRIBE group_rate_codes;

-- 查看group_room_types表结构
SELECT '=== group_room_types表结构 ===' AS info;
DESCRIBE group_room_types;

-- 查看当前数据情况
SELECT '=== hotels表租户分布 ===' AS info;
SELECT tenant_id, COUNT(*) AS hotel_count FROM hotels GROUP BY tenant_id ORDER BY tenant_id;

SELECT '=== group_rate_codes表是否有租户相关字段 ===' AS info;
SHOW CREATE TABLE group_rate_codes\G
