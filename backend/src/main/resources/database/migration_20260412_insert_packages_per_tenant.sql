-- 为每个租户插入包价数据

USE CRS;

-- ============================================
-- 租户1
-- ============================================

-- 早餐类
INSERT IGNORE INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(1, 'BREAKFAST', '早餐包价', '早餐', 'fixed', 1, 'daily', 'group', 30.00, 0, 'active', '包含每日早餐', NOW(), NOW()),
(1, 'FREE_BREAKFAST', '免费增早', '免费增早', 'fixed', 1, 'per_stay', 'hotel', NULL, 0, 'active', '免费增加一份早餐', NOW(), NOW());

-- 午餐类
INSERT IGNORE INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(1, 'LUNCH', '午餐包价', '午餐', 'fixed', 1, 'daily', 'group', 50.00, 0, 'active', '包含每日午餐', NOW(), NOW());

-- 晚餐类
INSERT IGNORE INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(1, 'DINNER', '晚餐包价', '晚餐', 'fixed', 1, 'daily', 'group', 80.00, 0, 'active', '包含每日晚餐', NOW(), NOW());

-- 综合类
INSERT IGNORE INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
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
(1, 'BIRTHDAY_PACKAGE', '生日包价', '综合', 'fixed', 1, 'per_stay', 'group', 200.00, 0, 'active', '包含生日蛋糕和布置', NOW(), NOW()),
(1, 'FAMILY_PACKAGE', '家庭包价', '综合', 'fixed', 1, 'per_stay', 'group', 300.00, 0, 'active', '包含儿童用品和活动', NOW(), NOW()),
(1, 'BUSINESS_PACKAGE', '商务包价', '综合', 'fixed', 1, 'daily', 'group', 100.00, 0, 'active', '包含商务中心服务', NOW(), NOW()),
(1, 'VACATION_PACKAGE', '度假包价', '综合', 'fixed', 1, 'per_stay', 'group', 500.00, 0, 'active', '包含景点门票和活动', NOW(), NOW());

-- ============================================
-- 租户2
-- ============================================

-- 早餐类
INSERT IGNORE INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(2, 'T2_BREAKFAST', '早餐包价', '早餐', 'fixed', 1, 'daily', 'group', 28.00, 0, 'active', '包含每日早餐', NOW(), NOW()),
(2, 'T2_FREE_BREAKFAST', '免费增早', '免费增早', 'fixed', 1, 'per_stay', 'hotel', NULL, 0, 'active', '免费增加一份早餐', NOW(), NOW());

-- 午餐类
INSERT IGNORE INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(2, 'T2_LUNCH', '午餐包价', '午餐', 'fixed', 1, 'daily', 'group', 45.00, 0, 'active', '包含每日午餐', NOW(), NOW());

-- 晚餐类
INSERT IGNORE INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(2, 'T2_DINNER', '晚餐包价', '晚餐', 'fixed', 1, 'daily', 'group', 75.00, 0, 'active', '包含每日晚餐', NOW(), NOW());

-- 综合类
INSERT IGNORE INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(2, 'T2_THREE_MEALS', '三餐包价', '综合', 'fixed', 3, 'daily', 'group', 140.00, 0, 'active', '包含每日三餐', NOW(), NOW()),
(2, 'T2_LATE_CHECKOUT', '延时退房', '延时退房', 'fixed', 1, 'per_stay', 'group', 18.00, 0, 'active', '可延迟退房至14:00', NOW(), NOW()),
(2, 'T2_EARLY_CHECKIN', '提前入住', '提前入住', 'fixed', 1, 'per_stay', 'group', 18.00, 0, 'active', '可提前入住至10:00', NOW(), NOW()),
(2, 'T2_EXECUTIVE_LOUNGE', '行政礼遇', '综合', 'fixed', 1, 'daily', 'group', 90.00, 0, 'active', '包含行政酒廊使用权', NOW(), NOW()),
(2, 'T2_SPA_PACKAGE', 'SPA包价', '综合', 'fixed', 1, 'per_stay', 'group', 180.00, 0, 'active', '包含一次SPA体验', NOW(), NOW()),
(2, 'T2_FITNESS_PACKAGE', '健身包价', '综合', 'fixed', 1, 'daily', 'group', 45.00, 0, 'active', '包含健身房使用权', NOW(), NOW()),
(2, 'T2_LAUNDRY_PACKAGE', '洗衣包价', '综合', 'fixed', 1, 'per_stay', 'group', 75.00, 0, 'active', '包含洗衣服务', NOW(), NOW()),
(2, 'T2_AIRPORT_PICKUP', '接机包价', '综合', 'fixed', 1, 'per_stay', 'group', 140.00, 0, 'active', '包含机场接机服务', NOW(), NOW()),
(2, 'T2_AIRPORT_DROPOFF', '送机包价', '综合', 'fixed', 1, 'per_stay', 'group', 140.00, 0, 'active', '包含机场送机服务', NOW(), NOW()),
(2, 'T2_MEETING_PACKAGE', '会议包价', '综合', 'fixed', 1, 'per_stay', 'group', 280.00, 0, 'active', '包含会议室使用权', NOW(), NOW()),
(2, 'T2_BIRTHDAY_PACKAGE', '生日包价', '综合', 'fixed', 1, 'per_stay', 'group', 180.00, 0, 'active', '包含生日蛋糕和布置', NOW(), NOW()),
(2, 'T2_FAMILY_PACKAGE', '家庭包价', '综合', 'fixed', 1, 'per_stay', 'group', 280.00, 0, 'active', '包含儿童用品和活动', NOW(), NOW()),
(2, 'T2_BUSINESS_PACKAGE', '商务包价', '综合', 'fixed', 1, 'daily', 'group', 90.00, 0, 'active', '包含商务中心服务', NOW(), NOW()),
(2, 'T2_VACATION_PACKAGE', '度假包价', '综合', 'fixed', 1, 'per_stay', 'group', 450.00, 0, 'active', '包含景点门票和活动', NOW(), NOW());

