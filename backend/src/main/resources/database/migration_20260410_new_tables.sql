-- ============================================================
-- CRS 新增表迁移脚本
-- 日期：2026-04-10
-- 说明：创建担保政策、取消政策、渠道映射、预订历史、接口日志、档案等表
-- ============================================================

-- 1. 担保政策表
CREATE TABLE IF NOT EXISTS guarantee_policies (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '政策名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '政策代码',
    type VARCHAR(50) NOT NULL COMMENT '担保类型：无担保/信用卡/预付/公司/第三方/特殊',
    guarantee_sub_type VARCHAR(50) COMMENT '担保子类型：一律担保/超时担保（仅信用卡）',
    guarantee_amount VARCHAR(50) COMMENT '担保金额：首晚/全额（仅信用卡）',
    latest_arrival_time VARCHAR(10) COMMENT '最晚到店时间（仅超时担保）',
    description TEXT COMMENT '描述',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    group_id INT COMMENT '集团ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_guarantee_policies_group_id (group_id),
    INDEX idx_guarantee_policies_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='担保政策表';

-- 2. 取消政策表
CREATE TABLE IF NOT EXISTS cancellation_policies (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '政策名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '政策代码',
    type VARCHAR(50) NOT NULL COMMENT '取消类型：免费取消/限时扣费/不可取消',
    cancellation_days INT COMMENT '提前天数（仅限时扣费）',
    cancellation_time VARCHAR(10) COMMENT '截止时间HH:mm（仅限时扣费）',
    cancellation_fee_type VARCHAR(50) COMMENT '扣费类型：首晚/全额房费（仅限时扣费）',
    description TEXT COMMENT '描述',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    group_id INT COMMENT '集团ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_cancellation_policies_group_id (group_id),
    INDEX idx_cancellation_policies_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='取消政策表';

-- 3. 渠道酒店映射表
CREATE TABLE IF NOT EXISTS channel_hotel_mappings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    channel_id INT NOT NULL COMMENT '渠道ID',
    channel_name VARCHAR(50) COMMENT '渠道名称',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    hotel_name VARCHAR(100) COMMENT '酒店名称',
    hotel_code VARCHAR(50) COMMENT '酒店CODE',
    channel_hotel_code VARCHAR(100) NOT NULL COMMENT '渠道酒店CODE',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active/inactive',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_channel_hotel_mappings_channel_id (channel_id),
    INDEX idx_channel_hotel_mappings_hotel_id (hotel_id),
    UNIQUE KEY uk_channel_hotel (channel_id, hotel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道酒店映射表';

-- 4. 渠道房型映射表
CREATE TABLE IF NOT EXISTS channel_room_type_mappings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    channel_id INT NOT NULL COMMENT '渠道ID',
    channel_name VARCHAR(50) COMMENT '渠道名称',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    hotel_name VARCHAR(100) COMMENT '酒店名称',
    room_type_id INT NOT NULL COMMENT '房型ID',
    room_type_name VARCHAR(100) COMMENT '房型名称',
    room_type_code VARCHAR(50) COMMENT '房型CODE',
    channel_room_type_code VARCHAR(100) NOT NULL COMMENT '渠道房型CODE',
    channel_room_type_name VARCHAR(100) COMMENT '渠道房型名称',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active/inactive',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_channel_room_type_mappings_channel_id (channel_id),
    INDEX idx_channel_room_type_mappings_hotel_id (hotel_id),
    UNIQUE KEY uk_channel_hotel_room (channel_id, hotel_id, room_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道房型映射表';

-- 5. 渠道房价映射表
CREATE TABLE IF NOT EXISTS channel_rate_code_mappings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    channel_id INT NOT NULL COMMENT '渠道ID',
    channel_name VARCHAR(50) COMMENT '渠道名称',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    hotel_name VARCHAR(100) COMMENT '酒店名称',
    rate_code_id INT NOT NULL COMMENT '房价码ID',
    rate_code_name VARCHAR(100) COMMENT '房价码名称',
    rate_code VARCHAR(50) COMMENT '房价码',
    channel_rate_code VARCHAR(100) NOT NULL COMMENT '渠道房价码',
    channel_rate_name VARCHAR(100) COMMENT '渠道房价名称',
    markup DECIMAL(5,2) DEFAULT 0 COMMENT '加价率%',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active/inactive',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_channel_rate_code_mappings_channel_id (channel_id),
    INDEX idx_channel_rate_code_mappings_hotel_id (hotel_id),
    UNIQUE KEY uk_channel_hotel_rate (channel_id, hotel_id, rate_code_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道房价映射表';

-- 6. 预订操作历史表
CREATE TABLE IF NOT EXISTS reservation_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL COMMENT '预订ID',
    content VARCHAR(200) NOT NULL COMMENT '操作内容',
    result VARCHAR(20) NOT NULL COMMENT '结果：成功/失败',
    operator VARCHAR(50) NOT NULL COMMENT '操作人',
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    log_id INT COMMENT '接口日志ID',
    INDEX idx_reservation_history_reservation_id (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预订操作历史表';

-- 7. 接口日志表
CREATE TABLE IF NOT EXISTS api_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT COMMENT '关联预订ID',
    request_body TEXT COMMENT '入参JSON',
    response_body TEXT COMMENT '出参JSON',
    error_message TEXT COMMENT '失败原因',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_api_logs_reservation_id (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='接口日志表';

-- 8. 档案表
CREATE TABLE IF NOT EXISTS archives (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '档案名称',
    type VARCHAR(50) NOT NULL COMMENT '档案类型：公司/个人',
    company_name VARCHAR(200) COMMENT '公司名称',
    company_tax_number VARCHAR(50) COMMENT '公司税号',
    member_number VARCHAR(50) COMMENT '会员号',
    member_level VARCHAR(50) COMMENT '会员等级',
    contact_name VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    contact_email VARCHAR(100) COMMENT '联系邮箱',
    address TEXT COMMENT '地址',
    description TEXT COMMENT '描述',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    group_id INT COMMENT '集团ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_archives_group_id (group_id),
    INDEX idx_archives_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='档案表';
