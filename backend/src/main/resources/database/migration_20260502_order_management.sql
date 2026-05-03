-- ============================================================
-- CRS 订单管理模块迁移脚本
-- 日期：2026-05-02
-- 说明：扩展reservation表字段 + 新增每日价格/入住人/支付/促销子表
-- ============================================================

SET @dbname = DATABASE();

-- ============================================================
-- 1. 扩展 reservation 表字段
-- ============================================================

-- helper: 添加列（如果不存在）
-- hotel_code
SET @colname = 'hotel_code'; SET @tablename = 'reservation';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN hotel_code VARCHAR(50) DEFAULT NULL COMMENT ''酒店编码'' AFTER hotel_id'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- hotel_name
SET @colname = 'hotel_name';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN hotel_name VARCHAR(200) DEFAULT NULL COMMENT ''酒店名称（快照）'' AFTER hotel_code'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- room_type_code
SET @colname = 'room_type_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN room_type_code VARCHAR(50) DEFAULT NULL COMMENT ''房型编码'' AFTER room_type_id'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- room_type_name
SET @colname = 'room_type_name';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN room_type_name VARCHAR(200) DEFAULT NULL COMMENT ''房型名称（快照）'' AFTER room_type_code'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- rate_plan_code
SET @colname = 'rate_plan_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN rate_plan_code VARCHAR(50) DEFAULT NULL COMMENT ''价格计划编码'' AFTER rate_plan_id'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- rate_plan_name
SET @colname = 'rate_plan_name';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN rate_plan_name VARCHAR(200) DEFAULT NULL COMMENT ''价格计划名称（快照）'' AFTER rate_plan_code'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- channel_code
SET @colname = 'channel_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN channel_code VARCHAR(50) DEFAULT NULL COMMENT ''渠道编码'' AFTER channel_id'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- channel_name
SET @colname = 'channel_name';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN channel_name VARCHAR(100) DEFAULT NULL COMMENT ''渠道名称（快照）'' AFTER channel_code'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- market_code
SET @colname = 'market_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN market_code VARCHAR(50) DEFAULT NULL COMMENT ''市场编码'' AFTER market_code_id'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- source_code
SET @colname = 'source_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN source_code VARCHAR(50) DEFAULT NULL COMMENT ''来源编码'' AFTER source_code_id'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- nights
SET @colname = 'nights';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN nights INT DEFAULT NULL COMMENT ''入住晚数'' AFTER check_out_date'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- contact_name
SET @colname = 'contact_name';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN contact_name VARCHAR(100) DEFAULT NULL COMMENT ''联系人姓名'' AFTER child_count'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- contact_phone
SET @colname = 'contact_phone';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN contact_phone VARCHAR(50) DEFAULT NULL COMMENT ''联系人手机号'' AFTER contact_name'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- contact_email
SET @colname = 'contact_email';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN contact_email VARCHAR(100) DEFAULT NULL COMMENT ''联系人邮箱'' AFTER contact_phone'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- member_no
SET @colname = 'member_no';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN member_no VARCHAR(50) DEFAULT NULL COMMENT ''会员编号'' AFTER contact_email'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- member_level
SET @colname = 'member_level';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN member_level VARCHAR(30) DEFAULT NULL COMMENT ''会员等级'' AFTER member_no'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- original_price
SET @colname = 'original_price';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN original_price DECIMAL(12,2) DEFAULT NULL COMMENT ''订单原价'' AFTER member_level'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- guarantee_info
SET @colname = 'guarantee_info';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN guarantee_info TEXT DEFAULT NULL COMMENT ''担保信息JSON'' AFTER guarantee_type'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- cancellation_policy_code
SET @colname = 'cancellation_policy_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN cancellation_policy_code VARCHAR(50) DEFAULT NULL COMMENT ''取消政策编码'' AFTER guarantee_info'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- cancellation_policy_desc
SET @colname = 'cancellation_policy_desc';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN cancellation_policy_desc VARCHAR(500) DEFAULT NULL COMMENT ''取消政策描述'' AFTER cancellation_policy_code'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- guarantee_policy_code
SET @colname = 'guarantee_policy_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN guarantee_policy_code VARCHAR(50) DEFAULT NULL COMMENT ''担保政策编码'' AFTER cancellation_policy_desc'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- guarantee_policy_desc
SET @colname = 'guarantee_policy_desc';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN guarantee_policy_desc VARCHAR(500) DEFAULT NULL COMMENT ''担保政策描述'' AFTER guarantee_policy_code'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- guest_remark
SET @colname = 'guest_remark';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN guest_remark VARCHAR(500) DEFAULT NULL COMMENT ''客人备注'' AFTER notes'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- hotel_remark
SET @colname = 'hotel_remark';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN hotel_remark VARCHAR(500) DEFAULT NULL COMMENT ''门店备注'' AFTER guest_remark'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- is_manual
SET @colname = 'is_manual';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN is_manual TINYINT(1) DEFAULT 0 COMMENT ''是否人工干预'' AFTER hotel_remark'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- manual_reason
SET @colname = 'manual_reason';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN manual_reason VARCHAR(500) DEFAULT NULL COMMENT ''人工干预原因'' AFTER is_manual'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- commission_rate
SET @colname = 'commission_rate';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN commission_rate DECIMAL(5,4) DEFAULT NULL COMMENT ''佣金比例'' AFTER manual_reason'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- commission_amount
SET @colname = 'commission_amount';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN commission_amount DECIMAL(12,2) DEFAULT NULL COMMENT ''佣金金额'' AFTER commission_rate'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- order_source
SET @colname = 'order_source';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN order_source VARCHAR(30) DEFAULT ''channel'' COMMENT ''订单来源'' AFTER commission_amount'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- cancelled_by
SET @colname = 'cancelled_by';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN cancelled_by VARCHAR(50) DEFAULT NULL COMMENT ''取消操作人'' AFTER modified_by'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- cancelled_at
SET @colname = 'cancelled_at';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN cancelled_at TIMESTAMP NULL DEFAULT NULL COMMENT ''取消时间'' AFTER cancelled_by'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- cancel_reason
SET @colname = 'cancel_reason';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN cancel_reason VARCHAR(500) DEFAULT NULL COMMENT ''取消原因'' AFTER cancelled_at'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- completed_at
SET @colname = 'completed_at';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation ADD COLUMN completed_at TIMESTAMP NULL DEFAULT NULL COMMENT ''完成时间'' AFTER cancel_reason'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 修改 total_price 类型从 double 到 decimal
ALTER TABLE reservation MODIFY COLUMN total_price DECIMAL(12,2) NOT NULL DEFAULT 0.00;

