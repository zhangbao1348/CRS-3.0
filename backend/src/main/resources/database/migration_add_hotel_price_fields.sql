-- 为hotels表添加价格相关字段
USE CRS;

-- 添加新字段
ALTER TABLE hotels 
ADD COLUMN support_multi_price VARCHAR(10) DEFAULT 'no' AFTER total_rooms,
ADD COLUMN multi_price_options TEXT AFTER support_multi_price,
ADD COLUMN support_room_type_price_diff VARCHAR(10) DEFAULT 'no' AFTER multi_price_options,
ADD COLUMN support_person_price_diff VARCHAR(10) DEFAULT 'no' AFTER support_room_type_price_diff;

-- 显示结果
SELECT '酒店价格相关字段添加完成！' AS result;
