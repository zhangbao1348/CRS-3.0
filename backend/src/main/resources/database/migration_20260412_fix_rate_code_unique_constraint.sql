-- 修复 rate_codes 表的唯一约束问题
USE CRS;

-- 先查看当前索引
SHOW INDEX FROM rate_codes;

-- 移除旧的唯一索引（如果存在）
ALTER TABLE rate_codes DROP INDEX UK_27wu8hi8r0qms8ch72sgjmo9e;

-- 添加新的联合唯一索引（hotel_id + rate_code）
ALTER TABLE rate_codes 
ADD UNIQUE INDEX uk_hotel_rate_code (hotel_id, rate_code);

SELECT 'rate_codes 表唯一约束修复完成！' AS result;
