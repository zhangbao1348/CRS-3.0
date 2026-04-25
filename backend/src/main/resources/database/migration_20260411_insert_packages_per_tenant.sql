-- 为每个租户插入包价数据
-- 执行时间: 2026-04-11

USE CRS;

-- 先清空所有现有数据
DELETE FROM packages;

-- ============================================
-- 租户1: 锦江酒店集团
-- ============================================

-- 早餐类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(1, 'BREAKFAST', '早餐包价', '早餐', 'fixed', 1, 'daily', 'group', 30.00, 0, 'active', '包含每日早餐', NOW(), NOW()),
(1, 'FREE_BREAKFAST', '免费增早', '免费增早', 'fixed', 1, 'per_stay', 'hotel', NULL, 0, 'active', '免费增加一份早餐', NOW(), NOW());

-- 午餐类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(1, 'LUNCH', '午餐包价', '午餐', 'fixed', 1, 'daily', 'group', 50.00, 0, 'active', '包含每日午餐', NOW(), NOW());

-- 晚餐类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(1, 'DINNER', '晚餐包价', '晚餐', 'fixed', 1, 'daily', 'group', 80.00, 0, 'active', '包含每日晚餐', NOW(), NOW());

-- 综合类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(1, 'THREE_MEALS', '三餐包价', '综合', 'fixed', 3, 'daily', 'group', 150.00, 0, 'active', '包含每日三餐', NOW(), NOW()),
(1, 'LATE_CHECKOUT', '延时退房', '延时退房', 'fixed', 1, 'per_stay', 'group', 20.00, 0, 'active', '可延迟退房至14:00', NOW(), NOW()),
(1, 'EARLY_CHECKIN', '提前入住', '提前入住', 'fixed', 1, 'per_stay', 'group', 20.00, 0, 'active', '可提前入住至10:00', NOW(), NOW()),
(1, 'EXECUTIVE_LOUNGE', '行政礼遇', '综合', 'fixed', 1, 'daily', 'group', 100.00, 0, 'active', '包含行政酒廊使用权', NOW(), NOW()),
(1, 'SPA_PACKAGE', 'SPA包价', '综合', 'fixed', 1, 'per_stay', 'group', 200.00, 0, 'active', '包含一次SPA体验', NOW(), NOW()),
(1, 'FITNESS_PACKAGE', '健身包价', '综合', 'fixed', 1, 'daily', 'group', 50.00, 0, 'active', '包含健身房使用权', NOW(), NOW()),
(1, 'LAUNDRY_PACKAGE', '洗衣包价', '综合', 'fixed', 1, 'per_stay', 'group', 80.00, 0, 'active', '包含洗衣服务', NOW(), NOW()),
(1, 'AIRPORT_PICKUP', '接机包价', '综合', 'fixed', 1, 'per_stay', 'group', 150.00, 0, 'active', '包含机场接机服务', NOW(), NOW()),
(1, 'AIRPORT_DROPOFF', '送机包价', '综合', 'fixed', 1, 'per_stay', 'group', 150.00, 0, 'active', '包含机场送机服务', NOW(), NOW()),
(1, 'MEETING_PACKAGE', '会议包价', '综合', 'fixed', 1, 'per_stay', 'group', 300.00, 0, 'active', '包含会议室使用权', NOW(), NOW()),
(1, 'WEDDING_PACKAGE', '婚礼包价', '综合', 'fixed', 1, 'per_stay', 'group', 5000.00, 0, 'active', '包含婚礼场地使用权', NOW(), NOW()),
(1, 'BIRTHDAY_PACKAGE', '生日包价', '综合', 'fixed', 1, 'per_stay', 'group', 200.00, 0, 'active', '包含生日蛋糕和布置', NOW(), NOW()),
(1, 'HONEYMOON_PACKAGE', '蜜月包价', '综合', 'fixed', 1, 'per_stay', 'group', 500.00, 0, 'active', '包含蜜月布置和香槟', NOW(), NOW()),
(1, 'FAMILY_PACKAGE', '家庭包价', '综合', 'fixed', 1, 'per_stay', 'group', 300.00, 0, 'active', '包含儿童用品和活动', NOW(), NOW()),
(1, 'BUSINESS_PACKAGE', '商务包价', '综合', 'fixed', 1, 'daily', 'group', 100.00, 0, 'active', '包含商务中心服务', NOW(), NOW()),
(1, 'VACATION_PACKAGE', '度假包价', '综合', 'fixed', 1, 'per_stay', 'group', 500.00, 0, 'active', '包含景点门票和活动', NOW(), NOW());

