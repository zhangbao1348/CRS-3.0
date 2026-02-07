-- 创建集团设施表
CREATE TABLE IF NOT EXISTS `group_facilities` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `facility_type` VARCHAR(50) NOT NULL COMMENT '设施类型',
  `facility_name` VARCHAR(100) NOT NULL COMMENT '设施名称',
  `facility_code` VARCHAR(50) NOT NULL COMMENT '设施代码',
  `available` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否可用',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '设施描述',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_facility_code` (`facility_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='集团设施表';

-- 插入测试数据
INSERT INTO `group_facilities` (`facility_type`, `facility_name`, `facility_code`, `available`, `description`) VALUES
('transportation', '收费停车场', 'PAID_PARKING', TRUE, '提供收费停车场服务'),
('transportation', '免费停车场', 'FREE_PARKING', TRUE, '提供免费停车场服务'),
('transportation', '免费接送机', 'FREE_SHUTTLE', TRUE, '提供免费接送机服务'),
('transportation', '收费接送机', 'PAID_SHUTTLE', TRUE, '提供收费接送机服务'),
('transportation', '租车服务', 'CAR_RENTAL', TRUE, '提供租车服务'),
('dining', '自助早餐厅', 'BUFFET_RESTAURANT', TRUE, '提供自助早餐服务'),
('dining', '咖啡厅', 'CAFE', TRUE, '提供咖啡和简餐服务'),
('dining', '中餐厅', 'CHINESE_RESTAURANT', TRUE, '提供中餐服务'),
('dining', '西餐厅', 'WESTERN_RESTAURANT', TRUE, '提供西餐服务'),
('dining', '酒吧', 'BAR', TRUE, '提供酒吧服务'),
('dining', '24小时便利店', 'CONVENIENCE_STORE', TRUE, '提供24小时便利店服务'),
('cleaning', '外送洗衣服务', 'LAUNDRY_SERVICE', TRUE, '提供外送洗衣服务'),
('cleaning', '干衣机', 'DRYER', TRUE, '提供干衣机服务'),
('cleaning', '熨斗/挂烫机', 'IRON', TRUE, '提供熨斗和挂烫机服务'),
('cleaning', '洗衣房', 'LAUNDRY_ROOM', TRUE, '提供洗衣房服务'),
('cleaning', '熨衣服务', 'VALET_SERVICE', TRUE, '提供熨衣服务'),
('cleaning', '洗衣服务', 'WASHING_SERVICE', TRUE, '提供洗衣服务'),
('other', '健身房', 'GYM', TRUE, '提供健身房服务'),
('other', '游泳池', 'SWIMMING_POOL', TRUE, '提供游泳池服务'),
('other', 'SPA', 'SPA', TRUE, '提供SPA服务'),
('other', '会议室', 'MEETING_ROOM', TRUE, '提供会议室服务'),
('other', '商务中心', 'BUSINESS_CENTER', TRUE, '提供商务中心服务'),
('other', '行李寄存', 'LUGGAGE_STORAGE', TRUE, '提供行李寄存服务'),
('other', '叫醒服务', 'WAKE_UP_SERVICE', TRUE, '提供叫醒服务'),
('other', 'concierge服务', 'CONCIERGE', TRUE, '提供concierge服务');
