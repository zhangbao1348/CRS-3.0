-- 创建 pms_inventory 表
CREATE TABLE IF NOT EXISTS pms_inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tenant_id INT NOT NULL,
    hotel_code VARCHAR(50) NOT NULL,
    room_type_code VARCHAR(50) NOT NULL,
    inventory_date DATE NOT NULL,
    physical_rooms INT NOT NULL DEFAULT 0,
    available_rooms INT NOT NULL DEFAULT 0,
    maintenance_rooms INT NOT NULL DEFAULT 0,
    overbook_count INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_pms_inventory (tenant_id, hotel_code, room_type_code, inventory_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 为 inventory_quota 增加 version 字段
ALTER TABLE inventory_quota ADD COLUMN version INT NOT NULL DEFAULT 0;

-- 从 hotel_room_types 初始化 pms_inventory 数据
INSERT INTO pms_inventory (tenant_id, hotel_code, room_type_code, inventory_date, physical_rooms, available_rooms, maintenance_rooms, overbook_count)
SELECT
    h.tenant_id,
    h.hotel_code,
    hrt.room_type_code,
    CURDATE(),
    IFNULL(hrt.total_rooms, 0),
    IFNULL(hrt.total_rooms, 0),
    0,
    0
FROM hotel_room_types hrt
JOIN hotels h ON h.id = hrt.hotel_id
WHERE hrt.status = 'active'
  AND hrt.total_rooms > 0
  AND NOT EXISTS (
      SELECT 1 FROM pms_inventory pi
      WHERE pi.tenant_id = h.tenant_id
        AND pi.hotel_code = h.hotel_code
        AND pi.room_type_code = hrt.room_type_code
        AND pi.inventory_date = CURDATE()
  );
