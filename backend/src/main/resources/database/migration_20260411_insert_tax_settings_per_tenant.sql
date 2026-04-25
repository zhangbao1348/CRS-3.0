-- 为每个租户插入税和服务费设置数据
-- 根据现有的5个租户，为每个租户插入默认的税率设置

USE CRS;

-- 为租户1（万豪国际集团）插入税率设置
INSERT IGNORE INTO tax_settings (tenant_id, tax_code, legal_name, bearer, base_type, rate_amount, rate_currency, calculation_rule, deductible, refundable, settlement_rule, compliance_requirements, remarks, status, created_at, updated_at) VALUES
(1, 'VAT-CN-001', '中国增值税(VAT)', 'guest', 'net_excluding_service', 6.0000, '%', 'inclusive', 'yes', 'full', 'checkin', '欧盟 VAT 需按季度申报，留存订单明细10年', '东京都宿泊税费1万日元以上免征，美国纽约长租30天以上免征', 'active', NOW(), NOW()),
(1, 'CITYTAX-FR-PAR-001', '法国巴黎城市税', 'guest', 'per_person_room', 4.0000, 'EUR', 'exclusive', 'no', 'none', 'prepaid', '日本宿泊税确认后不可退', '法国巴黎城市税，按人头每晚4欧元', 'active', NOW(), NOW()),
(1, 'TOURISM-JP-TKY-001', '日本东京都宿泊税', 'guest', 'per_person_room', 200.0000, 'CNY', 'exclusive', 'no', 'partial', 'checkin', '1万日元以上免征', '日本东京都宿泊税，1万日元以上免征', 'active', NOW(), NOW()),
(1, 'VAT-DE-001', '德国增值税(VAT)', 'hotel', 'including_service', 19.0000, '%', 'inclusive', 'yes', 'full', 'self_report', '德国VAT按月申报', '德国增值税，标准税率19%', 'active', NOW(), NOW()),
(1, 'TAX-US-NYC-001', '美国纽约市酒店税', 'guest', 'order_total', 5.8750, '%', 'exclusive', 'no', 'full', 'prepaid', '美国纽约长租30天以上免征', '美国纽约市酒店税，长租30天以上免征', 'inactive', NOW(), NOW());

-- 为租户2（希尔顿酒店集团）插入税率设置
INSERT IGNORE INTO tax_settings (tenant_id, tax_code, legal_name, bearer, base_type, rate_amount, rate_currency, calculation_rule, deductible, refundable, settlement_rule, compliance_requirements, remarks, status, created_at, updated_at) VALUES
(2, 'VAT-US-HIL-001', '美国销售税', 'guest', 'net_excluding_service', 8.8750, '%', 'exclusive', 'no', 'full', 'checkin', '美国销售税需按州申报', '希尔顿荣誉客会会员免税政策', 'active', NOW(), NOW()),
(2, 'CITYTAX-UK-LON-001', '英国伦敦市政税', 'guest', 'per_room_night', 2.0000, 'GBP', 'exclusive', 'no', 'none', 'prepaid', '伦敦市政税不可抵扣', '希尔顿伦敦酒店每晚2英镑市政税', 'active', NOW(), NOW()),
(2, 'VAT-GB-001', '英国增值税(VAT)', 'hotel', 'including_service', 20.0000, '%', 'inclusive', 'yes', 'full', 'self_report', '英国VAT按季度申报', '希尔顿英国增值税标准税率20%', 'active', NOW(), NOW()),
(2, 'TOURISM-IT-ROM-001', '意大利罗马旅游税', 'guest', 'per_person_room', 3.5000, 'EUR', 'exclusive', 'no', 'partial', 'checkin', '罗马旅游税按星级征收', '希尔顿罗马酒店旅游税3.5欧元', 'active', NOW(), NOW()),
(2, 'SERVICE-CHARGE-HIL-001', '希尔顿服务费', 'hotel', 'net_excluding_service', 10.0000, '%', 'inclusive', 'no', 'none', 'checkin', '服务费包含员工福利', '希尔顿标准服务费10%', 'active', NOW(), NOW());

