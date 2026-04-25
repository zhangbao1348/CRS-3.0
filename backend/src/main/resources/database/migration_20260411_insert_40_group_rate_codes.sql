-- 为上海全季酒店所属集团（tenant_id=2）插入40条集团房价码测试数据
-- 执行此脚本前请确保已经连接到 CRS 数据库

USE CRS;

-- 插入40条集团房价码数据
INSERT INTO group_rate_codes (group_id, rate_code, rate_name, description, status, created_at, updated_at) VALUES
(2, 'RACK', '标准价', '酒店标准挂牌价', 'active', NOW(), NOW()),
(2, 'WEEKEND', '周末价', '周末专属优惠价格', 'active', NOW(), NOW()),
(2, 'CORP', '企业价', '企业协议客户专享价', 'active', NOW(), NOW()),
(2, 'MEMBER', '会员价', '会员专享优惠价', 'active', NOW(), NOW()),
(2, 'GROUP', '团队价', '团队预订优惠价', 'active', NOW(), NOW()),
(2, 'PROMO', '促销价', '限时促销活动价', 'active', NOW(), NOW()),
(2, 'LONG_STAY', '长住价', '长住客人专享价', 'active', NOW(), NOW()),
(2, 'GOV', '政府价', '政府机关协议价', 'active', NOW(), NOW()),
(2, 'MILITARY', '军人价', '军人专享优惠价', 'active', NOW(), NOW()),
(2, 'STUDENT', '学生价', '学生专享优惠价', 'active', NOW(), NOW()),
(2, 'PEAK', '旺季价', '旅游旺季价格', 'active', NOW(), NOW()),
(2, 'OFF_PEAK', '淡季价', '旅游淡季优惠价', 'active', NOW(), NOW()),
(2, 'HOLIDAY', '节假日价', '节假日专属价格', 'active', NOW(), NOW()),
(2, 'BIRTHDAY', '生日价', '生日当天专享价', 'active', NOW(), NOW()),
(2, 'COUPLE', '情侣价', '情侣入住专享价', 'active', NOW(), NOW()),
(2, 'FAMILY', '家庭价', '家庭入住专享价', 'active', NOW(), NOW()),
(2, 'BUSINESS', '商务价', '商务客人专享价', 'active', NOW(), NOW()),
(2, 'MEETING', '会议价', '会议团队专享价', 'active', NOW(), NOW()),
(2, 'AIRLINE', '航空价', '航空公司机组人员价', 'active', NOW(), NOW()),
(2, 'EMPLOYEE', '酒店员工价', '酒店员工专享价', 'active', NOW(), NOW()),
(2, 'EARLY_BIRD', '早鸟价', '提前预订优惠价', 'active', NOW(), NOW()),
(2, 'LAST_MINUTE', '尾单价', '当日预订优惠价', 'active', NOW(), NOW()),
(2, 'PACKAGE', '套餐价', '含服务套餐价格', 'active', NOW(), NOW()),
(2, 'DINNER', '含晚餐价', '含晚餐套餐价格', 'active', NOW(), NOW()),
(2, 'SPA', '含SPA价', '含SPA服务价格', 'active', NOW(), NOW()),
(2, 'GOLF', '含高尔夫价', '含高尔夫球场价格', 'active', NOW(), NOW()),
(2, 'WEDDING', '婚宴价', '婚宴套餐价格', 'active', NOW(), NOW()),
(2, 'ANNIVERSARY', '周年庆价', '周年庆活动价', 'active', NOW(), NOW()),
(2, 'SPRING', '春季价', '春季专属价格', 'active', NOW(), NOW()),
(2, 'SUMMER', '夏季价', '夏季专属价格', 'active', NOW(), NOW()),
(2, 'AUTUMN', '秋季价', '秋季专属价格', 'active', NOW(), NOW()),
(2, 'WINTER', '冬季价', '冬季专属价格', 'active', NOW(), NOW()),
(2, 'NEW_YEAR', '新年价', '新年期间价格', 'active', NOW(), NOW()),
(2, 'CHRISTMAS', '圣诞价', '圣诞节期间价格', 'active', NOW(), NOW()),
(2, 'VALENTINE', '情人节价', '情人节专享价', 'active', NOW(), NOW()),
(2, 'MOON_FESTIVAL', '中秋价', '中秋节专享价', 'active', NOW(), NOW()),
(2, 'SPRING_FESTIVAL', '春节价', '春节期间价格', 'active', NOW(), NOW()),
(2, 'NATIONAL_DAY', '国庆价', '国庆期间价格', 'active', NOW(), NOW()),
(2, 'CORP_VIP', '企业VIP价', '重要企业客户价', 'active', NOW(), NOW()),
(2, 'DIAMOND', '钻石会员价', '钻石会员专享价', 'active', NOW(), NOW());

-- 显示插入结果
SELECT CONCAT('集团房价码测试数据插入完成！共插入 ', COUNT(*), ' 条数据') AS result FROM group_rate_codes WHERE group_id = 2;

-- 显示插入的数据
SELECT id, group_id, rate_code, rate_name, status FROM group_rate_codes WHERE group_id = 2 ORDER BY id;
