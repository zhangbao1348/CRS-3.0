-- 集团设施表增加 scope 字段，区分酒店设施和房型设施
ALTER TABLE group_facilities ADD COLUMN scope VARCHAR(20) NOT NULL DEFAULT 'hotel' COMMENT '适用范围：hotel（酒店设施）/ room_type（房型设施）' AFTER facility_code;

-- 将已有数据默认设为酒店设施
UPDATE group_facilities SET scope = 'hotel' WHERE scope IS NULL OR scope = '';
