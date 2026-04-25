-- 重新插入租户1的市场码分类和市场码数据

USE CRS;

-- 插入市场码分类数据
INSERT INTO market_code_categories (
    tenant_id,
    code,
    name,
    description,
    status,
    created_at,
    updated_at
) VALUES
-- 租户1: 万豪国际集团
(1, 'CORPORATE', '企业客户', '企业协议客户', 'active', NOW(), NOW()),
(1, 'TRAVEL_AGENCY', '旅行社', '旅行社合作渠道', 'active', NOW(), NOW()),
(1, 'OTA', '在线旅游平台', '在线旅游预订平台', 'active', NOW(), NOW()),
(1, 'DIRECT', '直客', '直接预订客户', 'active', NOW(), NOW());

-- 插入市场码数据
INSERT INTO market_codes (
    tenant_id,
    code,
    name,
    description,
    parent_id,
    level,
    status,
    created_at,
    updated_at
) VALUES
-- 租户1: 万豪国际集团 - 企业客户分类下的市场码
(1, 'CORP001', '微软中国', '微软中国协议客户', 1, 2, 'active', NOW(), NOW()),
(1, 'CORP002', 'IBM中国', 'IBM中国协议客户', 1, 2, 'active', NOW(), NOW()),
(1, 'CORP003', '通用电气', '通用电气协议客户', 1, 2, 'active', NOW(), NOW()),

-- 租户1: 旅行社分类下的市场码
(1, 'TA001', '美国运通', '美国运通旅游', 2, 2, 'active', NOW(), NOW()),
(1, 'TA002', '嘉信旅游', '嘉信国际旅游', 2, 2, 'active', NOW(), NOW()),

-- 租户1: OTA分类下的市场码
(1, 'OTA001', 'Booking.com', '缤客预订平台', 3, 2, 'active', NOW(), NOW()),
(1, 'OTA002', 'Expedia', '亿客行预订平台', 3, 2, 'active', NOW(), NOW()),
(1, 'OTA003', 'Agoda', '安可达预订平台', 3, 2, 'active', NOW(), NOW()),

-- 租户1: 直客分类下的市场码
(1, 'DIRECT001', '官网预订', '万豪官网直接预订', 4, 2, 'active', NOW(), NOW()),
(1, 'DIRECT002', 'APP预订', '万豪酒店APP预订', 4, 2, 'active', NOW(), NOW()),
(1, 'DIRECT003', '微信小程序', '万豪酒店微信小程序', 4, 2, 'active', NOW(), NOW());

SELECT CONCAT('市场码数据插入完成！共插入 ', COUNT(*), ' 条市场码分类数据') AS result FROM market_code_categories
UNION ALL
SELECT CONCAT('共插入 ', COUNT(*), ' 条市场码数据') AS result FROM market_codes;
