-- 插入房型大类数据
-- 日期：2026-04-11

USE CRS;

-- 为租户1和集团1插入房型大类数据
INSERT IGNORE INTO room_type_categories (tenant_id, group_id, category_code, category_name, sort_order, status) VALUES
(1, 1, 'STANDARD', '标准房', 1, 'active'),
(1, 1, 'KING', '大床房', 2, 'active'),
(1, 1, 'TWIN', '双床房', 3, 'active'),
(1, 1, 'SUITE', '套房', 4, 'active'),
(1, 1, 'EXECUTIVE', '行政房', 5, 'active'),
(1, 1, 'FAMILY', '家庭房', 6, 'active');

-- 验证数据
SELECT 
    id AS 'ID',
    category_code AS '编码',
    category_name AS '名称',
    sort_order AS '排序',
    status AS '状态'
FROM room_type_categories
WHERE tenant_id = 1
ORDER BY sort_order;

SELECT '房型大类数据插入完成！' AS message, COUNT(*) AS '数据数量' FROM room_type_categories WHERE tenant_id = 1;
