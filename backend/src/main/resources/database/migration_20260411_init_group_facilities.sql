-- 初始化集团设施数据
-- 日期：2026-04-11

USE CRS;

-- 插入集团设施数据（与酒店设施使用的编码匹配）
INSERT INTO group_facilities (facility_type, facility_name, facility_code, available, description) VALUES
-- 交通服务
('交通服务', '收费停车场', 'paidParking', TRUE, '酒店内收费停车场'),
('交通服务', '免费停车场', 'freeParking', TRUE, '酒店内免费停车场'),
('交通服务', '免费接送机', 'freeShuttle', TRUE, '免费机场接送服务'),
('交通服务', '收费接送机', 'paidShuttle', TRUE, '收费机场接送服务'),
('交通服务', '机场接机', 'airportPickup', TRUE, '机场接机服务'),
('交通服务', '火车站接送', 'trainStationPickup', TRUE, '火车站接送服务'),
('交通服务', '代客泊车', 'valetParking', TRUE, '代客泊车服务'),
('交通服务', '电动汽车充电桩', 'electricVehicleCharging', TRUE, '电动汽车充电桩'),
-- 餐饮服务
('餐饮服务', '自助早餐厅', 'buffetRestaurant', TRUE, '自助早餐餐厅'),
('餐饮服务', '咖啡厅', 'cafe', TRUE, '咖啡饮品服务'),
('餐饮服务', '中餐厅', 'chineseRestaurant', TRUE, '中餐服务'),
('餐饮服务', '西餐厅', 'westernRestaurant', TRUE, '西餐服务'),
('餐饮服务', '日餐厅', 'japaneseRestaurant', TRUE, '日餐服务'),
('餐饮服务', '酒吧/酒廊', 'loungeBar', TRUE, '酒吧和酒廊服务'),
('餐饮服务', '客房送餐', 'roomService', TRUE, '客房送餐服务'),
('餐饮服务', '茶室', 'teaHouse', TRUE, '茶室服务'),
-- 清洁服务
('清洁服务', '外送洗衣服务', 'laundryService', TRUE, '外送洗衣服务'),
('清洁服务', '干衣机', 'dryer', TRUE, '干衣机设备'),
('清洁服务', '熨斗/挂烫机', 'iron', TRUE, '熨斗和挂烫机'),
('清洁服务', '洗衣房', 'laundryRoom', TRUE, '自助洗衣房'),
('清洁服务', '熨衣服务', 'valetService', TRUE, '熨衣服务'),
('清洁服务', '洗衣服务', 'washingService', TRUE, '洗衣服务'),
('清洁服务', '每日清洁', 'dailyCleaning', TRUE, '每日客房清洁'),
('清洁服务', '夜床服务', 'turnDownService', TRUE, '夜床整理服务'),
-- 休闲娱乐
('休闲娱乐', '室内泳池', 'indoorPool', TRUE, '室内游泳池'),
('休闲娱乐', '室外泳池', 'outdoorPool', TRUE, '室外游泳池'),
('休闲娱乐', '健身房', 'fitnessCenter', TRUE, '健身中心'),
('休闲娱乐', 'SPA水疗', 'spa', TRUE, 'SPA水疗中心'),
('休闲娱乐', '桑拿', 'sauna', TRUE, '桑拿房'),
('休闲娱乐', '蒸汽房', 'steamRoom', TRUE, '蒸汽房'),
('休闲娱乐', '按摩', 'massage', TRUE, '按摩服务'),
('休闲娱乐', '美容美发', 'beautySalon', TRUE, '美容美发服务'),
-- 商务服务
('商务服务', '商务中心', 'businessCenter', TRUE, '商务中心服务'),
('商务服务', '会议室', 'meetingRooms', TRUE, '会议室服务');

-- 验证数据
SELECT 
    facility_type AS '设施类型',
    COUNT(*) AS '设施数量'
FROM group_facilities
GROUP BY facility_type;

SELECT '集团设施数据初始化完成！共插入 ' AS message, COUNT(*) AS '设施数量' FROM group_facilities;
