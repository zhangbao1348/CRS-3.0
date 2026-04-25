-- 为每个租户插入担保政策和取消政策数据
-- 执行时间: 2026-04-11

USE CRS;

-- ============================================
-- 1. 担保政策数据
-- ============================================

-- 先清空所有现有数据
DELETE FROM guarantee_policies;

-- ============================================
-- 租户1: 锦江酒店集团
-- ============================================
INSERT INTO guarantee_policies (tenant_id, name, code, type, guarantee_sub_type, guarantee_amount, latest_arrival_time, description, status, group_id, created_at, updated_at) VALUES
(1, '无需担保', 'NO_GUARANTEE', '无担保', NULL, NULL, NULL, '无需支付担保金，支持免费取消', 'active', 1, NOW(), NOW()),
(1, '信用卡担保', 'CC_GUARANTEE', '信用卡', '首晚房费', '首晚房费', '18:00', '需提供信用卡担保，超时取消将收取首晚房费', 'active', 1, NOW(), NOW()),
(1, '预付担保', 'PREPAY_GUARANTEE', '预付', '全额房费', '全额房费', NULL, '需全额预付房费，不可取消', 'active', 1, NOW(), NOW()),
(1, '公司担保', 'CORP_GUARANTEE', '公司', NULL, NULL, NULL, '需公司签署担保协议，挂账结算', 'active', 1, NOW(), NOW()),
(1, '第三方担保', 'THIRD_PARTY_GUARANTEE', '第三方', NULL, NULL, NULL, '由第三方平台提供担保，按照平台规则执行', 'active', 1, NOW(), NOW()),
(1, '特殊担保', 'SPECIAL_GUARANTEE', '特殊', NULL, NULL, NULL, '特殊情况下的担保政策，需单独审批', 'inactive', 1, NOW(), NOW());

-- ============================================
-- 租户2: 华住酒店集团
-- ============================================
INSERT INTO guarantee_policies (tenant_id, name, code, type, guarantee_sub_type, guarantee_amount, latest_arrival_time, description, status, group_id, created_at, updated_at) VALUES
(2, '免费担保', 'FREE_GUARANTEE', '无担保', NULL, NULL, NULL, '无需支付担保金', 'active', 2, NOW(), NOW()),
(2, '信用卡预授权', 'CC_AUTH', '信用卡', '首晚房费', '首晚房费', '20:00', '需提供信用卡预授权', 'active', 2, NOW(), NOW()),
(2, '支付宝担保', 'ALIPAY_GUARANTEE', '第三方', NULL, NULL, NULL, '通过支付宝平台提供担保', 'active', 2, NOW(), NOW()),
(2, '微信支付担保', 'WECHAT_GUARANTEE', '第三方', NULL, NULL, NULL, '通过微信支付平台提供担保', 'active', 2, NOW(), NOW()),
(2, '全额预付', 'FULL_PREPAY', '预付', '全额房费', '全额房费', NULL, '需全额预付房费，不可取消', 'active', 2, NOW(), NOW());

-- ============================================
-- 租户3: 万豪国际集团
-- ============================================
INSERT INTO guarantee_policies (tenant_id, name, code, type, guarantee_sub_type, guarantee_amount, latest_arrival_time, description, status, group_id, created_at, updated_at) VALUES
(3, '标准担保', 'STANDARD_GUARANTEE', '无担保', NULL, NULL, NULL, '万豪标准担保政策', 'active', 3, NOW(), NOW()),
(3, '万豪积分担保', 'MARRIOTT_POINTS', '积分', NULL, NULL, NULL, '使用万豪积分担保', 'active', 3, NOW(), NOW()),
(3, '信用卡担保', 'MARRIOTT_CC', '信用卡', '首晚房费', '首晚房费', '18:00', '需提供信用卡担保', 'active', 3, NOW(), NOW()),
(3, '公司协议担保', 'CORP_AGREEMENT', '公司', NULL, NULL, NULL, '公司协议客户担保', 'active', 3, NOW(), NOW()),
(3, '预付房费', 'PREPAY_ROOM', '预付', '全额房费', '全额房费', NULL, '需预付全额房费', 'active', 3, NOW(), NOW()),
(3, '特殊要求担保', 'SPECIAL_REQ', '特殊', NULL, NULL, NULL, '特殊要求需单独担保', 'inactive', 3, NOW(), NOW());

