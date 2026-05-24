-- 说明：
-- 1. 将 guarantee_policies.type 从中文文案归一化为规则编码
-- 2. 该脚本需通过终端手工执行，不在应用启动时自动运行

UPDATE guarantee_policies SET type = 'none' WHERE type = '无担保';
UPDATE guarantee_policies SET type = 'credit_card' WHERE type = '信用卡';
UPDATE guarantee_policies SET type = 'prepaid' WHERE type IN ('预付', 'prepay');
UPDATE guarantee_policies SET type = 'company' WHERE type = '公司';
UPDATE guarantee_policies SET type = 'third_party' WHERE type IN ('第三方', 'thirdparty');
UPDATE guarantee_policies SET type = 'special' WHERE type = '特殊';
