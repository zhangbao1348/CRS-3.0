-- 为每个租户插入房价大类数据
-- 根据现有的5个租户，为每个租户插入默认的房价大类

USE CRS;

-- 为租户1（万豪国际集团）插入房价大类
INSERT IGNORE INTO rate_types (tenant_id, code, name, description, sort_order, status, created_at, updated_at) VALUES
(1, 'BAR', '最佳可用房价', '酒店的标准房价，适用于所有客人', 1, 'active', NOW(), NOW()),
(1, 'CORP', '企业协议价', '与企业客户签订的协议价格', 2, 'active', NOW(), NOW()),
(1, 'PROMO', '促销价', '特别促销活动价格', 3, 'active', NOW(), NOW()),
(1, 'GROUP', '团队价', '适用于团队预订的价格', 4, 'active', NOW(), NOW()),
(1, 'PACKAGE', '包价', '包含额外服务的套餐价格', 5, 'active', NOW(), NOW()),
(1, 'LONGSTAY', '长住价', '适用于长期住宿客人的优惠价格', 6, 'active', NOW(), NOW());

-- 为租户2（希尔顿酒店集团）插入房价大类
INSERT IGNORE INTO rate_types (tenant_id, code, name, description, sort_order, status, created_at, updated_at) VALUES
(2, 'BAR', '最佳可用房价', '希尔顿荣誉客会最优弹性房价', 1, 'active', NOW(), NOW()),
(2, 'CORP', '企业协议价', '希尔顿企业客户协议价', 2, 'active', NOW(), NOW()),
(2, 'PROMO', '促销价', '希尔顿限时促销活动', 3, 'active', NOW(), NOW()),
(2, 'GROUP', '团队价', '希尔顿会议团队价格', 4, 'active', NOW(), NOW()),
(2, 'PACKAGE', '包价', '希尔顿套餐优惠', 5, 'active', NOW(), NOW()),
(2, 'HONORS', '荣誉客会价', '希尔顿荣誉客会会员专享价', 7, 'active', NOW(), NOW()),
(2, 'LONGSTAY', '长住价', '希尔顿长住优惠计划', 6, 'active', NOW(), NOW());

-- 为租户3（洲际酒店集团）插入房价大类
INSERT IGNORE INTO rate_types (tenant_id, code, name, description, sort_order, status, created_at, updated_at) VALUES
(3, 'BAR', '最佳可用房价', 'IHG最优弹性房价', 1, 'active', NOW(), NOW()),
(3, 'CORP', '企业协议价', 'IHG商务客户协议价', 2, 'active', NOW(), NOW()),
(3, 'PROMO', '促销价', 'IHG周末特惠活动', 3, 'active', NOW(), NOW()),
(3, 'GROUP', '团队价', 'IHG会议及活动团队价', 4, 'active', NOW(), NOW()),
(3, 'PACKAGE', '包价', 'IHG含早/含晚餐套餐', 5, 'active', NOW(), NOW()),
(3, 'IHGREWARDS', '优悦会价', 'IHG优悦会会员专享价', 7, 'active', NOW(), NOW()),
(3, 'LONGSTAY', '长住价', 'IHG长住套房优惠', 6, 'active', NOW(), NOW());

-- 为租户4（凯悦酒店集团）插入房价大类
INSERT IGNORE INTO rate_types (tenant_id, code, name, description, sort_order, status, created_at, updated_at) VALUES
(4, 'BAR', '最佳可用房价', '凯悦标准最优房价', 1, 'active', NOW(), NOW()),
(4, 'CORP', '企业协议价', '凯悦全球企业客户协议', 2, 'active', NOW(), NOW()),
(4, 'PROMO', '促销价', '凯悦季节促销活动', 3, 'active', NOW(), NOW()),
(4, 'GROUP', '团队价', '凯悦宴会及会议团队价', 4, 'active', NOW(), NOW()),
(4, 'PACKAGE', '包价', '凯悦水疗/餐饮套餐', 5, 'active', NOW(), NOW()),
(4, 'WORLDOFHYATT', '凯悦天地价', '凯悦天地会员专享', 7, 'active', NOW(), NOW()),
(4, 'LONGSTAY', '长住价', '凯悦长住公寓优惠', 6, 'active', NOW(), NOW());

-- 为租户5（雅高酒店集团）插入房价大类
INSERT IGNORE INTO rate_types (tenant_id, code, name, description, sort_order, status, created_at, updated_at) VALUES
(5, 'BAR', '最佳可用房价', '雅高标准最优房价', 1, 'active', NOW(), NOW()),
(5, 'CORP', '企业协议价', '雅高商务客户协议价', 2, 'active', NOW(), NOW()),
(5, 'PROMO', '促销价', '雅高A+会员促销活动', 3, 'active', NOW(), NOW()),
(5, 'GROUP', '团队价', '雅高活动及会议团队价', 4, 'active', NOW(), NOW()),
(5, 'PACKAGE', '包价', '雅高含早套餐优惠', 5, 'active', NOW(), NOW()),
(5, 'ALL', 'ALL会员价', '雅高ALL会员专享价', 7, 'active', NOW(), NOW()),
(5, 'LONGSTAY', '长住价', '雅高乐雅会长住优惠', 6, 'active', NOW(), NOW());

-- 显示完成信息
SELECT '各租户房价大类数据插入完成！' AS result;
SELECT tenant_id, COUNT(*) AS rate_type_count 
FROM rate_types 
GROUP BY tenant_id 
ORDER BY tenant_id;
