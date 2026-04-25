-- 使用CRS数据库
USE CRS;

-- ========================================
-- 初始化角色菜单关联（安全模式：仅在无数据时初始化）
-- ========================================

-- 检查是否已存在角色菜单关联数据，存在则跳过
-- 这样可以保护用户已自定义的角色菜单权限配置

-- 初始化超级管理员角色菜单关联（所有菜单）- 仅在无数据时插入
INSERT IGNORE INTO role_menus (role_id, menu_id, created_at)
SELECT 1, id, CURRENT_TIMESTAMP FROM menus
WHERE NOT EXISTS (SELECT 1 FROM role_menus WHERE role_id = 1);

-- 初始化系统管理员角色菜单关联（所有菜单）- 仅在无数据时插入
INSERT IGNORE INTO role_menus (role_id, menu_id, created_at)
SELECT 2, id, CURRENT_TIMESTAMP FROM menus
WHERE NOT EXISTS (SELECT 1 FROM role_menus WHERE role_id = 2);

-- 显示操作结果
SELECT CONCAT('角色菜单关联初始化完成！当前共有 ', COUNT(*), ' 条角色菜单关联记录') AS result 
FROM role_menus;