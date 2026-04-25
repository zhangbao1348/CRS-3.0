-- 最终验证

USE CRS;

SELECT '====================================' AS info;
SELECT '         最终验证报告         ' AS info;
SELECT '====================================' AS info;

-- 检查租户
SELECT '=== 1. 租户信息 ===' AS info;
SELECT id, tenant_code, tenant_name, status FROM tenants ORDER BY id;

-- 检查关键表
SELECT '=== 2. 关键表数据量 ===' AS info;

SELECT 'hotels' AS table_name, COUNT(*) AS row_count FROM hotels
UNION ALL
SELECT 'group_rate_codes', COUNT(*) FROM group_rate_codes
UNION ALL
SELECT 'group_room_types', COUNT(*) FROM group_room_types
UNION ALL
SELECT 'market_code_categories', COUNT(*) FROM market_code_categories
UNION ALL
SELECT 'market_codes', COUNT(*) FROM market_codes
UNION ALL
SELECT 'room_type_categories', COUNT(*) FROM room_type_categories
UNION ALL
SELECT 'rate_types', COUNT(*) FROM rate_types
UNION ALL
SELECT 'source_codes', COUNT(*) FROM source_codes
UNION ALL
SELECT 'cancellation_policies', COUNT(*) FROM cancellation_policies
UNION ALL
SELECT 'guarantee_policies', COUNT(*) FROM guarantee_policies
UNION ALL
SELECT 'packages', COUNT(*) FROM packages
UNION ALL
SELECT 'users', COUNT(*) FROM users
UNION ALL
SELECT 'roles', COUNT(*) FROM roles;

SELECT '====================================' AS info;
SELECT '      验证完成！系统已清理完毕      ' AS info;
SELECT '====================================' AS info;
