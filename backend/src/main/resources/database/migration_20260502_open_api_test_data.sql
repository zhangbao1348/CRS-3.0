-- ============================================================
-- 实时查询接口测试数据准备脚本
-- 执行方式: mysql -u root -p12345678 CRS < migration_20260502_open_api_test_data.sql
-- ============================================================

USE CRS;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 1. 为已连接渠道设置 access_key 和 access_secret
-- ============================================================
UPDATE tenant_channels SET access_key = 'crs_ctrip_key_001', access_secret = 'crs_ctrip_secret_001' WHERE tenant_id = 1 AND channel_code = 'CTRIP';
UPDATE tenant_channels SET access_key = 'crs_fliggy_key_001', access_secret = 'crs_fliggy_secret_001' WHERE tenant_id = 1 AND channel_code = 'FLIGGY';
UPDATE tenant_channels SET access_key = 'crs_redpower_key_001', access_secret = 'crs_redpower_secret_001' WHERE tenant_id = 1 AND channel_code = 'RED_POWER';
UPDATE tenant_channels SET access_key = 'crs_meituan_key_001', access_secret = 'crs_meituan_secret_001' WHERE tenant_id = 1 AND channel_code = 'MEITUAN';

-- ============================================================
-- 2. 插入渠道-酒店映射
-- ============================================================
INSERT IGNORE INTO channel_hotel_mappings (channel_id, channel_name, hotel_id, hotel_name, hotel_code, channel_hotel_code, status)
SELECT tc.id, tc.channel_name, h.id, h.chinese_name, h.hotel_code, CONCAT('CTRIP_', h.hotel_code), 'active'
FROM tenant_channels tc, hotels h
WHERE tc.tenant_id = 1 AND tc.channel_code = 'CTRIP' AND h.tenant_id = 1 AND h.status = 'active';

INSERT IGNORE INTO channel_hotel_mappings (channel_id, channel_name, hotel_id, hotel_name, hotel_code, channel_hotel_code, status)
SELECT tc.id, tc.channel_name, h.id, h.chinese_name, h.hotel_code, CONCAT('FLIGGY_', h.hotel_code), 'active'
FROM tenant_channels tc, hotels h
WHERE tc.tenant_id = 1 AND tc.channel_code = 'FLIGGY' AND h.tenant_id = 1 AND h.status = 'active' AND h.id <= 5;

-- ============================================================
-- 3. 插入库存数据
--    hotel_id=1(JJSH001), channel_id=1(携程/tenant_channels), 
--    rate_plan_id=9(BAR)/10(BAR_B1), room_type_id=13(ST1)/14(ST2)/15(ST3)
--    日期范围：2026-05-03 ~ 2026-05-10
-- ============================================================

-- ST1(room_type_id=13) + BAR(rate_plan_id=9): 每天5间可用
INSERT IGNORE INTO inventory (hotel_id, room_type_id, rate_plan_id, channel_id, date, allocated_rooms, available_rooms, status, created_at, updated_at)
VALUES
(1, 13, 9, 1, '2026-05-03', 10, 5, 'active', NOW(), NOW()),
(1, 13, 9, 1, '2026-05-04', 10, 5, 'active', NOW(), NOW()),
(1, 13, 9, 1, '2026-05-05', 10, 5, 'active', NOW(), NOW()),
(1, 13, 9, 1, '2026-05-06', 10, 5, 'active', NOW(), NOW()),
(1, 13, 9, 1, '2026-05-07', 10, 5, 'active', NOW(), NOW()),
(1, 13, 9, 1, '2026-05-08', 10, 5, 'active', NOW(), NOW()),
(1, 13, 9, 1, '2026-05-09', 10, 5, 'active', NOW(), NOW()),
(1, 13, 9, 1, '2026-05-10', 10, 5, 'active', NOW(), NOW());

-- ST1(room_type_id=13) + BAR_B1(rate_plan_id=10): 每天3间可用
INSERT IGNORE INTO inventory (hotel_id, room_type_id, rate_plan_id, channel_id, date, allocated_rooms, available_rooms, status, created_at, updated_at)
VALUES
(1, 13, 10, 1, '2026-05-03', 8, 3, 'active', NOW(), NOW()),
(1, 13, 10, 1, '2026-05-04', 8, 3, 'active', NOW(), NOW()),
(1, 13, 10, 1, '2026-05-05', 8, 3, 'active', NOW(), NOW()),
(1, 13, 10, 1, '2026-05-06', 8, 3, 'active', NOW(), NOW()),
(1, 13, 10, 1, '2026-05-07', 8, 3, 'active', NOW(), NOW()),
(1, 13, 10, 1, '2026-05-08', 8, 3, 'active', NOW(), NOW()),
(1, 13, 10, 1, '2026-05-09', 8, 3, 'active', NOW(), NOW()),
(1, 13, 10, 1, '2026-05-10', 8, 3, 'active', NOW(), NOW());

