-- 添加酒店管控字段

-- 为 hotels 表添加 allow_create_rate_code 字段
ALTER TABLE hotels ADD COLUMN allow_create_rate_code VARCHAR(20) DEFAULT 'allow' COMMENT '是否允许酒店创建房价码: allow-允许, disallow-不允许';

-- 为 hotels 表添加 allow_create_room_type 字段
ALTER TABLE hotels ADD COLUMN allow_create_room_type VARCHAR(20) DEFAULT 'allow' COMMENT '是否允许酒店创建房型: allow-允许, disallow-不允许';

-- 为现有酒店数据设置默认值
UPDATE hotels SET allow_create_rate_code = 'allow' WHERE allow_create_rate_code IS NULL;
UPDATE hotels SET allow_create_room_type = 'allow' WHERE allow_create_room_type IS NULL;

SELECT '酒店管控字段添加完成' AS message;
