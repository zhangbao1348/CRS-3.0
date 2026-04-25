-- 为packages表添加tenant_id字段
-- 执行时间: 2026-04-11

USE CRS;

-- 添加tenant_id字段（如果不存在）
SET @dbname = DATABASE();
SET @tablename = 'packages';
SET @columnname = 'tenant_id';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_schema = @dbname)
      AND (table_name = @tablename)
      AND (column_name = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' INT NOT NULL DEFAULT 1 AFTER id')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 为tenant_id字段添加索引（如果不存在）
SET @indexname = 'idx_packages_tenant_id';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE
      (table_schema = @dbname)
      AND (table_name = @tablename)
      AND (index_name = @indexname)
  ) > 0,
  'SELECT 1',
  CONCAT('CREATE INDEX ', @indexname, ' ON ', @tablename, '(tenant_id)')
));
PREPARE createIndexIfNotExists FROM @preparedStatement;
EXECUTE createIndexIfNotExists;
DEALLOCATE PREPARE createIndexIfNotExists;

-- 移除code上的唯一约束（如果存在），改为在同一租户内唯一
SET @oldindexname = 'code';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE
      (table_schema = @dbname)
      AND (table_name = @tablename)
      AND (index_name = @oldindexname)
  ) > 0,
  CONCAT('ALTER TABLE ', @tablename, ' DROP INDEX ', @oldindexname),
  'SELECT 1'
));
PREPARE dropIndexIfExists FROM @preparedStatement;
EXECUTE dropIndexIfExists;
DEALLOCATE PREPARE dropIndexIfExists;

-- 添加复合唯一索引（tenant_id, code）（如果不存在）
SET @newindexname = 'idx_packages_tenant_code';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE
      (table_schema = @dbname)
      AND (table_name = @tablename)
      AND (index_name = @newindexname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD UNIQUE KEY ', @newindexname, ' (tenant_id, code)')
));
PREPARE createUniqueIndexIfNotExists FROM @preparedStatement;
EXECUTE createUniqueIndexIfNotExists;
DEALLOCATE PREPARE createUniqueIndexIfNotExists;

SELECT 'tenant_id字段添加成功' AS message;
