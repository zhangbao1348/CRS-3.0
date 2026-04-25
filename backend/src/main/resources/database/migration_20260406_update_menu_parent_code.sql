-- 为菜单表添加parent_code字段
ALTER TABLE menus ADD COLUMN parent_code VARCHAR(50) DEFAULT NULL;

-- 更新菜单的parent_code
-- 根菜单（parent_id = 1）
UPDATE menus SET parent_code = 'crs-system' WHERE parent_id = 1;

-- 系统菜单（parent_id = 0）
UPDATE menus SET parent_code = NULL WHERE parent_id = 0;

-- CRS系统（parent_id = 0）
UPDATE menus SET parent_code = NULL WHERE menu_code = 'crs-system';

-- 订单管理（parent_id = 1）
UPDATE menus SET parent_code = 'crs-system' WHERE parent_id = 1 AND menu_type = 'directory';

-- 订单列表（parent_id = 56）
UPDATE menus SET parent_code = 'crs-reservation' WHERE parent_id = 56;

-- 库存管理（parent_id = 1）
UPDATE menus SET parent_code = 'crs-system' WHERE menu_code = 'crs-inventory';

-- 房控日历（parent_id = 58）
UPDATE menus SET parent_code = 'crs-inventory' WHERE parent_id = 58 AND menu_code = 'crs-inventory-calendar';

-- 房态管理（parent_id = 58）
UPDATE menus SET parent_code = 'crs-inventory' WHERE parent_id = 58 AND menu_code = 'crs-room-status';

-- 预订控制（parent_id = 58）
UPDATE menus SET parent_code = 'crs-inventory' WHERE parent_id = 58 AND menu_code = 'crs-booking-control';

-- 房型管理（parent_id = 1）
UPDATE menus SET parent_code = 'crs-system' WHERE menu_code = 'crs-room-management';

-- 房型管理（parent_id = 62）
UPDATE menus SET parent_code = 'crs-room-management' WHERE parent_id = 62;

-- 价格计划管理（parent_id = 1）
UPDATE menus SET parent_code = 'crs-system' WHERE menu_code = 'crs-rate-management';

-- 价格计划相关子菜单（parent_id = 64）
UPDATE menus SET parent_code = 'crs-rate-management' WHERE parent_id = 64;

-- 渠道管理（parent_id = 1）
UPDATE menus SET parent_code = 'crs-system' WHERE menu_code = 'crs-channel-management';

-- 渠道管理子菜单（parent_id = 71）
UPDATE menus SET parent_code = 'crs-channel-management' WHERE parent_id = 71;

-- 数据及报表（parent_id = 1）
UPDATE menus SET parent_code = 'crs-system' WHERE menu_code = 'crs-reports';

-- 报表子菜单（parent_id = 74）
UPDATE menus SET parent_code = 'crs-reports' WHERE parent_id = 74;

-- 集团管理（parent_id = 1）
UPDATE menus SET parent_code = 'crs-system' WHERE menu_code = 'crs-group-management';

-- 集团管理子菜单（parent_id = 79）
UPDATE menus SET parent_code = 'crs-group-management' WHERE parent_id = 79;

-- 集团促销管理（parent_id = 1）
UPDATE menus SET parent_code = 'crs-system' WHERE menu_code = 'crs-group-promotion';

-- 集团促销子菜单（parent_id = 94）
UPDATE menus SET parent_code = 'crs-group-promotion' WHERE parent_id = 94;

-- 系统设置（parent_id = 1）
UPDATE menus SET parent_code = 'crs-system' WHERE menu_code = 'crs-system-settings';

-- 系统设置子菜单（parent_id = 97）
UPDATE menus SET parent_code = 'crs-system-settings' WHERE parent_id = 97;

-- 超管设置（parent_id = 1）
UPDATE menus SET parent_code = 'crs-system' WHERE menu_code = 'crs-super-admin';

-- 超管设置子菜单（parent_id = 103）
UPDATE menus SET parent_code = 'crs-super-admin' WHERE parent_id = 103;