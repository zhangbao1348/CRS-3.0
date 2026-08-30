-- CRS 实体关联规范化 (CODE关联) - 数据库清理脚本
-- 目标：物理删除已在代码中弃用的冗余 ID 字段，强制实施基于 CODE 的多租户隔离。
-- 注意：本脚本包含自动查找并删除外键约束的逻辑，以避免 1828 错误。

-- 创建删除指定列外键的存储过程
DROP PROCEDURE IF EXISTS DropFKByColumn;
DELIMITER //
CREATE PROCEDURE DropFKByColumn(IN p_table_name VARCHAR(64), IN p_column_name VARCHAR(64))
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE fk_name VARCHAR(64);
    -- 查找引用了该列的所有外键
    DECLARE cur CURSOR FOR 
        SELECT CONSTRAINT_NAME 
        FROM information_schema.KEY_COLUMN_USAGE 
        WHERE TABLE_SCHEMA = DATABASE() 
          AND TABLE_NAME = p_table_name 
          AND COLUMN_NAME = p_column_name 
          AND REFERENCED_TABLE_NAME IS NOT NULL;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO fk_name;
        IF done THEN
            LEAVE read_loop;
        END IF;
        -- 动态执行删除外键语句
        SET @s = CONCAT('ALTER TABLE `', p_table_name, '` DROP FOREIGN KEY `', fk_name, '`');
        PREPARE stmt FROM @s;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END LOOP;
    CLOSE cur;
END //
DELIMITER ;

-- 1. 清理 reservation 表
CALL DropFKByColumn('reservation', 'hotel_id');
CALL DropFKByColumn('reservation', 'room_type_id');
CALL DropFKByColumn('reservation', 'rate_plan_id');
CALL DropFKByColumn('reservation', 'channel_id');
ALTER TABLE reservation 
    DROP COLUMN hotel_id,
    DROP COLUMN room_type_id,
    DROP COLUMN rate_plan_id,
    DROP COLUMN channel_id,
    DROP COLUMN market_code_id,
    DROP COLUMN source_code_id;

-- 2. 清理 inventory 表
CALL DropFKByColumn('inventory', 'hotel_id');
CALL DropFKByColumn('inventory', 'rate_plan_id');
CALL DropFKByColumn('inventory', 'room_type_id');
CALL DropFKByColumn('inventory', 'channel_id');
ALTER TABLE inventory 
    DROP COLUMN hotel_id,
    DROP COLUMN rate_plan_id,
    DROP COLUMN room_type_id,
    DROP COLUMN channel_id;

-- 3. 清理 base_prices 表
CALL DropFKByColumn('base_prices', 'hotel_id');
CALL DropFKByColumn('base_prices', 'rate_type_id');
CALL DropFKByColumn('base_prices', 'room_type_id');
ALTER TABLE base_prices 
    DROP COLUMN hotel_id,
    DROP COLUMN rate_type_id,
    DROP COLUMN room_type_id;

-- 4. 清理 rate_plans 表
CALL DropFKByColumn('rate_plans', 'hotel_id');
ALTER TABLE rate_plans 
    DROP COLUMN hotel_id;

-- 5. 清理 hotel_room_types 表
CALL DropFKByColumn('hotel_room_types', 'hotel_id');
CALL DropFKByColumn('hotel_room_types', 'group_room_type_id');
ALTER TABLE hotel_room_types 
    DROP COLUMN hotel_id,
    DROP COLUMN group_room_type_id,
    DROP COLUMN room_type_category_id;

-- 清理存储过程
DROP PROCEDURE IF EXISTS DropFKByColumn;

-- 验证脚本运行完成
SELECT 'CRS 冗余字段物理清理完成！已移除所有过时的 ID 关联字段。' AS result;
