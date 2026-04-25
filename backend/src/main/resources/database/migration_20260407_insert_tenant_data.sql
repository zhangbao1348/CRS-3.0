-- 插入模拟租户数据
INSERT INTO tenants (
    tenant_code, 
    tenant_name, 
    status, 
    expire_date, 
    contact_name, 
    contact_phone, 
    contact_email, 
    hotel_count, 
    address, 
    created_at, 
    updated_at
) VALUES 
-- 租户1: 万豪国际集团
('MARRIOT', '万豪国际集团', 'active', '2027-12-31', '张三', '13800138001', 'zhangsan@marriot.com', 5, '北京市朝阳区建国路88号', NOW(), NOW()),

-- 租户2: 希尔顿酒店集团
('HILTON', '希尔顿酒店集团', 'active', '2027-12-31', '李四', '13900139001', 'lisi@hilton.com', 3, '上海市浦东新区世纪大道100号', NOW(), NOW()),

-- 租户3: 洲际酒店集团
('IHG', '洲际酒店集团', 'active', '2027-12-31', '王五', '13700137001', 'wangwu@ihg.com', 4, '广州市天河区天河路385号', NOW(), NOW()),

-- 租户4: 凯悦酒店集团
('HYATT', '凯悦酒店集团', 'active', '2027-12-31', '赵六', '13600136001', 'zhaoliu@hyatt.com', 2, '深圳市福田区福华路355号', NOW(), NOW()),

-- 租户5: 雅高酒店集团
('ACCOR', '雅高酒店集团', 'active', '2027-12-31', '孙七', '13500135001', 'sunqi@accor.com', 3, '成都市锦江区红星路三段1号', NOW(), NOW());