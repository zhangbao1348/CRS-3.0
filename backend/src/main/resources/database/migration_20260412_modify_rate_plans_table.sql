-- 删除并重建 rate_plans 表，使其与集团房价码表结构一致

-- 1. 删除旧表（先删除外键约束）
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS rate_plans;
SET FOREIGN_KEY_CHECKS = 1;

-- 2. 创建新表，与 group_rate_codes 表结构一致
CREATE TABLE rate_plans (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    hotel_id INT NOT NULL COMMENT '酒店ID',
    source_group_rate_code_id INT COMMENT '来源集团房价码ID',
    rate_code VARCHAR(50) NOT NULL COMMENT '房价码代码',
    rate_name VARCHAR(100) NOT NULL COMMENT '房价码名称',
    description TEXT COMMENT '描述',
    rate_category VARCHAR(50) COMMENT '房价大类',
    market_code_id INT COMMENT '市场码ID',
    source_code_id INT COMMENT '来源码ID',
    rate_type VARCHAR(20) DEFAULT 'basic' COMMENT '房价类型',
    parent_rate_code_id INT COMMENT '父级房价码ID',
    derivative_level VARCHAR(20) DEFAULT 'basic' COMMENT '衍生级别',
    discount DECIMAL(10,2) COMMENT '折扣',
    rounding VARCHAR(20) COMMENT '取整方式',
    guarantee_rule VARCHAR(50) COMMENT '担保规则',
    cancellation_rule VARCHAR(50) COMMENT '取消规则',
    coupon_rule VARCHAR(20) DEFAULT 'unlimited' COMMENT '优惠券规则',
    promotion_rule VARCHAR(20) DEFAULT 'unlimited' COMMENT '促销规则',
    allow_points TINYINT(1) DEFAULT 0 COMMENT '允许积分兑换',
    points_type VARCHAR(20) COMMENT '积分类型',
    points_value DECIMAL(10,2) COMMENT '积分值',
    applicable_room_types JSON COMMENT '适用房型',
    packages JSON COMMENT '包价',
    personal_membership JSON COMMENT '个人会员',
    company_membership JSON COMMENT '企业会员',
    advance_booking_min INT COMMENT '提前预订最小天数',
    advance_booking_max INT COMMENT '提前预订最大天数',
    minimum_stay_min INT COMMENT '最小连住天数',
    minimum_stay_max INT COMMENT '最大连住天数',
    booking_start_time VARCHAR(10) COMMENT '预订开始时间',
    booking_end_time VARCHAR(10) COMMENT '预订结束时间',
    checkin_start_time VARCHAR(10) COMMENT '入住开始时间',
    checkin_end_time VARCHAR(10) COMMENT '入住结束时间',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    INDEX idx_hotel_id (hotel_id),
    INDEX idx_rate_code (rate_code),
    INDEX idx_source_group_rate_code_id (source_group_rate_code_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒店价格计划表';
