-- 数据库迁移脚本 - 2026-04-06
-- 添加 users 表缺失的字段

USE CRS;

-- 添加 avatar 字段（如果不存在）
ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar VARCHAR(255) COMMENT '头像' AFTER email;

-- 添加 last_login_time 字段（如果不存在）
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_login_time TIMESTAMP NULL COMMENT '最后登录时间' AFTER avatar;

-- 添加 last_login_ip 字段（如果不存在）
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_login_ip VARCHAR(50) COMMENT '最后登录IP' AFTER last_login_time;

-- 检查并更新 created_at 字段（确保存在）
SET @col_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'CRS' 
    AND TABLE_NAME = 'users' 
    AND COLUMN_NAME = 'created_at'
);

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE users ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''',
    'SELECT ''Column created_at already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并更新 updated_at 字段（确保存在）
SET @col_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'CRS' 
    AND TABLE_NAME = 'users' 
    AND COLUMN_NAME = 'updated_at'
);

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE users ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''',
    'SELECT ''Column updated_at already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并更新 tenant_id 字段（确保存在）
SET @col_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'CRS' 
    AND TABLE_NAME = 'users' 
    AND COLUMN_NAME = 'tenant_id'
);

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE users ADD COLUMN tenant_id INT COMMENT ''租户ID'' AFTER id',
    'SELECT ''Column tenant_id already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并更新 phone 字段（确保存在）
SET @col_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'CRS' 
    AND TABLE_NAME = 'users' 
    AND COLUMN_NAME = 'phone'
);

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE users ADD COLUMN phone VARCHAR(20) COMMENT ''手机号'' AFTER name',
    'SELECT ''Column phone already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 显示迁移完成信息
SELECT '数据库迁移完成！users 表字段已更新。' AS migration_status;
