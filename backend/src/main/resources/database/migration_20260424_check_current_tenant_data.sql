-- 查看当前租户和相关数据情况

USE CRS;

-- 查看所有租户
SELECT '=== 租户信息 ===' AS info;
SELECT id, tenant_code, tenant_name, status FROM tenants ORDER BY id;

-- 查看用户表租户分布
SELECT '=== 用户表租户分布 ===' AS info;
SELECT tenant_id, COUNT(*) AS user_count FROM users GROUP BY tenant_id ORDER BY tenant_id;

-- 查看角色表租户分布
SELECT '=== 角色表租户分布 ===' AS info;
SELECT tenant_id, COUNT(*) AS role_count FROM roles GROUP BY tenant_id ORDER BY tenant_id;

-- 查看集团表
SELECT '=== 集团表情况 ===' AS info;
SHOW TABLES LIKE '%group%';

-- 查看有tenant_id字段的表
SELECT '=== 有tenant_id字段的表 ===' AS info;
SELECT 
    TABLE_NAME,
    COLUMN_NAME
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'CRS' 
  AND COLUMN_NAME IN ('tenant_id', 'tenantId')
ORDER BY TABLE_NAME;
