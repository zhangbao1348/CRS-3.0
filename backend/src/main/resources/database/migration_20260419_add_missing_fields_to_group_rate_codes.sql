-- 添加缺失的字段到 group_rate_codes 表
USE crs;

-- 添加 market_code 字段
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'crs' 
    AND TABLE_NAME = 'group_rate_codes' 
    AND COLUMN_NAME = 'market_code');

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE group_rate_codes ADD COLUMN market_code VARCHAR(50) AFTER market_code_id',
    'SELECT ''market_code column already exists'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 source_code 字段
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'crs' 
    AND TABLE_NAME = 'group_rate_codes' 
    AND COLUMN_NAME = 'source_code');

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE group_rate_codes ADD COLUMN source_code VARCHAR(50) AFTER source_code_id',
    'SELECT ''source_code column already exists'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 parent_rate_code 字段
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'crs' 
    AND TABLE_NAME = 'group_rate_codes' 
    AND COLUMN_NAME = 'parent_rate_code');

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE group_rate_codes ADD COLUMN parent_rate_code VARCHAR(50) AFTER parent_rate_code_id',
    'SELECT ''parent_rate_code column already exists'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
