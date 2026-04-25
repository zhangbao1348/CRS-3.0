-- 为每个租户保留10个房型，删除多余的房型

-- 步骤1: 为每个租户标记要保留的10个房型
WITH ranked_room_types AS (
    SELECT 
        id, 
        group_id,
        ROW_NUMBER() OVER (PARTITION BY group_id ORDER BY id) as row_num
    FROM 
        group_room_types
)

-- 步骤2: 删除每个租户中排名超过10的房型
DELETE FROM 
    group_room_types
WHERE 
    id IN (
        SELECT 
            id
        FROM 
            ranked_room_types
        WHERE 
            row_num > 10
    );

-- 验证结果
SELECT 
    group_id, 
    COUNT(*) as room_type_count
FROM 
    group_room_types
GROUP BY 
    group_id
ORDER BY 
    group_id;
