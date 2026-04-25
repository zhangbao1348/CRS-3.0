-- 删除全部集团房型和集团房价码数据
-- 执行前请确保已备份重要数据

USE CRS;

SET FOREIGN_KEY_CHECKS = 0;

-- 删除集团房价码数据
DELETE FROM group_rate_codes;

-- 删除集团房型数据
DELETE FROM group_room_types;

SET FOREIGN_KEY_CHECKS = 1;

-- 验证删除结果
SELECT '删除后的集团房价码数量:' AS info, COUNT(*) AS count FROM group_rate_codes;
SELECT '删除后的集团房型数量:' AS info, COUNT(*) AS count FROM group_room_types;
