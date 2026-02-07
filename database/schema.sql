-- 集团表
CREATE TABLE IF NOT EXISTS groups (
  id INT PRIMARY KEY AUTO_INCREMENT,
  group_code VARCHAR(50) NOT NULL UNIQUE COMMENT '集团代码',
  group_name VARCHAR(100) NOT NULL COMMENT '集团名称',
  description TEXT COMMENT '集团描述',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集团表';

-- 酒店表
CREATE TABLE IF NOT EXISTS hotels (
  id INT PRIMARY KEY AUTO_INCREMENT,
  hotel_code VARCHAR(50) NOT NULL UNIQUE COMMENT '酒店代码',
  group_id INT NOT NULL COMMENT '所属集团ID',
  chinese_name VARCHAR(100) NOT NULL COMMENT '中文名称',
  english_name VARCHAR(100) NOT NULL COMMENT '英文名称',
  star_rating ENUM('1', '2', '3', '4', '5') COMMENT '星级',
  province VARCHAR(50) NOT NULL COMMENT '省份',
  city VARCHAR(50) NOT NULL COMMENT '城市',
  address VARCHAR(200) NOT NULL COMMENT '地址',
  longitude DECIMAL(10,6) COMMENT '经度',
  latitude DECIMAL(10,6) COMMENT '纬度',
  phone VARCHAR(20) NOT NULL COMMENT '电话',
  email VARCHAR(100) NOT NULL COMMENT '邮箱',
  introduction TEXT COMMENT '酒店介绍',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒店表';

-- 集团房型表
CREATE TABLE IF NOT EXISTS group_room_types (
  id INT PRIMARY KEY AUTO_INCREMENT,
  group_id INT NOT NULL COMMENT '所属集团ID',
  room_type_code VARCHAR(50) NOT NULL UNIQUE COMMENT '房型代码',
  room_type_name VARCHAR(100) NOT NULL COMMENT '房型名称',
  description TEXT COMMENT '房型描述',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集团房型表';

-- 集团房价码表
CREATE TABLE IF NOT EXISTS group_rate_codes (
  id INT PRIMARY KEY AUTO_INCREMENT,
  group_id INT NOT NULL COMMENT '所属集团ID',
  rate_code VARCHAR(50) NOT NULL UNIQUE COMMENT '房价码',
  rate_name VARCHAR(100) NOT NULL COMMENT '房价名称',
  description TEXT COMMENT '房价描述',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集团房价码表';

-- 市场码表
CREATE TABLE IF NOT EXISTS market_codes (
  id INT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(50) NOT NULL UNIQUE COMMENT '市场码',
  name VARCHAR(100) NOT NULL COMMENT '市场名称',
  description TEXT COMMENT '市场描述',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='市场码表';

-- 渠道码表
CREATE TABLE IF NOT EXISTS channel_codes (
  id INT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(50) NOT NULL UNIQUE COMMENT '渠道码',
  name VARCHAR(100) NOT NULL COMMENT '渠道名称',
  description TEXT COMMENT '渠道描述',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道码表';

-- 来源码表
CREATE TABLE IF NOT EXISTS source_codes (
  id INT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(50) NOT NULL UNIQUE COMMENT '来源码',
  name VARCHAR(100) NOT NULL COMMENT '来源名称',
  description TEXT COMMENT '来源描述',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='来源码表';

-- 税率设置表
CREATE TABLE IF NOT EXISTS tax_settings (
  id INT PRIMARY KEY AUTO_INCREMENT,
  tax_type ENUM('room', 'tax', 'fee') NOT NULL COMMENT '税率类型（房费/税费/服务费）',
  tax_name VARCHAR(100) NOT NULL COMMENT '税率名称',
  tax_rate DECIMAL(5,2) NOT NULL COMMENT '税率值（百分比）',
  description TEXT COMMENT '税率描述',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='税率设置表';

-- 包价表
CREATE TABLE IF NOT EXISTS packages (
  id INT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(50) NOT NULL UNIQUE COMMENT '包价代码',
  name VARCHAR(100) NOT NULL COMMENT '包价名称',
  description TEXT COMMENT '包价描述',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='包价表';

-- 集团担保政策表
CREATE TABLE IF NOT EXISTS group_guarantee_policies (
  id INT PRIMARY KEY AUTO_INCREMENT,
  group_id INT NOT NULL COMMENT '所属集团ID',
  policy_code VARCHAR(50) NOT NULL UNIQUE COMMENT '政策代码',
  policy_name VARCHAR(100) NOT NULL COMMENT '政策名称',
  policy_details TEXT NOT NULL COMMENT '政策详情',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集团担保政策表';

-- 集团取消政策表
CREATE TABLE IF NOT EXISTS group_cancellation_policies (
  id INT PRIMARY KEY AUTO_INCREMENT,
  group_id INT NOT NULL COMMENT '所属集团ID',
  policy_code VARCHAR(50) NOT NULL UNIQUE COMMENT '政策代码',
  policy_name VARCHAR(100) NOT NULL COMMENT '政策名称',
  policy_details TEXT NOT NULL COMMENT '政策详情',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集团取消政策表';

-- 房型表
CREATE TABLE IF NOT EXISTS room_types (
  id INT PRIMARY KEY AUTO_INCREMENT,
  hotel_id INT NOT NULL COMMENT '所属酒店ID',
  group_room_type_id INT COMMENT '关联集团房型ID',
  code VARCHAR(50) NOT NULL COMMENT '房型代码',
  name VARCHAR(100) NOT NULL COMMENT '房型名称',
  description TEXT COMMENT '房型描述',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_hotel_room_code (hotel_id, code),
  FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE,
  FOREIGN KEY (group_room_type_id) REFERENCES group_room_types(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房型表';

-- 价格类型表
CREATE TABLE IF NOT EXISTS rate_types (
  id INT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(50) NOT NULL UNIQUE COMMENT '价格类型代码',
  name VARCHAR(100) NOT NULL COMMENT '价格类型名称',
  description TEXT COMMENT '价格类型描述',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格类型表';

-- 房型差价体系表
CREATE TABLE IF NOT EXISTS room_type_diff_systems (
  id INT PRIMARY KEY AUTO_INCREMENT,
  hotel_id INT NOT NULL COMMENT '所属酒店ID',
  name VARCHAR(100) NOT NULL COMMENT '差价体系名称',
  description TEXT COMMENT '差价体系描述',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房型差价体系表';

-- 房型差价表
CREATE TABLE IF NOT EXISTS room_type_diffs (
  id INT PRIMARY KEY AUTO_INCREMENT,
  system_id INT NOT NULL COMMENT '差价体系ID',
  room_type_id INT NOT NULL COMMENT '房型ID',
  value DECIMAL(10,2) NOT NULL COMMENT '差价数值',
  start_date DATE NOT NULL COMMENT '起始日期',
  end_date DATE COMMENT '结束日期',
  weekdays VARCHAR(20) NOT NULL COMMENT '适用星期（逗号分隔的数字，1-7代表周一到周日）',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (system_id) REFERENCES room_type_diff_systems(id) ON DELETE CASCADE,
  FOREIGN KEY (room_type_id) REFERENCES room_types(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房型差价表';

-- 人数差价体系表
CREATE TABLE IF NOT EXISTS person_diff_systems (
  id INT PRIMARY KEY AUTO_INCREMENT,
  hotel_id INT NOT NULL COMMENT '所属酒店ID',
  name VARCHAR(100) NOT NULL COMMENT '差价体系名称',
  description TEXT COMMENT '差价体系描述',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人数差价体系表';

-- 人数差价表
CREATE TABLE IF NOT EXISTS person_diffs (
  id INT PRIMARY KEY AUTO_INCREMENT,
  system_id INT NOT NULL COMMENT '差价体系ID',
  person_type VARCHAR(50) NOT NULL COMMENT '人数类型（如2成人、3成人）',
  value DECIMAL(10,2) NOT NULL COMMENT '差价数值',
  start_date DATE NOT NULL COMMENT '起始日期',
  end_date DATE COMMENT '结束日期',
  weekdays VARCHAR(20) NOT NULL COMMENT '适用星期（逗号分隔的数字，1-7代表周一到周日）',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (system_id) REFERENCES person_diff_systems(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人数差价表';

-- 价格计划表
CREATE TABLE IF NOT EXISTS rate_plans (
  id INT PRIMARY KEY AUTO_INCREMENT,
  hotel_id INT NOT NULL COMMENT '所属酒店ID',
  rate_code VARCHAR(50) NOT NULL COMMENT '价格计划代码',
  rate_name VARCHAR(100) NOT NULL COMMENT '价格计划名称',
  rate_category VARCHAR(50) COMMENT '价格计划类别',
  market_code_id INT NOT NULL COMMENT '市场码ID',
  channel_code_id INT NOT NULL COMMENT '渠道码ID',
  source_code_id INT NOT NULL COMMENT '来源码ID',
  type ENUM('basic', 'derivative') NOT NULL COMMENT '类型：basic-基础价格计划，derivative-衍生价格计划',
  parent_rate_code VARCHAR(50) COMMENT '父级价格计划代码（仅衍生价格计划）',
  discount DECIMAL(5,2) COMMENT '折扣（仅衍生价格计划）',
  rounding ENUM('round', 'floor', 'ceil') COMMENT '取整方式（仅衍生价格计划）',
  room_type_diff_id INT COMMENT '房型价差体系ID（仅基础价格计划）',
  person_diff_id INT COMMENT '人数价差体系ID（仅基础价格计划）',
  guarantee_policy_id INT NOT NULL COMMENT '担保政策ID',
  cancellation_policy_id INT NOT NULL COMMENT '取消政策ID',
  coupon_rule ENUM('unlimited', 'limited', 'disabled') NOT NULL COMMENT '可用优惠券规则',
  promotion_rule ENUM('unlimited', 'limited', 'disabled') NOT NULL COMMENT '可用促销规则',
  allow_points BOOLEAN DEFAULT FALSE COMMENT '是否允许积分兑换',
  min_advance_booking INT COMMENT '提前预订最小天数',
  max_advance_booking INT COMMENT '提前预订最大天数',
  min_stay_nights INT COMMENT '连住最小天数',
  max_stay_nights INT COMMENT '连住最大天数',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_hotel_rate_code (hotel_id, rate_code),
  FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE,
  FOREIGN KEY (market_code_id) REFERENCES market_codes(id),
  FOREIGN KEY (channel_code_id) REFERENCES channel_codes(id),
  FOREIGN KEY (source_code_id) REFERENCES source_codes(id),
  FOREIGN KEY (room_type_diff_id) REFERENCES room_type_diff_systems(id) ON DELETE SET NULL,
  FOREIGN KEY (person_diff_id) REFERENCES person_diff_systems(id) ON DELETE SET NULL,
  FOREIGN KEY (guarantee_policy_id) REFERENCES group_guarantee_policies(id),
  FOREIGN KEY (cancellation_policy_id) REFERENCES group_cancellation_policies(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格计划表';

-- 基础价格表
CREATE TABLE IF NOT EXISTS base_prices (
  id INT PRIMARY KEY AUTO_INCREMENT,
  hotel_id INT NOT NULL COMMENT '所属酒店ID',
  rate_type_id INT NOT NULL COMMENT '价格类型ID',
  room_type_id INT NOT NULL COMMENT '房型ID',
  base_price DECIMAL(10,2) NOT NULL COMMENT '基准价格',
  price DECIMAL(10,2) NOT NULL COMMENT '计算后的价格',
  date DATE NOT NULL COMMENT '日期',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_hotel_rate_room_date (hotel_id, rate_type_id, room_type_id, date),
  FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE,
  FOREIGN KEY (rate_type_id) REFERENCES rate_types(id),
  FOREIGN KEY (room_type_id) REFERENCES room_types(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基础价格表';

-- 库存表
CREATE TABLE IF NOT EXISTS inventory (
  id INT PRIMARY KEY AUTO_INCREMENT,
  hotel_id INT NOT NULL COMMENT '所属酒店ID',
  rate_plan_id INT NOT NULL COMMENT '价格计划ID',
  room_type_id INT NOT NULL COMMENT '房型ID',
  date DATE NOT NULL COMMENT '日期',
  available_rooms INT NOT NULL DEFAULT 0 COMMENT '可用房间数',
  allocated_rooms INT NOT NULL DEFAULT 0 COMMENT '已分配房间数',
  status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_hotel_rate_room_date (hotel_id, rate_plan_id, room_type_id, date),
  FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE,
  FOREIGN KEY (rate_plan_id) REFERENCES rate_plans(id) ON DELETE CASCADE,
  FOREIGN KEY (room_type_id) REFERENCES room_types(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 价格计划包价关联表
CREATE TABLE IF NOT EXISTS rate_plan_packages (
  id INT PRIMARY KEY AUTO_INCREMENT,
  rate_plan_id INT NOT NULL COMMENT '价格计划ID',
  package_id INT NOT NULL COMMENT '包价ID',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_rate_package (rate_plan_id, package_id),
  FOREIGN KEY (rate_plan_id) REFERENCES rate_plans(id) ON DELETE CASCADE,
  FOREIGN KEY (package_id) REFERENCES packages(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格计划包价关联表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_logs (
  id INT PRIMARY KEY AUTO_INCREMENT,
  operator VARCHAR(50) NOT NULL COMMENT '操作人',
  time DATETIME NOT NULL COMMENT '操作时间',
  type VARCHAR(50) NOT NULL COMMENT '操作类型（如：房型差价，人数差价等）',
  action VARCHAR(50) NOT NULL COMMENT '动作（如：新增，修改，删除等）',
  details TEXT NOT NULL COMMENT '详细内容',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 插入默认数据
-- 插入默认市场码
INSERT IGNORE INTO market_codes (code, name, description) VALUES 
('DOMESTIC', '国内市场', '国内市场'),
('INTERNATIONAL', '国际市场', '国际市场');

-- 插入默认渠道码
INSERT IGNORE INTO channel_codes (code, name, description) VALUES 
('OTA', 'OTA渠道', '在线旅行社渠道'),
('DIRECT', '直客渠道', '直接客户渠道'),
('GROUP', '团客渠道', '团队客户渠道');

-- 插入默认来源码
INSERT IGNORE INTO source_codes (code, name, description) VALUES 
('WEBSITE', '官网', '酒店官网'),
('APP', 'APP', '酒店APP'),
('CALL_CENTER', '呼叫中心', '酒店呼叫中心'),
('THIRD_PARTY', '第三方', '第三方平台');

-- 插入默认税率
INSERT IGNORE INTO tax_settings (tax_type, tax_name, tax_rate, description) VALUES 
('tax', '增值税', 6.00, '增值税'),
('fee', '服务费', 10.00, '服务费');

-- 插入默认包价
INSERT IGNORE INTO packages (code, name, description) VALUES 
('BB', '含早', '包含早餐'),
('HB', '半餐', '包含早餐和晚餐'),
('FB', '全餐', '包含早餐、午餐和晚餐');
