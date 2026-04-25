-- 检查group相关表的结构

USE CRS;

SELECT '=== group_rate_codes 表结构 ===' AS info;
DESCRIBE group_rate_codes;

SELECT '=== group_room_types 表结构 ===' AS info;
DESCRIBE group_room_types;

SELECT '=== group_cancellation_policies 表结构 ===' AS info;
DESCRIBE group_cancellation_policies;

SELECT '=== group_guarantee_policies 表结构 ===' AS info;
DESCRIBE group_guarantee_policies;

SELECT '=== group_facilities 表结构 ===' AS info;
DESCRIBE group_facilities;
