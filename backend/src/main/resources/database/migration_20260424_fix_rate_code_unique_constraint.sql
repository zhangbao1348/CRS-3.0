-- 修复 group_rate_codes 表的唯一约束
-- 将 rate_code 全局唯一改为 (group_id, rate_code) 租户内唯一
ALTER TABLE group_rate_codes DROP INDEX IF EXISTS uk_rate_code;
ALTER TABLE group_rate_codes DROP INDEX IF EXISTS UK_rate_code;
ALTER TABLE group_rate_codes DROP INDEX IF EXISTS rate_code;

-- 删除 JPA 自动生成的唯一索引（可能名称不同）
-- 先查看现有索引: SHOW INDEX FROM group_rate_codes;
-- 如果有 rate_code 相关的唯一索引，手动删除

-- 添加租户级别的联合唯一约束
ALTER TABLE group_rate_codes ADD UNIQUE INDEX uk_tenant_rate_code (group_id, rate_code);