-- ============================================================
-- 添加索引
-- ============================================================
SET @tablename = 'reservation';

SET @indexname = 'idx_reservation_tenant_hotel';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND INDEX_NAME = @indexname) > 0,
  'SELECT 1',
  'CREATE INDEX idx_reservation_tenant_hotel ON reservation (tenant_id, hotel_id)'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @indexname = 'idx_reservation_tenant_channel';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND INDEX_NAME = @indexname) > 0,
  'SELECT 1',
  'CREATE INDEX idx_reservation_tenant_channel ON reservation (tenant_id, channel_id)'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @indexname = 'idx_reservation_check_in_date';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND INDEX_NAME = @indexname) > 0,
  'SELECT 1',
  'CREATE INDEX idx_reservation_check_in_date ON reservation (tenant_id, check_in_date)'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @indexname = 'idx_reservation_status';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND INDEX_NAME = @indexname) > 0,
  'SELECT 1',
  'CREATE INDEX idx_reservation_status ON reservation (tenant_id, reservation_status)'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @indexname = 'idx_reservation_channel_order';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND INDEX_NAME = @indexname) > 0,
  'SELECT 1',
  'CREATE INDEX idx_reservation_channel_order ON reservation (channel_id, channel_order_number)'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @indexname = 'idx_reservation_created_at';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND INDEX_NAME = @indexname) > 0,
  'SELECT 1',
  'CREATE INDEX idx_reservation_created_at ON reservation (tenant_id, created_at)'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 2. 扩展 reservation_history 表字段
-- ============================================================
SET @tablename = 'reservation_history';

SET @colname = 'action';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation_history ADD COLUMN action VARCHAR(50) DEFAULT NULL COMMENT ''操作类型'' AFTER reservation_id'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @colname = 'operator_type';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation_history ADD COLUMN operator_type VARCHAR(20) DEFAULT ''system'' COMMENT ''操作人类型'' AFTER operator'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @colname = 'detail';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE reservation_history ADD COLUMN detail TEXT DEFAULT NULL COMMENT ''操作详情JSON'' AFTER operator_type'
));
PREPARE stmt FROM @preparedStatement; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 3. 新增子表
-- ============================================================

