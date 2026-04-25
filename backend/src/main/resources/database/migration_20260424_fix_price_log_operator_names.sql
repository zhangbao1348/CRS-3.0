-- 修复已有的价格日志中URL编码的操作人名称
-- 将 %E8%B6%85%E7%BA%A7%E7%AE%A1%E7%90%86%E5%91%981 解码为 超级管理员1
UPDATE hotel_price_logs 
SET operator_name = '超级管理员1' 
WHERE operator_name LIKE '%25E%' OR operator_name LIKE '%E8%';
