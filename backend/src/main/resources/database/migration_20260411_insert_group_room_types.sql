-- 为每个集团插入40个集团房型数据
-- 使用数据库中已存在的房型大类ID

-- 首先获取现有租户和房型大类ID
SET @standard_id = (SELECT id FROM room_type_categories WHERE category_code = 'STANDARD' LIMIT 1);
SET @king_id = (SELECT id FROM room_type_categories WHERE category_code = 'KING' LIMIT 1);
SET @twin_id = (SELECT id FROM room_type_categories WHERE category_code = 'TWIN' LIMIT 1);
SET @suite_id = (SELECT id FROM room_type_categories WHERE category_code = 'SUITE' LIMIT 1);
SET @executive_id = (SELECT id FROM room_type_categories WHERE category_code = 'EXECUTIVE' LIMIT 1);
SET @family_id = (SELECT id FROM room_type_categories WHERE category_code = 'FAMILY' LIMIT 1);

-- 为每个集团插入40个集团房型
-- 集团1: 万豪国际集团

-- 标准房 (7个)
INSERT INTO group_room_types (group_id, room_type_code, room_type_name, description, room_type_category_id, max_occupancy, sort_order, status, created_at, updated_at) VALUES
(1, 'MAR-STD-001', '标准大床房', '万豪标准大床房', @standard_id, 2, 1, 'active', NOW(), NOW()),
(1, 'MAR-STD-002', '标准双床房', '万豪标准双床房', @twin_id, 2, 2, 'active', NOW(), NOW()),
(1, 'MAR-STD-003', '标准套房', '万豪标准套房', @suite_id, 4, 3, 'active', NOW(), NOW()),
(1, 'MAR-STD-004', '标准行政房', '万豪标准行政房', @executive_id, 2, 4, 'active', NOW(), NOW()),
(1, 'MAR-STD-005', '标准家庭房', '万豪标准家庭房', @family_id, 4, 5, 'active', NOW(), NOW()),
(1, 'MAR-STD-006', '高级标准房', '万豪高级标准房', @standard_id, 2, 6, 'active', NOW(), NOW()),
(1, 'MAR-STD-007', '豪华标准房', '万豪豪华标准房', @standard_id, 2, 7, 'active', NOW(), NOW());

-- 大床房 (7个)
INSERT INTO group_room_types (group_id, room_type_code, room_type_name, description, room_type_category_id, max_occupancy, sort_order, status, created_at, updated_at) VALUES
(1, 'MAR-KING-001', '豪华大床房', '万豪豪华大床房', @king_id, 2, 8, 'active', NOW(), NOW()),
(1, 'MAR-KING-002', '行政大床房', '万豪行政大床房', @king_id, 2, 9, 'active', NOW(), NOW()),
(1, 'MAR-KING-003', '海景大床房', '万豪海景大床房', @king_id, 2, 10, 'active', NOW(), NOW()),
(1, 'MAR-KING-004', '城景大床房', '万豪城景大床房', @king_id, 2, 11, 'active', NOW(), NOW()),
(1, 'MAR-KING-005', '园景大床房', '万豪园景大床房', @king_id, 2, 12, 'active', NOW(), NOW()),
(1, 'MAR-KING-006', '山景大床房', '万豪山景大床房', @king_id, 2, 13, 'active', NOW(), NOW()),
(1, 'MAR-KING-007', '湖景大床房', '万豪湖景大床房', @king_id, 2, 14, 'active', NOW(), NOW());

-- 双床房 (7个)
INSERT INTO group_room_types (group_id, room_type_code, room_type_name, description, room_type_category_id, max_occupancy, sort_order, status, created_at, updated_at) VALUES
(1, 'MAR-TWIN-001', '豪华双床房', '万豪豪华双床房', @twin_id, 2, 15, 'active', NOW(), NOW()),
(1, 'MAR-TWIN-002', '行政双床房', '万豪行政双床房', @twin_id, 2, 16, 'active', NOW(), NOW()),
(1, 'MAR-TWIN-003', '海景双床房', '万豪海景双床房', @twin_id, 2, 17, 'active', NOW(), NOW()),
(1, 'MAR-TWIN-004', '城景双床房', '万豪城景双床房', @twin_id, 2, 18, 'active', NOW(), NOW()),
(1, 'MAR-TWIN-005', '园景双床房', '万豪园景双床房', @twin_id, 2, 19, 'active', NOW(), NOW()),
(1, 'MAR-TWIN-006', '山景双床房', '万豪山景双床房', @twin_id, 2, 20, 'active', NOW(), NOW()),
(1, 'MAR-TWIN-007', '湖景双床房', '万豪湖景双床房', @twin_id, 2, 21, 'active', NOW(), NOW());

-- 套房 (6个)
INSERT INTO group_room_types (group_id, room_type_code, room_type_name, description, room_type_category_id, max_occupancy, sort_order, status, created_at, updated_at) VALUES
(1, 'MAR-SUITE-001', '商务套房', '万豪商务套房', @suite_id, 4, 22, 'active', NOW(), NOW()),
(1, 'MAR-SUITE-002', '行政套房', '万豪行政套房', @suite_id, 4, 23, 'active', NOW(), NOW()),
(1, 'MAR-SUITE-003', '总统套房', '万豪总统套房', @suite_id, 6, 24, 'active', NOW(), NOW()),
(1, 'MAR-SUITE-004', '海景套房', '万豪海景套房', @suite_id, 4, 25, 'active', NOW(), NOW()),
(1, 'MAR-SUITE-005', '城景套房', '万豪城景套房', @suite_id, 4, 26, 'active', NOW(), NOW()),
(1, 'MAR-SUITE-006', '豪华套房', '万豪豪华套房', @suite_id, 4, 27, 'active', NOW(), NOW());

