-- 重新插入租户1的集团房型数据

USE CRS;

-- 房型名称和描述模板
SET @room_type_names = '豪华大床房,高级双床房,行政套房,总统套房,海景房,山景房,城景房,湖景房,园景房,家庭房,蜜月房,商务房,无烟房,吸烟房,无障碍房,连通房,复式套房,阁楼套房,水疗房,高尔夫房,滑雪房,海滩房,养生房,主题房,豪华套房,行政大床房,高级套房,皇家套房,迷你套房,亲子房,情侣房,电竞房,影音房,阅读房,艺术房,音乐房,运动房,瑜伽房,冥想房,SPA套房,温泉房,泳池房,露台房,阳台房,顶层房,底楼房,转角房,中间房,边间房,内景房,外景房,江景房,河景房,森林景房,花园景房,庭院景房,公园景房,高尔夫景房,滑雪场景房,海滩景房,山景房,海景房,湖景房,江景房,河景房,城景房,园景房,森林景房,花园景房,庭院景房,公园景房,高尔夫景房,滑雪场景房,海滩景房,标准单人间,标准双人间,标准大床房,标准双床房,高级单人间,高级双人间,高级大床房,高级双床房,豪华单人间,豪华双人间,豪华大床房,豪华双床房,行政单人间,行政双人间,行政大床房,行政双床房';

SET @descriptions = '温馨舒适的房间，配备现代化设施,高品质住宿体验，尽享尊贵服务,宽敞明亮的套房，独立客厅和卧室,顶级奢华体验，专属管家服务,无敌海景，浪漫度假首选,壮丽山景，亲近自然,城市天际线景观，繁华尽收眼底,宁静湖景，心旷神怡,绿意盎然的园景，清新自然,适合全家入住，温馨和睦,浪漫蜜月，甜蜜时光,商务出差首选，办公便利,清新无烟环境，健康舒适,允许吸烟，自由选择,无障碍设施，方便行动不便人士,两间连通，灵活组合,复式结构，空间宽敞,阁楼层高，独具特色,SPA设施，放松身心,高尔夫球场景观，运动休闲,滑雪度假，欢乐无限,海滩度假，阳光沙滩,养生健康，调理身心,特色主题，别具一格';

-- 清空现有的group_room_types数据
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM group_room_types;
SET FOREIGN_KEY_CHECKS = 1;

-- 为租户1插入100个集团房型（房型大类ID: 1-5）
INSERT INTO group_room_types (group_id, room_type_code, room_type_name, description, room_type_category_id, max_occupancy, sort_order, status, tenant_code, created_at, updated_at) 
SELECT 
    1 AS group_id,
    CONCAT('GRP1_RT', LPAD(n, 3, '0')) AS room_type_code,
    SUBSTRING_INDEX(SUBSTRING_INDEX(@room_type_names, ',', n), ',', -1) AS room_type_name,
    CONCAT(SUBSTRING_INDEX(SUBSTRING_INDEX(@descriptions, ',', ((n - 1) % 24) + 1), ',', -1), '，编号', n) AS description,
    ((n - 1) % 5) + 1 AS room_type_category_id,
    CASE 
        WHEN n % 3 = 0 THEN 4
        WHEN n % 3 = 1 THEN 1
        ELSE 2
    END AS max_occupancy,
    n AS sort_order,
    CASE 
        WHEN n % 20 = 0 THEN 'inactive'
        ELSE 'active'
    END AS status,
    'MARRIOT' AS tenant_code,
    NOW() AS created_at,
    NOW() AS updated_at
FROM (
    SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
    UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15
    UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
    UNION SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25
    UNION SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29 UNION SELECT 30
    UNION SELECT 31 UNION SELECT 32 UNION SELECT 33 UNION SELECT 34 UNION SELECT 35
    UNION SELECT 36 UNION SELECT 37 UNION SELECT 38 UNION SELECT 39 UNION SELECT 40
    UNION SELECT 41 UNION SELECT 42 UNION SELECT 43 UNION SELECT 44 UNION SELECT 45
    UNION SELECT 46 UNION SELECT 47 UNION SELECT 48 UNION SELECT 49 UNION SELECT 50
    UNION SELECT 51 UNION SELECT 52 UNION SELECT 53 UNION SELECT 54 UNION SELECT 55
    UNION SELECT 56 UNION SELECT 57 UNION SELECT 58 UNION SELECT 59 UNION SELECT 60
    UNION SELECT 61 UNION SELECT 62 UNION SELECT 63 UNION SELECT 64 UNION SELECT 65
    UNION SELECT 66 UNION SELECT 67 UNION SELECT 68 UNION SELECT 69 UNION SELECT 70
    UNION SELECT 71 UNION SELECT 72 UNION SELECT 73 UNION SELECT 74 UNION SELECT 75
    UNION SELECT 76 UNION SELECT 77 UNION SELECT 78 UNION SELECT 79 UNION SELECT 80
    UNION SELECT 81 UNION SELECT 82 UNION SELECT 83 UNION SELECT 84 UNION SELECT 85
    UNION SELECT 86 UNION SELECT 87 UNION SELECT 88 UNION SELECT 89 UNION SELECT 90
    UNION SELECT 91 UNION SELECT 92 UNION SELECT 93 UNION SELECT 94 UNION SELECT 95
    UNION SELECT 96 UNION SELECT 97 UNION SELECT 98 UNION SELECT 99 UNION SELECT 100
) AS numbers;

-- 显示插入结果
SELECT CONCAT('集团房型数据初始化完成！共插入 ', COUNT(*), ' 条集团房型数据') AS result 
FROM group_room_types;
