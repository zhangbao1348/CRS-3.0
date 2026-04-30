-- 为 tenant_channels 表添加渠道配置字段（价格设置、佣金设置）
-- 兼容 MySQL 8.0（不使用 IF NOT EXISTS）
USE CRS;

SET @dbname = DATABASE();
SET @tablename = 'tenant_channels';

-- price_rounding
SET @col = 'price_rounding';
SET @s = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = @dbname AND table_name = @tablename AND column_name = @col) > 0,
    'SELECT 1',
    CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @col, ' VARCHAR(20) DEFAULT ''keep'' COMMENT ''价格取整方式：keep/ceil/floor''')
));
PREPARE stmt FROM @s;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- prepaid_commission_type
SET @col = 'prepaid_commission_type';
SET @s = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = @dbname AND table_name = @tablename AND column_name = @col) > 0,
    'SELECT 1',
    CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @col, ' VARCHAR(20) DEFAULT ''percentage'' COMMENT ''预付佣金类型：percentage/fixed''')
));
PREPARE stmt FROM @s;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- prepaid_commission_value
SET @col = 'prepaid_commission_value';
SET @s = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = @dbname AND table_name = @tablename AND column_name = @col) > 0,
    'SELECT 1',
    CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @col, ' DECIMAL(10,2) NULL COMMENT ''预付佣金数值''')
));
PREPARE stmt FROM @s;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- postpaid_commission_type
SET @col = 'postpaid_commission_type';
SET @s = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = @dbname AND table_name = @tablename AND column_name = @col) > 0,
    'SELECT 1',
    CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @col, ' VARCHAR(20) DEFAULT ''percentage'' COMMENT ''现付佣金类型：percentage/fixed''')
));
PREPARE stmt FROM @s;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- postpaid_commission_value
SET @col = 'postpaid_commission_value';
SET @s = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = @dbname AND table_name = @tablename AND column_name = @col) > 0,
    'SELECT 1',
    CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @col, ' DECIMAL(10,2) NULL COMMENT ''现付佣金数值''')
));
PREPARE stmt FROM @s;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT '渠道配置字段添加完成' AS result;
