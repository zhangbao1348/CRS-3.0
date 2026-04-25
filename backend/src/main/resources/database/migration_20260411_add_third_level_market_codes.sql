-- 为每个租户添加第三级市场码

-- 租户1: 锦江酒店集团
-- 企业客户 -> CORP002 腾讯科技 -> 具体部门
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(1, 'CORP002-DEV', '腾讯研发部', '腾讯科技研发部门', 6, 3, 'active', NOW(), NOW()),
(1, 'CORP002-MKT', '腾讯市场部', '腾讯科技市场部门', 6, 3, 'active', NOW(), NOW()),
(1, 'CORP002-OP', '腾讯运营部', '腾讯科技运营部门', 6, 3, 'active', NOW(), NOW()),

-- 企业客户 -> CORP003 华为技术 -> 具体部门
(1, 'CORP003-RD', '华为研发部', '华为技术研发部门', 7, 3, 'active', NOW(), NOW()),
(1, 'CORP003-SALES', '华为销售部', '华为技术销售部门', 7, 3, 'active', NOW(), NOW()),
(1, 'CORP003-SUPPORT', '华为技术支持', '华为技术支持部门', 7, 3, 'active', NOW(), NOW()),

-- 旅行社 -> TA001 中国国旅 -> 具体类型
(1, 'TA001-OUTBOUND', '中国国旅出境游', '中国国旅出境旅游', 10, 3, 'active', NOW(), NOW()),
(1, 'TA001-INBOUND', '中国国旅入境游', '中国国旅入境旅游', 10, 3, 'active', NOW(), NOW()),
(1, 'TA001-DOMESTIC', '中国国旅国内游', '中国国旅国内旅游', 10, 3, 'active', NOW(), NOW()),

-- 在线旅游平台 -> OTA001 携程旅行 -> 具体产品
(1, 'OTA001-HOTEL', '携程酒店', '携程旅行酒店预订', 13, 3, 'active', NOW(), NOW()),
(1, 'OTA001-FLIGHT', '携程机票', '携程旅行机票预订', 13, 3, 'active', NOW(), NOW()),
(1, 'OTA001-PACKAGE', '携程套餐', '携程旅行套餐产品', 13, 3, 'active', NOW(), NOW()),

-- 直客 -> DIRECT001 官网预订 -> 具体渠道
(1, 'DIRECT001-PC', '官网PC端', '锦江官网PC端预订', 17, 3, 'active', NOW(), NOW()),
(1, 'DIRECT001-MOBILE', '官网移动端', '锦江官网移动端预订', 17, 3, 'active', NOW(), NOW()),
(1, 'DIRECT001-WECHAT', '官网微信', '锦江官网微信预订', 17, 3, 'active', NOW(), NOW());

-- 租户2: 华住酒店集团
-- 企业客户 -> CORP001 字节跳动 -> 具体部门
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(2, 'CORP001-ENG', '字节跳动工程', '字节跳动工程部门', 24, 3, 'active', NOW(), NOW()),
(2, 'CORP001-PROD', '字节跳动产品', '字节跳动产品部门', 24, 3, 'active', NOW(), NOW()),
(2, 'CORP001-MKT', '字节跳动市场', '字节跳动市场部门', 24, 3, 'active', NOW(), NOW()),

-- 旅行社 -> TA001 携程国旅 -> 具体类型
(2, 'TA001-BUSINESS', '携程国旅商务', '携程国旅商务旅游', 27, 3, 'active', NOW(), NOW()),
(2, 'TA001-LEISURE', '携程国旅休闲', '携程国旅休闲旅游', 27, 3, 'active', NOW(), NOW()),

-- 在线旅游平台 -> OTA001 携程旅行 -> 具体产品
(2, 'OTA001-HOTEL', '携程酒店', '携程旅行酒店预订', 29, 3, 'active', NOW(), NOW()),
(2, 'OTA001-HOMESTAY', '携程民宿', '携程旅行民宿预订', 29, 3, 'active', NOW(), NOW()),

