-- 使用CRS数据库
USE CRS;

-- 清空现有数据
DELETE FROM market_codes;
DELETE FROM market_code_categories;

-- 重置自增ID
ALTER TABLE market_codes AUTO_INCREMENT = 1;
ALTER TABLE market_code_categories AUTO_INCREMENT = 1;

-- 为租户1插入市场码数据
-- 根节点（分类）
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(1, 'CORPORATE', '企业客户', '企业协议客户', NULL, 1, 'active', NOW(), NOW()),
(1, 'TRAVEL_AGENCY', '旅行社', '旅行社合作渠道', NULL, 1, 'active', NOW(), NOW()),
(1, 'OTA', '在线旅游平台', '在线旅游预订平台', NULL, 1, 'active', NOW(), NOW()),
(1, 'DIRECT', '直客', '直接预订客户', NULL, 1, 'active', NOW(), NOW());

-- 企业客户子节点 (使用明确的ID引用)
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(1, 'CORP001', '阿里巴巴', '阿里巴巴集团协议客户', 1, 2, 'active', NOW(), NOW()),
(1, 'CORP002', '腾讯科技', '腾讯科技协议客户', 1, 2, 'active', NOW(), NOW()),
(1, 'CORP003', '华为技术', '华为技术协议客户', 1, 2, 'active', NOW(), NOW()),
(1, 'CORP004', '百度在线', '百度在线协议客户', 1, 2, 'active', NOW(), NOW()),
(1, 'CORP005', '京东集团', '京东集团协议客户', 1, 2, 'active', NOW(), NOW());

-- 旅行社子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(1, 'TA001', '中国国旅', '中国国际旅行社', 2, 2, 'active', NOW(), NOW()),
(1, 'TA002', '中青旅', '中国青年旅行社', 2, 2, 'active', NOW(), NOW()),
(1, 'TA003', '康辉旅游', '康辉国际旅行社', 2, 2, 'active', NOW(), NOW());

-- OTA子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(1, 'OTA001', '携程旅行', '携程旅行网', 3, 2, 'active', NOW(), NOW()),
(1, 'OTA002', '去哪儿', '去哪儿旅行', 3, 2, 'active', NOW(), NOW()),
(1, 'OTA003', '飞猪旅行', '飞猪旅行平台', 3, 2, 'active', NOW(), NOW()),
(1, 'OTA004', '同程旅行', '同程旅行网', 3, 2, 'active', NOW(), NOW());

-- 直客子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(1, 'DIRECT001', '官网预订', '锦江官网直接预订', 4, 2, 'active', NOW(), NOW()),
(1, 'DIRECT002', '微信小程序', '锦江酒店微信小程序', 4, 2, 'active', NOW(), NOW()),
(1, 'DIRECT003', 'APP预订', '锦江酒店APP预订', 4, 2, 'active', NOW(), NOW());

-- ========== 租户2: 华住酒店集团 ==========
-- 根节点（分类）
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(2, 'CORPORATE', '企业客户', '企业协议客户', NULL, 1, 'active', NOW(), NOW()),
(2, 'TRAVEL_AGENCY', '旅行社', '旅行社合作渠道', NULL, 1, 'active', NOW(), NOW()),
(2, 'OTA', '在线旅游平台', '在线旅游预订平台', NULL, 1, 'active', NOW(), NOW()),
(2, 'MEMBER', '会员', '华住会员', NULL, 1, 'active', NOW(), NOW());

-- 获取租户2的根节点ID
SET @tenant2_start_id = (SELECT MAX(id) FROM market_codes) + 1;

-- 企业客户子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(2, 'CORP001', '字节跳动', '字节跳动协议客户', @tenant2_start_id, 2, 'active', NOW(), NOW()),
(2, 'CORP002', '美团点评', '美团点评协议客户', @tenant2_start_id, 2, 'active', NOW(), NOW()),
(2, 'CORP003', '小米科技', '小米科技协议客户', @tenant2_start_id, 2, 'active', NOW(), NOW());