-- ============================================
-- 租户3
-- ============================================

-- 早餐类
INSERT IGNORE INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(3, 'T3_BREAKFAST', '早餐包价', '早餐', 'fixed', 1, 'daily', 'group', 35.00, 0, 'active', '包含每日早餐', NOW(), NOW()),
(3, 'T3_FREE_BREAKFAST', '免费增早', '免费增早', 'fixed', 1, 'per_stay', 'hotel', NULL, 0, 'active', '免费增加一份早餐', NOW(), NOW());

-- 午餐类
INSERT IGNORE INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(3, 'T3_LUNCH', '午餐包价', '午餐', 'fixed', 1, 'daily', 'group', 55.00, 0, 'active', '包含每日午餐', NOW(), NOW());

-- 晚餐类
INSERT IGNORE INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(3, 'T3_DINNER', '晚餐包价', '晚餐', 'fixed', 1, 'daily', 'group', 90.00, 0, 'active', '包含每日晚餐', NOW(), NOW());

-- 综合类
INSERT IGNORE INTO packages (tenant_id, code, name, type, quantity_type, fixed_quantity, frequency, price_type, fixed_price, tax_included, status, description, created_at, updated_at) VALUES
(3, 'T3_THREE_MEALS', '三餐包价', '综合', 'fixed', 3, 'daily', 'group', 165.00, 0, 'active', '包含每日三餐', NOW(), NOW()),
(3, 'T3_LATE_CHECKOUT', '延时退房', '延时退房', 'fixed', 1, 'per_stay', 'group', 25.00, 0, 'active', '可延迟退房至14:00', NOW(), NOW()),
(3, 'T3_EARLY_CHECKIN', '提前入住', '提前入住', 'fixed', 1, 'per_stay', 'group', 25.00, 0, 'active', '可提前入住至10:00', NOW(), NOW()),
(3, 'T3_EXECUTIVE_LOUNGE', '行政礼遇', '综合', 'fixed', 1, 'daily', 'group', 120.00, 0, 'active', '包含行政酒廊使用权', NOW(), NOW()),
(3, 'T3_SPA_PACKAGE', 'SPA包价', '综合', 'fixed', 1, 'per_stay', 'group', 250.00, 0, 'active', '包含一次SPA体验', NOW(), NOW()),
(3, 'T3_FITNESS_PACKAGE', '健身包价', '综合', 'fixed', 1, 'daily', 'group', 60.00, 0, 'active', '包含健身房使用权', NOW(), NOW()),
(3, 'T3_LAUNDRY_PACKAGE', '洗衣包价', '综合', 'fixed', 1, 'per_stay', 'group', 95.00, 0, 'active', '包含洗衣服务', NOW(), NOW()),
(3, 'T3_AIRPORT_PICKUP', '接机包价', '综合', 'fixed', 1, 'per_stay', 'group', 180.00, 0, 'active', '包含机场接机服务', NOW(), NOW()),
(3, 'T3_AIRPORT_DROPOFF', '送机包价', '综合', 'fixed', 1, 'per_stay', 'group', 180.00, 0, 'active', '包含机场送机服务', NOW(), NOW()),
(3, 'T3_MEETING_PACKAGE', '会议包价', '综合', 'fixed', 1, 'per_stay', 'group', 350.00, 0, 'active', '包含会议室使用权', NOW(), NOW()),
(3, 'T3_BIRTHDAY_PACKAGE', '生日包价', '综合', 'fixed', 1, 'per_stay', 'group', 250.00, 0, 'active', '包含生日蛋糕和布置', NOW(), NOW()),
(3, 'T3_FAMILY_PACKAGE', '家庭包价', '综合', 'fixed', 1, 'per_stay', 'group', 350.00, 0, 'active', '包含儿童用品和活动', NOW(), NOW()),
(3, 'T3_BUSINESS_PACKAGE', '商务包价', '综合', 'fixed', 1, 'daily', 'group', 120.00, 0, 'active', '包含商务中心服务', NOW(), NOW()),
(3, 'T3_VACATION_PACKAGE', '度假包价', '综合', 'fixed', 1, 'per_stay', 'group', 600.00, 0, 'active', '包含景点门票和活动', NOW(), NOW());

-- 显示插入数据统计
SELECT CONCAT('包价数据插入完成！共为 ', COUNT(DISTINCT tenant_id), ' 个租户插入 ', COUNT(*), ' 条包价数据') AS result 
FROM packages;

SELECT tenant_id AS 租户ID, COUNT(*) AS 包价数量 
FROM packages 
GROUP BY tenant_id 
ORDER BY tenant_id;
