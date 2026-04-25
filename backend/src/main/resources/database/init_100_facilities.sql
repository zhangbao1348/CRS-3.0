-- 设施数据初始化脚本
-- 包含100个设施信息，按租户分配

USE CRS;

-- 确保表存在
CREATE TABLE IF NOT EXISTS tenant_facilities (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id INT NOT NULL COMMENT '租户ID',
    facility_category_id INT COMMENT '设施分类ID',
    facility_code VARCHAR(50) NOT NULL COMMENT '设施编码',
    facility_name VARCHAR(100) NOT NULL COMMENT '设施名称',
    facility_type VARCHAR(50) NOT NULL COMMENT '设施类型',
    description TEXT COMMENT '设施描述',
    icon VARCHAR(255) COMMENT '设施图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_facility_code (facility_code),
    INDEX idx_category_id (facility_category_id),
    INDEX idx_status (status),
    UNIQUE KEY uk_tenant_facility (tenant_id, facility_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户设施库表';

-- 创建设施分类表
CREATE TABLE IF NOT EXISTS tenant_facility_categories (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id INT NOT NULL COMMENT '租户ID',
    category_code VARCHAR(50) NOT NULL COMMENT '分类编码',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_category_code (category_code),
    INDEX idx_status (status),
    UNIQUE KEY uk_tenant_category (tenant_id, category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户设施分类表';

-- ============================================
-- 插入设施分类
-- ============================================

-- 租户1：锦江酒店集团 - 设施分类
INSERT INTO tenant_facility_categories (tenant_id, category_code, category_name, description, sort_order, status) VALUES
(1, 'TRANSPORT', '交通服务', '酒店交通相关设施', 1, 'active'),
(1, 'DINING', '餐饮服务', '酒店餐饮相关设施', 2, 'active'),
(1, 'CLEANING', '清洁服务', '酒店清洁相关设施', 3, 'active'),
(1, 'RECREATION', '休闲娱乐', '酒店休闲娱乐设施', 4, 'active'),
(1, 'BUSINESS', '商务服务', '酒店商务相关设施', 5, 'active'),
(1, 'FRONT_DESK', '前台服务', '酒店前台服务设施', 6, 'active'),
(1, 'GENERAL', '通用设施', '酒店通用设施', 7, 'active');

-- 租户2：首旅如家酒店集团 - 设施分类
INSERT INTO tenant_facility_categories (tenant_id, category_code, category_name, description, sort_order, status) VALUES
(2, 'TRANSPORT', '交通服务', '酒店交通相关设施', 1, 'active'),
(2, 'DINING', '餐饮服务', '酒店餐饮相关设施', 2, 'active'),
(2, 'CLEANING', '清洁服务', '酒店清洁相关设施', 3, 'active'),
(2, 'RECREATION', '休闲娱乐', '酒店休闲娱乐设施', 4, 'active'),
(2, 'BUSINESS', '商务服务', '酒店商务相关设施', 5, 'active'),
(2, 'FRONT_DESK', '前台服务', '酒店前台服务设施', 6, 'active'),
(2, 'GENERAL', '通用设施', '酒店通用设施', 7, 'active');

-- 租户3：华住酒店集团 - 设施分类
INSERT INTO tenant_facility_categories (tenant_id, category_code, category_name, description, sort_order, status) VALUES
(3, 'TRANSPORT', '交通服务', '酒店交通相关设施', 1, 'active'),
(3, 'DINING', '餐饮服务', '酒店餐饮相关设施', 2, 'active'),
(3, 'CLEANING', '清洁服务', '酒店清洁相关设施', 3, 'active'),
(3, 'RECREATION', '休闲娱乐', '酒店休闲娱乐设施', 4, 'active'),
(3, 'BUSINESS', '商务服务', '酒店商务相关设施', 5, 'active'),
(3, 'FRONT_DESK', '前台服务', '酒店前台服务设施', 6, 'active'),
(3, 'GENERAL', '通用设施', '酒店通用设施', 7, 'active');

-- ============================================
-- 插入设施数据 - 每个租户约33个设施，共100个
-- ============================================

-- 租户1：锦江酒店集团 - 33个设施
INSERT INTO tenant_facilities (tenant_id, facility_category_id, facility_code, facility_name, facility_type, description, sort_order, status) VALUES
(1, 1, 'paidParking', '收费停车场', 'TRANSPORT', '酒店内收费停车场', 1, 'active'),
(1, 1, 'freeParking', '免费停车场', 'TRANSPORT', '酒店内免费停车场', 2, 'active'),
(1, 1, 'freeShuttle', '免费接送机', 'TRANSPORT', '免费机场接送服务', 3, 'active'),
(1, 1, 'paidShuttle', '收费接送机', 'TRANSPORT', '收费机场接送服务', 4, 'active'),
(1, 1, 'airportPickup', '机场接机', 'TRANSPORT', '机场接机服务', 5, 'active'),
(1, 1, 'trainStationPickup', '火车站接送', 'TRANSPORT', '火车站接送服务', 6, 'active'),
(1, 1, 'valetParking', '代客泊车', 'TRANSPORT', '代客泊车服务', 7, 'active'),
(1, 1, 'electricVehicleCharging', '电动汽车充电桩', 'TRANSPORT', '电动汽车充电桩', 8, 'active'),
(1, 2, 'buffetRestaurant', '自助早餐厅', 'DINING', '自助早餐餐厅', 1, 'active'),
(1, 2, 'cafe', '咖啡厅', 'DINING', '咖啡饮品服务', 2, 'active'),
(1, 2, 'chineseRestaurant', '中餐厅', 'DINING', '中餐服务', 3, 'active'),
(1, 2, 'westernRestaurant', '西餐厅', 'DINING', '西餐服务', 4, 'active'),
(1, 2, 'japaneseRestaurant', '日餐厅', 'DINING', '日餐服务', 5, 'active'),
(1, 2, 'loungeBar', '酒吧/酒廊', 'DINING', '酒吧和酒廊服务', 6, 'active'),
(1, 2, 'roomService', '客房送餐', 'DINING', '客房送餐服务', 7, 'active'),
(1, 2, 'teaHouse', '茶室', 'DINING', '茶室服务', 8, 'active'),
(1, 3, 'laundryService', '外送洗衣服务', 'CLEANING', '外送洗衣服务', 1, 'active'),
(1, 3, 'dryer', '干衣机', 'CLEANING', '干衣机设备', 2, 'active'),
(1, 3, 'iron', '熨斗/挂烫机', 'CLEANING', '熨斗和挂烫机', 3, 'active'),
(1, 3, 'laundryRoom', '洗衣房', 'CLEANING', '自助洗衣房', 4, 'active'),
(1, 3, 'valetService', '熨衣服务', 'CLEANING', '熨衣服务', 5, 'active'),
(1, 3, 'washingService', '洗衣服务', 'CLEANING', '洗衣服务', 6, 'active'),
(1, 3, 'dailyCleaning', '每日清洁', 'CLEANING', '每日客房清洁', 7, 'active'),
(1, 3, 'turnDownService', '夜床服务', 'CLEANING', '夜床整理服务', 8, 'active'),
(1, 4, 'indoorPool', '室内泳池', 'RECREATION', '室内游泳池', 1, 'active'),
(1, 4, 'outdoorPool', '室外泳池', 'RECREATION', '室外游泳池', 2, 'active'),
(1, 4, 'fitnessCenter', '健身房', 'RECREATION', '健身中心', 3, 'active'),
(1, 4, 'spa', 'SPA水疗', 'RECREATION', 'SPA水疗中心', 4, 'active'),
(1, 4, 'sauna', '桑拿', 'RECREATION', '桑拿房', 5, 'active'),
(1, 4, 'steamRoom', '蒸汽房', 'RECREATION', '蒸汽房', 6, 'active'),
(1, 4, 'massage', '按摩', 'RECREATION', '按摩服务', 7, 'active'),
(1, 4, 'beautySalon', '美容美发', 'RECREATION', '美容美发服务', 8, 'active'),
(1, 5, 'businessCenter', '商务中心', 'BUSINESS', '商务中心服务', 1, 'active');

-- 租户2：首旅如家酒店集团 - 33个设施
INSERT INTO tenant_facilities (tenant_id, facility_category_id, facility_code, facility_name, facility_type, description, sort_order, status) VALUES
(2, 8, 'paidParking', '收费停车场', 'TRANSPORT', '酒店内收费停车场', 1, 'active'),
(2, 8, 'freeParking', '免费停车场', 'TRANSPORT', '酒店内免费停车场', 2, 'active'),
(2, 8, 'freeShuttle', '免费接送机', 'TRANSPORT', '免费机场接送服务', 3, 'active'),
(2, 8, 'paidShuttle', '收费接送机', 'TRANSPORT', '收费机场接送服务', 4, 'active'),
(2, 8, 'airportPickup', '机场接机', 'TRANSPORT', '机场接机服务', 5, 'active'),
(2, 8, 'trainStationPickup', '火车站接送', 'TRANSPORT', '火车站接送服务', 6, 'active'),
(2, 8, 'valetParking', '代客泊车', 'TRANSPORT', '代客泊车服务', 7, 'active'),
(2, 8, 'electricVehicleCharging', '电动汽车充电桩', 'TRANSPORT', '电动汽车充电桩', 8, 'active'),
(2, 9, 'buffetRestaurant', '自助早餐厅', 'DINING', '自助早餐餐厅', 1, 'active'),
(2, 9, 'cafe', '咖啡厅', 'DINING', '咖啡饮品服务', 2, 'active'),
(2, 9, 'chineseRestaurant', '中餐厅', 'DINING', '中餐服务', 3, 'active'),
(2, 9, 'westernRestaurant', '西餐厅', 'DINING', '西餐服务', 4, 'active'),
(2, 9, 'japaneseRestaurant', '日餐厅', 'DINING', '日餐服务', 5, 'active'),
(2, 9, 'loungeBar', '酒吧/酒廊', 'DINING', '酒吧和酒廊服务', 6, 'active'),
(2, 9, 'roomService', '客房送餐', 'DINING', '客房送餐服务', 7, 'active'),
(2, 9, 'teaHouse', '茶室', 'DINING', '茶室服务', 8, 'active'),
(2, 10, 'laundryService', '外送洗衣服务', 'CLEANING', '外送洗衣服务', 1, 'active'),
(2, 10, 'dryer', '干衣机', 'CLEANING', '干衣机设备', 2, 'active'),
(2, 10, 'iron', '熨斗/挂烫机', 'CLEANING', '熨斗和挂烫机', 3, 'active'),
(2, 10, 'laundryRoom', '洗衣房', 'CLEANING', '自助洗衣房', 4, 'active'),
(2, 10, 'valetService', '熨衣服务', 'CLEANING', '熨衣服务', 5, 'active'),
(2, 10, 'washingService', '洗衣服务', 'CLEANING', '洗衣服务', 6, 'active'),
(2, 10, 'dailyCleaning', '每日清洁', 'CLEANING', '每日客房清洁', 7, 'active'),
(2, 10, 'turnDownService', '夜床服务', 'CLEANING', '夜床整理服务', 8, 'active'),
(2, 11, 'indoorPool', '室内泳池', 'RECREATION', '室内游泳池', 1, 'active'),
(2, 11, 'outdoorPool', '室外泳池', 'RECREATION', '室外游泳池', 2, 'active'),
(2, 11, 'fitnessCenter', '健身房', 'RECREATION', '健身中心', 3, 'active'),
(2, 11, 'spa', 'SPA水疗', 'RECREATION', 'SPA水疗中心', 4, 'active'),
(2, 11, 'sauna', '桑拿', 'RECREATION', '桑拿房', 5, 'active'),
(2, 11, 'steamRoom', '蒸汽房', 'RECREATION', '蒸汽房', 6, 'active'),
(2, 11, 'massage', '按摩', 'RECREATION', '按摩服务', 7, 'active'),
(2, 11, 'beautySalon', '美容美发', 'RECREATION', '美容美发服务', 8, 'active'),
(2, 12, 'businessCenter', '商务中心', 'BUSINESS', '商务中心服务', 1, 'active');

-- 租户3：华住酒店集团 - 34个设施（凑够100个）
INSERT INTO tenant_facilities (tenant_id, facility_category_id, facility_code, facility_name, facility_type, description, sort_order, status) VALUES
(3, 15, 'paidParking', '收费停车场', 'TRANSPORT', '酒店内收费停车场', 1, 'active'),
(3, 15, 'freeParking', '免费停车场', 'TRANSPORT', '酒店内免费停车场', 2, 'active'),
(3, 15, 'freeShuttle', '免费接送机', 'TRANSPORT', '免费机场接送服务', 3, 'active'),
(3, 15, 'paidShuttle', '收费接送机', 'TRANSPORT', '收费机场接送服务', 4, 'active'),
(3, 15, 'airportPickup', '机场接机', 'TRANSPORT', '机场接机服务', 5, 'active'),
(3, 15, 'trainStationPickup', '火车站接送', 'TRANSPORT', '火车站接送服务', 6, 'active'),
(3, 15, 'valetParking', '代客泊车', 'TRANSPORT', '代客泊车服务', 7, 'active'),
(3, 15, 'electricVehicleCharging', '电动汽车充电桩', 'TRANSPORT', '电动汽车充电桩', 8, 'active'),
(3, 16, 'buffetRestaurant', '自助早餐厅', 'DINING', '自助早餐餐厅', 1, 'active'),
(3, 16, 'cafe', '咖啡厅', 'DINING', '咖啡饮品服务', 2, 'active'),
(3, 16, 'chineseRestaurant', '中餐厅', 'DINING', '中餐服务', 3, 'active'),
(3, 16, 'westernRestaurant', '西餐厅', 'DINING', '西餐服务', 4, 'active'),
(3, 16, 'japaneseRestaurant', '日餐厅', 'DINING', '日餐服务', 5, 'active'),
(3, 16, 'loungeBar', '酒吧/酒廊', 'DINING', '酒吧和酒廊服务', 6, 'active'),
(3, 16, 'roomService', '客房送餐', 'DINING', '客房送餐服务', 7, 'active'),
(3, 16, 'teaHouse', '茶室', 'DINING', '茶室服务', 8, 'active'),
(3, 17, 'laundryService', '外送洗衣服务', 'CLEANING', '外送洗衣服务', 1, 'active'),
(3, 17, 'dryer', '干衣机', 'CLEANING', '干衣机设备', 2, 'active'),
(3, 17, 'iron', '熨斗/挂烫机', 'CLEANING', '熨斗和挂烫机', 3, 'active'),
(3, 17, 'laundryRoom', '洗衣房', 'CLEANING', '自助洗衣房', 4, 'active'),
(3, 17, 'valetService', '熨衣服务', 'CLEANING', '熨衣服务', 5, 'active'),
(3, 17, 'washingService', '洗衣服务', 'CLEANING', '洗衣服务', 6, 'active'),
(3, 17, 'dailyCleaning', '每日清洁', 'CLEANING', '每日客房清洁', 7, 'active'),
(3, 17, 'turnDownService', '夜床服务', 'CLEANING', '夜床整理服务', 8, 'active'),
(3, 18, 'indoorPool', '室内泳池', 'RECREATION', '室内游泳池', 1, 'active'),
(3, 18, 'outdoorPool', '室外泳池', 'RECREATION', '室外游泳池', 2, 'active'),
(3, 18, 'fitnessCenter', '健身房', 'RECREATION', '健身中心', 3, 'active'),
(3, 18, 'spa', 'SPA水疗', 'RECREATION', 'SPA水疗中心', 4, 'active'),
(3, 18, 'sauna', '桑拿', 'RECREATION', '桑拿房', 5, 'active'),
(3, 18, 'steamRoom', '蒸汽房', 'RECREATION', '蒸汽房', 6, 'active'),
(3, 18, 'massage', '按摩', 'RECREATION', '按摩服务', 7, 'active'),
(3, 18, 'beautySalon', '美容美发', 'RECREATION', '美容美发服务', 8, 'active'),
(3, 19, 'businessCenter', '商务中心', 'BUSINESS', '商务中心服务', 1, 'active'),
(3, 19, 'meetingRooms', '会议室', 'BUSINESS', '会议室服务', 2, 'active');

-- ============================================
-- 验证数据
-- ============================================

SELECT 
    t.tenant_name AS '租户名称',
    COUNT(DISTINCT tfc.id) AS '分类数量',
    COUNT(DISTINCT tf.id) AS '设施数量'
FROM tenants t
LEFT JOIN tenant_facility_categories tfc ON t.id = tfc.tenant_id
LEFT JOIN tenant_facilities tf ON t.id = tf.tenant_id
GROUP BY t.id, t.tenant_name;

SELECT '设施数据初始化完成！' AS message;
