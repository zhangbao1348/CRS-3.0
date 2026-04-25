-- 为每个租户插入40条集团房价码数据

USE CRS;

-- ============================================
-- 租户1
-- ============================================
INSERT IGNORE INTO group_rate_codes (
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
    created_at, updated_at
) VALUES
(1, 'T1_RACK', '标准价', '酒店标准挂牌价', 'active', 'BAR', 'BAR', 'standard', 1, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_WEEKEND', '周末价', '周末专属优惠价格', 'active', 'PROMO', 'PROMO', 'standard', 1, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.9, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_CORP', '企业价', '企业协议客户专享价', 'active', 'CORP', 'CORP', 'standard', 1, 34, NULL, NULL, NULL, NULL, 'CORP_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_MEMBER', '会员价', '会员专享优惠价', 'active', 'BAR', 'BAR', 'standard', 1, 4, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.9, 'round', 1, 'percentage', 100, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_GROUP', '团队价', '团队预订优惠价', 'active', 'GROUP', 'GROUP', 'standard', 2, 23, NULL, NULL, 5, 30, 'CORP_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', 0.75, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_PROMO', '促销价', '限时促销活动价', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, 7, 60, NULL, NULL, 'NO_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', 0.8, 'round', 1, 'fixed', 0, 'limited_time', NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_LONG_STAY', '长住价', '长住客人专享价', 'active', 'LONGSTAY', 'LONGSTAY', 'standard', 1, 1, NULL, NULL, 7, 30, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.7, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_GOV', '政府价', '政府机关协议价', 'active', 'CORP', 'CORP', 'standard', 1, 36, NULL, NULL, NULL, NULL, 'CORP_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.8, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_PEAK', '旺季价', '旅游旺季价格', 'active', 'BAR', 'BAR', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'CC_GUARANTEE', 'NON_REFUNDABLE', '00:00', '23:59', '14:00', '23:59', 1.2, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_OFF_PEAK', '淡季价', '旅游淡季优惠价', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_HOLIDAY', '节假日价', '节假日专属价格', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'CC_GUARANTEE', 'NON_REFUNDABLE', '00:00', '23:59', '14:00', '23:59', 1.3, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_BIRTHDAY', '生日价', '生日当天专享价', 'active', 'PROMO', 'PROMO', 'standard', 1, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.8, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[15]', NULL, NULL, NOW(), NOW()),
(1, 'T1_COUPLE', '情侣价', '情侣入住专享价', 'active', 'PROMO', 'PROMO', 'standard', 1, 1, NULL, NULL, 1, 7, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.9, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[1,8]', NULL, NULL, NOW(), NOW()),
(1, 'T1_FAMILY', '家庭价', '家庭入住专享价', 'active', 'PACKAGE', 'PACKAGE', 'standard', 1, 1, NULL, NULL, 2, 7, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[16]', NULL, NULL, NOW(), NOW()),
(1, 'T1_BUSINESS', '商务价', '商务客人专享价', 'active', 'CORP', 'CORP', 'standard', 1, 34, NULL, NULL, 1, 30, 'CORP_GUARANTEE', 'FREE_CANCEL', '08:00', '18:00', '14:00', '23:59', 0.88, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[17]', NULL, NULL, NOW(), NOW()),
(1, 'T1_MEETING', '会议价', '会议团队专享价', 'active', 'GROUP', 'GROUP', 'standard', 2, 41, NULL, NULL, 3, 30, 'CORP_GUARANTEE', '7DAYS_ADVANCE', '00:00', '23:59', '14:00', '23:59', 0.7, 'round', 0, 'fixed', 0, NULL, NULL, NULL, '[14]', NULL, NULL, NOW(), NOW()),
(1, 'T1_EARLY_BIRD', '早鸟价', '提前预订优惠价', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, 14, 60, NULL, NULL, 'CC_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', 0.75, 'round', 1, 'fixed', 0, 'early_bird', NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_LAST_MINUTE', '尾单价', '当日预订优惠价', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '12:00', '23:59', '14:00', '23:59', 0.7, 'round', 1, 'fixed', 0, 'last_minute', NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_PACKAGE', '套餐价', '含服务套餐价格', 'active', 'PACKAGE', 'PACKAGE', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'CC_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[1,3,4]', NULL, NULL, NOW(), NOW()),
(1, 'T1_SPA', '含SPA价', '含SPA服务价格', 'active', 'PACKAGE', 'PACKAGE', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'CC_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[9]', NULL, NULL, NOW(), NOW()),
(1, 'T1_SPRING', '春季价', '春季专属价格', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.95, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[18]', NULL, NULL, NOW(), NOW()),
(1, 'T1_SUMMER', '夏季价', '夏季专属价格', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 1.05, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_AUTUMN', '秋季价', '秋季专属价格', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.9, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_WINTER', '冬季价', '冬季专属价格', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 1.1, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_NEW_YEAR', '新年价', '新年期间价格', 'active', 'PROMO', 'PROMO', 'standard', 3, 1, NULL, NULL, NULL, NULL, 'CC_GUARANTEE', 'NON_REFUNDABLE', '00:00', '23:59', '14:00', '23:59', 1.4, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_CORP_VIP', '企业VIP价', '重要企业客户价', 'active', 'CORP', 'CORP', 'standard', 1, 34, NULL, NULL, NULL, NULL, 'CORP_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.75, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_DIAMOND', '钻石会员价', '钻石会员专享价', 'active', 'BAR', 'BAR', 'standard', 1, 13, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.7, 'round', 1, 'percentage', 200, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_WEBSITE', '官网专属价', '官网预订专享价', 'active', 'BAR', 'BAR', 'standard', 87, 5, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.95, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_APP', 'APP专属价', 'APP预订专享价', 'active', 'BAR', 'BAR', 'standard', 87, 6, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.92, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_OTA', 'OTA渠道价', 'OTA渠道专享价', 'active', 'PROMO', 'PROMO', 'standard', 29, 25, NULL, NULL, NULL, NULL, 'THIRD_PARTY_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', 0.88, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_OTA_CTRIP', '携程专属价', '携程专享价', 'active', 'PROMO', 'PROMO', 'standard', 85, 25, NULL, NULL, NULL, NULL, 'THIRD_PARTY_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_OTA_MEITUAN', '美团专属价', '美团专享价', 'active', 'PROMO', 'PROMO', 'standard', 3, 26, NULL, NULL, NULL, NULL, 'THIRD_PARTY_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', 0.83, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_OTA_FLIGGY', '飞猪专属价', '飞猪专享价', 'active', 'PROMO', 'PROMO', 'standard', 86, 27, NULL, NULL, NULL, NULL, 'THIRD_PARTY_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', 0.84, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_DIRECT', '直接预订价', '直接预订专享价', 'active', 'BAR', 'BAR', 'standard', 1, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.98, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_WALKIN', '前台价', '前台散客价', 'active', 'BAR', 'BAR', 'standard', 1, 3, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 1, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_MEMBER_GOLD', '黄金会员价', '黄金会员专享价', 'active', 'BAR', 'BAR', 'standard', 1, 15, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.88, 'round', 1, 'percentage', 150, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_MEMBER_PLATINUM', '白金会员价', '白金会员专享价', 'active', 'BAR', 'BAR', 'standard', 1, 14, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.82, 'round', 1, 'percentage', 180, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 'T1_THREE_MEALS', '含三餐价', '含三餐套餐价', 'active', 'PACKAGE', 'PACKAGE', 'standard', 1, 1, NULL, NULL, NULL, NULL, 'CC_GUARANTEE', 'PARTIAL_FEE', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[5]', NULL, NULL, NOW(), NOW()),
(1, 'T1_LATE_CHECKOUT', '含延时退房价', '含延时退房套餐价', 'active', 'PACKAGE', 'PACKAGE', 'standard', 1, 1, NULL, NULL, NULL, NULL, 'NO_GUARANTEE', 'FREE_CANCEL', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[6]', NULL, NULL, NOW(), NOW());

-- ============================================
-- 租户2
-- ============================================
INSERT IGNORE INTO group_rate_codes (
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
    created_at, updated_at
) VALUES
(2, 'T2_RACK', '标准价', '酒店标准挂牌价', 'active', 'BAR', 'BAR', 'standard', 20, 49, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_WEEKEND', '周末价', '周末专属优惠价格', 'active', 'PROMO', 'PROMO', 'standard', 20, 49, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.9, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_CORP', '企业价', '企业协议客户专享价', 'active', 'CORP', 'CORP', 'standard', 20, 61, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_MEMBER', '会员价', '会员专享优惠价', 'active', 'BAR', 'BAR', 'standard', 23, 49, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_MEMBER', '00:00', '23:59', '14:00', '23:59', 0.88, 'round', 1, 'percentage', 100, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_GROUP', '团队价', '团队预订优惠价', 'active', 'GROUP', 'GROUP', 'standard', 21, 41, NULL, NULL, 5, 30, 'FREE_GUARANTEE', 'TIME_LIMITED', '00:00', '23:59', '14:00', '23:59', 0.75, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_PROMO', '促销价', '限时促销活动价', 'active', 'PROMO', 'PROMO', 'standard', 22, 49, 7, 60, NULL, NULL, 'FREE_GUARANTEE', 'TIME_LIMITED', '00:00', '23:59', '14:00', '23:59', 0.8, 'round', 1, 'fixed', 0, 'limited_time', NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_LONG_STAY', '长住价', '长住客人专享价', 'active', 'LONGSTAY', 'LONGSTAY', 'standard', 20, 49, NULL, NULL, 7, 30, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.7, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_PEAK', '旺季价', '旅游旺季价格', 'active', 'BAR', 'BAR', 'standard', 22, 49, NULL, NULL, NULL, NULL, 'CC_AUTH', 'HUAZHU_NON_REFUND', '00:00', '23:59', '14:00', '23:59', 1.2, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_OFF_PEAK', '淡季价', '旅游淡季优惠价', 'active', 'PROMO', 'PROMO', 'standard', 22, 49, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_HOLIDAY', '节假日价', '节假日专属价格', 'active', 'PROMO', 'PROMO', 'standard', 22, 49, NULL, NULL, NULL, NULL, 'CC_AUTH', 'HUAZHU_NON_REFUND', '00:00', '23:59', '14:00', '23:59', 1.3, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_BIRTHDAY', '生日价', '生日当天专享价', 'active', 'PROMO', 'PROMO', 'standard', 20, 49, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.8, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[87]', NULL, NULL, NOW(), NOW()),
(2, 'T2_COUPLE', '情侣价', '情侣入住专享价', 'active', 'PROMO', 'PROMO', 'standard', 20, 49, NULL, NULL, 1, 7, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.9, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[73,80]', NULL, NULL, NOW(), NOW()),
(2, 'T2_FAMILY', '家庭价', '家庭入住专享价', 'active', 'PACKAGE', 'PACKAGE', 'standard', 20, 49, NULL, NULL, 2, 7, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[88]', NULL, NULL, NOW(), NOW()),
(2, 'T2_BUSINESS', '商务价', '商务客人专享价', 'active', 'CORP', 'CORP', 'standard', 20, 61, NULL, NULL, 1, 30, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '08:00', '18:00', '14:00', '23:59', 0.88, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[89]', NULL, NULL, NOW(), NOW()),
(2, 'T2_MEETING', '会议价', '会议团队专享价', 'active', 'GROUP', 'GROUP', 'standard', 21, 41, NULL, NULL, 3, 30, 'FREE_GUARANTEE', '3DAYS_ADVANCE', '00:00', '23:59', '14:00', '23:59', 0.7, 'round', 0, 'fixed', 0, NULL, NULL, NULL, '[86]', NULL, NULL, NOW(), NOW()),
(2, 'T2_EARLY_BIRD', '早鸟价', '提前预订优惠价', 'active', 'PROMO', 'PROMO', 'standard', 22, 49, 14, 60, NULL, NULL, 'CC_AUTH', 'TIME_LIMITED', '00:00', '23:59', '14:00', '23:59', 0.75, 'round', 1, 'fixed', 0, 'early_bird', NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_LAST_MINUTE', '尾单价', '当日预订优惠价', 'active', 'PROMO', 'PROMO', 'standard', 22, 49, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '12:00', '23:59', '14:00', '23:59', 0.7, 'round', 1, 'fixed', 0, 'last_minute', NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_PACKAGE', '套餐价', '含服务套餐价格', 'active', 'PACKAGE', 'PACKAGE', 'standard', 22, 49, NULL, NULL, NULL, NULL, 'CC_AUTH', 'TIME_LIMITED', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[73,75,76]', NULL, NULL, NOW(), NOW()),
(2, 'T2_SPA', '含SPA价', '含SPA服务价格', 'active', 'PACKAGE', 'PACKAGE', 'standard', 22, 49, NULL, NULL, NULL, NULL, 'CC_AUTH', 'TIME_LIMITED', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[81]', NULL, NULL, NOW(), NOW()),
(2, 'T2_SPRING', '春季价', '春季专属价格', 'active', 'PROMO', 'PROMO', 'standard', 22, 49, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.95, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[90]', NULL, NULL, NOW(), NOW()),
(2, 'T2_SUMMER', '夏季价', '夏季专属价格', 'active', 'PROMO', 'PROMO', 'standard', 22, 49, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '00:00', '23:59', '14:00', '23:59', 1.05, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_AUTUMN', '秋季价', '秋季专属价格', 'active', 'PROMO', 'PROMO', 'standard', 22, 49, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.9, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_WINTER', '冬季价', '冬季专属价格', 'active', 'PROMO', 'PROMO', 'standard', 22, 49, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '00:00', '23:59', '14:00', '23:59', 1.1, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_NEW_YEAR', '新年价', '新年期间价格', 'active', 'PROMO', 'PROMO', 'standard', 22, 49, NULL, NULL, NULL, NULL, 'CC_AUTH', 'HUAZHU_NON_REFUND', '00:00', '23:59', '14:00', '23:59', 1.4, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_WEBSITE', '官网专属价', '官网预订专享价', 'active', 'BAR', 'BAR', 'standard', 20, 52, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.95, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_OTA', 'OTA渠道价', 'OTA渠道专享价', 'active', 'PROMO', 'PROMO', 'standard', 29, 57, NULL, NULL, NULL, NULL, 'ALIPAY_GUARANTEE', 'TIME_LIMITED', '00:00', '23:59', '14:00', '23:59', 0.88, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_OTA_CTRIP', '携程专属价', '携程专享价', 'active', 'PROMO', 'PROMO', 'standard', 27, 59, NULL, NULL, NULL, NULL, 'ALIPAY_GUARANTEE', 'TIME_LIMITED', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_OTA_MEITUAN', '美团专属价', '美团专享价', 'active', 'PROMO', 'PROMO', 'standard', 30, 60, NULL, NULL, NULL, NULL, 'WECHAT_GUARANTEE', 'TIME_LIMITED', '00:00', '23:59', '14:00', '23:59', 0.83, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_DIRECT', '直接预订价', '直接预订专享价', 'active', 'BAR', 'BAR', 'standard', 20, 49, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.98, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_WALKIN', '前台价', '前台散客价', 'active', 'BAR', 'BAR', 'standard', 20, 51, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '00:00', '23:59', '14:00', '23:59', 1, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_MEMBER_GOLD', '金会员价', '金会员专享价', 'active', 'BAR', 'BAR', 'standard', 31, 49, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_MEMBER', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'percentage', 150, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_MEMBER_PLATINUM', '铂金会员价', '铂金会员专享价', 'active', 'BAR', 'BAR', 'standard', 33, 49, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_MEMBER', '00:00', '23:59', '14:00', '23:59', 0.78, 'round', 1, 'percentage', 200, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 'T2_THREE_MEALS', '含三餐价', '含三餐套餐价', 'active', 'PACKAGE', 'PACKAGE', 'standard', 20, 49, NULL, NULL, NULL, NULL, 'CC_AUTH', 'TIME_LIMITED', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[77]', NULL, NULL, NOW(), NOW()),
(2, 'T2_LATE_CHECKOUT', '含延时退房价', '含延时退房套餐价', 'active', 'PACKAGE', 'PACKAGE', 'standard', 20, 49, NULL, NULL, NULL, NULL, 'FREE_GUARANTEE', 'HUAZHU_STANDARD', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[78]', NULL, NULL, NOW(), NOW());

-- ============================================
-- 租户3
-- ============================================
INSERT IGNORE INTO group_rate_codes (
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
    created_at, updated_at
) VALUES
(3, 'T3_RACK', '标准价', '酒店标准挂牌价', 'active', 'BAR', 'BAR', 'standard', 34, 49, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_WEEKEND', '周末价', '周末专属优惠价格', 'active', 'PROMO', 'PROMO', 'standard', 34, 49, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', 0.9, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_CORP', '企业价', '企业协议客户专享价', 'active', 'CORP', 'CORP', 'standard', 34, 34, NULL, NULL, NULL, NULL, 'CORP_AGREEMENT', 'CORP_CANCEL', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_MEMBER', '会员价', '会员专享优惠价', 'active', 'BAR', 'BAR', 'standard', 37, 49, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'POINTS_REDEEM', '00:00', '23:59', '14:00', '23:59', 0.88, 'round', 1, 'percentage', 100, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_GROUP', '团队价', '团队预订优惠价', 'active', 'GROUP', 'GROUP', 'standard', 35, 23, NULL, NULL, 5, 30, 'CORP_AGREEMENT', 'MARRIOTT_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.75, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_PROMO', '促销价', '限时促销活动价', 'active', 'PROMO', 'PROMO', 'standard', 36, 49, 7, 60, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.8, 'round', 1, 'fixed', 0, 'limited_time', NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_LONG_STAY', '长住价', '长住客人专享价', 'active', 'LONGSTAY', 'LONGSTAY', 'standard', 34, 49, NULL, NULL, 7, 30, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', 0.7, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_PEAK', '旺季价', '旅游旺季价格', 'active', 'BAR', 'BAR', 'standard', 36, 49, NULL, NULL, NULL, NULL, 'MARRIOTT_CC', 'PREPAY_RATE', '00:00', '23:59', '14:00', '23:59', 1.2, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_OFF_PEAK', '淡季价', '旅游淡季优惠价', 'active', 'PROMO', 'PROMO', 'standard', 36, 49, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_HOLIDAY', '节假日价', '节假日专属价格', 'active', 'PROMO', 'PROMO', 'standard', 36, 49, NULL, NULL, NULL, NULL, 'MARRIOTT_CC', 'PREPAY_RATE', '00:00', '23:59', '14:00', '23:59', 1.3, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_BIRTHDAY', '生日价', '生日当天专享价', 'active', 'PROMO', 'PROMO', 'standard', 34, 49, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', 0.8, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[105]', NULL, NULL, NOW(), NOW()),
(3, 'T3_COUPLE', '情侣价', '情侣入住专享价', 'active', 'PROMO', 'PROMO', 'standard', 34, 49, NULL, NULL, 1, 7, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', 0.9, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[91,98]', NULL, NULL, NOW(), NOW()),
(3, 'T3_FAMILY', '家庭价', '家庭入住专享价', 'active', 'PACKAGE', 'PACKAGE', 'standard', 34, 49, NULL, NULL, 2, 7, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[106]', NULL, NULL, NOW(), NOW()),
(3, 'T3_BUSINESS', '商务价', '商务客人专享价', 'active', 'CORP', 'CORP', 'standard', 34, 34, NULL, NULL, 1, 30, 'CORP_AGREEMENT', 'CORP_CANCEL', '08:00', '18:00', '14:00', '23:59', 0.88, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[107]', NULL, NULL, NOW(), NOW()),
(3, 'T3_MEETING', '会议价', '会议团队专享价', 'active', 'GROUP', 'GROUP', 'standard', 35, 41, NULL, NULL, 3, 30, 'CORP_AGREEMENT', 'SPECIAL_PERIOD', '00:00', '23:59', '14:00', '23:59', 0.7, 'round', 0, 'fixed', 0, NULL, NULL, NULL, '[104]', NULL, NULL, NOW(), NOW()),
(3, 'T3_EARLY_BIRD', '早鸟价', '提前预订优惠价', 'active', 'PROMO', 'PROMO', 'standard', 36, 49, 14, 60, NULL, NULL, 'MARRIOTT_CC', 'MARRIOTT_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.75, 'round', 1, 'fixed', 0, 'early_bird', NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_LAST_MINUTE', '尾单价', '当日预订优惠价', 'active', 'PROMO', 'PROMO', 'standard', 36, 49, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '12:00', '23:59', '14:00', '23:59', 0.7, 'round', 1, 'fixed', 0, 'last_minute', NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_PACKAGE', '套餐价', '含服务套餐价格', 'active', 'PACKAGE', 'PACKAGE', 'standard', 36, 49, NULL, NULL, NULL, NULL, 'MARRIOTT_CC', 'MARRIOTT_STANDARD', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[91,93,94]', NULL, NULL, NOW(), NOW()),
(3, 'T3_SPA', '含SPA价', '含SPA服务价格', 'active', 'PACKAGE', 'PACKAGE', 'standard', 36, 49, NULL, NULL, NULL, NULL, 'MARRIOTT_CC', 'MARRIOTT_STANDARD', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[99]', NULL, NULL, NOW(), NOW()),
(3, 'T3_SPRING', '春季价', '春季专属价格', 'active', 'PROMO', 'PROMO', 'standard', 36, 49, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', 0.95, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[108]', NULL, NULL, NOW(), NOW()),
(3, 'T3_SUMMER', '夏季价', '夏季专属价格', 'active', 'PROMO', 'PROMO', 'standard', 36, 49, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', 1.05, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_AUTUMN', '秋季价', '秋季专属价格', 'active', 'PROMO', 'PROMO', 'standard', 36, 49, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', 0.9, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_WINTER', '冬季价', '冬季专属价格', 'active', 'PROMO', 'PROMO', 'standard', 36, 49, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', 1.1, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_NEW_YEAR', '新年价', '新年期间价格', 'active', 'PROMO', 'PROMO', 'standard', 36, 49, NULL, NULL, NULL, NULL, 'MARRIOTT_CC', 'PREPAY_RATE', '00:00', '23:59', '14:00', '23:59', 1.4, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_WEBSITE', '官网专属价', '官网预订专享价', 'active', 'BAR', 'BAR', 'standard', 34, 52, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', 0.95, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_OTA', 'OTA渠道价', 'OTA渠道专享价', 'active', 'PROMO', 'PROMO', 'standard', 43, 57, NULL, NULL, NULL, NULL, 'MARRIOTT_CC', 'MARRIOTT_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.88, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_OTA_BOOKING', 'Booking专属价', 'Booking.com专享价', 'active', 'PROMO', 'PROMO', 'standard', 101, 25, NULL, NULL, NULL, NULL, 'MARRIOTT_CC', 'MARRIOTT_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_OTA_EXPEDIA', 'Expedia专属价', 'Expedia专享价', 'active', 'PROMO', 'PROMO', 'standard', 44, 32, NULL, NULL, NULL, NULL, 'MARRIOTT_CC', 'MARRIOTT_STANDARD', '00:00', '23:59', '14:00', '23:59', 0.83, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_DIRECT', '直接预订价', '直接预订专享价', 'active', 'BAR', 'BAR', 'standard', 34, 49, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', 0.98, 'round', 1, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_WALKIN', '前台价', '前台散客价', 'active', 'BAR', 'BAR', 'standard', 34, 3, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', 1, 'round', 0, 'fixed', 0, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_MEMBER_SILVER', '银卡会员价', '银卡会员专享价', 'active', 'BAR', 'BAR', 'standard', 46, 49, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', 0.9, 'round', 1, 'percentage', 100, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_MEMBER_GOLD', '金卡会员价', '金卡会员专享价', 'active', 'BAR', 'BAR', 'standard', 47, 49, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', 0.85, 'round', 1, 'percentage', 150, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_MEMBER_PLATINUM', '白金会员价', '白金会员专享价', 'active', 'BAR', 'BAR', 'standard', 48, 49, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', 0.78, 'round', 1, 'percentage', 200, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 'T3_THREE_MEALS', '含三餐价', '含三餐套餐价', 'active', 'PACKAGE', 'PACKAGE', 'standard', 34, 49, NULL, NULL, NULL, NULL, 'MARRIOTT_CC', 'MARRIOTT_STANDARD', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[95]', NULL, NULL, NOW(), NOW()),
(3, 'T3_LATE_CHECKOUT', '含延时退房价', '含延时退房套餐价', 'active', 'PACKAGE', 'PACKAGE', 'standard', 34, 49, NULL, NULL, NULL, NULL, 'STANDARD_GUARANTEE', 'MARRIOTT_FLEX', '00:00', '23:59', '14:00', '23:59', NULL, 'round', 1, 'fixed', 0, NULL, NULL, NULL, '[96]', NULL, NULL, NOW(), NOW());

-- 显示插入结果
SELECT CONCAT('集团房价码数据插入完成！共为 ', COUNT(DISTINCT group_id), ' 个租户插入 ', COUNT(*), ' 条集团房价码数据') AS result 
FROM group_rate_codes;

-- 查看各租户的集团房价码数量
SELECT group_id AS 租户ID, COUNT(*) AS 集团房价码数量 
FROM group_rate_codes 
GROUP BY group_id 
ORDER BY group_id;