-- ============================================
-- 租户4: 希尔顿酒店集团
-- ============================================
INSERT INTO guarantee_policies (tenant_id, name, code, type, guarantee_sub_type, guarantee_amount, latest_arrival_time, description, status, group_id, created_at, updated_at) VALUES
(4, '希尔顿荣誉客会担保', 'HHONORS_GUARANTEE', '积分', NULL, NULL, NULL, '希尔顿荣誉客会会员担保', 'active', 4, NOW(), NOW()),
(4, '信用卡担保', 'HILTON_CC', '信用卡', '首晚房费', '首晚房费', '18:00', '需提供信用卡担保', 'active', 4, NOW(), NOW()),
(4, '预付预订', 'ADVANCE_PURCHASE', '预付', '全额房费', '全额房费', NULL, '预付预订，不可取消', 'active', 4, NOW(), NOW()),
(4, '公司账户担保', 'COMPANY_ACCOUNT', '公司', NULL, NULL, NULL, '公司账户直接担保', 'active', 4, NOW(), NOW()),
(4, '无担保预订', 'NO_GUARANTEE_BOOK', '无担保', NULL, NULL, NULL, '无需担保的预订', 'active', 4, NOW(), NOW());

-- ============================================
-- 租户5: 洲际酒店集团
-- ============================================
INSERT INTO guarantee_policies (tenant_id, name, code, type, guarantee_sub_type, guarantee_amount, latest_arrival_time, description, status, group_id, created_at, updated_at) VALUES
(5, 'IHG优悦会担保', 'IHG_REWARDS', '积分', NULL, NULL, NULL, 'IHG优悦会会员担保', 'active', 5, NOW(), NOW()),
(5, '信用卡担保', 'IHG_CC', '信用卡', '首晚房费', '首晚房费', '18:00', '需提供信用卡担保', 'active', 5, NOW(), NOW()),
(5, '提前购买价', 'ADVANCE_RATE', '预付', '全额房费', '全额房费', NULL, '提前购买价，不可取消', 'active', 5, NOW(), NOW()),
(5, '公司协议担保', 'IHG_CORP', '公司', NULL, NULL, NULL, '公司协议客户担保', 'active', 5, NOW(), NOW()),
(5, '灵活担保', 'FLEXIBLE_GUARANTEE', '无担保', NULL, NULL, NULL, '灵活担保政策', 'active', 5, NOW(), NOW());

-- ============================================
-- 2. 取消政策数据
-- ============================================

-- 先清空所有现有数据
DELETE FROM cancellation_policies;

-- ============================================
-- 租户1: 锦江酒店集团
-- ============================================
INSERT INTO cancellation_policies (tenant_id, name, code, type, cancellation_days, cancellation_time, cancellation_fee_type, description, status, group_id, created_at, updated_at) VALUES
(1, '免费取消', 'FREE_CANCEL', '免费取消', 1, '18:00', '无', '入住前24小时可免费取消', 'active', 1, NOW(), NOW()),
(1, '部分费用', 'PARTIAL_FEE', '部分费用', 0, '12:00', '50%-100%', '入住前12小时取消收取50%房费，12小时内取消收取100%房费', 'active', 1, NOW(), NOW()),
(1, '不可取消', 'NON_REFUNDABLE', '不可取消', 0, NULL, '100%', '预订后不可取消，无论何时取消均收取100%房费', 'active', 1, NOW(), NOW()),
(1, '特殊取消', 'SPECIAL_CANCEL', '特殊取消', NULL, NULL, '协商确定', '根据特殊情况协商取消，具体费用双方协商', 'active', 1, NOW(), NOW()),
(1, '提前7天取消', '7DAYS_ADVANCE', '提前取消', 7, NULL, '100%', '入住前7天可免费取消，7天内取消收取100%房费', 'active', 1, NOW(), NOW()),
(1, '提前14天取消', '14DAYS_ADVANCE', '提前取消', 14, NULL, '100%', '入住前14天可免费取消，14天内取消收取100%房费', 'inactive', 1, NOW(), NOW());

-- ============================================
-- 租户2: 华住酒店集团
-- ============================================
INSERT INTO cancellation_policies (tenant_id, name, code, type, cancellation_days, cancellation_time, cancellation_fee_type, description, status, group_id, created_at, updated_at) VALUES
(2, '华住标准取消', 'HUAZHU_STANDARD', '免费取消', 1, '18:00', '无', '入住前24小时可免费取消', 'active', 2, NOW(), NOW()),
(2, '华住会员取消', 'HUAZHU_MEMBER', '免费取消', 2, '18:00', '无', '华住会员入住前48小时可免费取消', 'active', 2, NOW(), NOW()),
(2, '限时扣费', 'TIME_LIMITED', '部分费用', 0, '18:00', '首晚房费', '入住当天18:00前取消免费，之后收取首晚房费', 'active', 2, NOW(), NOW()),
(2, '不可取消', 'HUAZHU_NON_REFUND', '不可取消', 0, NULL, '100%', '预订后不可取消', 'active', 2, NOW(), NOW()),
(2, '提前3天取消', '3DAYS_ADVANCE', '提前取消', 3, NULL, '100%', '入住前3天可免费取消', 'active', 2, NOW(), NOW());

