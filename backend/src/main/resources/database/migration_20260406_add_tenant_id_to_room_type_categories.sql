-- 为 room_type_categories 表添加 tenant_id 字段
-- 日期: 2026-04-06

USE CRS;

-- 添加 tenant_id 字段
ALTER TABLE room_type_categories 
ADD COLUMN tenant_id INT COMMENT '租户ID'
AFTER id;

-- 为现有数据设置默认租户ID
UPDATE room_type_categories 
SET tenant_id = 1;

-- 添加索引
ALTER TABLE room_type_categories 
ADD INDEX idx_tenant_id (tenant_id);

-- 添加唯一索引（租户ID + 编码）
ALTER TABLE room_type_categories 
ADD UNIQUE KEY uk_tenant_category_code (tenant_id, category_code);

SELECT 'room_type_categories 表添加 tenant_id 字段完成！' AS result;
