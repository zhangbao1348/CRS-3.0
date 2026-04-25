-- CRS系统完整数据库表初始化脚本
-- 包含所有必要的表结构

USE CRS;

-- 1. 集团表
CREATE TABLE IF NOT EXISTS `groups` (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    group_code VARCHAR(50) NOT NULL UNIQUE COMMENT '集团编码',
    group_name VARCHAR(100) NOT NULL COMMENT '集团名称',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_group_code (group_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='集团表';

-- 2. 酒店表
CREATE TABLE IF NOT EXISTS hotels (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    hotel_code VARCHAR(50) NOT NULL UNIQUE COMMENT '酒店编码',
    tenant_id INT COMMENT '租户ID（等同于集团ID）',
    chinese_name VARCHAR(100) NOT NULL COMMENT '中文名称',
    english_name VARCHAR(100) NOT NULL COMMENT '英文名称',
    star_rating VARCHAR(10) COMMENT '星级',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    address VARCHAR(200) NOT NULL COMMENT '地址',
    longitude DOUBLE COMMENT '经度',
    latitude DOUBLE COMMENT '纬度',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    email VARCHAR(100) NOT NULL COMMENT '电子邮箱',
    introduction TEXT COMMENT '简介',
    total_rooms INT COMMENT '总房间数',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_hotel_code (hotel_code),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_status (status),
    INDEX idx_city (city)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒店表';

-- 3. 房型表
CREATE TABLE IF NOT EXISTS room_types (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    room_type_code VARCHAR(50) NOT NULL COMMENT '房型编码',
    room_type_name VARCHAR(100) NOT NULL COMMENT '房型名称',
    description TEXT COMMENT '描述',
    max_occupancy INT DEFAULT 2 COMMENT '最大入住人数',
    total_rooms INT COMMENT '房间数',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_hotel_id (hotel_id),
    INDEX idx_room_type_code (room_type_code),
    INDEX idx_status (status),
    UNIQUE KEY uk_hotel_room_type (hotel_id, room_type_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房型表';

-- 4. 房价码表
CREATE TABLE IF NOT EXISTS rate_codes (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    rate_code VARCHAR(50) NOT NULL COMMENT '房价码',
    rate_name VARCHAR(100) NOT NULL COMMENT '房价名称',
    description TEXT COMMENT '描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_hotel_id (hotel_id),
    INDEX idx_rate_code (rate_code),
    INDEX idx_status (status),
    UNIQUE KEY uk_hotel_rate_code (hotel_id, rate_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房价码表';

-- 5. 价格计划表
CREATE TABLE IF NOT EXISTS rate_plans (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    rate_plan_code VARCHAR(50) NOT NULL COMMENT '价格计划编码',
    rate_plan_name VARCHAR(100) NOT NULL COMMENT '价格计划名称',
    description TEXT COMMENT '描述',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_hotel_id (hotel_id),
    INDEX idx_rate_plan_code (rate_plan_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='价格计划表';

-- 6. 基础价格表
CREATE TABLE IF NOT EXISTS base_prices (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    room_type_id INT NOT NULL COMMENT '房型ID',
    rate_code_id INT NOT NULL COMMENT '房价码ID',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    price DECIMAL(10, 2) NOT NULL COMMENT '价格',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_hotel_id (hotel_id),
    INDEX idx_room_type_id (room_type_id),
    INDEX idx_rate_code_id (rate_code_id),
    INDEX idx_date_range (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='基础价格表';

-- 7. 库存表
CREATE TABLE IF NOT EXISTS inventories (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    room_type_id INT NOT NULL COMMENT '房型ID',
    inventory_date DATE NOT NULL COMMENT '库存日期',
    total_rooms INT NOT NULL COMMENT '总房间数',
    available_rooms INT NOT NULL COMMENT '可用房间数',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_hotel_id (hotel_id),
    INDEX idx_room_type_id (room_type_id),
    INDEX idx_inventory_date (inventory_date),
    UNIQUE KEY uk_hotel_room_date (hotel_id, room_type_id, inventory_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存表';

-- 8. 酒店房型分配表
CREATE TABLE IF NOT EXISTS hotel_room_type_allocations (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    room_type_id INT NOT NULL COMMENT '房型ID',
    is_editable BOOLEAN DEFAULT TRUE COMMENT '是否可编辑',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_hotel_id (hotel_id),
    INDEX idx_room_type_id (room_type_id),
    UNIQUE KEY uk_hotel_room (hotel_id, room_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒店房型分配表';

-- 9. 酒店房价码分配表
CREATE TABLE IF NOT EXISTS hotel_rate_code_allocations (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    rate_code_id INT NOT NULL COMMENT '房价码ID',
    is_basic_info_editable BOOLEAN DEFAULT TRUE COMMENT '基础信息是否可编辑',
    is_price_info_editable BOOLEAN DEFAULT TRUE COMMENT '价格信息是否可编辑',
    is_booking_limit_editable BOOLEAN DEFAULT TRUE COMMENT '预订限制是否可编辑',
    is_guarantee_rule_editable BOOLEAN DEFAULT TRUE COMMENT '担保规则是否可编辑',
    is_promotion_editable BOOLEAN DEFAULT TRUE COMMENT '促销信息是否可编辑',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_hotel_id (hotel_id),
    INDEX idx_rate_code_id (rate_code_id),
    UNIQUE KEY uk_hotel_rate (hotel_id, rate_code_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒店房价码分配表';

-- 10. 酒店设施表
CREATE TABLE IF NOT EXISTS hotel_facilities (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    facility_type VARCHAR(50) NOT NULL COMMENT '设施类型',
    facility_code VARCHAR(50) NOT NULL COMMENT '设施编码',
    facility_name VARCHAR(100) NOT NULL COMMENT '设施名称',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_hotel_id (hotel_id),
    INDEX idx_facility_type (facility_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒店设施表';

-- 11. 酒店图片表
CREATE TABLE IF NOT EXISTS hotel_images (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    image_type VARCHAR(50) NOT NULL COMMENT '图片类型',
    image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
    image_title VARCHAR(200) COMMENT '图片标题',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_hotel_id (hotel_id),
    INDEX idx_image_type (image_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒店图片表';

-- 显示完成信息
SELECT 'CRS系统数据库表初始化完成！' AS result;
