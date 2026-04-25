-- 创建酒店设施表并插入100个设施数据
-- 日期：2026-04-11

USE CRS;

-- 创建酒店设施表
CREATE TABLE IF NOT EXISTS hotel_facilities (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    facility_type VARCHAR(50) NOT NULL COMMENT '设施类型',
    facility_name VARCHAR(100) NOT NULL COMMENT '设施名称',
    facility_code VARCHAR(50) NOT NULL COMMENT '设施编码',
    available BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否可用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_hotel_id (hotel_id),
    INDEX idx_facility_code (facility_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒店设施表';

-- 为前10家酒店各插入10个设施，共100个设施数据

-- 酒店1：上海锦江饭店 - 10个设施
INSERT INTO hotel_facilities (hotel_id, facility_type, facility_name, facility_code, available) VALUES
(1, '交通服务', '收费停车场', 'paidParking', TRUE),
(1, '交通服务', '免费停车场', 'freeParking', TRUE),
(1, '交通服务', '免费接送机', 'freeShuttle', TRUE),
(1, '交通服务', '代客泊车', 'valetParking', TRUE),
(1, '餐饮服务', '自助早餐厅', 'buffetRestaurant', TRUE),
(1, '餐饮服务', '咖啡厅', 'cafe', TRUE),
(1, '餐饮服务', '中餐厅', 'chineseRestaurant', TRUE),
(1, '清洁服务', '外送洗衣服务', 'laundryService', TRUE),
(1, '清洁服务', '熨斗/挂烫机', 'iron', TRUE),
(1, '清洁服务', '每日清洁', 'dailyCleaning', TRUE);

-- 酒店2：北京长城饭店 - 10个设施
INSERT INTO hotel_facilities (hotel_id, facility_type, facility_name, facility_code, available) VALUES
(2, '交通服务', '收费停车场', 'paidParking', TRUE),
(2, '交通服务', '免费停车场', 'freeParking', TRUE),
(2, '交通服务', '机场接机', 'airportPickup', TRUE),
(2, '交通服务', '电动汽车充电桩', 'electricVehicleCharging', TRUE),
(2, '餐饮服务', '自助早餐厅', 'buffetRestaurant', TRUE),
(2, '餐饮服务', '西餐厅', 'westernRestaurant', TRUE),
(2, '餐饮服务', '酒吧/酒廊', 'loungeBar', TRUE),
(2, '清洁服务', '洗衣房', 'laundryRoom', TRUE),
(2, '清洁服务', '熨衣服务', 'valetService', TRUE),
(2, '清洁服务', '夜床服务', 'turnDownService', TRUE);

-- 酒店3：广州白云宾馆 - 10个设施
INSERT INTO hotel_facilities (hotel_id, facility_type, facility_name, facility_code, available) VALUES
(3, '交通服务', '收费停车场', 'paidParking', TRUE),
(3, '交通服务', '免费停车场', 'freeParking', TRUE),
(3, '交通服务', '免费接送机', 'freeShuttle', TRUE),
(3, '交通服务', '火车站接送', 'trainStationPickup', TRUE),
(3, '餐饮服务', '自助早餐厅', 'buffetRestaurant', TRUE),
(3, '餐饮服务', '咖啡厅', 'cafe', TRUE),
(3, '餐饮服务', '日餐厅', 'japaneseRestaurant', TRUE),
(3, '清洁服务', '干衣机', 'dryer', TRUE),
(3, '清洁服务', '洗衣服务', 'washingService', TRUE),
(3, '清洁服务', '每日清洁', 'dailyCleaning', TRUE);

-- 酒店4：深圳东华假日酒店 - 10个设施
INSERT INTO hotel_facilities (hotel_id, facility_type, facility_name, facility_code, available) VALUES
(4, '交通服务', '收费停车场', 'paidParking', TRUE),
(4, '交通服务', '免费停车场', 'freeParking', TRUE),
(4, '交通服务', '代客泊车', 'valetParking', TRUE),
(4, '交通服务', '电动汽车充电桩', 'electricVehicleCharging', TRUE),
(4, '餐饮服务', '自助早餐厅', 'buffetRestaurant', TRUE),
(4, '餐饮服务', '客房送餐', 'roomService', TRUE),
(4, '餐饮服务', '茶室', 'teaHouse', TRUE),
(4, '清洁服务', '外送洗衣服务', 'laundryService', TRUE),
(4, '清洁服务', '熨斗/挂烫机', 'iron', TRUE),
(4, '清洁服务', '夜床服务', 'turnDownService', TRUE);

-- 酒店5：杭州黄龙饭店 - 10个设施
INSERT INTO hotel_facilities (hotel_id, facility_type, facility_name, facility_code, available) VALUES
(5, '交通服务', '收费停车场', 'paidParking', TRUE),
(5, '交通服务', '免费停车场', 'freeParking', TRUE),
(5, '交通服务', '免费接送机', 'freeShuttle', TRUE),
(5, '交通服务', '机场接机', 'airportPickup', TRUE),
(5, '餐饮服务', '自助早餐厅', 'buffetRestaurant', TRUE),
(5, '餐饮服务', '咖啡厅', 'cafe', TRUE),
(5, '餐饮服务', '中餐厅', 'chineseRestaurant', TRUE),
(5, '清洁服务', '洗衣房', 'laundryRoom', TRUE),
(5, '清洁服务', '熨衣服务', 'valetService', TRUE),
(5, '清洁服务', '每日清洁', 'dailyCleaning', TRUE);

-- 酒店6：南京金陵饭店 - 10个设施
INSERT INTO hotel_facilities (hotel_id, facility_type, facility_name, facility_code, available) VALUES
(6, '交通服务', '收费停车场', 'paidParking', TRUE),
(6, '交通服务', '免费停车场', 'freeParking', TRUE),
(6, '交通服务', '火车站接送', 'trainStationPickup', TRUE),
(6, '交通服务', '代客泊车', 'valetParking', TRUE),
(6, '餐饮服务', '自助早餐厅', 'buffetRestaurant', TRUE),
(6, '餐饮服务', '西餐厅', 'westernRestaurant', TRUE),
(6, '餐饮服务', '酒吧/酒廊', 'loungeBar', TRUE),
(6, '清洁服务', '干衣机', 'dryer', TRUE),
(6, '清洁服务', '洗衣服务', 'washingService', TRUE),
(6, '清洁服务', '夜床服务', 'turnDownService', TRUE);

-- 酒店7：成都锦江宾馆 - 10个设施
INSERT INTO hotel_facilities (hotel_id, facility_type, facility_name, facility_code, available) VALUES
(7, '交通服务', '收费停车场', 'paidParking', TRUE),
(7, '交通服务', '免费停车场', 'freeParking', TRUE),
(7, '交通服务', '免费接送机', 'freeShuttle', TRUE),
(7, '交通服务', '电动汽车充电桩', 'electricVehicleCharging', TRUE),
(7, '餐饮服务', '自助早餐厅', 'buffetRestaurant', TRUE),
(7, '餐饮服务', '咖啡厅', 'cafe', TRUE),
(7, '餐饮服务', '客房送餐', 'roomService', TRUE),
(7, '清洁服务', '外送洗衣服务', 'laundryService', TRUE),
(7, '清洁服务', '熨斗/挂烫机', 'iron', TRUE),
(7, '清洁服务', '每日清洁', 'dailyCleaning', TRUE);

-- 酒店8：西安索菲特人民大厦 - 10个设施
INSERT INTO hotel_facilities (hotel_id, facility_type, facility_name, facility_code, available) VALUES
(8, '交通服务', '收费停车场', 'paidParking', TRUE),
(8, '交通服务', '免费停车场', 'freeParking', TRUE),
(8, '交通服务', '机场接机', 'airportPickup', TRUE),
(8, '交通服务', '代客泊车', 'valetParking', TRUE),
(8, '餐饮服务', '自助早餐厅', 'buffetRestaurant', TRUE),
(8, '餐饮服务', '日餐厅', 'japaneseRestaurant', TRUE),
(8, '餐饮服务', '茶室', 'teaHouse', TRUE),
(8, '清洁服务', '洗衣房', 'laundryRoom', TRUE),
(8, '清洁服务', '熨衣服务', 'valetService', TRUE),
(8, '清洁服务', '夜床服务', 'turnDownService', TRUE);

-- 酒店9：青岛海景花园大酒店 - 10个设施
INSERT INTO hotel_facilities (hotel_id, facility_type, facility_name, facility_code, available) VALUES
(9, '交通服务', '收费停车场', 'paidParking', TRUE),
(9, '交通服务', '免费停车场', 'freeParking', TRUE),
(9, '交通服务', '免费接送机', 'freeShuttle', TRUE),
(9, '交通服务', '火车站接送', 'trainStationPickup', TRUE),
(9, '餐饮服务', '自助早餐厅', 'buffetRestaurant', TRUE),
(9, '餐饮服务', '中餐厅', 'chineseRestaurant', TRUE),
(9, '餐饮服务', '酒吧/酒廊', 'loungeBar', TRUE),
(9, '清洁服务', '干衣机', 'dryer', TRUE),
(9, '清洁服务', '洗衣服务', 'washingService', TRUE),
(9, '清洁服务', '每日清洁', 'dailyCleaning', TRUE);

-- 酒店10：大连富丽华大酒店 - 10个设施
INSERT INTO hotel_facilities (hotel_id, facility_type, facility_name, facility_code, available) VALUES
(10, '交通服务', '收费停车场', 'paidParking', TRUE),
(10, '交通服务', '免费停车场', 'freeParking', TRUE),
(10, '交通服务', '电动汽车充电桩', 'electricVehicleCharging', TRUE),
(10, '交通服务', '代客泊车', 'valetParking', TRUE),
(10, '餐饮服务', '自助早餐厅', 'buffetRestaurant', TRUE),
(10, '餐饮服务', '西餐厅', 'westernRestaurant', TRUE),
(10, '餐饮服务', '客房送餐', 'roomService', TRUE),
(10, '清洁服务', '外送洗衣服务', 'laundryService', TRUE),
(10, '清洁服务', '熨斗/挂烫机', 'iron', TRUE),
(10, '清洁服务', '夜床服务', 'turnDownService', TRUE);

-- 验证数据
SELECT 
    h.hotel_code AS '酒店编码',
    h.chinese_name AS '酒店名称',
    COUNT(hf.id) AS '设施数量'
FROM hotels h
LEFT JOIN hotel_facilities hf ON h.id = hf.hotel_id
WHERE h.id <= 10
GROUP BY h.id, h.hotel_code, h.chinese_name;

SELECT 'hotel_facilities表创建完成！共插入 ' AS message, COUNT(*) AS '设施数量' FROM hotel_facilities;
