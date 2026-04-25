-- 为每个租户插入房价大类数据

USE CRS;

-- 为租户1插入房价大类
INSERT IGNORE INTO rate_types (tenant_id, code, name, description, sort_order, status, created_at, updated_at) VALUES
(1, 'BAR', '最佳可用房价', '酒店的标准房价，适用于所有客人', 1, 'active', NOW(), NOW()),
(1, 'CORP', '企业协议价', '与企业客户签订的协议价格', 2, 'active', NOW(), NOW()),
(1, 'PROMO', '促销价', '特别促销活动价格', 3, 'active', NOW(), NOW()),
(1, 'GROUP', '团队价', '适用于团队预订的价格', 4, 'active', NOW(), NOW()),
(1, 'PACKAGE', '包价', '包含额外服务的套餐价格', 5, 'active', NOW(), NOW()),
(1, 'LONGSTAY', '长住价', '适用于长期住宿客人的优惠价格', 6, 'active', NOW(), NOW());

-- 为租户2插入房价大类
INSERT IGNORE INTO rate_types (tenant_id, code, name, description, sort_order, status, created_at, updated_at) VALUES
(2, 'BAR', '最佳可用房价', '酒店的标准房价，适用于所有客人', 1, 'active', NOW(), NOW()),
(2, 'CORP', '企业协议价', '与企业客户签订的协议价格', 2, 'active', NOW(), NOW()),
(2, 'PROMO', '促销价', '特别促销活动价格', 3, 'active', NOW(), NOW()),
(2, 'GROUP', '团队价', '适用于团队预订的价格', 4, 'active', NOW(), NOW()),
(2, 'PACKAGE', '包价', '包含额外服务的套餐价格', 5, 'active', NOW(), NOW()),
(2, 'LONGSTAY', '长住价', '适用于长期住宿客人的优惠价格', 6, 'active', NOW(), NOW());

-- 为租户3插入房价大类
INSERT IGNORE INTO rate_types (tenant_id, code, name, description, sort_order, status, created_at, updated_at) VALUES
(3, 'BAR', '最佳可用房价', '酒店的标准房价，适用于所有客人', 1, 'active', NOW(), NOW()),
(3, 'CORP', '企业协议价', '与企业客户签订的协议价格', 2, 'active', NOW(), NOW()),
(3, 'PROMO', '促销价', '特别促销活动价格', 3, 'active', NOW(), NOW()),
(3, 'GROUP', '团队价', '适用于团队预订的价格', 4, 'active', NOW(), NOW()),
(3, 'PACKAGE', '包价', '包含额外服务的套餐价格', 5, 'active', NOW(), NOW()),
(3, 'LONGSTAY', '长住价', '适用于长期住宿客人的优惠价格', 6, 'active', NOW(), NOW());

-- 显示完成信息
SELECT CONCAT('房价大类数据插入完成！共为 ', COUNT(DISTINCT tenant_id), ' 个租户插入 ', COUNT(*), ' 条房价大类数据') AS result 
FROM rate_types;

-- 查看各租户的房价大类数量
SELECT tenant_id AS 租户ID, COUNT(*) AS 房价大类数量 
FROM rate_types 
GROUP BY tenant_id 
ORDER BY tenant_id;
