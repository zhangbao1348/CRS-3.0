-- 使用CRS数据库
USE CRS;

-- ========================================
-- 1. 初始化菜单数据（安全模式：使用INSERT IGNORE避免覆盖已有数据）
-- ========================================

-- CRS系统菜单 - 使用INSERT IGNORE避免重复插入和覆盖
INSERT IGNORE INTO menus (parent_id, menu_code, menu_name, menu_type, path, icon, sort_order, status, system_type, permission, created_at, updated_at) VALUES
(0, 'crs-system', 'CRS系统', 'directory', '/crs', 'HomeOutlined', 1, 'active', 'crs', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'crs-dashboard', '首页', 'menu', '/dashboard', 'HomeOutlined', 1, 'active', 'crs', 'crs:dashboard:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'crs-reservation', '订单', 'directory', '/reservation', 'FileTextOutlined', 2, 'active', 'crs', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'crs-reservation-list', '订单', 'menu', '/reservation/reservation-list', 'FileTextOutlined', 1, 'active', 'crs', 'crs:reservation:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'crs-inventory', '库存管理', 'directory', '/inventory-management', 'InboxOutlined', 3, 'active', 'crs', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'crs-inventory-calendar', '房控日历', 'menu', '/inventory', 'CalendarOutlined', 1, 'active', 'crs', 'crs:inventory:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'crs-room-status', '房态管理', 'menu', '/inventory/room-status', 'HomeOutlined', 2, 'active', 'crs', 'crs:roomstatus:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'crs-booking-control', '预订控制', 'menu', '/inventory/booking-control', 'FilterOutlined', 3, 'active', 'crs', 'crs:booking:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'crs-room-management', '房型管理', 'directory', '/room-management', 'ApartmentOutlined', 4, 'active', 'crs', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'crs-room-type', '房型管理', 'menu', '/room-management/room-type', 'HomeOutlined', 1, 'active', 'crs', 'crs:roomtype:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'crs-rate-management', '价格计划管理', 'directory', '/rate-management', 'DollarOutlined', 5, 'active', 'crs', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'crs-rate-plan', '价格计划', 'menu', '/rate-management/rate-plan', 'TagOutlined', 1, 'active', 'crs', 'crs:rateplan:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'crs-room-type-diff', '房型差价设置', 'menu', '/rate-management/room-type-diff', 'ApartmentOutlined', 2, 'active', 'crs', 'crs:roomtypediff:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'crs-person-diff', '人数差价设置', 'menu', '/rate-management/person-diff', 'UserOutlined', 3, 'active', 'crs', 'crs:persondiff:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'crs-rack-rate', '基础价格设置', 'menu', '/rate-management/rack-rate', 'DollarOutlined', 4, 'active', 'crs', 'crs:rackrate:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'crs-package-setting', '包价设置', 'menu', '/rate-management/package-setting', 'GiftOutlined', 5, 'active', 'crs', 'crs:package:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'crs-price-query', '价格查询', 'menu', '/rate-management/price-query', 'SearchOutlined', 6, 'active', 'crs', 'crs:pricequery:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'crs-channel-management', '渠道管理', 'directory', '/channel-management', 'LinkOutlined', 6, 'active', 'crs', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, 'crs-channel-list', '渠道列表', 'menu', '/channel-management/channel-list', 'LinkOutlined', 1, 'active', 'crs', 'crs:channel:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, 'crs-channel-mapping', '渠道映射', 'menu', '/channel-management/channel-mapping', 'FilterOutlined', 2, 'active', 'crs', 'crs:channelmapping:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'crs-reports', '数据及报表', 'directory', '/reports', 'BarChartOutlined', 7, 'active', 'crs', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(21, 'crs-reservation-reports', '订单报表', 'menu', '/reports/reservation-reports', 'BarChartOutlined', 1, 'active', 'crs', 'crs:reservationreport:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(21, 'crs-occupancy-reports', '出租率报表', 'menu', '/reports/occupancy-reports', 'PieChartOutlined', 2, 'active', 'crs', 'crs:occupancyreport:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(21, 'crs-revenue-reports', 'revenue报表', 'menu', '/reports/revenue-reports', 'DollarOutlined', 3, 'active', 'crs', 'crs:revenuereport:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(21, 'crs-data-export', '数据导出', 'menu', '/reports/data-export', 'ExportOutlined', 4, 'active', 'crs', 'crs:dataexport:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'crs-group-management', '集团管理', 'directory', '/group-management', 'BuildOutlined', 8, 'active', 'crs', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'crs-hotel-management', '酒店管理', 'menu', '/group-management/hotel-management', 'ApartmentOutlined', 1, 'active', 'crs', 'crs:hotel:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'crs-group-room-type', '集团房型管理', 'menu', '/group-management/group-room-type', 'HomeOutlined', 2, 'active', 'crs', 'crs:grouproomtype:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'crs-group-rate-code', '集团房价码管理', 'menu', '/group-management/group-rate-code', 'DollarOutlined', 3, 'active', 'crs', 'crs:groupratecode:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'crs-market-code', '市场码管理', 'menu', '/group-management/market-code', 'GlobalOutlined', 4, 'active', 'crs', 'crs:marketcode:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'crs-rate-category', '房价大类管理', 'menu', '/group-management/rate-category', 'DollarOutlined', 5, 'active', 'crs', 'crs:ratecategory:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'crs-room-type-category', '房型大类管理', 'menu', '/group-management/room-type-category', 'ApartmentOutlined', 6, 'active', 'crs', 'crs:roomtypecategory:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'crs-channel-code', '渠道码管理', 'menu', '/group-management/channel-code', 'LinkOutlined', 7, 'active', 'crs', 'crs:channelcode:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'crs-source-code', '来源码管理', 'menu', '/group-management/source-code', 'FileTextOutlined', 8, 'active', 'crs', 'crs:sourcecode:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'crs-tax-setting', '税和服务费设置', 'menu', '/group-management/tax-setting', 'SettingOutlined', 9, 'active', 'crs', 'crs:taxsetting:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'crs-group-package-setting', '包价设置', 'menu', '/group-management/package-setting', 'TagOutlined', 10, 'active', 'crs', 'crs:grouppackage:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'crs-group-guarantee', '集团担保政策管理', 'menu', '/group-management/group-guarantee', 'SafetyCertificateOutlined', 11, 'active', 'crs', 'crs:groupguarantee:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'crs-group-cancellation', '集团取消政策管理', 'menu', '/group-management/group-cancellation', 'CloseCircleOutlined', 12, 'active', 'crs', 'crs:groupcancellation:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'crs-facility-management', '集团设施管理', 'menu', '/group-management/facility-management', 'BuildOutlined', 13, 'active', 'crs', 'crs:facility:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'crs-archive-management', '档案管理', 'menu', '/group-management/archive-management', 'FolderOutlined', 14, 'active', 'crs', 'crs:archive:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'crs-group-promotion', '集团促销管理', 'directory', '/group-promotion-management', 'GiftOutlined', 9, 'active', 'crs', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(39, 'crs-ota-promotion', 'OTA促销管理', 'menu', '/group-promotion-management/ota-promotion-management', 'GlobalOutlined', 1, 'active', 'crs', 'crs:otapromotion:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(39, 'crs-ctrip-activity', '携程活动管理', 'menu', '/group-promotion-management/ctrip-activity-management', 'CalendarOutlined', 2, 'active', 'crs', 'crs:ctripactivity:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'crs-system-settings', '系统设置', 'directory', '/system-settings', 'SettingOutlined', 10, 'active', 'crs', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(42, 'crs-user-management', '用户管理', 'menu', '/system-settings/user-management', 'UserOutlined', 1, 'active', 'crs', 'system:user:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(42, 'crs-role-management', '角色管理', 'menu', '/system-settings/role-management', 'SafetyCertificateOutlined', 2, 'active', 'crs', 'system:role:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(42, 'crs-menu-management', '菜单管理', 'menu', '/system-settings/menu-management', 'MenuOutlined', 3, 'active', 'crs', 'system:menu:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(42, 'crs-group-settings', '集团设置', 'menu', '/system-settings/group-settings', 'BuildOutlined', 4, 'active', 'crs', 'crs:groupsettings:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(42, 'crs-custom-channel', '自定义渠道设置', 'menu', '/system-settings/custom-channel-setting', 'LinkOutlined', 5, 'active', 'crs', 'crs:customchannel:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'crs-super-admin', '超管设置', 'directory', '/super-admin-settings', 'SafetyCertificateOutlined', 11, 'active', 'crs', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(48, 'crs-tenant-management', '租户管理', 'menu', '/super-admin-settings/tenant-management', 'BuildOutlined', 1, 'active', 'crs', 'superadmin:tenant:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(48, 'crs-platform-settings', '平台设置', 'menu', '/super-admin-settings/platform-settings', 'SettingOutlined', 2, 'active', 'crs', 'superadmin:platform:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(48, 'crs-system-monitoring', '系统监控', 'menu', '/super-admin-settings/system-monitoring', 'BarChartOutlined', 3, 'active', 'crs', 'superadmin:monitoring:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(48, 'crs-super-role-management', '角色管理', 'menu', '/super-admin-settings/role-management', 'SafetyCertificateOutlined', 4, 'active', 'crs', 'superadmin:role:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(48, 'crs-super-menu-management', '菜单管理', 'menu', '/super-admin-settings/menu-management', 'MenuOutlined', 5, 'active', 'crs', 'superadmin:menu:view', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========================================
-- 2. 初始化角色数据（安全模式：使用INSERT IGNORE避免覆盖已有数据）
-- ========================================

INSERT IGNORE INTO roles (tenant_id, role_code, role_name, description, status, data_scope, created_at, updated_at) VALUES
(NULL, 'super_admin', '超级管理员', '拥有系统所有权限', 'active', 'all', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(NULL, 'admin', '系统管理员', '拥有系统管理权限', 'active', 'all', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(NULL, 'tenant_admin', '租户管理员', '拥有租户级权限', 'active', 'all', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(NULL, 'user', '普通用户', '拥有基础查看权限', 'active', 'self', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========================================
-- 3. 初始化超级管理员角色菜单关联（安全模式：仅在无数据时初始化）
-- ========================================

-- 初始化超级管理员角色菜单关联（所有菜单）- 仅在无数据时插入
INSERT IGNORE INTO role_menus (role_id, menu_id, created_at)
SELECT 1, id, CURRENT_TIMESTAMP FROM menus
WHERE NOT EXISTS (SELECT 1 FROM role_menus WHERE role_id = 1);

-- ========================================
-- 4. 初始化系统管理员角色菜单关联（安全模式：仅在无数据时初始化）
-- ========================================

-- 初始化系统管理员角色菜单关联（所有菜单）- 仅在无数据时插入
INSERT IGNORE INTO role_menus (role_id, menu_id, created_at)
SELECT 2, id, CURRENT_TIMESTAMP FROM menus
WHERE NOT EXISTS (SELECT 1 FROM role_menus WHERE role_id = 2);

-- ========================================
-- 4.1 初始化5个超级管理员用户（安全模式：使用INSERT IGNORE避免覆盖）
-- ========================================

-- 注意：使用INSERT IGNORE，不会覆盖已存在的用户
INSERT IGNORE INTO users (username, password, name, email, status, tenant_id, created_at, updated_at) VALUES
('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '超级管理员1', 'admin1@example.com', 'active', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin2', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '超级管理员2', 'admin2@example.com', 'active', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin3', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '超级管理员3', 'admin3@example.com', 'active', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin4', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '超级管理员4', 'admin4@example.com', 'active', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin5', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '超级管理员5', 'admin5@example.com', 'active', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========================================
-- 5. 更新admin用户密码（安全模式：仅在用户存在且需要时更新）
-- ========================================

-- 注意：此操作会覆盖admin用户密码，如果您已自定义密码，请注释掉此语句
-- UPDATE users SET password = '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW' WHERE username = 'admin';

-- ========================================
-- 6. 为admin用户分配超级管理员角色（安全模式：使用INSERT IGNORE避免覆盖）
-- ========================================

-- 注意：使用INSERT IGNORE，不会覆盖已存在的用户角色关联
INSERT IGNORE INTO user_roles (user_id, role_id, tenant_id, created_at) VALUES 
(1, 1, NULL, CURRENT_TIMESTAMP),
(2, 1, NULL, CURRENT_TIMESTAMP),
(3, 1, NULL, CURRENT_TIMESTAMP),
(4, 1, NULL, CURRENT_TIMESTAMP),
(5, 1, NULL, CURRENT_TIMESTAMP);

-- ========================================
-- 显示初始化结果
-- ========================================

SELECT CONCAT('认证数据初始化完成！当前共有 ', COUNT(*), ' 个菜单') AS result FROM menus
UNION ALL
SELECT CONCAT('认证数据初始化完成！当前共有 ', COUNT(*), ' 个角色') AS result FROM roles
UNION ALL
SELECT CONCAT('认证数据初始化完成！当前共有 ', COUNT(*), ' 条角色菜单关联') AS result FROM role_menus
UNION ALL
SELECT CONCAT('认证数据初始化完成！当前共有 ', COUNT(*), ' 个用户') AS result FROM users
UNION ALL
SELECT CONCAT('认证数据初始化完成！当前共有 ', COUNT(*), ' 条用户角色关联') AS result FROM user_roles;
