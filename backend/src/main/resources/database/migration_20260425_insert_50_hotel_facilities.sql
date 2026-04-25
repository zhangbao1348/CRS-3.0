-- 插入50条酒店设施信息
-- 日期：2026-04-25

USE CRS;

-- 清空现有酒店设施数据（可选）
-- DELETE FROM hotel_facilities;

-- 为租户1的酒店分配设施（25条）
INSERT INTO hotel_facilities (hotel_id, hotel_code, facility_type, facility_name, facility_code, available, created_at, updated_at) VALUES
-- 上海锦江饭店设施（8条）
(1, 'JJSH001', '交通服务', '免费停车场', 'freeParking', TRUE, NOW(), NOW()),
(1, 'JJSH001', '交通服务', '代客泊车', 'valetParking', TRUE, NOW(), NOW()),
(1, 'JJSH001', '餐饮服务', '中餐厅', 'chineseRestaurant', TRUE, NOW(), NOW()),
(1, 'JJSH001', '餐饮服务', '西餐厅', 'westernRestaurant', TRUE, NOW(), NOW()),
(1, 'JJSH001', '清洁服务', '洗衣服务', 'washingService', TRUE, NOW(), NOW()),
(1, 'JJSH001', '休闲娱乐', '健身房', 'fitnessCenter', TRUE, NOW(), NOW()),
(1, 'JJSH001', '商务服务', '商务中心', 'businessCenter', TRUE, NOW(), NOW()),
(1, 'JJSH001', '前台服务', '24小时前台', 'twentyFourHourFrontDesk', TRUE, NOW(), NOW()),

-- 北京长城饭店设施（7条）
(2, 'JJBJ001', '交通服务', '免费停车场', 'freeParking', TRUE, NOW(), NOW()),
(2, 'JJBJ001', '交通服务', '电动汽车充电桩', 'electricVehicleCharging', TRUE, NOW(), NOW()),
(2, 'JJBJ001', '餐饮服务', '咖啡厅', 'cafe', TRUE, NOW(), NOW()),
(2, 'JJBJ001', '清洁服务', '洗衣房', 'laundryRoom', TRUE, NOW(), NOW()),
(2, 'JJBJ001', '休闲娱乐', '室内泳池', 'indoorPool', TRUE, NOW(), NOW()),
(2, 'JJBJ001', '商务服务', '会议室', 'meetingRooms', TRUE, NOW(), NOW()),
(2, 'JJBJ001', '前台服务', '快速入住', 'expressCheckIn', TRUE, NOW(), NOW()),

-- 广州白云宾馆设施（5条）
(3, 'JJGZ001', '交通服务', '免费停车场', 'freeParking', TRUE, NOW(), NOW()),
(3, 'JJGZ001', '餐饮服务', '自助早餐厅', 'buffetRestaurant', TRUE, NOW(), NOW()),
(3, 'JJGZ001', '清洁服务', '每日清洁', 'dailyCleaning', TRUE, NOW(), NOW()),
(3, 'JJGZ001', '休闲娱乐', 'SPA水疗', 'spa', TRUE, NOW(), NOW()),
(3, 'JJGZ001', '前台服务', '外币兑换', 'currencyExchange', TRUE, NOW(), NOW()),

-- 深圳东华假日酒店设施（5条）
(4, 'JJSZ001', '交通服务', '免费停车场', 'freeParking', TRUE, NOW(), NOW()),
(4, 'JJSZ001', '餐饮服务', '客房送餐', 'roomService', TRUE, NOW(), NOW()),
(4, 'JJSZ001', '清洁服务', '熨斗/挂烫机', 'iron', TRUE, NOW(), NOW()),
(4, 'JJSZ001', '休闲娱乐', '健身中心', 'fitnessCenter', TRUE, NOW(), NOW()),
(4, 'JJSZ001', '前台服务', '保险箱', 'safeDepositBox', TRUE, NOW(), NOW()),