-- ST2(room_type_id=14) + BAR(rate_plan_id=9): 每天4间可用
INSERT IGNORE INTO inventory (hotel_id, room_type_id, rate_plan_id, channel_id, date, allocated_rooms, available_rooms, status, created_at, updated_at)
VALUES
(1, 14, 9, 1, '2026-05-03', 8, 4, 'active', NOW(), NOW()),
(1, 14, 9, 1, '2026-05-04', 8, 4, 'active', NOW(), NOW()),
(1, 14, 9, 1, '2026-05-05', 8, 4, 'active', NOW(), NOW()),
(1, 14, 9, 1, '2026-05-06', 8, 4, 'active', NOW(), NOW()),
(1, 14, 9, 1, '2026-05-07', 8, 4, 'active', NOW(), NOW()),
(1, 14, 9, 1, '2026-05-08', 8, 4, 'active', NOW(), NOW()),
(1, 14, 9, 1, '2026-05-09', 8, 4, 'active', NOW(), NOW()),
(1, 14, 9, 1, '2026-05-10', 8, 4, 'active', NOW(), NOW());

-- ST2(room_type_id=14) + BAR_B1(rate_plan_id=10): 每天2间可用
INSERT IGNORE INTO inventory (hotel_id, room_type_id, rate_plan_id, channel_id, date, allocated_rooms, available_rooms, status, created_at, updated_at)
VALUES
(1, 14, 10, 1, '2026-05-03', 5, 2, 'active', NOW(), NOW()),
(1, 14, 10, 1, '2026-05-04', 5, 2, 'active', NOW(), NOW()),
(1, 14, 10, 1, '2026-05-05', 5, 2, 'active', NOW(), NOW()),
(1, 14, 10, 1, '2026-05-06', 5, 2, 'active', NOW(), NOW()),
(1, 14, 10, 1, '2026-05-07', 5, 2, 'active', NOW(), NOW()),
(1, 14, 10, 1, '2026-05-08', 5, 2, 'active', NOW(), NOW()),
(1, 14, 10, 1, '2026-05-09', 5, 2, 'active', NOW(), NOW()),
(1, 14, 10, 1, '2026-05-10', 5, 2, 'active', NOW(), NOW());

-- ============================================================
-- 4. 库存不足场景测试数据（ST3 + BAR: 5月7日库存为0）
-- ============================================================
INSERT IGNORE INTO inventory (hotel_id, room_type_id, rate_plan_id, channel_id, date, allocated_rooms, available_rooms, status, created_at, updated_at)
VALUES
(1, 15, 9, 1, '2026-05-03', 10, 5, 'active', NOW(), NOW()),
(1, 15, 9, 1, '2026-05-04', 10, 5, 'active', NOW(), NOW()),
(1, 15, 9, 1, '2026-05-05', 10, 5, 'active', NOW(), NOW()),
(1, 15, 9, 1, '2026-05-06', 10, 5, 'active', NOW(), NOW()),
(1, 15, 9, 1, '2026-05-07', 10, 0, 'active', NOW(), NOW()),
(1, 15, 9, 1, '2026-05-08', 10, 5, 'active', NOW(), NOW()),
(1, 15, 9, 1, '2026-05-09', 10, 5, 'active', NOW(), NOW()),
(1, 15, 9, 1, '2026-05-10', 10, 5, 'active', NOW(), NOW());

-- ============================================================
-- 5. 房态关闭场景测试数据（ST1房型 5月8日关房）
-- ============================================================
INSERT IGNORE INTO room_status (tenant_id, hotel_code, dimension_type, dimension_code, status_date, is_open, created_at, updated_at)
VALUES (1, 'JJSH001', 'room_type', 'ST1', '2026-05-08', 0, NOW(), NOW());

-- ============================================================
-- 6. 预订控制测试数据（5月3日需提前3天预订，最少住2晚）
-- ============================================================
INSERT IGNORE INTO booking_controls (tenant_id, hotel_code, dimension_type, dimension_code, control_date, cancellation_rule, advance_booking_days, min_stay, max_stay, created_at, updated_at)
VALUES (1, 'JJSH001', 'hotel', '', '2026-05-03', 'FREE_CANCEL', 3, 2, 30, NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;
