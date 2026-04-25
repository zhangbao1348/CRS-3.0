-- 创建税和服务费设置表
-- 用于存储各租户的税率和服务费配置

USE CRS;

-- 创建税和服务费设置表
CREATE TABLE IF NOT EXISTS tax_settings (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_id INT NOT NULL COMMENT '租户ID',
    tax_code VARCHAR(50) NOT NULL COMMENT '税率编码',
    legal_name VARCHAR(100) NOT NULL COMMENT '税费法定全称',
    bearer VARCHAR(20) COMMENT '税费承担主体',
    base_type VARCHAR(50) COMMENT '计税基数类型',
    rate_amount DECIMAL(10, 4) COMMENT '税率/定额标准',
    rate_currency VARCHAR(10) COMMENT '税率货币单位',
    calculation_rule VARCHAR(20) COMMENT '计税计算规则',
    deductible VARCHAR(10) COMMENT '是否可进项抵扣',
    refundable VARCHAR(20) COMMENT '取消订单是否可退',
    settlement_rule VARCHAR(50) COMMENT '结算缴纳规则',
    compliance_requirements TEXT COMMENT '合规要求',
    remarks TEXT COMMENT '备注',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_tax_code (tax_code),
    INDEX idx_status (status),
    UNIQUE KEY uk_tenant_tax_code (tenant_id, tax_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='税和服务费设置表';

-- 显示完成信息
SELECT '税和服务费设置表创建完成！' AS result;
