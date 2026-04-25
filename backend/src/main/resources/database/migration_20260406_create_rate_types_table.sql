-- 数据库迁移脚本 - 2026-04-06
-- 创建房价大类表 rate_types

USE CRS;

-- 创建房价大类表
CREATE TABLE IF NOT EXISTS rate_types (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '房价大类编码',
    name VARCHAR(100) NOT NULL COMMENT '房价大类名称',
    description TEXT COMMENT '描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_code (code),
    INDEX idx_status (status),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房价大类表';

-- 插入示例数据
INSERT IGNORE INTO rate_types (code, name, description, sort_order, status) VALUES
('BAR', '最佳可用房价', '酒店标准最佳可用房价', 1, 'active'),
('CORP', '企业协议价', '与企业签订的协议价格', 2, 'active'),
('PROMO', '促销价', '各类促销活动价格', 3, 'active'),
('GROUP', '团队价', '团队预订专用价格', 4, 'active'),
('PACKAGE', '包价', '包含其他服务的套餐价格', 5, 'active'),
('LONGSTAY', '长住价', '长期住宿优惠价格', 6, 'active'),
('SEASONAL', '季节价', '不同季节的价格', 7, 'active'),
('WEEKEND', '周末价', '周末专用价格', 8, 'active');

-- 显示迁移完成信息
SELECT '数据库迁移完成！rate_types 表已创建。' AS migration_status;