-- ============================================
-- 租户2: 华住酒店集团
-- ============================================

-- 早餐类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(2, 'BREAKFAST', '早餐包价', '早餐', 'fixed', 1, 'daily', 'group', 28.00, 0, 'active', '包含每日早餐', NOW(), NOW()),
(2, 'FREE_BREAKFAST', '免费增早', '免费增早', 'fixed', 1, 'per_stay', 'hotel', NULL, 0, 'active', '免费增加一份早餐', NOW(), NOW());

-- 午餐类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(2, 'LUNCH', '午餐包价', '午餐', 'fixed', 1, 'daily', 'group', 45.00, 0, 'active', '包含每日午餐', NOW(), NOW());

-- 晚餐类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(2, 'DINNER', '晚餐包价', '晚餐', 'fixed', 1, 'daily', 'group', 75.00, 0, 'active', '包含每日晚餐', NOW(), NOW());

-- 综合类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(2, 'THREE_MEALS', '三餐包价', '综合', 'fixed', 3, 'daily', 'group', 140.00, 0, 'active', '包含每日三餐', NOW(), NOW()),
(2, 'LATE_CHECKOUT', '延时退房', '延时退房', 'fixed', 1, 'per_stay', 'group', 18.00, 0, 'active', '可延迟退房至14:00', NOW(), NOW()),
(2, 'EARLY_CHECKIN', '提前入住', '提前入住', 'fixed', 1, 'per_stay', 'group', 18.00, 0, 'active', '可提前入住至10:00', NOW(), NOW()),
(2, 'EXECUTIVE_LOUNGE', '行政礼遇', '综合', 'fixed', 1, 'daily', 'group', 90.00, 0, 'active', '包含行政酒廊使用权', NOW(), NOW()),
(2, 'SPA_PACKAGE', 'SPA包价', '综合', 'fixed', 1, 'per_stay', 'group', 180.00, 0, 'active', '包含一次SPA体验', NOW(), NOW()),
(2, 'FITNESS_PACKAGE', '健身包价', '综合', 'fixed', 1, 'daily', 'group', 45.00, 0, 'active', '包含健身房使用权', NOW(), NOW()),
(2, 'LAUNDRY_PACKAGE', '洗衣包价', '综合', 'fixed', 1, 'per_stay', 'group', 75.00, 0, 'active', '包含洗衣服务', NOW(), NOW()),
(2, 'AIRPORT_PICKUP', '接机包价', '综合', 'fixed', 1, 'per_stay', 'group', 140.00, 0, 'active', '包含机场接机服务', NOW(), NOW()),
(2, 'AIRPORT_DROPOFF', '送机包价', '综合', 'fixed', 1, 'per_stay', 'group', 140.00, 0, 'active', '包含机场送机服务', NOW(), NOW()),
(2, 'MEETING_PACKAGE', '会议包价', '综合', 'fixed', 1, 'per_stay', 'group', 280.00, 0, 'active', '包含会议室使用权', NOW(), NOW()),
(2, 'BIRTHDAY_PACKAGE', '生日包价', '综合', 'fixed', 1, 'per_stay', 'group', 180.00, 0, 'active', '包含生日蛋糕和布置', NOW(), NOW()),
(2, 'FAMILY_PACKAGE', '家庭包价', '综合', 'fixed', 1, 'per_stay', 'group', 280.00, 0, 'active', '包含儿童用品和活动', NOW(), NOW()),
(2, 'BUSINESS_PACKAGE', '商务包价', '综合', 'fixed', 1, 'daily', 'group', 90.00, 0, 'active', '包含商务中心服务', NOW(), NOW()),
(2, 'VACATION_PACKAGE', '度假包价', '综合', 'fixed', 1, 'per_stay', 'group', 450.00, 0, 'active', '包含景点门票和活动', NOW(), NOW());

