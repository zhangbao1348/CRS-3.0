-- 迁移脚本：为所有实体添加CODE字段，支持CODE关联查询
-- 日期：2026-05-05
-- 注意：本系统中 Group 实际对应 Tenant，group_id = tenant_id

-- ============================================================
-- 1. BasePrice - 添加 rate_type_code, room_type_code
-- ============================================================
SET @dbname = DATABASE();
SET @tablename = 'base_prices';

SET @colname = 'rate_type_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE base_prices ADD COLUMN rate_type_code VARCHAR(50)'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @colname = 'room_type_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE base_prices ADD COLUMN room_type_code VARCHAR(50)'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

UPDATE base_prices bp
INNER JOIN rate_types rt ON bp.rate_type_id = rt.id
SET bp.rate_type_code = rt.code
WHERE bp.rate_type_code IS NULL;

UPDATE base_prices bp
INNER JOIN room_types rt ON bp.room_type_id = rt.id
SET bp.room_type_code = rt.code
WHERE bp.room_type_code IS NULL;

-- ============================================================
-- 2. GroupRoomType - 添加 group_code, room_type_category_code
-- ============================================================
SET @tablename = 'group_room_types';

SET @colname = 'group_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE group_room_types ADD COLUMN group_code VARCHAR(50)'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @colname = 'room_type_category_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE group_room_types ADD COLUMN room_type_category_code VARCHAR(50)'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- group_id 对应 tenants.id, group_code 对应 tenants.tenant_code
UPDATE group_room_types grt
INNER JOIN tenants t ON grt.group_id = t.id
SET grt.group_code = t.tenant_code
WHERE grt.group_code IS NULL;

UPDATE group_room_types grt
INNER JOIN room_type_categories rtc ON grt.room_type_category_id = rtc.id
SET grt.room_type_category_code = rtc.category_code
WHERE grt.room_type_category_code IS NULL AND grt.room_type_category_id IS NOT NULL;

-- ============================================================
-- 3. ChannelCode - 添加 parent_code
-- ============================================================
SET @tablename = 'channel_codes';
SET @colname = 'parent_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE channel_codes ADD COLUMN parent_code VARCHAR(50)'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

UPDATE channel_codes cc
INNER JOIN channel_codes parent ON cc.parent_id = parent.id
SET cc.parent_code = parent.code
WHERE cc.parent_id IS NOT NULL AND cc.parent_code IS NULL;

-- ============================================================
-- 4. SourceCode - 添加 parent_code
-- ============================================================
SET @tablename = 'source_codes';
SET @colname = 'parent_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE source_codes ADD COLUMN parent_code VARCHAR(50)'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

UPDATE source_codes sc
INNER JOIN source_codes parent ON sc.parent_id = parent.id
SET sc.parent_code = parent.code
WHERE sc.parent_id IS NOT NULL AND sc.parent_code IS NULL;

-- ============================================================
-- 5. MarketCode - 添加 parent_code
-- ============================================================
SET @tablename = 'market_codes';
SET @colname = 'parent_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE market_codes ADD COLUMN parent_code VARCHAR(50)'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

UPDATE market_codes mc
INNER JOIN market_codes parent ON mc.parent_id = parent.id
SET mc.parent_code = parent.code
WHERE mc.parent_id IS NOT NULL AND mc.parent_code IS NULL;

-- ============================================================
-- 6. RoomType - 添加 group_room_type_code
-- ============================================================
SET @tablename = 'room_types';
SET @colname = 'group_room_type_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE room_types ADD COLUMN group_room_type_code VARCHAR(50)'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

UPDATE room_types rt
INNER JOIN group_room_types grt ON rt.group_room_type_id = grt.id
SET rt.group_room_type_code = grt.room_type_code
WHERE rt.group_room_type_id IS NOT NULL AND rt.group_room_type_code IS NULL;

-- ============================================================
-- 7. RoomTypeCategory - 添加 group_code
-- ============================================================
SET @tablename = 'room_type_categories';
SET @colname = 'group_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE room_type_categories ADD COLUMN group_code VARCHAR(50)'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- group_id 对应 tenants.id
UPDATE room_type_categories rtc
INNER JOIN tenants t ON rtc.group_id = t.id
SET rtc.group_code = t.tenant_code
WHERE rtc.group_code IS NULL;

-- ============================================================
-- 8. GroupRateCode - 添加 group_code
-- ============================================================
SET @tablename = 'group_rate_codes';
SET @colname = 'group_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE group_rate_codes ADD COLUMN group_code VARCHAR(50)'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- group_id 对应 tenants.id
UPDATE group_rate_codes grc
INNER JOIN tenants t ON grc.group_id = t.id
SET grc.group_code = t.tenant_code
WHERE grc.group_code IS NULL;

-- ============================================================
-- 9. RatePlan - 添加 room_type_diff_code, person_diff_code
-- 注意：依赖步骤12和13先添加code列
-- ============================================================

-- 先添加 RoomTypeDiffSystem.code 和 PersonDiffSystem.code
SET @tablename = 'room_type_diff_systems';
SET @colname = 'code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE room_type_diff_systems ADD COLUMN code VARCHAR(50)'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

UPDATE room_type_diff_systems SET code = CONCAT('RTDS_', id) WHERE code IS NULL;

SET @tablename = 'person_diff_systems';
SET @colname = 'code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE person_diff_systems ADD COLUMN code VARCHAR(50)'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

UPDATE person_diff_systems SET code = CONCAT('PDS_', id) WHERE code IS NULL;

-- 现在添加 RatePlan 的 CODE 字段
SET @tablename = 'rate_plans';

SET @colname = 'room_type_diff_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE rate_plans ADD COLUMN room_type_diff_code VARCHAR(50)'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @colname = 'person_diff_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE rate_plans ADD COLUMN person_diff_code VARCHAR(50)'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

UPDATE rate_plans rp
INNER JOIN room_type_diff_systems rtds ON rp.room_type_diff_id = rtds.id
SET rp.room_type_diff_code = rtds.code
WHERE rp.room_type_diff_id IS NOT NULL AND rp.room_type_diff_code IS NULL;

UPDATE rate_plans rp
INNER JOIN person_diff_systems pds ON rp.person_diff_id = pds.id
SET rp.person_diff_code = pds.code
WHERE rp.person_diff_id IS NOT NULL AND rp.person_diff_code IS NULL;

-- ============================================================
-- 10. RoomTypeDiff - 添加 system_code
-- ============================================================
SET @tablename = 'room_type_diffs';
SET @colname = 'system_code';
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = @colname) > 0,
  'SELECT 1',
  'ALTER TABLE room_type_diffs ADD COLUMN system_code VARCHAR(50)'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

UPDATE room_type_diffs rtd
INNER JOIN room_type_diff_systems rtds ON rtd.system_id = rtds.id
SET rtd.system_code = rtds.code
WHERE rtd.system_code IS NULL;

-- 注意：person_diffs 表当前不存在，跳过其 system_code 回填
-- 当该表创建后，需确保 system_code 字段同步填充
