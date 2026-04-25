-- 更新集团房价码表结构以支持新字段
-- 执行时间：2026-04-11

USE crs;

-- 先尝试删除已存在的字段（如果存在）
-- 注意：这会删除数据，仅用于开发环境
-- ALTER TABLE group_rate_codes 
-- DROP COLUMN IF EXISTS rate_category,
-- DROP COLUMN IF EXISTS market_code_id,
-- DROP COLUMN IF EXISTS source_code_id,
-- DROP COLUMN IF EXISTS rate_type,
-- DROP COLUMN IF EXISTS parent_rate_code_id,
-- DROP COLUMN IF EXISTS derivative_level,
-- DROP COLUMN IF EXISTS discount,
-- DROP COLUMN IF EXISTS rounding,
-- DROP COLUMN IF EXISTS guarantee_rule,
-- DROP COLUMN IF EXISTS cancellation_rule,
-- DROP COLUMN IF EXISTS coupon_rule,
-- DROP COLUMN IF EXISTS promotion_rule,
-- DROP COLUMN IF EXISTS allow_points,
-- DROP COLUMN IF EXISTS points_type,
-- DROP COLUMN IF EXISTS points_value;

-- 添加新字段 - 逐个添加以避免重复添加错误
ALTER TABLE group_rate_codes ADD COLUMN rate_category VARCHAR(50) COMMENT '房价大类' AFTER description;
ALTER TABLE group_rate_codes ADD COLUMN market_code_id INT COMMENT '市场码ID' AFTER rate_category;
ALTER TABLE group_rate_codes ADD COLUMN source_code_id INT COMMENT '来源码ID' AFTER market_code_id;
ALTER TABLE group_rate_codes ADD COLUMN rate_type VARCHAR(20) DEFAULT 'basic' COMMENT '房价码类型：basic-基础，derivative-衍生' AFTER source_code_id;
ALTER TABLE group_rate_codes ADD COLUMN parent_rate_code_id INT COMMENT '父级房价码ID' AFTER rate_type;
ALTER TABLE group_rate_codes ADD COLUMN derivative_level VARCHAR(20) DEFAULT 'basic' COMMENT '衍生层级：basic-基础，level1-一级衍生，level2-二级衍生' AFTER parent_rate_code_id;
ALTER TABLE group_rate_codes ADD COLUMN discount DECIMAL(5,2) COMMENT '折扣' AFTER derivative_level;
ALTER TABLE group_rate_codes ADD COLUMN rounding VARCHAR(20) COMMENT '取整方式：round-四舍五入，floor-向下取整，ceil-向上取整' AFTER discount;
ALTER TABLE group_rate_codes ADD COLUMN guarantee_rule VARCHAR(50) COMMENT '担保规则' AFTER rounding;
ALTER TABLE group_rate_codes ADD COLUMN cancellation_rule VARCHAR(50) COMMENT '取消规则' AFTER guarantee_rule;
ALTER TABLE group_rate_codes ADD COLUMN coupon_rule VARCHAR(20) DEFAULT 'unlimited' COMMENT '优惠券规则：unlimited-不限制，limited-限制部分，disabled-不可用' AFTER cancellation_rule;
ALTER TABLE group_rate_codes ADD COLUMN promotion_rule VARCHAR(20) DEFAULT 'unlimited' COMMENT '促销规则：unlimited-不限制，limited-限制部分，disabled-不可用' AFTER coupon_rule;
ALTER TABLE group_rate_codes ADD COLUMN allow_points TINYINT(1) DEFAULT 0 COMMENT '是否允许积分兑换' AFTER promotion_rule;
ALTER TABLE group_rate_codes ADD COLUMN points_type VARCHAR(20) COMMENT '积分类型：fixed-固定值，rate-按比例' AFTER allow_points;
ALTER TABLE group_rate_codes ADD COLUMN points_value DECIMAL(10,2) COMMENT '积分值' AFTER points_type;

-- 显示表结构确认
DESCRIBE group_rate_codes;