-- ============================================
-- 租户3: 万豪国际集团
-- ============================================

-- 早餐类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(3, 'BREAKFAST', '早餐包价', '早餐', 'fixed', 1, 'daily', 'group', 35.00, 0, 'active', '包含每日早餐', NOW(), NOW()),
(3, 'FREE_BREAKFAST', '免费增早', '免费增早', 'fixed', 1, 'per_stay', 'hotel', NULL, 0, 'active', '免费增加一份早餐', NOW(), NOW());

-- 午餐类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(3, 'LUNCH', '午餐包价', '午餐', 'fixed', 1, 'daily', 'group', 55.00, 0, 'active', '包含每日午餐', NOW(), NOW());

-- 晚餐类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(3, 'DINNER', '晚餐包价', '晚餐', 'fixed', 1, 'daily', 'group', 90.00, 0, 'active', '包含每日晚餐', NOW(), NOW());

-- 综合类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(3, 'THREE_MEALS', '三餐包价', '综合', 'fixed', 3, 'daily', 'group', 165.00, 0, 'active', '包含每日三餐', NOW(), NOW()),
(3, 'LATE_CHECKOUT', '延时退房', '延时退房', 'fixed', 1, 'per_stay', 'group', 25.00, 0, 'active', '可延迟退房至14:00', NOW(), NOW()),
(3, 'EARLY_CHECKIN', '提前入住', '提前入住', 'fixed', 1, 'per_stay', 'group', 25.00, 0, 'active', '可提前入住至10:00', NOW(), NOW()),
(3, 'EXECUTIVE_LOUNGE', '行政礼遇', '综合', 'fixed', 1, 'daily', 'group', 120.00, 0, 'active', '包含行政酒廊使用权', NOW(), NOW()),
(3, 'SPA_PACKAGE', 'SPA包价', '综合', 'fixed', 1, 'per_stay', 'group', 250.00, 0, 'active', '包含一次SPA体验', NOW(), NOW()),
(3, 'FITNESS_PACKAGE', '健身包价', '综合', 'fixed', 1, 'daily', 'group', 60.00, 0, 'active', '包含健身房使用权', NOW(), NOW()),
(3, 'LAUNDRY_PACKAGE', '洗衣包价', '综合', 'fixed', 1, 'per_stay', 'group', 95.00, 0, 'active', '包含洗衣服务', NOW(), NOW()),
(3, 'AIRPORT_PICKUP', '接机包价', '综合', 'fixed', 1, 'per_stay', 'group', 180.00, 0, 'active', '包含机场接机服务', NOW(), NOW()),
(3, 'AIRPORT_DROPOFF', '送机包价', '综合', 'fixed', 1, 'per_stay', 'group', 180.00, 0, 'active', '包含机场送机服务', NOW(), NOW()),
(3, 'MEETING_PACKAGE', '会议包价', '综合', 'fixed', 1, 'per_stay', 'group', 350.00, 0, 'active', '包含会议室使用权', NOW(), NOW()),
(3, 'WEDDING_PACKAGE', '婚礼包价', '综合', 'fixed', 1, 'per_stay', 'group', 6000.00, 0, 'active', '包含婚礼场地使用权', NOW(), NOW()),
(3, 'BIRTHDAY_PACKAGE', '生日包价', '综合', 'fixed', 1, 'per_stay', 'group', 250.00, 0, 'active', '包含生日蛋糕和布置', NOW(), NOW()),
(3, 'HONEYMOON_PACKAGE', '蜜月包价', '综合', 'fixed', 1, 'per_stay', 'group', 600.00, 0, 'active', '包含蜜月布置和香槟', NOW(), NOW()),
(3, 'FAMILY_PACKAGE', '家庭包价', '综合', 'fixed', 1, 'per_stay', 'group', 350.00, 0, 'active', '包含儿童用品和活动', NOW(), NOW()),
(3, 'BUSINESS_PACKAGE', '商务包价', '综合', 'fixed', 1, 'daily', 'group', 120.00, 0, 'active', '包含商务中心服务', NOW(), NOW()),
(3, 'VACATION_PACKAGE', '度假包价', '综合', 'fixed', 1, 'per_stay', 'group', 600.00, 0, 'active', '包含景点门票和活动', NOW(), NOW());

