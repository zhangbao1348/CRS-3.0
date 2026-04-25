-- 增加集团设施到100个
-- 日期：2026-04-11
-- 当前已有34个，新增66个，总计100个

USE CRS;

-- 交通服务（原有8个，新增10个，共18个）
INSERT INTO group_facilities (facility_type, facility_name, facility_code, available, description) VALUES
('交通服务', '自行车租赁', 'bicycleRental', TRUE, '自行车租赁服务'),
('交通服务', '租车服务', 'carRental', TRUE, '汽车租赁服务'),
('交通服务', '出租车服务', 'taxiService', TRUE, '出租车叫车服务'),
('交通服务', '机场快速通道', 'airportFastTrack', TRUE, '机场快速通道服务'),
('交通服务', '高铁接送', 'highSpeedRailPickup', TRUE, '高铁站接送服务'),
('交通服务', '地铁接送', 'subwayPickup', TRUE, '地铁站接送服务'),
('交通服务', '直升机停机坪', 'helipad', TRUE, '直升机停机坪'),
('交通服务', '行李寄存', 'luggageStorage', TRUE, '行李寄存服务'),
('交通服务', '礼宾车服务', 'limousineService', TRUE, '豪华礼宾车服务'),
('交通服务', '摩托车停放', 'motorcycleParking', TRUE, '摩托车停车位');

-- 餐饮服务（原有8个，新增12个，共20个）
INSERT INTO group_facilities (facility_type, facility_name, facility_code, available, description) VALUES
('餐饮服务', '韩餐厅', 'koreanRestaurant', TRUE, '韩国料理餐厅'),
('餐饮服务', '泰餐厅', 'thaiRestaurant', TRUE, '泰国料理餐厅'),
('餐饮服务', '意大利餐厅', 'italianRestaurant', TRUE, '意大利料理餐厅'),
('餐饮服务', '法式餐厅', 'frenchRestaurant', TRUE, '法国料理餐厅'),
('餐饮服务', '户外烧烤', 'outdoorBBQ', TRUE, '户外烧烤区'),
('餐饮服务', '糕点店', 'bakery', TRUE, '面包糕点店'),
('餐饮服务', '泳池酒吧', 'poolBar', TRUE, '泳池边酒吧'),
('餐饮服务', '雪茄吧', 'cigarBar', TRUE, '雪茄酒吧'),
('餐饮服务', '红酒吧', 'wineBar', TRUE, '红酒吧'),
('餐饮服务', '下午茶', 'afternoonTea', TRUE, '下午茶服务'),
('餐饮服务', '早餐服务', 'breakfastService', TRUE, '早餐服务'),
('餐饮服务', '深夜食堂', 'midnightDining', TRUE, '深夜食堂');

-- 清洁服务（原有8个，新增10个，共18个）
INSERT INTO group_facilities (facility_type, facility_name, facility_code, available, description) VALUES
('清洁服务', '擦鞋服务', 'shoeShine', TRUE, '擦鞋服务'),
('清洁服务', '干洗服务', 'dryCleaning', TRUE, '干洗服务'),
('清洁服务', '缝补服务', 'tailoringService', TRUE, '缝补服务'),
('清洁服务', '鞋油', 'shoePolish', TRUE, '鞋油服务'),
('清洁服务', '房间除臭', 'roomDeodorization', TRUE, '房间除臭服务'),
('清洁服务', '空气净化', 'airPurifier', TRUE, '空气净化器'),
('清洁服务', '加湿器', 'humidifier', TRUE, '加湿器'),
('清洁服务', '扫地机器人', 'robotVacuum', TRUE, '扫地机器人'),
('清洁服务', '房间香薰', 'roomAromatherapy', TRUE, '房间香薰服务'),
('清洁服务', '窗户清洁', 'windowCleaning', TRUE, '窗户清洁服务');

-- 休闲娱乐（原有8个，新增14个，共22个）
INSERT INTO group_facilities (facility_type, facility_name, facility_code, available, description) VALUES
('休闲娱乐', 'KTV', 'karaoke', TRUE, '卡拉OK'),
('休闲娱乐', '棋牌室', 'gameRoom', TRUE, '棋牌室'),
('休闲娱乐', '桌球室', 'billiardRoom', TRUE, '桌球室'),
('休闲娱乐', '保龄球', 'bowling', TRUE, '保龄球'),
('休闲娱乐', '壁球场', 'squashCourt', TRUE, '壁球场'),
('休闲娱乐', '网球场', 'tennisCourt', TRUE, '网球场'),
('休闲娱乐', '高尔夫练习场', 'golfDrivingRange', TRUE, '高尔夫练习场'),
('休闲娱乐', '瑜伽室', 'yogaRoom', TRUE, '瑜伽室'),
('休闲娱乐', '健身房', 'pilatesRoom', TRUE, '普拉提室'),
('休闲娱乐', '儿童乐园', 'kidsPlayground', TRUE, '儿童乐园'),
('休闲娱乐', '电影院', 'cinema', TRUE, '电影院'),
('休闲娱乐', '图书馆', 'library', TRUE, '图书馆'),
('休闲娱乐', '游戏室', 'arcade', TRUE, '游戏室'),
('休闲娱乐', '迷你高尔夫', 'miniGolf', TRUE, '迷你高尔夫');

-- 商务服务（原有2个，新增10个，共12个）
INSERT INTO group_facilities (facility_type, facility_name, facility_code, available, description) VALUES
('商务服务', '秘书服务', 'secretarialService', TRUE, '秘书服务'),
('商务服务', '传真服务', 'faxService', TRUE, '传真服务'),
('商务服务', '打印服务', 'printingService', TRUE, '打印服务'),
('商务服务', '复印服务', 'copyingService', TRUE, '复印服务'),
('商务服务', '快递服务', 'courierService', TRUE, '快递服务'),
('商务服务', '会议室预订', 'meetingRoomBooking', TRUE, '会议室预订'),
('商务服务', '商务套餐', 'businessPackage', TRUE, '商务套餐'),
('商务服务', '电话会议', 'videoConference', TRUE, '电话会议'),
('商务服务', '翻译服务', 'translationService', TRUE, '翻译服务'),
('商务服务', '商务中心', 'executiveLounge', TRUE, '行政酒廊');

-- 前台服务（新增10个）
INSERT INTO group_facilities (facility_type, facility_name, facility_code, available, description) VALUES
('前台服务', '24小时前台', 'twentyFourHourFrontDesk', TRUE, '24小时前台服务'),
('前台服务', '快速入住', 'expressCheckIn', TRUE, '快速入住服务'),
('前台服务', '快速退房', 'expressCheckOut', TRUE, '快速退房服务'),
('前台服务', '外币兑换', 'currencyExchange', TRUE, '外币兑换服务'),
('前台服务', '票务服务', 'ticketService', TRUE, '票务服务'),
('前台服务', '旅游咨询', 'touristInformation', TRUE, '旅游咨询服务'),
('前台服务', '叫醒服务', 'wakeUpCall', TRUE, '叫醒服务'),
('前台服务', '留言服务', 'messageService', TRUE, '留言服务'),
('前台服务', '保险箱', 'safeDepositBox', TRUE, '保险箱服务'),
('前台服务', '门童', 'doorman', TRUE, '门童服务');

-- 验证数据
SELECT 
    facility_type AS '设施类型',
    COUNT(*) AS '设施数量'
FROM group_facilities
GROUP BY facility_type
ORDER BY facility_type;

SELECT '集团设施增加完成！共 ' AS message, COUNT(*) AS '设施数量' FROM group_facilities;
