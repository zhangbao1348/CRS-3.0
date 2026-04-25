-- 创建酒店图片表
-- 日期：2026-04-11

USE CRS;

-- 创建酒店图片表
CREATE TABLE IF NOT EXISTS hotel_images (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    hotel_id INT NOT NULL COMMENT '酒店ID',
    image_type VARCHAR(50) NOT NULL COMMENT '图片类型：logo-店图, external-外观图片, restaurant-餐厅图片, lobby-大堂图片',
    image_path VARCHAR(255) NOT NULL COMMENT '图片存储路径',
    image_name VARCHAR(100) NOT NULL COMMENT '图片名称',
    description VARCHAR(200) COMMENT '图片描述',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_hotel_id (hotel_id),
    INDEX idx_image_type (image_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒店图片表';

-- 插入示例图片数据（为前3家酒店各插入4类图片）

-- 酒店1：上海锦江饭店 - 示例图片
INSERT INTO hotel_images (hotel_id, image_type, image_path, image_name, description, sort_order) VALUES
(1, 'logo', '/images/hotel1/logo.jpg', '上海锦江饭店店图', '锦江饭店品牌标识', 1),
(1, 'external', '/images/hotel1/external.jpg', '上海锦江饭店外观', '酒店外观全景', 1),
(1, 'restaurant', '/images/hotel1/restaurant.jpg', '上海锦江饭店餐厅', '餐厅环境', 1),
(1, 'lobby', '/images/hotel1/lobby.jpg', '上海锦江饭店大堂', '酒店大堂', 1);

-- 酒店2：北京长城饭店 - 示例图片
INSERT INTO hotel_images (hotel_id, image_type, image_path, image_name, description, sort_order) VALUES
(2, 'logo', '/images/hotel2/logo.jpg', '北京长城饭店店图', '长城饭店品牌标识', 1),
(2, 'external', '/images/hotel2/external.jpg', '北京长城饭店外观', '酒店外观全景', 1),
(2, 'restaurant', '/images/hotel2/restaurant.jpg', '北京长城饭店餐厅', '餐厅环境', 1),
(2, 'lobby', '/images/hotel2/lobby.jpg', '北京长城饭店大堂', '酒店大堂', 1);

-- 酒店3：广州白云宾馆 - 示例图片
INSERT INTO hotel_images (hotel_id, image_type, image_path, image_name, description, sort_order) VALUES
(3, 'logo', '/images/hotel3/logo.jpg', '广州白云宾馆店图', '白云宾馆品牌标识', 1),
(3, 'external', '/images/hotel3/external.jpg', '广州白云宾馆外观', '酒店外观全景', 1),
(3, 'restaurant', '/images/hotel3/restaurant.jpg', '广州白云宾馆餐厅', '餐厅环境', 1),
(3, 'lobby', '/images/hotel3/lobby.jpg', '广州白云宾馆大堂', '酒店大堂', 1);

-- 验证数据
SELECT 
    h.hotel_code AS '酒店编码',
    h.chinese_name AS '酒店名称',
    COUNT(hi.id) AS '图片数量'
FROM hotels h
LEFT JOIN hotel_images hi ON h.id = hi.hotel_id
WHERE h.id <= 3
GROUP BY h.id, h.hotel_code, h.chinese_name;

SELECT 'hotel_images表创建完成！共插入 ' AS message, COUNT(*) AS '图片数量' FROM hotel_images;