CREATE TABLE IF NOT EXISTS reservation_daily_price (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL COMMENT '关联订单ID',
    price_date DATE NOT NULL COMMENT '价格日期',
    original_price DECIMAL(12,2) DEFAULT NULL COMMENT '原价（折扣前）',
    actual_price DECIMAL(12,2) NOT NULL COMMENT '实际价格（折扣后含税）',
    tax_amount DECIMAL(12,2) DEFAULT NULL COMMENT '税费金额',
    service_charge DECIMAL(12,2) DEFAULT NULL COMMENT '服务费',
    breakfast_included TINYINT(1) DEFAULT 0 COMMENT '是否含早餐',
    breakfast_count INT DEFAULT 0 COMMENT '早餐份数',
    packages_json TEXT DEFAULT NULL COMMENT '包价信息JSON',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_reservation_date (reservation_id, price_date),
    INDEX idx_reservation_id (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单每日价格明细表';

CREATE TABLE IF NOT EXISTS reservation_guest (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL COMMENT '关联订单ID',
    guest_type VARCHAR(20) DEFAULT 'guest' COMMENT '客人类型：contact/guest',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    phone VARCHAR(50) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    id_type VARCHAR(30) DEFAULT NULL COMMENT '证件类型',
    id_number VARCHAR(100) DEFAULT NULL COMMENT '证件号码',
    member_no VARCHAR(50) DEFAULT NULL COMMENT '会员编号',
    member_level VARCHAR(30) DEFAULT NULL COMMENT '会员等级',
    room_number VARCHAR(20) DEFAULT NULL COMMENT '房间号',
    pms_account VARCHAR(100) DEFAULT NULL COMMENT 'PMS账号',
    pms_status VARCHAR(30) DEFAULT NULL COMMENT 'PMS状态',
    sort_order INT DEFAULT 0 COMMENT '排序序号',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_reservation_id (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单入住人信息表';

CREATE TABLE IF NOT EXISTS reservation_payment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL COMMENT '关联订单ID',
    payment_method VARCHAR(30) NOT NULL COMMENT '支付方式',
    payment_type VARCHAR(20) DEFAULT 'payment' COMMENT '支付类型：payment/refund',
    payment_amount DECIMAL(12,2) NOT NULL COMMENT '支付金额',
    transaction_id VARCHAR(100) DEFAULT NULL COMMENT '第三方支付流水号',
    credit_card_last4 VARCHAR(4) DEFAULT NULL COMMENT '信用卡尾号4位',
    credit_card_expiry VARCHAR(10) DEFAULT NULL COMMENT '信用卡有效期',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '支付状态：pending/success/failed',
    paid_at TIMESTAMP NULL DEFAULT NULL COMMENT '支付成功时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_reservation_id (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单支付记录表';

CREATE TABLE IF NOT EXISTS reservation_promotion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL COMMENT '关联订单ID',
    promotion_name VARCHAR(200) NOT NULL COMMENT '优惠名称',
    discount_type VARCHAR(30) NOT NULL COMMENT '折扣类型',
    discount_value DECIMAL(12,2) DEFAULT NULL COMMENT '折扣值',
    discount_amount DECIMAL(12,2) NOT NULL COMMENT '实际优惠金额',
    promotion_code VARCHAR(50) DEFAULT NULL COMMENT '优惠券码',
    provider VARCHAR(30) DEFAULT NULL COMMENT '优惠承担方',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_reservation_id (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单促销优惠表';

-- ============================================================
-- 4. 迁移已有数据
-- ============================================================

UPDATE reservation SET contact_name = guest_name WHERE contact_name IS NULL AND guest_name IS NOT NULL;
UPDATE reservation SET contact_phone = guest_phone WHERE contact_phone IS NULL AND guest_phone IS NOT NULL;
UPDATE reservation SET contact_email = guest_email WHERE contact_email IS NULL AND guest_email IS NOT NULL;

UPDATE reservation SET nights = DATEDIFF(check_out_date, check_in_date) WHERE nights IS NULL;

UPDATE reservation r JOIN hotels h ON r.hotel_id = h.id SET r.tenant_id = h.tenant_id WHERE r.tenant_id IS NULL;

UPDATE reservation r JOIN hotels h ON r.hotel_id = h.id SET r.hotel_code = h.hotel_code, r.hotel_name = h.chinese_name WHERE r.hotel_code IS NULL;
UPDATE reservation r JOIN hotel_room_types rt ON r.room_type_id = rt.id SET r.room_type_code = rt.room_type_code, r.room_type_name = rt.room_type_name WHERE r.room_type_code IS NULL;
UPDATE reservation r JOIN rate_plans rp ON r.rate_plan_id = rp.id SET r.rate_plan_code = rp.rate_code, r.rate_plan_name = rp.rate_name WHERE r.rate_plan_code IS NULL;
