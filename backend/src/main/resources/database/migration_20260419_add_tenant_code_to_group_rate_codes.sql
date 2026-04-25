-- 添加缺失的 tenant_code 字段到 group_rate_codes 表
-- 先检查字段是否存在，如果不存在则添加
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'crs' 
    AND TABLE_NAME = 'group_rate_codes' 
    AND COLUMN_NAME = 'tenant_code');

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE group_rate_codes ADD COLUMN tenant_code VARCHAR(50) AFTER group_id',
    'SELECT ''tenant_code column already exists'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 为 tenant_code 添加索引以提高查询性能
SET @idx_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = 'crs' 
    AND TABLE_NAME = 'group_rate_codes' 
    AND INDEX_NAME = 'idx_group_rate_codes_tenant_code');

SET @sql = IF(@idx_exists = 0,
    'CREATE INDEX idx_group_rate_codes_tenant_code ON group_rate_codes(tenant_code)',
    'SELECT ''index already exists'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;