-- ============================================
-- 租户4: 希尔顿酒店集团
-- ============================================

-- 早餐类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(4, 'BREAKFAST', '早餐包价', '早餐', 'fixed', 1, 'daily', 'group', 32.00, 0, 'active', '包含每日早餐', NOW(), NOW()),
(4, 'FREE_BREAKFAST', '免费增早', '免费增早', 'fixed', 1, 'per_stay', 'hotel', NULL, 0, 'active', '免费增加一份早餐', NOW(), NOW());

-- 午餐类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(4, 'LUNCH', '午餐包价', '午餐', 'fixed', 1, 'daily', 'group', 52.00, 0, 'active', '包含每日午餐', NOW(), NOW());

-- 晚餐类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(4, 'DINNER', '晚餐包价', '晚餐', 'fixed', 1, 'daily', 'group', 85.00, 0, 'active', '包含每日晚餐', NOW(), NOW());

-- 综合类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(4, 'THREE_MEALS', '三餐包价', '综合', 'fixed', 3, 'daily', 'group', 158.00, 0, 'active', '包含每日三餐', NOW(), NOW()),
(4, 'LATE_CHECKOUT', '延时退房', '延时退房', 'fixed', 1, 'per_stay', 'group', 22.00, 0, 'active', '可延迟退房至14:00', NOW(), NOW()),
(4, 'EARLY_CHECKIN', '提前入住', '提前入住', 'fixed', 1, 'per_stay', 'group', 22.00, 0, 'active', '可提前入住至10:00', NOW(), NOW()),
(4, 'EXECUTIVE_LOUNGE', '行政礼遇', '综合', 'fixed', 1, 'daily', 'group', 110.00, 0, 'active', '包含行政酒廊使用权', NOW(), NOW()),
(4, 'SPA_PACKAGE', 'SPA包价', '综合', 'fixed', 1, 'per_stay', 'group', 220.00, 0, 'active', '包含一次SPA体验', NOW(), NOW()),
(4, 'FITNESS_PACKAGE', '健身包价', '综合', 'fixed', 1, 'daily', 'group', 55.00, 0, 'active', '包含健身房使用权', NOW(), NOW()),
(4, 'LAUNDRY_PACKAGE', '洗衣包价', '综合', 'fixed', 1, 'per_stay', 'group', 88.00, 0, 'active', '包含洗衣服务', NOW(), NOW()),
(4, 'AIRPORT_PICKUP', '接机包价', '综合', 'fixed', 1, 'per_stay', 'group', 165.00, 0, 'active', '包含机场接机服务', NOW(), NOW()),
(4, 'AIRPORT_DROPOFF', '送机包价', '综合', 'fixed', 1, 'per_stay', 'group', 165.00, 0, 'active', '包含机场送机服务', NOW(), NOW()),
(4, 'MEETING_PACKAGE', '会议包价', '综合', 'fixed', 1, 'per_stay', 'group', 330.00, 0, 'active', '包含会议室使用权', NOW(), NOW()),
(4, 'BIRTHDAY_PACKAGE', '生日包价', '综合', 'fixed', 1, 'per_stay', 'group', 220.00, 0, 'active', '包含生日蛋糕和布置', NOW(), NOW()),
(4, 'FAMILY_PACKAGE', '家庭包价', '综合', 'fixed', 1, 'per_stay', 'group', 330.00, 0, 'active', '包含儿童用品和活动', NOW(), NOW()),
(4, 'BUSINESS_PACKAGE', '商务包价', '综合', 'fixed', 1, 'daily', 'group', 110.00, 0, 'active', '包含商务中心服务', NOW(), NOW());

