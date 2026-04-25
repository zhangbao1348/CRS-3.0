-- 修改酒店房价码分配表结构，修改字段名
USE CRS;

-- 修改字段名（如果字段存在）
SET @dbname = DATABASE();
SET @tablename = 'hotel_rate_code_allocations';

-- 检查并修改 is_basic_info_editable 字段
SET @column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = @dbname
    AND table_name = @tablename
    AND column_name = 'is_basic_info_editable'
);

SET @sql = IF(@column_exists > 0,
    'ALTER TABLE hotel_rate_code_allocations CHANGE COLUMN is_basic_info_editable basic_info_editable BOOLEAN DEFAULT FALSE',
    'SELECT "Column is_basic_info_editable does not exist" AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并修改 is_price_info_editable 字段
SET @column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = @dbname
    AND table_name = @tablename
    AND column_name = 'is_price_info_editable'
);

SET @sql = IF(@column_exists > 0,
    'ALTER TABLE hotel_rate_code_allocations CHANGE COLUMN is_price_info_editable price_info_editable BOOLEAN DEFAULT FALSE',
    'SELECT "Column is_price_info_editable does not exist" AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并修改 is_booking_limit_editable 字段
SET @column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = @dbname
    AND table_name = @tablename
    AND column_name = 'is_booking_limit_editable'
);

SET @sql = IF(@column_exists > 0,
    'ALTER TABLE hotel_rate_code_allocations CHANGE COLUMN is_booking_limit_editable booking_limit_editable BOOLEAN DEFAULT FALSE',
    'SELECT "Column is_booking_limit_editable does not exist" AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并修改 is_guarantee_rule_editable 字段
SET @column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = @dbname
    AND table_name = @tablename
    AND column_name = 'is_guarantee_rule_editable'
);

SET @sql = IF(@column_exists > 0,
    'ALTER TABLE hotel_rate_code_allocations CHANGE COLUMN is_guarantee_rule_editable guarantee_rule_editable BOOLEAN DEFAULT FALSE',
    'SELECT "Column is_guarantee_rule_editable does not exist" AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并修改 is_promotion_editable 字段
SET @column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = @dbname
    AND table_name = @tablename
    AND column_name = 'is_promotion_editable'
);

SET @sql = IF(@column_exists > 0,
    'ALTER TABLE hotel_rate_code_allocations CHANGE COLUMN is_promotion_editable promotion_editable BOOLEAN DEFAULT FALSE',
    'SELECT "Column is_promotion_editable does not exist" AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 显示完成信息
SELECT '酒店房价码分配表结构修改完成！' AS result;
