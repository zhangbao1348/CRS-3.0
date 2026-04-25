-- 清理其他表中不属于租户1的数据

USE CRS;

-- 清理room_type_categories
DELETE FROM room_type_categories WHERE tenant_id != 1;

-- 清理market_code_categories
DELETE FROM market_code_categories WHERE tenant_id != 1;

-- 清理market_codes
DELETE FROM market_codes WHERE tenant_id != 1;

-- 清理source_codes
DELETE FROM source_codes WHERE tenant_id != 1;

-- 清理rate_types
DELETE FROM rate_types WHERE tenant_id != 1;

-- 清理tax_settings
DELETE FROM tax_settings WHERE tenant_id != 1;

-- 清理cancellation_policies
DELETE FROM cancellation_policies WHERE tenant_id != 1;

-- 清理guarantee_policies
DELETE FROM guarantee_policies WHERE tenant_id != 1;

-- 清理channel_codes
DELETE FROM channel_codes WHERE tenant_id != 1;

-- 清理packages
DELETE FROM packages WHERE tenant_id != 1;

-- 清理users和roles（先删除role_user关联）
DELETE FROM user_roles WHERE tenant_id != 1;
DELETE FROM users WHERE tenant_id != 1;
DELETE FROM roles WHERE tenant_id != 1;

-- 显示清理后的情况
SELECT '=== 清理完成 ===' AS result;

SELECT 'room_type_categories' AS table_name, COUNT(*) AS row_count FROM room_type_categories
UNION ALL
SELECT 'market_code_categories', COUNT(*) FROM market_code_categories
UNION ALL
SELECT 'market_codes', COUNT(*) FROM market_codes
UNION ALL
SELECT 'source_codes', COUNT(*) FROM source_codes
UNION ALL
SELECT 'rate_types', COUNT(*) FROM rate_types
UNION ALL
SELECT 'tax_settings', COUNT(*) FROM tax_settings
UNION ALL
SELECT 'cancellation_policies', COUNT(*) FROM cancellation_policies
UNION ALL
SELECT 'guarantee_policies', COUNT(*) FROM guarantee_policies
UNION ALL
SELECT 'channel_codes', COUNT(*) FROM channel_codes
UNION ALL
SELECT 'packages', COUNT(*) FROM packages
UNION ALL
SELECT 'users', COUNT(*) FROM users
UNION ALL
SELECT 'roles', COUNT(*) FROM roles;