-- 旅行社子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(2, 'TA001', '携程国旅', '携程国际旅行社', @tenant2_start_id + 1, 2, 'active', NOW(), NOW()),
(2, 'TA002', '途牛旅游', '途牛旅游网', @tenant2_start_id + 1, 2, 'active', NOW(), NOW());

-- OTA子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(2, 'OTA001', '携程旅行', '携程旅行网', @tenant2_start_id + 2, 2, 'active', NOW(), NOW()),
(2, 'OTA002', '美团酒店', '美团酒店预订', @tenant2_start_id + 2, 2, 'active', NOW(), NOW());

-- 会员子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(2, 'MEMBER001', '华住金会员', '华住金会员', @tenant2_start_id + 3, 2, 'active', NOW(), NOW()),
(2, 'MEMBER002', '华住银会员', '华住银会员', @tenant2_start_id + 3, 2, 'active', NOW(), NOW()),
(2, 'MEMBER003', '华住铂金会员', '华住铂金会员', @tenant2_start_id + 3, 2, 'active', NOW(), NOW());

-- ========== 租户3: 万豪国际集团 ==========
-- 根节点（分类）
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(3, 'CORPORATE', '企业客户', '企业协议客户', NULL, 1, 'active', NOW(), NOW()),
(3, 'TRAVEL_AGENCY', '旅行社', '旅行社合作渠道', NULL, 1, 'active', NOW(), NOW()),
(3, 'OTA', '在线旅游平台', '在线旅游预订平台', NULL, 1, 'active', NOW(), NOW()),
(3, 'MARRIOTT_BONVOY', '万豪旅享家', '万豪会员体系', NULL, 1, 'active', NOW(), NOW());

SET @tenant3_start_id = (SELECT MAX(id) FROM market_codes) + 1;

-- 企业客户子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(3, 'CORP001', '微软中国', '微软中国协议客户', @tenant3_start_id, 2, 'active', NOW(), NOW()),
(3, 'CORP002', 'IBM中国', 'IBM中国协议客户', @tenant3_start_id, 2, 'active', NOW(), NOW()),
(3, 'CORP003', '通用电气', '通用电气协议客户', @tenant3_start_id, 2, 'active', NOW(), NOW());

-- 旅行社子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(3, 'TA001', '美国运通', '美国运通旅游', @tenant3_start_id + 1, 2, 'active', NOW(), NOW()),
(3, 'TA002', '嘉信旅游', '嘉信国际旅游', @tenant3_start_id + 1, 2, 'active', NOW(), NOW());

-- OTA子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(3, 'OTA001', 'Booking.com', '缤客预订平台', @tenant3_start_id + 2, 2, 'active', NOW(), NOW()),
(3, 'OTA002', 'Expedia', '亿客行预订平台', @tenant3_start_id + 2, 2, 'active', NOW(), NOW()),
(3, 'OTA003', 'Agoda', '安可达预订平台', @tenant3_start_id + 2, 2, 'active', NOW(), NOW());

-- 万豪旅享家子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(3, 'BONVOY001', '银卡会员', '万豪旅享家银卡', @tenant3_start_id + 3, 2, 'active', NOW(), NOW()),
(3, 'BONVOY002', '金卡会员', '万豪旅享家金卡', @tenant3_start_id + 3, 2, 'active', NOW(), NOW()),
(3, 'BONVOY003', '白金会员', '万豪旅享家白金卡', @tenant3_start_id + 3, 2, 'active', NOW(), NOW());

-- ========== 租户4: 希尔顿酒店集团 ==========
-- 根节点（分类）
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(4, 'CORPORATE', '企业客户', '企业协议客户', NULL, 1, 'active', NOW(), NOW()),
(4, 'TRAVEL_AGENCY', '旅行社', '旅行社合作渠道', NULL, 1, 'active', NOW(), NOW()),
(4, 'OTA', '在线旅游平台', '在线旅游预订平台', NULL, 1, 'active', NOW(), NOW()),
(4, 'HILTON_HONORS', '希尔顿荣誉客会', '希尔顿会员体系', NULL, 1, 'active', NOW(), NOW());