-- 为租户2的酒店分配设施（25条）
-- 上海和平饭店设施（8条）
(11, 'SLSH001', '交通服务', '代客泊车', 'valetParking', TRUE, NOW(), NOW()),
(11, 'SLSH001', '交通服务', '免费接送机', 'freeShuttle', TRUE, NOW(), NOW()),
(11, 'SLSH001', '餐饮服务', '中餐厅', 'chineseRestaurant', TRUE, NOW(), NOW()),
(11, 'SLSH001', '餐饮服务', '酒吧/酒廊', 'loungeBar', TRUE, NOW(), NOW()),
(11, 'SLSH001', '清洁服务', '外送洗衣服务', 'laundryService', TRUE, NOW(), NOW()),
(11, 'SLSH001', '休闲娱乐', '室外泳池', 'outdoorPool', TRUE, NOW(), NOW()),
(11, 'SLSH001', '商务服务', '商务中心', 'businessCenter', TRUE, NOW(), NOW()),
(11, 'SLSH001', '前台服务', '门童', 'doorman', TRUE, NOW(), NOW()),

-- 北京建国饭店设施（7条）
(12, 'SLBJ001', '交通服务', '免费停车场', 'freeParking', TRUE, NOW(), NOW()),
(12, 'SLBJ001', '交通服务', '电动汽车充电桩', 'electricVehicleCharging', TRUE, NOW(), NOW()),
(12, 'SLBJ001', '餐饮服务', '西餐厅', 'westernRestaurant', TRUE, NOW(), NOW()),
(12, 'SLBJ001', '清洁服务', '夜床服务', 'turnDownService', TRUE, NOW(), NOW()),
(12, 'SLBJ001', '休闲娱乐', '健身房', 'fitnessCenter', TRUE, NOW(), NOW()),
(12, 'SLBJ001', '商务服务', '会议室', 'meetingRooms', TRUE, NOW(), NOW()),
(12, 'SLBJ001', '前台服务', '24小时前台', 'twentyFourHourFrontDesk', TRUE, NOW(), NOW()),

-- 广州白天鹅宾馆设施（5条）
(13, 'SLGZ001', '交通服务', '免费停车场', 'freeParking', TRUE, NOW(), NOW()),
(13, 'SLGZ001', '餐饮服务', '日餐厅', 'japaneseRestaurant', TRUE, NOW(), NOW()),
(13, 'SLGZ001', '清洁服务', '洗衣房', 'laundryRoom', TRUE, NOW(), NOW()),
(13, 'SLGZ001', '休闲娱乐', '室内泳池', 'indoorPool', TRUE, NOW(), NOW()),
(13, 'SLGZ001', '前台服务', '叫醒服务', 'wakeUpCall', TRUE, NOW(), NOW()),

-- 深圳威尼斯酒店设施（5条）
(14, 'SLSZ001', '交通服务', '免费停车场', 'freeParking', TRUE, NOW(), NOW()),
(14, 'SLSZ001', '餐饮服务', '自助餐', 'buffetRestaurant', TRUE, NOW(), NOW()),
(14, 'SLSZ001', '清洁服务', '熨衣服务', 'valetService', TRUE, NOW(), NOW()),
(14, 'SLSZ001', '休闲娱乐', '室外泳池', 'outdoorPool', TRUE, NOW(), NOW()),
(14, 'SLSZ001', '前台服务', '旅游咨询', 'touristInformation', TRUE, NOW(), NOW()),

-- 为租户3的酒店分配设施（增加6条，凑够50条）
-- 上海全季酒店设施（3条）
(21, 'HZSH001', '交通服务', '免费停车场', 'freeParking', TRUE, NOW(), NOW()),
(21, 'HZSH001', '餐饮服务', '自助早餐厅', 'buffetRestaurant', TRUE, NOW(), NOW()),
(21, 'HZSH001', '清洁服务', '每日清洁', 'dailyCleaning', TRUE, NOW(), NOW()),

-- 北京汉庭酒店设施（3条）
(22, 'HZBJ001', '交通服务', '免费停车场', 'freeParking', TRUE, NOW(), NOW()),
(22, 'HZBJ001', '清洁服务', '洗衣房', 'laundryRoom', TRUE, NOW(), NOW()),
(22, 'HZBJ001', '前台服务', '快速退房', 'expressCheckOut', TRUE, NOW(), NOW());

-- 验证数据
SELECT 
    h.hotel_code AS '酒店代码',
    h.chinese_name AS '酒店名称',
    COUNT(hf.id) AS '设施数量'
FROM hotels h
LEFT JOIN hotel_facilities hf ON h.id = hf.hotel_id
WHERE hf.id IS NOT NULL
GROUP BY h.id, h.hotel_code, h.chinese_name
ORDER BY h.tenant_id, h.id;

SELECT '酒店设施数据插入完成！共 ' AS message, COUNT(*) AS '设施数量' FROM hotel_facilities;
