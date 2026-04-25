-- 为每个租户插入取消政策数据

USE CRS;

-- ============================================
-- 租户1
-- ============================================
INSERT IGNORE INTO cancellation_policies (tenant_id, name, code, type, cancellation_days, cancellation_time, cancellation_fee_type, description, status, group_id, created_at, updated_at) VALUES
(1, '免费取消', 'FREE_CANCEL', '免费取消', 1, '18:00', '无', '入住前24小时可免费取消', 'active', 1, NOW(), NOW()),
(1, '部分费用', 'PARTIAL_FEE', '部分费用', 0, '12:00', '50%-100%', '入住前12小时取消收取50%房费，12小时内取消收取100%房费', 'active', 1, NOW(), NOW()),
(1, '不可取消', 'NON_REFUNDABLE', '不可取消', 0, NULL, '100%', '预订后不可取消，无论何时取消均收取100%房费', 'active', 1, NOW(), NOW()),
(1, '特殊取消', 'SPECIAL_CANCEL', '特殊取消', NULL, NULL, '协商确定', '根据特殊情况协商取消，具体费用双方协商', 'active', 1, NOW(), NOW()),
(1, '提前7天取消', '7DAYS_ADVANCE', '提前取消', 7, NULL, '100%', '入住前7天可免费取消，7天内取消收取100%房费', 'active', 1, NOW(), NOW()),
(1, '提前14天取消', '14DAYS_ADVANCE', '提前取消', 14, NULL, '100%', '入住前14天可免费取消，14天内取消收取100%房费', 'inactive', 1, NOW(), NOW());

-- ============================================
-- 租户2
-- ============================================
INSERT IGNORE INTO cancellation_policies (tenant_id, name, code, type, cancellation_days, cancellation_time, cancellation_fee_type, description, status, group_id, created_at, updated_at) VALUES
(2, '华住标准取消', 'HUAZHU_STANDARD', '免费取消', 1, '18:00', '无', '入住前24小时可免费取消', 'active', 2, NOW(), NOW()),
(2, '华住会员取消', 'HUAZHU_MEMBER', '免费取消', 2, '18:00', '无', '华住会员入住前48小时可免费取消', 'active', 2, NOW(), NOW()),
(2, '限时扣费', 'TIME_LIMITED', '部分费用', 0, '18:00', '首晚房费', '入住当天18:00前取消免费，之后收取首晚房费', 'active', 2, NOW(), NOW()),
(2, '不可取消', 'HUAZHU_NON_REFUND', '不可取消', 0, NULL, '100%', '预订后不可取消', 'active', 2, NOW(), NOW()),
(2, '提前3天取消', '3DAYS_ADVANCE', '提前取消', 3, NULL, '100%', '入住前3天可免费取消', 'active', 2, NOW(), NOW());

-- ============================================
-- 租户3
-- ============================================
INSERT IGNORE INTO cancellation_policies (tenant_id, name, code, type, cancellation_days, cancellation_time, cancellation_fee_type, description, status, group_id, created_at, updated_at) VALUES
(3, '万豪灵活取消', 'MARRIOTT_FLEX', '免费取消', 1, '18:00', '无', '入住前24小时可免费取消', 'active', 3, NOW(), NOW()),
(3, '万豪标准取消', 'MARRIOTT_STANDARD', '部分费用', 1, '18:00', '首晚房费', '入住前24小时取消免费，之后收取首晚房费', 'active', 3, NOW(), NOW()),
(3, '预付价取消', 'PREPAY_RATE', '不可取消', 0, NULL, '100%', '预付价预订不可取消', 'active', 3, NOW(), NOW()),
(3, '积分兑换取消', 'POINTS_REDEEM', '免费取消', 3, NULL, '无', '积分兑换入住前72小时可取消', 'active', 3, NOW(), NOW()),
(3, '公司协议取消', 'CORP_CANCEL', '免费取消', 2, '18:00', '无', '公司协议客户入住前48小时可取消', 'active', 3, NOW(), NOW()),
(3, '特殊时段取消', 'SPECIAL_PERIOD', '不可取消', 0, NULL, '100%', '特殊时段预订不可取消', 'inactive', 3, NOW(), NOW());

-- 显示插入数据统计
SELECT CONCAT('取消政策数据插入完成！共为 ', COUNT(DISTINCT tenant_id), ' 个租户插入 ', COUNT(*), ' 条取消政策数据') AS result 
FROM cancellation_policies;

SELECT tenant_id AS 租户ID, COUNT(*) AS 取消政策数量 
FROM cancellation_policies 
GROUP BY tenant_id 
ORDER BY tenant_id;
