-- 插入包价测试数据
INSERT INTO packages (
    code, 
    name, 
    description, 
    status, 
    type, 
    quantity_type, 
    fixed_quantity, 
    frequency, 
    price_type, 
    fixed_price, 
    tax_included, 
    created_at, 
    updated_at
) VALUES 
-- 早餐包价
('BREAKFAST_SINGLE', '单人早餐', '包含单人早餐', 'active', '早餐', 'fixed', 1, '每天出现一次', 'group', 50.00, false, NOW(), NOW()),

-- 早餐包价
('BREAKFAST_DOUBLE', '双人早餐', '包含双人早餐', 'active', '早餐', 'fixed', 2, '每天出现一次', 'group', 80.00, false, NOW(), NOW()),

-- 午餐包价
('LUNCH_SET', '商务午餐', '包含商务午餐套餐', 'active', '午餐', 'fixed', 1, '每天出现一次', 'hotel', NULL, false, NOW(), NOW()),

-- 晚餐包价
('DINNER_BUFFET', '自助晚餐', '包含自助晚餐', 'active', '晚餐', 'per_person', NULL, '每天出现一次', 'group', 120.00, true, NOW(), NOW()),

-- 综合包价
('ALL_INCLUSIVE', '全包套餐', '包含三餐和下午茶', 'active', '综合', 'fixed', 1, '每次入住出现一次', 'group', 280.00, true, NOW(), NOW());