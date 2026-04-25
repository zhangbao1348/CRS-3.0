-- 修复渠道映射表缺失字段
-- 兼容 MySQL 8.0

USE CRS;

-- =============================================
-- 1. 修复 channel_rate_code_mappings 表
-- =============================================
SET @dbname = DATABASE();
SET @tablename = 'channel_rate_code_mappings';

-- 添加 channel_code
SET @columnname = 'channel_code';
SET @preparedStatement = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = @dbname AND table_name = @tablename AND column_name = @columnname) > 0,
    'SELECT 1',
    CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(50) COMMENT ''渠道CODE''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加 hotel_code
SET @columnname = 'hotel_code';
SET @preparedStatement = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = @dbname AND table_name = @tablename AND column_name = @columnname) > 0,
    'SELECT 1',
    CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(50) COMMENT ''酒店CODE''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- =============================================
-- 2. 修复 channel_room_type_mappings 表
-- =============================================
SET @tablename = 'channel_room_type_mappings';

-- 添加 channel_code
SET @columnname = 'channel_code';
SET @preparedStatement = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = @dbname AND table_name = @tablename AND column_name = @columnname) > 0,
    'SELECT 1',
    CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(50) COMMENT ''渠道CODE''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加 hotel_code
SET @columnname = 'hotel_code';
SET @preparedStatement = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE table_schema = @dbname AND table_name = @tablename AND column_name = @columnname) > 0,
    'SELECT 1',
    CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(50) COMMENT ''酒店CODE''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- =============================================
-- 3. 修复 channel_hotel_mappings 表
-- =============================================
SET @tablename = 'channel_hotel_mappings';

-- 添加 channel_code（这个表已经有了）

SELECT '渠道映射表缺失字段已成功添加！' AS result;
