-- 删除租户2、3、4、5的数据，仅保留租户1

USE CRS;

-- 首先获取租户1的tenant_code
SET @tenant1_code = (SELECT tenant_code FROM tenants WHERE id = 1);
SELECT CONCAT('租户1的tenant_code: ', @tenant1_code) AS info;

-- 1. 删除有tenant_id字段的表
DELETE FROM hotel_price_logs WHERE tenant_id IN (2, 3, 4, 5);
SELECT 'hotel_price_logs 数据已删除' AS result;

DELETE FROM hotel_prices WHERE tenant_id IN (2, 3, 4, 5);
SELECT 'hotel_prices 数据已删除' AS result;

DELETE FROM hotels WHERE tenant_id IN (2, 3, 4, 5);
SELECT 'hotels 数据已删除' AS result;

DELETE FROM cancellation_policies WHERE tenant_id IN (2, 3, 4, 5);
SELECT 'cancellation_policies 数据已删除' AS result;

DELETE FROM channel_codes WHERE tenant_id IN (2, 3, 4, 5);
SELECT 'channel_codes 数据已删除' AS result;

DELETE FROM guarantee_policies WHERE tenant_id IN (2, 3, 4, 5);
SELECT 'guarantee_policies 数据已删除' AS result;

DELETE FROM market_code_categories WHERE tenant_id IN (2, 3, 4, 5);
SELECT 'market_code_categories 数据已删除' AS result;

DELETE FROM market_codes WHERE tenant_id IN (2, 3, 4, 5);
SELECT 'market_codes 数据已删除' AS result;

DELETE FROM packages WHERE tenant_id IN (2, 3, 4, 5);
SELECT 'packages 数据已删除' AS result;

DELETE FROM rate_types WHERE tenant_id IN (2, 3, 4, 5);
SELECT 'rate_types 数据已删除' AS result;

DELETE FROM room_type_categories WHERE tenant_id IN (2, 3, 4, 5);
SELECT 'room_type_categories 数据已删除' AS result;

DELETE FROM source_codes WHERE tenant_id IN (2, 3, 4, 5);
SELECT 'source_codes 数据已删除' AS result;

DELETE FROM tax_settings WHERE tenant_id IN (2, 3, 4, 5);
SELECT 'tax_settings 数据已删除' AS result;

DELETE FROM user_roles WHERE tenant_id IN (2, 3, 4, 5);
SELECT 'user_roles 数据已删除' AS result;

DELETE FROM roles WHERE tenant_id IN (2, 3, 4, 5);
SELECT 'roles 数据已删除' AS result;

DELETE FROM users WHERE tenant_id IN (2, 3, 4, 5);
SELECT 'users 数据已删除' AS result;

-- 2. 删除有tenant_code字段的表
DELETE FROM group_rate_codes WHERE tenant_code != @tenant1_code OR tenant_code IS NULL;
SELECT 'group_rate_codes 数据已删除' AS result;

DELETE FROM group_room_types WHERE tenant_code != @tenant1_code OR tenant_code IS NULL;
SELECT 'group_room_types 数据已删除' AS result;

DELETE FROM group_cancellation_policies WHERE tenant_code != @tenant1_code OR tenant_code IS NULL;
SELECT 'group_cancellation_policies 数据已删除' AS result;

DELETE FROM group_guarantee_policies WHERE tenant_code != @tenant1_code OR tenant_code IS NULL;
SELECT 'group_guarantee_policies 数据已删除' AS result;

DELETE FROM group_facilities WHERE tenant_code != @tenant1_code OR tenant_code IS NULL;
SELECT 'group_facilities 数据已删除' AS result;

-- 3. 最后删除tenants表中的租户2、3、4、5
DELETE FROM tenants WHERE id IN (2, 3, 4, 5);
SELECT 'tenants 表中租户2、3、4、5已删除' AS result;

-- 4. 验证删除结果
SELECT '=== 剩余租户 ===' AS info;
SELECT id, tenant_code, tenant_name, status FROM tenants ORDER BY id;

SELECT '=== 验证完成！仅保留租户1 ===' AS final_result;