-- 会员 -> MEMBER001 华住金会员 -> 具体等级
(2, 'MEMBER001-PLATINUM', '金会员-白金', '华住金会员白金等级', 31, 3, 'active', NOW(), NOW()),
(2, 'MEMBER001-GOLD', '金会员-黄金', '华住金会员黄金等级', 31, 3, 'active', NOW(), NOW());

-- 租户3: 万豪国际集团
-- 企业客户 -> CORP001 微软中国 -> 具体部门
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(3, 'CORP001-SALES', '微软销售', '微软中国销售部门', 38, 3, 'active', NOW(), NOW()),
(3, 'CORP001-TECH', '微软技术', '微软中国技术部门', 38, 3, 'active', NOW(), NOW()),

-- 在线旅游平台 -> OTA001 Booking.com -> 具体产品
(3, 'OTA001-HOTEL', 'Booking酒店', 'Booking.com酒店预订', 43, 3, 'active', NOW(), NOW()),
(3, 'OTA001-APARTMENT', 'Booking公寓', 'Booking.com公寓预订', 43, 3, 'active', NOW(), NOW()),

-- 万豪旅享家 -> BONVOY001 银卡会员 -> 具体权益
(3, 'BONVOY001-BASIC', '银卡基础', '万豪旅享家银卡基础权益', 46, 3, 'active', NOW(), NOW()),
(3, 'BONVOY001-PLUS', '银卡Plus', '万豪旅享家银卡Plus权益', 46, 3, 'active', NOW(), NOW());

-- 租户4: 希尔顿酒店集团
-- 企业客户 -> CORP001 甲骨文中国 -> 具体部门
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(4, 'CORP001-SALES', '甲骨文销售', '甲骨文中国销售部门', 53, 3, 'active', NOW(), NOW()),
(4, 'CORP001-CONSULT', '甲骨文咨询', '甲骨文中国咨询部门', 53, 3, 'active', NOW(), NOW()),

-- 在线旅游平台 -> OTA001 Booking.com -> 具体产品
(4, 'OTA001-HOTEL', 'Booking酒店', 'Booking.com酒店预订', 56, 3, 'active', NOW(), NOW()),
(4, 'OTA001-VILLA', 'Booking别墅', 'Booking.com别墅预订', 56, 3, 'active', NOW(), NOW()),

-- 希尔顿荣誉客会 -> HONORS001 蓝卡会员 -> 具体权益
(4, 'HONORS001-BASIC', '蓝卡基础', '希尔顿荣誉客会蓝卡基础权益', 58, 3, 'active', NOW(), NOW()),
(4, 'HONORS001-STANDARD', '蓝卡标准', '希尔顿荣誉客会蓝卡标准权益', 58, 3, 'active', NOW(), NOW());

-- 租户5: 洲际酒店集团
-- 企业客户 -> CORP001 壳牌中国 -> 具体部门
INSERT INTO market_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
(5, 'CORP001-OP', '壳牌运营', '壳牌中国运营部门', 66, 3, 'active', NOW(), NOW()),
(5, 'CORP001-TECH', '壳牌技术', '壳牌中国技术部门', 66, 3, 'active', NOW(), NOW()),

-- 在线旅游平台 -> OTA001 Booking.com -> 具体产品
(5, 'OTA001-HOTEL', 'Booking酒店', 'Booking.com酒店预订', 69, 3, 'active', NOW(), NOW()),
(5, 'OTA001-RESORT', 'Booking度假村', 'Booking.com度假村预订', 69, 3, 'active', NOW(), NOW()),

-- IHG优悦会 -> REWARDS001 俱乐部会员 -> 具体权益
(5, 'REWARDS001-BASIC', '俱乐部基础', 'IHG优悦会俱乐部基础权益', 71, 3, 'active', NOW(), NOW()),
(5, 'REWARDS001-STANDARD', '俱乐部标准', 'IHG优悦会俱乐部标准权益', 71, 3, 'active', NOW(), NOW());