SET @tenant4_start_id = (SELECT MAX(id) FROM market_codes) + 1;

-- 企业客户子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(4, 'CORP001', '甲骨文中国', '甲骨文中国协议客户', @tenant4_start_id, 2, 'active', NOW(), NOW()),
(4, 'CORP002', '思科中国', '思科中国协议客户', @tenant4_start_id, 2, 'active', NOW(), NOW());

-- 旅行社子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(4, 'TA001', '卡尔森旅游', '卡尔森国际旅游', @tenant4_start_id + 1, 2, 'active', NOW(), NOW());

-- OTA子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(4, 'OTA001', 'Booking.com', '缤客预订平台', @tenant4_start_id + 2, 2, 'active', NOW(), NOW()),
(4, 'OTA002', 'Hotels.com', '好订网', @tenant4_start_id + 2, 2, 'active', NOW(), NOW());

-- 希尔顿荣誉客会子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(4, 'HONORS001', '蓝卡会员', '希尔顿荣誉客会蓝卡', @tenant4_start_id + 3, 2, 'active', NOW(), NOW()),
(4, 'HONORS002', '银卡会员', '希尔顿荣誉客会银卡', @tenant4_start_id + 3, 2, 'active', NOW(), NOW()),
(4, 'HONORS003', '金卡会员', '希尔顿荣誉客会金卡', @tenant4_start_id + 3, 2, 'active', NOW(), NOW()),
(4, 'HONORS004', '钻石会员', '希尔顿荣誉客会钻石卡', @tenant4_start_id + 3, 2, 'active', NOW(), NOW());

-- ========== 租户5: 洲际酒店集团 ==========
-- 根节点（分类）
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(5, 'CORPORATE', '企业客户', '企业协议客户', NULL, 1, 'active', NOW(), NOW()),
(5, 'TRAVEL_AGENCY', '旅行社', '旅行社合作渠道', NULL, 1, 'active', NOW(), NOW()),
(5, 'OTA', '在线旅游平台', '在线旅游预订平台', NULL, 1, 'active', NOW(), NOW()),
(5, 'IHG_REWARDS', 'IHG优悦会', '洲际会员体系', NULL, 1, 'active', NOW(), NOW());

SET @tenant5_start_id = (SELECT MAX(id) FROM market_codes) + 1;

-- 企业客户子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(5, 'CORP001', '壳牌中国', '壳牌中国协议客户', @tenant5_start_id, 2, 'active', NOW(), NOW()),
(5, 'CORP002', 'BP中国', 'BP中国协议客户', @tenant5_start_id, 2, 'active', NOW(), NOW());

-- 旅行社子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(5, 'TA001', '托马斯库克', '托马斯库克旅游', @tenant5_start_id + 1, 2, 'active', NOW(), NOW());

-- OTA子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(5, 'OTA001', 'Booking.com', '缤客预订平台', @tenant5_start_id + 2, 2, 'active', NOW(), NOW()),
(5, 'OTA002', 'Agoda', '安可达预订平台', @tenant5_start_id + 2, 2, 'active', NOW(), NOW());

-- IHG优悦会子节点
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(5, 'REWARDS001', '俱乐部会员', 'IHG优悦会俱乐部', @tenant5_start_id + 3, 2, 'active', NOW(), NOW()),
(5, 'REWARDS002', '金卡会员', 'IHG优悦会金卡', @tenant5_start_id + 3, 2, 'active', NOW(), NOW()),
(5, 'REWARDS003', '白金卡会员', 'IHG优悦会白金卡', @tenant5_start_id + 3, 2, 'active', NOW(), NOW()),
(5, 'REWARDS004', '至悦会员', 'IHG优悦会至悦', @tenant5_start_id + 3, 2, 'active', NOW(), NOW());
