-- 使用CRS数据库
USE CRS;

-- 创建租户表
CREATE TABLE IF NOT EXISTS tenants (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    tenant_code VARCHAR(50) NOT NULL UNIQUE COMMENT '租户代码',
    tenant_name VARCHAR(100) NOT NULL COMMENT '租户名称',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    expire_date DATE COMMENT '到期日期',
    contact_name VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    contact_email VARCHAR(100) COMMENT '电子邮箱',
    hotel_count INT DEFAULT 0 COMMENT '酒店数量',
    address TEXT COMMENT '地址',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_tenant_code (tenant_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户表';

-- 创建用户表（更新版，增加tenant_id关联）
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    tenant_id INT COMMENT '租户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    role VARCHAR(20) NOT NULL COMMENT '角色',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_username (username),
    INDEX idx_status (status),
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入初始租户数据
INSERT INTO tenants (tenant_code, tenant_name, status, expire_date, contact_name, contact_phone, contact_email, hotel_count, address) VALUES
('JINJIANG', '锦江酒店集团', 'active', '2027-12-31', '张经理', '13800138001', 'zhang@jinjiang.com', 20, '上海市浦东新区'),
('HUAZHU', '华住酒店集团', 'active', '2027-06-30', '李经理', '13800138002', 'li@huazhu.com', 15, '上海市长宁区');

-- 插入初始用户数据（密码都是admin123）
INSERT INTO users (tenant_id, username, password, name, phone, email, role, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', '13800138000', 'admin@jinjiang.com', 'admin', 'active'),
(1, 'zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张三', '13800138001', 'zhangsan@jinjiang.com', 'operator', 'active');
