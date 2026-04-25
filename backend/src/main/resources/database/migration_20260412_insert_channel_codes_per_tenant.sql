-- 为每个租户插入多个渠道码

USE CRS;

-- 存储过程：为指定租户插入渠道码
DELIMITER //
CREATE PROCEDURE InsertChannelCodesForTenant(IN p_tenant_id INT)
BEGIN
    DECLARE v_online_id INT;
    DECLARE v_offline_id INT;
    DECLARE v_ota_id INT;
    DECLARE v_direct_id INT;
    DECLARE v_travel_id INT;
    DECLARE v_corp_id INT;
    
    -- 插入一级节点：在线渠道
    INSERT IGNORE INTO channel_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) 
    VALUES (p_tenant_id, 'ONLINE', '在线渠道', '在线销售渠道', NULL, 1, 'active', NOW(), NOW());
    SELECT id INTO v_online_id FROM channel_codes WHERE tenant_id = p_tenant_id AND code = 'ONLINE';
    
    -- 插入一级节点：线下渠道
    INSERT IGNORE INTO channel_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) 
    VALUES (p_tenant_id, 'OFFLINE', '线下渠道', '线下销售渠道', NULL, 1, 'active', NOW(), NOW());
    SELECT id INTO v_offline_id FROM channel_codes WHERE tenant_id = p_tenant_id AND code = 'OFFLINE';
    
    -- 插入二级节点：OTA渠道
    INSERT IGNORE INTO channel_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) 
    VALUES (p_tenant_id, 'OTA', 'OTA渠道', '在线旅行社渠道', v_online_id, 2, 'active', NOW(), NOW());
    SELECT id INTO v_ota_id FROM channel_codes WHERE tenant_id = p_tenant_id AND code = 'OTA';
    
    -- 插入二级节点：直销渠道
    INSERT IGNORE INTO channel_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) 
    VALUES (p_tenant_id, 'DIRECT', '直销渠道', '直接销售渠道', v_online_id, 2, 'active', NOW(), NOW());
    SELECT id INTO v_direct_id FROM channel_codes WHERE tenant_id = p_tenant_id AND code = 'DIRECT';
    
    -- 插入二级节点：旅行社
    INSERT IGNORE INTO channel_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) 
    VALUES (p_tenant_id, 'TRAVEL', '旅行社', '旅行社渠道', v_offline_id, 2, 'active', NOW(), NOW());
    SELECT id INTO v_travel_id FROM channel_codes WHERE tenant_id = p_tenant_id AND code = 'TRAVEL';
    
    -- 插入二级节点：企业协议
    INSERT IGNORE INTO channel_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) 
    VALUES (p_tenant_id, 'CORP', '企业协议', '企业协议渠道', v_offline_id, 2, 'active', NOW(), NOW());
    SELECT id INTO v_corp_id FROM channel_codes WHERE tenant_id = p_tenant_id AND code = 'CORP';
    
    -- 插入三级节点：OTA渠道下的子节点
    INSERT IGNORE INTO channel_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
    (p_tenant_id, 'CTRIP', '携程', '携程旅行网', v_ota_id, 3, 'active', NOW(), NOW()),
    (p_tenant_id, 'MEITUAN', '美团', '美团酒店', v_ota_id, 3, 'active', NOW(), NOW()),
    (p_tenant_id, 'FLIGGY', '飞猪', '飞猪旅行', v_ota_id, 3, 'active', NOW(), NOW());
    
    -- 插入三级节点：直销渠道下的子节点
    INSERT IGNORE INTO channel_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
    (p_tenant_id, 'WEBSITE', '官网', '官方网站', v_direct_id, 3, 'active', NOW(), NOW()),
    (p_tenant_id, 'APP', 'APP', '手机应用', v_direct_id, 3, 'active', NOW(), NOW()),
    (p_tenant_id, 'CALLCENTER', '呼叫中心', '电话预订', v_direct_id, 3, 'active', NOW(), NOW()),
    (p_tenant_id, 'WXMINI', '微信小程序', '微信小程序', v_direct_id, 3, 'active', NOW(), NOW());
    
    -- 插入三级节点：旅行社渠道下的子节点
    INSERT IGNORE INTO channel_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
    (p_tenant_id, 'CTS', '中国旅行社', '中国旅行社总社', v_travel_id, 3, 'active', NOW(), NOW()),
    (p_tenant_id, 'CYTS', '中青旅', '中国青年旅行社', v_travel_id, 3, 'active', NOW(), NOW()),
    (p_tenant_id, 'CITS', '国旅', '中国国际旅行社', v_travel_id, 3, 'active', NOW(), NOW());
    
    -- 插入三级节点：企业协议渠道下的子节点
    INSERT IGNORE INTO channel_codes (tenant_id, code, name, description, parent_id, level, status, created_at, updated_at) VALUES
    (p_tenant_id, 'FORTUNE500', '世界500强', '世界500强企业协议', v_corp_id, 3, 'active', NOW(), NOW()),
    (p_tenant_id, 'GOV', '政府协议', '政府机关协议', v_corp_id, 3, 'active', NOW(), NOW()),
    (p_tenant_id, 'MICE', 'MICE协议', '会议展览协议', v_corp_id, 3, 'active', NOW(), NOW());
END //
DELIMITER ;

-- 为每个租户调用存储过程插入渠道码
CALL InsertChannelCodesForTenant(1);
CALL InsertChannelCodesForTenant(2);
CALL InsertChannelCodesForTenant(3);

-- 清理存储过程
DROP PROCEDURE IF EXISTS InsertChannelCodesForTenant;

-- 显示完成信息
SELECT CONCAT('渠道码数据插入完成！共为 ', COUNT(DISTINCT tenant_id), ' 个租户插入 ', COUNT(*), ' 条渠道码数据') AS result 
FROM channel_codes;

-- 查看各租户的渠道码数量
SELECT tenant_id AS 租户ID, COUNT(*) AS 渠道码数量 
FROM channel_codes 
GROUP BY tenant_id 
ORDER BY tenant_id;