-- 行政房 (6个)
INSERT INTO group_room_types (group_id, room_type_code, room_type_name, description, room_type_category_id, max_occupancy, sort_order, status, created_at, updated_at) VALUES
(1, 'MAR-EXEC-001', '商务行政房', '万豪商务行政房', @executive_id, 2, 28, 'active', NOW(), NOW()),
(1, 'MAR-EXEC-002', '豪华行政房', '万豪豪华行政房', @executive_id, 2, 29, 'active', NOW(), NOW()),
(1, 'MAR-EXEC-003', '海景行政房', '万豪海景行政房', @executive_id, 2, 30, 'active', NOW(), NOW()),
(1, 'MAR-EXEC-004', '城景行政房', '万豪城景行政房', @executive_id, 2, 31, 'active', NOW(), NOW()),
(1, 'MAR-EXEC-005', '园景行政房', '万豪园景行政房', @executive_id, 2, 32, 'active', NOW(), NOW()),
(1, 'MAR-EXEC-006', '湖景行政房', '万豪湖景行政房', @executive_id, 2, 33, 'active', NOW(), NOW());

-- 家庭房 (7个)
INSERT INTO group_room_types (group_id, room_type_code, room_type_name, description, room_type_category_id, max_occupancy, sort_order, status, created_at, updated_at) VALUES
(1, 'MAR-FAMILY-001', '标准家庭房', '万豪标准家庭房', @family_id, 4, 34, 'active', NOW(), NOW()),
(1, 'MAR-FAMILY-002', '豪华家庭房', '万豪豪华家庭房', @family_id, 4, 35, 'active', NOW(), NOW()),
(1, 'MAR-FAMILY-003', '海景家庭房', '万豪海景家庭房', @family_id, 4, 36, 'active', NOW(), NOW()),
(1, 'MAR-FAMILY-004', '城景家庭房', '万豪城景家庭房', @family_id, 4, 37, 'active', NOW(), NOW()),
(1, 'MAR-FAMILY-005', '园景家庭房', '万豪园景家庭房', @family_id, 4, 38, 'active', NOW(), NOW()),
(1, 'MAR-FAMILY-006', '山景家庭房', '万豪山景家庭房', @family_id, 4, 39, 'active', NOW(), NOW()),
(1, 'MAR-FAMILY-007', '湖景家庭房', '万豪湖景家庭房', @family_id, 4, 40, 'active', NOW(), NOW());

-- 集团2: 希尔顿酒店集团 (复用房型数据，调整代码前缀)
INSERT INTO group_room_types (group_id, room_type_code, room_type_name, description, room_type_category_id, max_occupancy, sort_order, status, created_at, updated_at)
SELECT 
    2 as group_id,
    REPLACE(room_type_code, 'MAR-', 'HIL-') as room_type_code,
    REPLACE(room_type_name, '万豪', '希尔顿') as room_type_name,
    REPLACE(description, '万豪', '希尔顿') as description,
    room_type_category_id,
    max_occupancy,
    sort_order,
    status,
    NOW(),
    NOW()
FROM group_room_types
WHERE group_id = 1;

-- 集团3: 洲际酒店集团
INSERT INTO group_room_types (group_id, room_type_code, room_type_name, description, room_type_category_id, max_occupancy, sort_order, status, created_at, updated_at)
SELECT 
    3 as group_id,
    REPLACE(room_type_code, 'MAR-', 'IHG-') as room_type_code,
    REPLACE(room_type_name, '万豪', '洲际') as room_type_name,
    REPLACE(description, '万豪', '洲际') as description,
    room_type_category_id,
    max_occupancy,
    sort_order,
    status,
    NOW(),
    NOW()
FROM group_room_types
WHERE group_id = 1;

-- 集团4: 凯悦酒店集团
INSERT INTO group_room_types (group_id, room_type_code, room_type_name, description, room_type_category_id, max_occupancy, sort_order, status, created_at, updated_at)
SELECT 
    4 as group_id,
    REPLACE(room_type_code, 'MAR-', 'HYATT-') as room_type_code,
    REPLACE(room_type_name, '万豪', '凯悦') as room_type_name,
    REPLACE(description, '万豪', '凯悦') as description,
    room_type_category_id,
    max_occupancy,
    sort_order,
    status,
    NOW(),
    NOW()
FROM group_room_types
WHERE group_id = 1;

-- 集团5: 雅高酒店集团
INSERT INTO group_room_types (group_id, room_type_code, room_type_name, description, room_type_category_id, max_occupancy, sort_order, status, created_at, updated_at)
SELECT 
    5 as group_id,
    REPLACE(room_type_code, 'MAR-', 'ACCOR-') as room_type_code,
    REPLACE(room_type_name, '万豪', '雅高') as room_type_name,
    REPLACE(description, '万豪', '雅高') as description,
    room_type_category_id,
    max_occupancy,
    sort_order,
    status,
    NOW(),
    NOW()
FROM group_room_types
WHERE group_id = 1;

-- 统计插入结果
SELECT '集团房型数据插入完成' AS message;
SELECT group_id, COUNT(*) AS room_type_count FROM group_room_types GROUP BY group_id;
