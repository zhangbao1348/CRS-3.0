-- 集团房型管理功能数据库迁移脚本
-- 日期: 2026-04-06
-- 功能: 添加房型大类表，完善集团房型和酒店房型表结构

USE CRS;

-- 1. 创建房型大类表
CREATE TABLE IF NOT EXISTS room_type_categories (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    group_id INT NOT NULL COMMENT '集团ID',
    category_code VARCHAR(50) NOT NULL COMMENT '大类编码',
    category_name VARCHAR(100) NOT NULL COMMENT '大类名称',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_group_id (group_id),
    INDEX idx_category_code (category_code),
    INDEX idx_status (status),
    UNIQUE KEY uk_group_category (group_id, category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房型大类表';

-- 2. 为 group_room_types 表添加字段
ALTER TABLE group_room_types 
ADD COLUMN IF NOT EXISTS room_type_category_id INT COMMENT '房型大类ID' AFTER description,
ADD COLUMN IF NOT EXISTS max_occupancy INT DEFAULT 2 COMMENT '最大入住人数' AFTER room_type_category_id,
ADD COLUMN IF NOT EXISTS sort_order INT DEFAULT 0 COMMENT '排序' AFTER max_occupancy,
ADD INDEX IF NOT EXISTS idx_room_type_category_id (room_type_category_id);

-- 3. 为 hotel_room_types 表添加字段
ALTER TABLE hotel_room_types 
ADD COLUMN IF NOT EXISTS max_occupancy INT DEFAULT 2 COMMENT '最大入住人数' AFTER description,
ADD COLUMN IF NOT EXISTS total_rooms INT COMMENT '房间数' AFTER max_occupancy,
ADD COLUMN IF NOT EXISTS sort_order INT DEFAULT 0 COMMENT '排序' AFTER total_rooms,
ADD COLUMN IF NOT EXISTS room_type_category_id INT COMMENT '房型大类ID' AFTER sort_order,
ADD INDEX IF NOT EXISTS idx_room_type_category_id (room_type_category_id);

-- 4. 插入默认房型大类数据
INSERT IGNORE INTO room_type_categories (group_id, category_code, category_name, sort_order, status) VALUES
(1, 'KING', '大床房', 1, 'active'),
(1, 'TWIN', '双床房', 2, 'active'),
(1, 'SUITE', '套房', 3, 'active'),
(1, 'FAMILY', '家庭房', 4, 'active'),
(1, 'DELUXE', '豪华房', 5, 'active');

-- 显示完成信息
SELECT '集团房型管理数据库迁移完成！' AS result;