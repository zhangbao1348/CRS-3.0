-- 为每个租户插入房型大类数据

USE CRS;

-- 为租户1插入房型大类
INSERT IGNORE INTO room_type_categories (tenant_id, group_id, category_code, category_name, sort_order, status, created_at, updated_at) VALUES
(1, 1, 'STANDARD', '标准房', 1, 'active', NOW(), NOW()),
(1, 1, 'DELUXE', '豪华房', 2, 'active', NOW(), NOW()),
(1, 1, 'SUITE', '套房', 3, 'active', NOW(), NOW()),
(1, 1, 'FAMILY', '家庭房', 4, 'active', NOW(), NOW()),
(1, 1, 'SPECIAL', '特色房', 5, 'active', NOW(), NOW());

-- 为租户2插入房型大类
INSERT IGNORE INTO room_type_categories (tenant_id, group_id, category_code, category_name, sort_order, status, created_at, updated_at) VALUES
(2, 2, 'STANDARD', '标准房', 1, 'active', NOW(), NOW()),
(2, 2, 'DELUXE', '豪华房', 2, 'active', NOW(), NOW()),
(2, 2, 'SUITE', '套房', 3, 'active', NOW(), NOW()),
(2, 2, 'FAMILY', '家庭房', 4, 'active', NOW(), NOW()),
(2, 2, 'SPECIAL', '特色房', 5, 'active', NOW(), NOW());

-- 为租户3插入房型大类
INSERT IGNORE INTO room_type_categories (tenant_id, group_id, category_code, category_name, sort_order, status, created_at, updated_at) VALUES
(3, 3, 'STANDARD', '标准房', 1, 'active', NOW(), NOW()),
(3, 3, 'DELUXE', '豪华房', 2, 'active', NOW(), NOW()),
(3, 3, 'SUITE', '套房', 3, 'active', NOW(), NOW()),
(3, 3, 'FAMILY', '家庭房', 4, 'active', NOW(), NOW()),
(3, 3, 'SPECIAL', '特色房', 5, 'active', NOW(), NOW());

-- 显示插入结果
SELECT CONCAT('房型大类数据初始化完成！共为 ', COUNT(DISTINCT tenant_id), ' 个租户插入 ', COUNT(*), ' 条房型大类数据') AS result 
FROM room_type_categories;

-- 查看各租户的房型大类数量
SELECT tenant_id AS 租户ID, COUNT(*) AS 房型大类数量 
FROM room_type_categories 
GROUP BY tenant_id 
ORDER BY tenant_id;
