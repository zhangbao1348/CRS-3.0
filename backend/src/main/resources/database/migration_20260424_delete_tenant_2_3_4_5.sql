-- 删除租户2、3、4、5的数据，仅保留租户1

USE CRS;

-- 获取租户1的tenant_code
SELECT @tenant1_code := tenant_code FROM tenants WHERE id = 1;
SELECT CONCAT('租户1的tenant_code: ', @tenant1_code) AS info;

-- =============================================
-- 安全删除函数：检查表和字段是否存在
-- =============================================

-- 1. 删除有 tenant_id 字段的表数据（删除ID为2、3、4、5的数据）
-- 动态检查并删除

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'hotel_price_logs'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'hotel_price_logs 数据已删除' AS result;
ELSE
    SELECT 'hotel_price_logs 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'hotel_prices'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'hotel_prices 数据已删除' AS result;
ELSE
    SELECT 'hotel_prices 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'base_prices'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'base_prices 数据已删除' AS result;
ELSE
    SELECT 'base_prices 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'inventories'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'inventories 数据已删除' AS result;
ELSE
    SELECT 'inventories 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'rate_plans'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'rate_plans 数据已删除' AS result;
ELSE
    SELECT 'rate_plans 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'group_room_type_hotel'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'group_room_type_hotel 数据已删除' AS result;
ELSE
    SELECT 'group_room_type_hotel 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'hotel_rate_code_allocations'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'hotel_rate_code_allocations 数据已删除' AS result;
ELSE
    SELECT 'hotel_rate_code_allocations 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'hotel_room_types'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'hotel_room_types 数据已删除' AS result;
ELSE
    SELECT 'hotel_room_types 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'hotel_room_type_allocations'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'hotel_room_type_allocations 数据已删除' AS result;
ELSE
    SELECT 'hotel_room_type_allocations 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'channel_rate_code_mappings'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'channel_rate_code_mappings 数据已删除' AS result;
ELSE
    SELECT 'channel_rate_code_mappings 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'channel_room_type_mappings'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'channel_room_type_mappings 数据已删除' AS result;
ELSE
    SELECT 'channel_room_type_mappings 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'channel_hotel_mappings'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'channel_hotel_mappings 数据已删除' AS result;
ELSE
    SELECT 'channel_hotel_mappings 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'hotels'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'hotels 数据已删除' AS result;
ELSE
    SELECT 'hotels 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'cancellation_policies'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'cancellation_policies 数据已删除' AS result;
ELSE
    SELECT 'cancellation_policies 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'channel_codes'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'channel_codes 数据已删除' AS result;
ELSE
    SELECT 'channel_codes 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'guarantee_policies'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'guarantee_policies 数据已删除' AS result;
ELSE
    SELECT 'guarantee_policies 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'market_code_categories'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'market_code_categories 数据已删除' AS result;
ELSE
    SELECT 'market_code_categories 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'market_codes'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'market_codes 数据已删除' AS result;
ELSE
    SELECT 'market_codes 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'packages'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'packages 数据已删除' AS result;
ELSE
    SELECT 'packages 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'rate_types'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'rate_types 数据已删除' AS result;
ELSE
    SELECT 'rate_types 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'room_type_categories'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'room_type_categories 数据已删除' AS result;
ELSE
    SELECT 'room_type_categories 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'source_codes'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'source_codes 数据已删除' AS result;
ELSE
    SELECT 'source_codes 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'tax_settings'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'tax_settings 数据已删除' AS result;
ELSE
    SELECT 'tax_settings 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'user_roles'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'user_roles 数据已删除' AS result;
ELSE
    SELECT 'user_roles 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'roles'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'roles 数据已删除' AS result;
ELSE
    SELECT 'roles 无tenant_id字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_id IN (2,3,4,5)') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'users'
  AND COLUMN_NAME = 'tenant_id'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'users 数据已删除' AS result;
ELSE
    SELECT 'users 无tenant_id字段，跳过' AS result;
END IF;

-- 2. 删除有 tenant_code 字段的表数据（仅保留租户1的数据）
SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_code != \"', @tenant1_code, '\" OR tenant_code IS NULL') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'group_rate_codes'
  AND COLUMN_NAME = 'tenant_code'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'group_rate_codes 数据已删除' AS result;
ELSE
    SELECT 'group_rate_codes 无tenant_code字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_code != \"', @tenant1_code, '\" OR tenant_code IS NULL') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'group_room_types'
  AND COLUMN_NAME = 'tenant_code'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'group_room_types 数据已删除' AS result;
ELSE
    SELECT 'group_room_types 无tenant_code字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_code != \"', @tenant1_code, '\" OR tenant_code IS NULL') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'group_cancellation_policies'
  AND COLUMN_NAME = 'tenant_code'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'group_cancellation_policies 数据已删除' AS result;
ELSE
    SELECT 'group_cancellation_policies 无tenant_code字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_code != \"', @tenant1_code, '\" OR tenant_code IS NULL') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'group_guarantee_policies'
  AND COLUMN_NAME = 'tenant_code'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'group_guarantee_policies 数据已删除' AS result;
ELSE
    SELECT 'group_guarantee_policies 无tenant_code字段，跳过' AS result;
END IF;

SET @sql = NULL;
SELECT CONCAT('DELETE FROM `', table_name, '` WHERE tenant_code != \"', @tenant1_code, '\" OR tenant_code IS NULL') INTO @sql
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'CRS'
  AND TABLE_NAME = 'group_facilities'
  AND COLUMN_NAME = 'tenant_code'
LIMIT 1;

IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SELECT 'group_facilities 数据已删除' AS result;
ELSE
    SELECT 'group_facilities 无tenant_code字段，跳过' AS result;
END IF;

-- 3. 最后删除 tenants 表中的租户2、3、4、5
DELETE FROM tenants WHERE id IN (2,3,4,5);
SELECT 'tenants 表中租户2、3、4、5已删除' AS result;

-- =============================================
-- 验证删除结果
-- =============================================
SELECT '=== 剩余租户 ===' AS info;
SELECT id, tenant_code, tenant_name, status FROM tenants ORDER BY id;

SELECT '=== 验证完成！仅保留租户1 ===' AS final_result;