-- 为租户3（洲际酒店集团）插入税率设置
INSERT IGNORE INTO tax_settings (tenant_id, tax_code, legal_name, bearer, base_type, rate_amount, rate_currency, calculation_rule, deductible, refundable, settlement_rule, compliance_requirements, remarks, status, created_at, updated_at) VALUES
(3, 'VAT-AU-SYD-001', '澳大利亚GST', 'guest', 'order_total', 10.0000, '%', 'inclusive', 'yes', 'full', 'checkin', '澳大利亚GST按季度申报', '洲际悉尼酒店GST 10%', 'active', NOW(), NOW()),
(3, 'CITYTAX-SG-SGP-001', '新加坡消费税', 'guest', 'net_excluding_service', 8.0000, '%', 'exclusive', 'yes', 'full', 'prepaid', '新加坡GST可抵扣', '洲际新加坡酒店消费税8%', 'active', NOW(), NOW()),
(3, 'VAT-SG-001', '新加坡增值税', 'hotel', 'including_service', 9.0000, '%', 'inclusive', 'yes', 'full', 'self_report', '新加坡VAT按月申报', '洲际新加坡增值税9%', 'active', NOW(), NOW()),
(3, 'TOURISM-TH-BKK-001', '泰国曼谷旅游税', 'guest', 'per_room_night', 30.0000, 'THB', 'exclusive', 'no', 'none', 'checkin', '泰国旅游税支持旅游发展', '洲际曼谷酒店旅游税30泰铢', 'active', NOW(), NOW()),
(3, 'SERVICE-CHARGE-IGH-001', '洲际服务费', 'hotel', 'net_excluding_service', 12.0000, '%', 'inclusive', 'no', 'none', 'checkin', '服务费用于员工培训', '洲际标准服务费12%', 'active', NOW(), NOW());

-- 为租户4（凯悦酒店集团）插入税率设置
INSERT IGNORE INTO tax_settings (tenant_id, tax_code, legal_name, bearer, base_type, rate_amount, rate_currency, calculation_rule, deductible, refundable, settlement_rule, compliance_requirements, remarks, status, created_at, updated_at) VALUES
(4, 'VAT-JP-TYO-001', '日本消费税', 'guest', 'net_excluding_service', 10.0000, '%', 'inclusive', 'yes', 'full', 'checkin', '日本消费税每两年调整', '凯悦东京酒店消费税10%', 'active', NOW(), NOW()),
(4, 'CITYTAX-JP-OSA-001', '日本大阪宿泊税', 'guest', 'per_person_room', 100.0000, 'JPY', 'exclusive', 'no', 'none', 'prepaid', '大阪宿泊税1万日元以下免征', '凯悦大阪酒店宿泊税100日元', 'active', NOW(), NOW()),
(4, 'VAT-KR-SEO-001', '韩国增值税', 'hotel', 'including_service', 10.0000, '%', 'inclusive', 'yes', 'full', 'self_report', '韩国VAT按月申报', '凯悦首尔酒店增值税10%', 'active', NOW(), NOW()),
(4, 'TOURISM-MY-KUL-001', '马来西亚吉隆坡旅游税', 'guest', 'per_room_night', 10.0000, 'MYR', 'exclusive', 'no', 'partial', 'checkin', '马来西亚旅游税支持旅游业', '凯悦吉隆坡酒店旅游税10马币', 'active', NOW(), NOW()),
(4, 'SERVICE-CHARGE-HYATT-001', '凯悦服务费', 'hotel', 'net_excluding_service', 15.0000, '%', 'inclusive', 'no', 'none', 'checkin', '凯悦天地会员服务费折扣', '凯悦标准服务费15%', 'active', NOW(), NOW());

-- 为租户5（雅高酒店集团）插入税率设置
INSERT IGNORE INTO tax_settings (tenant_id, tax_code, legal_name, bearer, base_type, rate_amount, rate_currency, calculation_rule, deductible, refundable, settlement_rule, compliance_requirements, remarks, status, created_at, updated_at) VALUES
(5, 'VAT-FR-PAR-001', '法国增值税(VAT)', 'guest', 'net_excluding_service', 20.0000, '%', 'inclusive', 'yes', 'full', 'checkin', '法国VAT按月申报', '雅高巴黎酒店VAT 20%', 'active', NOW(), NOW()),
(5, 'CITYTAX-ES-MAD-001', '西班牙马德里城市税', 'guest', 'per_person_room', 2.5000, 'EUR', 'exclusive', 'no', 'none', 'prepaid', '马德里城市税按人头征收', '雅高马德里酒店城市税2.5欧元', 'active', NOW(), NOW()),
(5, 'VAT-IT-MIL-001', '意大利增值税', 'hotel', 'including_service', 22.0000, '%', 'inclusive', 'yes', 'full', 'self_report', '意大利VAT按季度申报', '雅高米兰酒店增值税22%', 'active', NOW(), NOW()),
(5, 'TOURISM-PT-LIS-001', '葡萄牙里斯本旅游税', 'guest', 'per_room_night', 2.0000, 'EUR', 'exclusive', 'no', 'partial', 'checkin', '里斯本旅游税支持文化保护', '雅高里斯本酒店旅游税2欧元', 'active', NOW(), NOW()),
(5, 'SERVICE-CHARGE-ACCOR-001', '雅高服务费', 'hotel', 'net_excluding_service', 10.0000, '%', 'inclusive', 'no', 'none', 'checkin', 'ALL会员服务费优惠', '雅高标准服务费10%', 'active', NOW(), NOW());

-- 显示完成信息
SELECT '各租户税和服务费设置数据插入完成！' AS result;
SELECT tenant_id, COUNT(*) AS tax_setting_count 
FROM tax_settings 
GROUP BY tenant_id 
ORDER BY tenant_id;
