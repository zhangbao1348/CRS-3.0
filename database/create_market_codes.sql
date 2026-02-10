-- 创建市场码表
CREATE TABLE IF NOT EXISTS `market_codes` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(50) NOT NULL COMMENT '市场码CODE',
  `name` VARCHAR(100) NOT NULL COMMENT '市场码名称',
  `description` TEXT COMMENT '描述',
  `parent_id` INT COMMENT '父ID',
  `level` INT NOT NULL DEFAULT 1 COMMENT '层级',
  `status` ENUM('active', 'inactive') NOT NULL DEFAULT 'active' COMMENT '状态',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_level` (`level`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='市场码表';

-- 插入初始数据
INSERT INTO `market_codes` (`code`, `name`, `description`, `parent_id`, `level`, `status`) VALUES
('ONLINE', '线上市场', '线上销售渠道', NULL, 1, 'active'),
('OTA', 'OTA平台', '在线旅行社平台', 1, 2, 'active'),
('CTRIP', '携程', '携程旅行网', 2, 3, 'active'),
('MEITUAN', '美团', '美团旅行', 2, 3, 'active'),
('FLIGGY', '飞猪', '飞猪旅行', 2, 3, 'active'),
('DIRECT', '直销平台', '酒店直销渠道', 1, 2, 'active'),
('OFFICIAL', '官网预订', '酒店官方网站', 6, 3, 'active'),
('WECHAT', '微信小程序', '微信小程序预订', 6, 3, 'active'),
('OFFLINE', '线下市场', '线下销售渠道', NULL, 1, 'active'),
('TRAVEL_AGENCY', '旅行社', '传统旅行社', 9, 2, 'active'),
('DOMESTIC_TA', '国内旅行社', '国内旅行社客户', 10, 3, 'active'),
('INTERNATIONAL_TA', '国际旅行社', '国际旅行社客户', 10, 3, 'active'),
('CORPORATE', '企业客户', '企业商务客户', 9, 2, 'active'),
('LOCAL_CORP', '本地企业', '本地企业客户', 13, 3, 'active'),
('MNC', '跨国企业', '跨国企业客户', 13, 3, 'active');
