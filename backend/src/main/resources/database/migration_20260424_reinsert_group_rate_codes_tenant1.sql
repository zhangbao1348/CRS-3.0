-- 重新插入租户1的集团房价码数据

USE CRS;

-- 清空现有的group_rate_codes数据
TRUNCATE TABLE group_rate_codes;

-- 重新插入租户1的集团房价码数据
INSERT INTO group_rate_codes (
    group_id, rate_code, rate_name, description, status,
    rate_type, rate_category, derivative_level,
    market_code_id, source_code_id,
    advance_booking_min, advance_booking_max,
    minimum_stay_min, minimum_stay_max,
    guarantee_rule, cancellation_rule,
    booking_start_time, booking_end_time,
    checkin_start_time, checkin_end_time,
    discount, rounding,
    allow_points, points_type, points_value,
    promotion_rule, coupon_rule,
    applicable_room_types, packages,
    personal_membership, company_membership,
    tenant_code,
    created_at, updated_at
) VALUES
(1, 'T1_RACK', '标准价', '酒店标准挂牌价', 'active', 'BAR', 'BAR', 'standard', 1, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_WEEKEND', '周末价', '周末专属优惠价格', 'active', 'PROMO', 'PROMO', 'standard', 1, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.9, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_CORP', '企业价', '企业协议客户专享价', 'active', 'CORP', 'CORP', 'standard', 1, 34, NULL, NULL, NULL, NULL, 'CORP_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_MEMBER', '会员价', '会员专享优惠价', 'active', 'BAR', 'BAR', 'standard', 1, 4, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.9, 'round', 1, 'percentage', 100, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_GROUP', '团队价', '团队预订优惠价', 'active', 'GROUP', 'GROUP', 'standard', 2, 23, NULL, NULL, 5, 30, 'CORP_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', 0.75, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_PROMO', '促销价', '限时促销活动价', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, 7, 60, NULL, NULL, 'NO_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', 0.8, 'round', 1, 'fixed', 0, 'limited_time', NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_LONG_STAY', '长住价', '长住客人专享价', 'active', 'LONGSTAY', 'LONGSTAY', 'standard', 1, 1, NULL, NULL, 7, 30, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.7, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_GOV', '政府价', '政府机关协议价', 'active', 'CORP', 'CORP', 'standard', 1, 36, NULL, NULL, NULL, NULL, 'CORP_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.8, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_PEAK', '旺季价', '旅游旺季价格', 'active', 'BAR', 'BAR', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'CC_GUARANTEE', 'NON_REFUNDABLE', '00:00', '23:59', '14:00', '23:59', 1.2, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_OFF_PEAK', '淡季价', '旅游淡季优惠价', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_HOLIDAY', '节假日价', '节假日专属价格', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'CC_GUARANTEE', 'NON_REFUNDABLE', '00:00', '23:59', '14:00', '23:59', 1.3, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_BIRTHDAY', '生日价', '生日当天专享价', 'active', 'PROMO', 'PROMO', 'standard', 1, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.8, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[15]', NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_COUPLE', '情侣价', '情侣入住专享价', 'active', 'PROMO', 'PROMO', 'standard', 1, 1, NULL, NULL, 1, 7, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.9, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[1,8]', NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_FAMILY', '家庭价', '家庭入住专享价', 'active', 'PACKAGE', 'PACKAGE', 'standard', 1, 1, NULL, NULL, 2, 7, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[16]', NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_BUSINESS', '商务价', '商务客人专享价', 'active', 'CORP', 'CORP', 'standard', 1, 34, NULL, NULL, 1, 30, 'CORP_GUARANTEE', 'FREE_CANCEL', '08:00', '18:00', '14:00', '23:59', 0.88, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[17]', NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_MEETING', '会议价', '会议团队专享价', 'active', 'GROUP', 'GROUP', 'standard', 2, 41, NULL, NULL, 3, 30, 'CORP_GUARANTEE', '7DAYS_ADVANCE', '00:00', '23:59', '14:00', '23:59', 0.7, 'round', 0, 'fixed', 0, NULL, NULL, NULL, '[14]', NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_EARLY_BIRD', '早鸟价', '提前预订优惠价', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, 14, 60, NULL, NULL, 'CC_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', 0.75, 'round', 1, 'fixed', 0, 'early_bird', NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_LAST_MINUTE', '尾单价', '当日预订优惠价', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '12:00', '23:59', '14:00', '23:59', 0.7, 'round', 1, 'fixed', 0, 'last_minute', NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_PACKAGE', '套餐价', '含服务套餐价格', 'active', 'PACKAGE', 'PACKAGE', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'CC_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[1,3,4]', NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_SPA', '含SPA价', '含SPA服务价格', 'active', 'PACKAGE', 'PACKAGE', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'CC_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[9]', NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_SPRING', '春季价', '春季专属价格', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.95, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[18]', NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_SUMMER', '夏季价', '夏季专属价格', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 1.05, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_AUTUMN', '秋季价', '秋季专属价格', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.9, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_WINTER', '冬季价', '冬季专属价格', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 1.1, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_NEW_YEAR', '新年价', '新年期间价格', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'CC_GUARANTEE', 'NON_REFUNDABLE', '00:00', '23:59', '14:00', '23:59', 1.4, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_CORP_VIP', '企业VIP价', '重要企业客户价', 'active', 'CORP', 'CORP', 'standard', 1, 34, NULL, NULL, NULL, NULL, 'CORP_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.75, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_DIAMOND', '钻石会员价', '钻石会员专享价', 'active', 'BAR', 'BAR', 'standard', 1, 13, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.7, 'round', 1, 'percentage', 200, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_WEBSITE', '官网专属价', '官网预订专享价', 'active', 'BAR', 'BAR', 'standard', 87, 5, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.95, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_APP', 'APP专属价', 'APP预订专享价', 'active', 'BAR', 'BAR', 'standard', 87, 6, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.92, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_OTA', 'OTA渠道价', 'OTA渠道专享价', 'active', 'PROMO', 'PROMO', 'standard', 29, 25, NULL, NULL, NULL, NULL, 'THIRD_PARTY_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', 0.88, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_OTA_CTRIP', '携程专属价', '携程专享价', 'active', 'PROMO', 'PROMO', 'standard', 85, 25, NULL, NULL, NULL, NULL, 'THIRD_PARTY_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_OTA_MEITUAN', '美团专属价', '美团专享价', 'active', 'PROMO', 'PROMO', 'standard', 3, 26, NULL, NULL, NULL, NULL, 'THIRD_PARTY_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', 0.83, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_OTA_FLIGGY', '飞猪专属价', '飞猪专享价', 'active', 'PROMO', 'PROMO', 'standard', 86, 27, NULL, NULL, NULL, NULL, 'THIRD_PARTY_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', 0.84, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_DIRECT', '直接预订价', '直接预订专享价', 'active', 'BAR', 'BAR', 'standard', 1, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.98, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_WALKIN', '前台价', '前台散客价', 'active', 'BAR', 'BAR', 'standard', 1, 3, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 1, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_MEMBER_GOLD', '黄金会员价', '黄金会员专享价', 'active', 'BAR', 'BAR', 'standard', 1, 15, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.88, 'round', 1, 'percentage', 150, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_MEMBER_PLATINUM', '白金会员价', '白金会员专享价', 'active', 'BAR', 'BAR', 'standard', 1, 14, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.82, 'round', 1, 'percentage', 180, NULL, NULL, NULL, NULL, NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_THREE_MEALS', '含三餐价', '含三餐套餐价', 'active', 'PACKAGE', 'PACKAGE', 'standard', 1, 1, NULL, NULL, NULL, NULL, 'CC_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[5]', NULL, NULL, 'MARRIOT', NOW(), NOW()),
(1, 'T1_LATE_CHECKOUT', '含延时退房价', '含延时退房套餐价', 'active', 'PACKAGE', 'PACKAGE', 'standard', 1, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[6]', NULL, NULL, 'MARRIOT', NOW(), NOW());

-- 显示插入结果
SELECT CONCAT('集团房价码数据插入完成！共插入 ', COUNT(*), ' 条集团房价码数据') AS result 
FROM group_rate_codes;
