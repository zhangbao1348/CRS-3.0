-- 删除酒店价格计划数据、价格数据和集团下发数据
-- 执行前请确认需要保留集团本身的数据

USE CRS;

-- =============================================
-- 安全删除表数据的存储过程
-- =============================================
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS SafeDeleteFromTable(IN tableName VARCHAR(100))
BEGIN
    DECLARE tableExists INT;
    SELECT COUNT(*) INTO tableExists 
    FROM INFORMATION_SCHEMA.TABLES 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = tableName;
    
    IF tableExists > 0 THEN
        SET @sql = CONCAT('DELETE FROM `', tableName, '`');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SELECT CONCAT(tableName, ' 表数据已删除') AS result;
    ELSE
        SELECT CONCAT(tableName, ' 表不存在，跳过') AS result;
    END IF;
END //
DELIMITER ;

-- =============================================
-- 1. 删除酒店价格日志数据
-- =============================================
CALL SafeDeleteFromTable('hotel_price_logs');

-- =============================================
-- 2. 删除酒店价格数据
-- =============================================
CALL SafeDeleteFromTable('hotel_prices');

-- =============================================
-- 3. 删除基础价格数据
-- =============================================
CALL SafeDeleteFromTable('base_prices');

-- =============================================
-- 4. 删除库存数据
-- =============================================
CALL SafeDeleteFromTable('inventories');

-- =============================================
-- 5. 删除价格计划数据
-- =============================================
CALL SafeDeleteFromTable('rate_plans');

-- =============================================
-- 6. 删除集团房型下发数据
-- =============================================
CALL SafeDeleteFromTable('group_room_type_hotel');

-- =============================================
-- 7. 删除酒店房价码分配数据（集团下发）
-- =============================================
CALL SafeDeleteFromTable('hotel_rate_code_allocations');

-- =============================================
-- 8. 删除酒店房型数据（包含集团下发的）
-- =============================================
CALL SafeDeleteFromTable('hotel_room_types');

-- =============================================
-- 9. 删除酒店房型分配数据
-- =============================================
CALL SafeDeleteFromTable('hotel_room_type_allocations');

-- =============================================
-- 10. 删除渠道映射相关数据
-- =============================================
CALL SafeDeleteFromTable('channel_rate_code_mappings');
CALL SafeDeleteFromTable('channel_room_type_mappings');
CALL SafeDeleteFromTable('channel_hotel_mappings');

-- =============================================
-- 清理存储过程
-- =============================================
DROP PROCEDURE IF EXISTS SafeDeleteFromTable;

-- =============================================
-- 显示完成信息
-- =============================================
SELECT '所有酒店价格计划数据、价格数据和集团下发数据删除完成！' AS final_result;
