-- 创建房型设施表
-- 日期：2026-04-25

USE CRS;

-- 创建房型设施表
CREATE TABLE IF NOT EXISTS room_type_facilities (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    room_type_id INT NOT NULL COMMENT '房型ID',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    hotel_code VARCHAR(50) COMMENT '酒店编码',
    room_type_code VARCHAR(50) COMMENT '房型编码',
    facility_type VARCHAR(50) NOT NULL COMMENT '设施类型',
    facility_name VARCHAR(100) NOT NULL COMMENT '设施名称',
    facility_code VARCHAR(50) NOT NULL COMMENT '设施编码',
    available BOOLEAN DEFAULT TRUE COMMENT '是否可用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_room_type_id (room_type_id),
    INDEX idx_hotel_id (hotel_id),
    INDEX idx_hotel_code (hotel_code),
    INDEX idx_facility_type (facility_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房型设施表';

SELECT '房型设施表创建完成！' AS result;
