-- 修复 group_rate_codes 表中 parent_rate_code 字段的值
-- 之前存的是父级的 ID 字符串，应该存父级的 rate_code 字符串
UPDATE group_rate_codes child
INNER JOIN group_rate_codes parent ON child.parent_rate_code_id = parent.id
SET child.parent_rate_code = parent.rate_code
WHERE child.parent_rate_code_id IS NOT NULL;

-- 修复 rate_plans 表中 parent_rate_code 字段的值
UPDATE rate_plans child
INNER JOIN group_rate_codes parent ON child.parent_rate_code_id = parent.id
SET child.parent_rate_code = parent.rate_code
WHERE child.parent_rate_code_id IS NOT NULL;
