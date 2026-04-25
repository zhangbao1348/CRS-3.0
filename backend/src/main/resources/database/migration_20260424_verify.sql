-- 验证删除结果

USE CRS;

SELECT '=== 验证删除结果 ===' AS info;

-- 检查剩余租户
SELECT '=== 剩余租户 ===' AS section;
SELECT id, tenant_code, tenant_name, status FROM tenants ORDER BY id;

-- 检查一些关键表的数据量
SELECT '=== 关键表数据量 ===' AS section;

SELECT 'hotels' AS table_name, COUNT(*) AS row_count FROM hotels
UNION ALL
SELECT 'group_rate_codes' AS table_name, COUNT(*) AS row_count FROM group_rate_codes
UNION ALL
SELECT 'group_room_types' AS table_name, COUNT(*) AS row_count FROM group_room_types
UNION ALL
SELECT 'users' AS table_name, COUNT(*) AS row_count FROM users
UNION ALL
SELECT 'roles' AS table_name, COUNT(*) AS row_count FROM roles;

SELECT '=== 验证完成！仅保留租户1 ===' AS final_result;
