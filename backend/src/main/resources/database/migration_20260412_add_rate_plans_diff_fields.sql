-- 为rate_plans表添加差价配置字段
-- 执行日期：2026-04-12

-- 添加房型差价ID字段
ALTER TABLE rate_plans ADD COLUMN room_type_diff_id INT COMMENT '房型差价ID';

-- 添加人数差价ID字段
ALTER TABLE rate_plans ADD COLUMN person_diff_id INT COMMENT '人数差价ID';