-- ============================================
-- 租户5: 洲际酒店集团
-- ============================================

-- 早餐类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(5, 'BREAKFAST', '早餐包价', '早餐', 'fixed', 1, 'daily', 'group', 33.00, 0, 'active', '包含每日早餐', NOW(), NOW()),
(5, 'FREE_BREAKFAST', '免费增早', '免费增早', 'fixed', 1, 'per_stay', 'hotel', NULL, 0, 'active', '免费增加一份早餐', NOW(), NOW());

-- 午餐类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(5, 'LUNCH', '午餐包价', '午餐', 'fixed', 1, 'daily', 'group', 53.00, 0, 'active', '包含每日午餐', NOW(), NOW());

-- 晚餐类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(5, 'DINNER', '晚餐包价', '晚餐', 'fixed', 1, 'daily', 'group', 88.00, 0, 'active', '包含每日晚餐', NOW(), NOW());

-- 综合类
INSERT INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(5, 'THREE_MEALS', '三餐包价', '综合', 'fixed', 3, 'daily', 'group', 162.00, 0, 'active', '包含每日三餐', NOW(), NOW()),
(5, 'LATE_CHECKOUT', '延时退房', '延时退房', 'fixed', 1, 'per_stay', 'group', 23.00, 0, 'active', '可延迟退房至14:00', NOW(), NOW()),
(5, 'EARLY_CHECKIN', '提前入住', '提前入住', 'fixed', 1, 'per_stay', 'group', 23.00, 0, 'active', '可提前入住至10:00', NOW(), NOW()),
(5, 'EXECUTIVE_LOUNGE', '行政礼遇', '综合', 'fixed', 1, 'daily', 'group', 115.00, 0, 'active', '包含行政酒廊使用权', NOW(), NOW()),
(5, 'SPA_PACKAGE', 'SPA包价', '综合', 'fixed', 1, 'per_stay', 'group', 230.00, 0, 'active', '包含一次SPA体验', NOW(), NOW()),
(5, 'FITNESS_PACKAGE', '健身包价', '综合', 'fixed', 1, 'daily', 'group', 58.00, 0, 'active', '包含健身房使用权', NOW(), NOW()),
(5, 'LAUNDRY_PACKAGE', '洗衣包价', '综合', 'fixed', 1, 'per_stay', 'group', 92.00, 0, 'active', '包含洗衣服务', NOW(), NOW()),
(5, 'AIRPORT_PICKUP', '接机包价', '综合', 'fixed', 1, 'per_stay', 'group', 170.00, 0, 'active', '包含机场接机服务', NOW(), NOW()),
(5, 'AIRPORT_DROPOFF', '送机包价', '综合', 'fixed', 1, 'per_stay', 'group', 170.00, 0, 'active', '包含机场送机服务', NOW(), NOW()),
(5, 'MEETING_PACKAGE', '会议包价', '综合', 'fixed', 1, 'per_stay', 'group', 340.00, 0, 'active', '包含会议室使用权', NOW(), NOW()),
(5, 'BIRTHDAY_PACKAGE', '生日包价', '综合', 'fixed', 1, 'per_stay', 'group', 230.00, 0, 'active', '包含生日蛋糕和布置', NOW(), NOW()),
(5, 'FAMILY_PACKAGE', '家庭包价', '综合', 'fixed', 1, 'per_stay', 'group', 340.00, 0, 'active', '包含儿童用品和活动', NOW(), NOW()),
(5, 'BUSINESS_PACKAGE', '商务包价', '综合', 'fixed', 1, 'daily', 'group', 115.00, 0, 'active', '包含商务中心服务', NOW(), NOW());

-- 显示插入数据统计
SELECT tenant_id, COUNT(*) as package_count FROM packages GROUP BY tenant_id ORDER BY tenant_id;

SELECT '包价模拟数据插入成功' AS message;
