-- 修复hotel_rate_code_allocations表的外键约束
-- 日期：2026-04-11

USE CRS;

-- 删除旧的外键约束
ALTER TABLE hotel_rate_code_allocations 
    DROP FOREIGN KEY FKmqnnhtqdunmlibcnitsewbv8b;

-- 添加新的外键约束，引用group_rate_codes表
ALTER TABLE hotel_rate_code_allocations 
    ADD CONSTRAINT fk_hotel_rate_code_group 
    FOREIGN KEY (rate_code_id) REFERENCES group_rate_codes(id);

-- 验证修改
SHOW CREATE TABLE hotel_rate_code_allocations\G

SELECT 'hotel_rate_code_allocations表外键约束修复完成！' AS message;
