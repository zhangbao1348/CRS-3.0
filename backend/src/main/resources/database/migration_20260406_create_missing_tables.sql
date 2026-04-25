-- 创建缺失的集团房型管理相关表
-- 执行此脚本前请确保已经连接到 CRS 数据库

USE CRS;

-- 1. 创建集团房型表
CREATE TABLE IF NOT EXISTS group_room_types (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    group_id INT NOT NULL COMMENT '集团ID',
    room_type_code VARCHAR(50) NOT NULL COMMENT '房型代码',
    room_type_name VARCHAR(100) NOT NULL COMMENT '房型名称',
    description TEXT COMMENT '描述',
    room_type_category_id INT COMMENT '房型大类ID',
    max_occupancy INT DEFAULT 2 COMMENT '最大入住人数',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_group_id (group_id),
    INDEX idx_room_type_code (room_type_code),
    INDEX idx_status (status),
    INDEX idx_room_type_category_id (room_type_category_id),
    UNIQUE KEY uk_room_type_code (room_type_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='集团房型表';

-- 2. 创建酒店房型表
CREATE TABLE IF NOT EXISTS hotel_room_types (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    group_room_type_id INT COMMENT '集团房型ID',
    room_type_code VARCHAR(50) NOT NULL COMMENT '房型代码',
    room_type_name VARCHAR(100) NOT NULL COMMENT '房型名称',
    description TEXT COMMENT '描述',
    max_occupancy INT DEFAULT 2 COMMENT '最大入住人数',
    total_rooms INT COMMENT '房间数',
    sort_order INT DEFAULT 0 COMMENT '排序',
    room_type_category_id INT COMMENT '房型大类ID',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_hotel_id (hotel_id),
    INDEX idx_group_room_type_id (group_room_type_id),
    INDEX idx_room_type_code (room_type_code),
    INDEX idx_status (status),
    INDEX idx_room_type_category_id (room_type_category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒店房型表';

-- 3. 创建集团房型-酒店关联表
CREATE TABLE IF NOT EXISTS group_room_type_hotel (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    group_room_type_id INT NOT NULL COMMENT '集团房型ID',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    allocated TINYINT(1) DEFAULT 1 COMMENT '是否已分配',
    room_info_editable TINYINT(1) DEFAULT 0 COMMENT '房型信息是否可编辑',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_group_room_type_id (group_room_type_id),
    INDEX idx_hotel_id (hotel_id),
    UNIQUE KEY uk_group_room_type_hotel (group_room_type_id, hotel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='集团房型-酒店关联表';

-- 4. 创建房型大类表
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

-- 插入默认房型大类数据（如果不存在）
INSERT IGNORE INTO room_type_categories (group_id, category_code, category_name, sort_order, status) VALUES
(1, 'KING', '大床房', 1, 'active'),
(1, 'TWIN', '双床房', 2, 'active'),
(1, 'SUITE', '套房', 3, 'active'),
(1, 'FAMILY', '家庭房', 4, 'active'),
(1, 'DELUXE', '豪华房', 5, 'active');

-- 显示完成信息
SELECT '集团房型管理相关表创建完成！' AS result;