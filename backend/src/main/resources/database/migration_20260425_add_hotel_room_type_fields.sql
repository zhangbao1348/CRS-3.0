-- 酒店房型表增加详细字段
ALTER TABLE hotel_room_types ADD COLUMN english_name VARCHAR(200) COMMENT '英文名称' AFTER room_type_name;
ALTER TABLE hotel_room_types ADD COLUMN area DECIMAL(8,2) COMMENT '面积（平方米）' AFTER description;
ALTER TABLE hotel_room_types ADD COLUMN floor VARCHAR(50) COMMENT '所在楼层' AFTER area;
ALTER TABLE hotel_room_types ADD COLUMN window_type VARCHAR(20) COMMENT '窗型：有窗/无窗' AFTER floor;
ALTER TABLE hotel_room_types ADD COLUMN bed_type VARCHAR(50) COMMENT '床型' AFTER window_type;
ALTER TABLE hotel_room_types ADD COLUMN max_children INT DEFAULT 0 COMMENT '最大入住儿童数' AFTER max_occupancy;
