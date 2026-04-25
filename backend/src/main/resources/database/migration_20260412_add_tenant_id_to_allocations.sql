-- 为 hotel_rate_code_allocations 表添加 tenant_id 字段
USE CRS;

-- 添加 tenant_id 字段
ALTER TABLE hotel_rate_code_allocations 
ADD COLUMN tenant_id INT NOT NULL AFTER id;

-- 如果有数据，先设置默认的 tenant_id（假设是租户2）
UPDATE hotel_rate_code_allocations SET tenant_id = 2;

-- 添加新的唯一索引（包含 tenant_id）
ALTER TABLE hotel_rate_code_allocations 
ADD UNIQUE INDEX uk_tenant_hotel_rate_code (tenant_id, hotel_code, rate_code);

-- 为 tenant_id 字段添加普通索引，提高查询效率
ALTER TABLE hotel_rate_code_allocations 
ADD INDEX idx_tenant_id (tenant_id);

SELECT 'hotel_rate_code_allocations 表添加 tenant_id 字段完成！' AS result;