-- ============================================
-- 租户3: 万豪国际集团
-- ============================================
INSERT INTO cancellation_policies (tenant_id, name, code, type, cancellation_days, cancellation_time, cancellation_fee_type, description, status, group_id, created_at, updated_at) VALUES
(3, '万豪灵活取消', 'MARRIOTT_FLEX', '免费取消', 1, '18:00', '无', '入住前24小时可免费取消', 'active', 3, NOW(), NOW()),
(3, '万豪标准取消', 'MARRIOTT_STANDARD', '部分费用', 1, '18:00', '首晚房费', '入住前24小时取消免费，之后收取首晚房费', 'active', 3, NOW(), NOW()),
(3, '预付价取消', 'PREPAY_RATE', '不可取消', 0, NULL, '100%', '预付价预订不可取消', 'active', 3, NOW(), NOW()),
(3, '积分兑换取消', 'POINTS_REDEEM', '免费取消', 3, NULL, '无', '积分兑换入住前72小时可取消', 'active', 3, NOW(), NOW()),
(3, '公司协议取消', 'CORP_CANCEL', '免费取消', 2, '18:00', '无', '公司协议客户入住前48小时可取消', 'active', 3, NOW(), NOW()),
(3, '特殊时段取消', 'SPECIAL_PERIOD', '不可取消', 0, NULL, '100%', '特殊时段预订不可取消', 'inactive', 3, NOW(), NOW());

-- ============================================
-- 租户4: 希尔顿酒店集团
-- ============================================
INSERT INTO cancellation_policies (tenant_id, name, code, type, cancellation_days, cancellation_time, cancellation_fee_type, description, status, group_id, created_at, updated_at) VALUES
(4, '希尔顿灵活房价', 'HILTON_FLEX', '免费取消', 1, '18:00', '无', '入住前24小时可免费取消', 'active', 4, NOW(), NOW()),
(4, '希尔顿半灵活房价', 'HILTON_SEMI_FLEX', '部分费用', 1, '18:00', '首晚房费', '入住前24小时取消免费，之后收取首晚房费', 'active', 4, NOW(), NOW()),
(4, '希尔顿预付房价', 'HILTON_PREPAY', '不可取消', 0, NULL, '100%', '预付房价不可取消', 'active', 4, NOW(), NOW()),
(4, '希尔顿荣誉客会取消', 'HHONORS_CANCEL', '免费取消', 2, '18:00', '无', '希尔顿荣誉客会会员入住前48小时可取消', 'active', 4, NOW(), NOW()),
(4, '公司账户取消', 'COMPANY_CANCEL', '免费取消', 3, NULL, '无', '公司账户客户入住前72小时可取消', 'active', 4, NOW(), NOW());

-- ============================================
-- 租户5: 洲际酒店集团
-- ============================================
INSERT INTO cancellation_policies (tenant_id, name, code, type, cancellation_days, cancellation_time, cancellation_fee_type, description, status, group_id, created_at, updated_at) VALUES
(5, 'IHG最佳弹性房价', 'IHG_BEST_FLEX', '免费取消', 1, '18:00', '无', '入住前24小时可免费取消', 'active', 5, NOW(), NOW()),
(5, 'IHG半弹性房价', 'IHG_SEMI_FLEX', '部分费用', 1, '18:00', '首晚房费', '入住前24小时取消免费，之后收取首晚房费', 'active', 5, NOW(), NOW()),
(5, 'IHG预付房价', 'IHG_PREPAY', '不可取消', 0, NULL, '100%', '预付房价不可取消', 'active', 5, NOW(), NOW()),
(5, 'IHG优悦会取消', 'IHG_REWARDS_CANCEL', '免费取消', 2, '18:00', '无', 'IHG优悦会会员入住前48小时可取消', 'active', 5, NOW(), NOW()),
(5, '公司协议取消', 'IHG_CORP_CANCEL', '免费取消', 3, NULL, '无', '公司协议客户入住前72小时可取消', 'active', 5, NOW(), NOW());
