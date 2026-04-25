-- 数据库迁移脚本 - 添加 users 表缺失的字段
-- 简单版本，直接添加字段

USE CRS;

-- 添加 avatar 字段
ALTER TABLE users ADD COLUMN avatar VARCHAR(255) COMMENT '头像';

-- 添加 last_login_time 字段
ALTER TABLE users ADD COLUMN last_login_time TIMESTAMP NULL COMMENT '最后登录时间';

-- 添加 last_login_ip 字段
ALTER TABLE users ADD COLUMN last_login_ip VARCHAR(50) COMMENT '最后登录IP';

-- 显示迁移完成信息
SELECT '数据库迁移完成！users 表字段已更新。' AS migration_status;
