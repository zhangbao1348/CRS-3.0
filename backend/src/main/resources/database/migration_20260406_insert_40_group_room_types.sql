-- 数据库迁移脚本 - 2026-04-06
-- 插入40个集团房型示例数据

USE CRS;

-- 插入40个集团房型数据
INSERT IGNORE INTO group_room_types (group_id, room_type_code, room_type_name, description, room_type_category_id, max_occupancy, sort_order, status) VALUES 
-- 标准间系列 (1-8)
(1, 'STD_SINGLE', '标准单人间', '温馨单人间，适合单人商务出行', 1, 1, 1, 'active'),
(1, 'STD_TWIN', '标准双床间', '标准双床房，两张单人床', 1, 2, 2, 'active'),
(1, 'STD_DOUBLE', '标准大床房', '标准大床房，一张大床', 1, 2, 3, 'active'),
(1, 'STD_SUPERIOR', '高级标准间', '升级标准间，设施更完善', 1, 2, 4, 'active'),
(1, 'STD_ECONOMY', '经济标准间', '经济实惠型标准间', 1, 2, 5, 'active'),
(1, 'STD_ACCESSIBLE', '无障碍标准间', '专为行动不便人士设计的标准间', 1, 2, 6, 'active'),
(1, 'STD_SMOKING', '吸烟标准间', '允许吸烟的标准间', 1, 2, 7, 'inactive'),
(1, 'STD_NONSMOKING', '无烟标准间', '无烟标准间', 1, 2, 8, 'active'),

-- 豪华间系列 (9-16)
(1, 'DLX_SINGLE', '豪华单人间', '豪华单人间，高端配置', 2, 1, 9, 'active'),
(1, 'DLX_TWIN', '豪华双床间', '豪华双床房，高品质体验', 2, 2, 10, 'active'),
(1, 'DLX_DOUBLE', '豪华大床房', '豪华大床房，舒适享受', 2, 2, 11, 'active'),
(1, 'DLX_LAKEVIEW', '湖景豪华间', '可观赏湖景的豪华间', 2, 2, 12, 'active'),
(1, 'DLX_CITYVIEW', '城景豪华间', '可观赏城市景观的豪华间', 2, 2, 13, 'active'),
(1, 'DLX_OCEANVIEW', '海景豪华间', '可观赏海景的豪华间', 2, 2, 14, 'active'),
(1, 'DLX_CORNER', '转角豪华间', '转角位置，视野开阔', 2, 2, 15, 'active'),
(1, 'DLX_PRESIDENTIAL', '总统豪华间', '顶级豪华配置', 2, 2, 16, 'inactive'),

-- 套房系列 (17-24)
(1, 'STE_JUNIOR', '小型套房', '精致小型套房', 3, 2, 17, 'active'),
(1, 'STE_EXECUTIVE', '行政套房', '行政套房，独立客厅', 3, 2, 18, 'active'),
(1, 'STE_FAMILY', '家庭套房', '适合家庭入住的套房', 3, 4, 19, 'active'),
(1, 'STE_PRESIDENTIAL', '总统套房', '顶级总统套房，尊享服务', 3, 4, 20, 'active'),
(1, 'STE_ROYAL', '皇家套房', '皇家级别的奢华套房', 3, 4, 21, 'active'),
(1, 'STE_HONEYMOON', '蜜月套房', '专为蜜月夫妇设计', 3, 2, 22, 'active'),
(1, 'STE_BUSINESS', '商务套房', '商务出差首选套房', 3, 2, 23, 'active'),
(1, 'STE_DUPLEX', '复式套房', '双层复式结构', 3, 4, 24, 'active'),

-- 家庭房系列 (25-32)
(1, 'FAM_STANDARD', '标准家庭房', '标准家庭房，适合3-4人', 4, 4, 25, 'active'),
(1, 'FAM_LARGE', '大家庭房', '大型家庭房，适合5-6人', 4, 6, 26, 'active'),
(1, 'FAM_CONNECTING', '连通家庭房', '两间连通的房间', 4, 6, 27, 'active'),
(1, 'FAM_KIDS', '儿童友好家庭房', '配备儿童设施的家庭房', 4, 4, 28, 'active'),
(1, 'FAM_MULTIGEN', '多代家庭房', '适合多代同堂入住', 4, 8, 29, 'active'),
(1, 'FAM_BUNK', '双层床家庭房', '配备双层床的家庭房', 4, 4, 30, 'active'),
(1, 'FAM_PET', '宠物友好家庭房', '允许携带宠物的家庭房', 4, 4, 31, 'active'),
(1, 'FAM_LUXURY', '豪华家庭房', '豪华配置的家庭房', 4, 4, 32, 'active'),

-- 特色房系列 (33-40)
(1, 'SPEC_THEME', '主题房', '特色主题设计房间', 5, 2, 33, 'active'),
(1, 'SPEC_SPA', 'SPA房', '配备SPA设施的房间', 5, 2, 34, 'active'),
(1, 'SPEC_GOLF', '高尔夫房', '高尔夫景观房', 5, 2, 35, 'active'),
(1, 'SPEC_SKI', '滑雪房', '滑雪度假特色房', 5, 2, 36, 'active'),
(1, 'SPEC_BEACH', '海滩房', '海滩度假特色房', 5, 2, 37, 'active'),
(1, 'SPEC_MOUNTAIN', '山景房', '山景特色房', 5, 2, 38, 'active'),
(1, 'SPEC_ROMANTIC', '浪漫房', '浪漫氛围特色房', 5, 2, 39, 'active'),
(1, 'SPEC_WELLNESS', '养生房', '养生健康特色房', 5, 2, 40, 'active');

-- 显示插入完成信息
SELECT '数据插入完成！共插入40个集团房型数据。' AS insert_status;

-- 查看插入的数据
SELECT id, room_type_code, room_type_name, description, max_occupancy, sort_order, status 
FROM group_room_types 
ORDER BY sort_order;
