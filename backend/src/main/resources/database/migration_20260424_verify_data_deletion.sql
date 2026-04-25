-- 验证数据删除结果

USE CRS;

-- =============================================
-- 检查表数据数量的存储过程
-- =============================================
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS CheckTableRowCount(IN tableName VARCHAR(100))
BEGIN
    DECLARE tableExists INT;
    DECLARE rowCount INT;
    
    SELECT COUNT(*) INTO tableExists 
    FROM INFORMATION_SCHEMA.TABLES 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = tableName;
    
    IF tableExists > 0 THEN
        SET @sql = CONCAT('SELECT COUNT(*) INTO @rowCount FROM `', tableName, '`');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        
        SELECT tableName AS '表名', @rowCount AS '数据行数';
    ELSE
        SELECT tableName AS '表名', '表不存在' AS '数据行数';
    END IF;
END //
DELIMITER ;

-- =============================================
-- 验证各表数据
-- =============================================
CALL CheckTableRowCount('hotel_price_logs');
CALL CheckTableRowCount('hotel_prices');
CALL CheckTableRowCount('base_prices');
CALL CheckTableRowCount('rate_plans');
CALL CheckTableRowCount('group_room_type_hotel');
CALL CheckTableRowCount('hotel_rate_code_allocations');
CALL CheckTableRowCount('hotel_room_types');
CALL CheckTableRowCount('channel_rate_code_mappings');
CALL CheckTableRowCount('channel_room_type_mappings');
CALL CheckTableRowCount('channel_hotel_mappings');

-- =============================================
-- 验证集团表数据是否保留（应该保留）
-- =============================================
CALL CheckTableRowCount('group_room_types');
CALL CheckTableRowCount('group_rate_codes');
CALL CheckTableRowCount('hotels');
CALL CheckTableRowCount('groups');

-- =============================================
-- 清理存储过程
-- =============================================
DROP PROCEDURE IF EXISTS CheckTableRowCount;

-- =============================================
-- 显示完成信息
-- =============================================
SELECT '数据验证完成！' AS final_result;
