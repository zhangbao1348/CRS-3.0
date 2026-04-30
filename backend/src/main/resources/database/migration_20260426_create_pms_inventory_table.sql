-- PMS库存数据表
-- 存储从PMS系统同步过来的每日房型库存数据

USE CRS;

CREATE TABLE IF NOT EXISTS pms_inventory (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id INT NOT NULL COMMENT '租户ID',
    hotel_code VARCHAR(50) NOT NULL COMMENT '酒店CODE',
    room_type_code VARCHAR(50) NOT NULL COMMENT '房型CODE',
    inventory_date DATE NOT NULL COMMENT '库存日期',
    physical_rooms INT NOT NULL DEFAULT 0 COMMENT '物理房型数（总房间数）',
    available_rooms INT NOT NULL DEFAULT 0 COMMENT '剩余可售房数',
    maintenance_rooms INT NOT NULL DEFAULT 0 COMMENT '维修房数',
    overbook_count INT NOT NULL DEFAULT 0 COMMENT '超预订数',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_tenant_hotel_room_date (tenant_id, hotel_code, room_type_code, inventory_date),
    INDEX idx_hotel_code (hotel_code),
    INDEX idx_room_type_code (room_type_code),
    INDEX idx_inventory_date (inventory_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PMS库存数据表';

-- PMS同步日志表
CREATE TABLE IF NOT EXISTS pms_sync_logs (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id INT NOT NULL COMMENT '租户ID',
    hotel_code VARCHAR(50) NOT NULL COMMENT '酒店CODE',
    sync_type VARCHAR(30) NOT NULL COMMENT '同步类型：inventory/full',
    sync_status VARCHAR(20) NOT NULL COMMENT '同步状态：success/failed',
    sync_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '同步时间',
    detail TEXT COMMENT '同步明细（JSON）',
    error_message TEXT COMMENT '错误信息',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_hotel_code (hotel_code),
    INDEX idx_sync_time (sync_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PMS同步日志表';
