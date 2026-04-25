-- 为每个租户插入20条集团房价码数据（参考T1_RACK）

USE CRS;

-- ============================================
-- 租户1
-- ============================================
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
    created_at, updated_at
) VALUES
(1, 'T1_RACK', '标准价', '酒店标准挂牌价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_WEEKEND', '周末价', '周末专属优惠价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_CORP', '企业价', '企业协议客户专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_MEMBER', '会员价', '会员专享优惠价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_GROUP', '团队价', '团队预订优惠价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_PROMO', '促销价', '限时促销活动价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_LONG_STAY', '长住价', '长住客人专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_PEAK', '旺季价', '旅游旺季价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_OFF_PEAK', '淡季价', '旅游淡季优惠价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_HOLIDAY', '节假日价', '节假日专属价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_BIRTHDAY', '生日价', '生日当天专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_COUPLE', '情侣价', '情侣入住专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_FAMILY', '家庭价', '家庭入住专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_BUSINESS', '商务价', '商务客人专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_MEETING', '会议价', '会议团队专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_EARLY_BIRD', '早鸟价', '提前预订优惠价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_LAST_MINUTE', '尾单价', '当日预订优惠价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_PACKAGE', '套餐价', '含服务套餐价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_SPA', '含SPA价', '含SPA服务价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(1, 'T1_SPRING', '春季价', '春季专属价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW());

-- ============================================
-- 租户2
-- ============================================
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
    created_at, updated_at
) VALUES
(2, 'T2_RACK', '标准价', '酒店标准挂牌价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_WEEKEND', '周末价', '周末专属优惠价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_CORP', '企业价', '企业协议客户专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_MEMBER', '会员价', '会员专享优惠价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_GROUP', '团队价', '团队预订优惠价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_PROMO', '促销价', '限时促销活动价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_LONG_STAY', '长住价', '长住客人专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_PEAK', '旺季价', '旅游旺季价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_OFF_PEAK', '淡季价', '旅游淡季优惠价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_HOLIDAY', '节假日价', '节假日专属价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_BIRTHDAY', '生日价', '生日当天专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_COUPLE', '情侣价', '情侣入住专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_FAMILY', '家庭价', '家庭入住专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_BUSINESS', '商务价', '商务客人专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_MEETING', '会议价', '会议团队专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_EARLY_BIRD', '早鸟价', '提前预订优惠价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_LAST_MINUTE', '尾单价', '当日预订优惠价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_PACKAGE', '套餐价', '含服务套餐价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_SPA', '含SPA价', '含SPA服务价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(2, 'T2_SPRING', '春季价', '春季专属价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW());

-- ============================================
-- 租户3
-- ============================================
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
    created_at, updated_at
) VALUES
(3, 'T3_RACK', '标准价', '酒店标准挂牌价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_WEEKEND', '周末价', '周末专属优惠价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_CORP', '企业价', '企业协议客户专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_MEMBER', '会员价', '会员专享优惠价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_GROUP', '团队价', '团队预订优惠价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_PROMO', '促销价', '限时促销活动价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_LONG_STAY', '长住价', '长住客人专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_PEAK', '旺季价', '旅游旺季价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_OFF_PEAK', '淡季价', '旅游淡季优惠价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_HOLIDAY', '节假日价', '节假日专属价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_BIRTHDAY', '生日价', '生日当天专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_COUPLE', '情侣价', '情侣入住专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_FAMILY', '家庭价', '家庭入住专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_BUSINESS', '商务价', '商务客人专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_MEETING', '会议价', '会议团队专享价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_EARLY_BIRD', '早鸟价', '提前预订优惠价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_LAST_MINUTE', '尾单价', '当日预订优惠价', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_PACKAGE', '套餐价', '含服务套餐价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_SPA', '含SPA价', '含SPA服务价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW()),
(3, 'T3_SPRING', '春季价', '春季专属价格', 'active', 'basic', 'CORP', 'basic', 75, 7, 1, 5, 1, 10, 'ALIPAY_GUARANTEE', 'HUAZHU_STANDARD', '2026-04-12', '2026-04-30', '2026-04-12', '2026-04-30', NULL, NULL, 1, 'fixed', NULL, 'unlimited', 'unlimited', '[]', '[]', '["gold", "silver"]', '["silver-company", "gold-company"]', NOW(), NOW());

-- 显示插入结果
SELECT CONCAT('集团房价码数据插入完成！共为 ', COUNT(DISTINCT group_id), ' 个租户插入 ', COUNT(*), ' 条集团房价码数据') AS result 
FROM group_rate_codes;

-- 查看各租户的集团房价码数量
SELECT group_id AS 租户ID, COUNT(*) AS 集团房价码数量 
FROM group_rate_codes 
GROUP BY group_id 
ORDER BY group_id;
