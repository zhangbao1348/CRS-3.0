CREATE TABLE IF NOT EXISTS dictionary_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tenant_id INT NOT NULL COMMENT '租户ID',
    type_code VARCHAR(50) NOT NULL COMMENT '字典类型编码',
    type_name VARCHAR(100) NOT NULL COMMENT '字典类型名称',
    description TEXT COMMENT '描述',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态：active/inactive',
    built_in TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否内置',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_dictionary_type (tenant_id, type_code),
    INDEX idx_dictionary_type_status (tenant_id, status),
    INDEX idx_dictionary_type_sort (tenant_id, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

CREATE TABLE IF NOT EXISTS dictionary_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tenant_id INT NOT NULL COMMENT '租户ID',
    type_code VARCHAR(50) NOT NULL COMMENT '字典类型编码',
    item_code VARCHAR(50) NOT NULL COMMENT '字典项编码',
    item_name VARCHAR(100) NOT NULL COMMENT '字典项名称',
    item_value VARCHAR(100) DEFAULT NULL COMMENT '字典项值',
    description TEXT COMMENT '描述',
    is_default TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态：active/inactive',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_dictionary_item (tenant_id, type_code, item_code),
    INDEX idx_dictionary_item_query (tenant_id, type_code, status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典项表';
