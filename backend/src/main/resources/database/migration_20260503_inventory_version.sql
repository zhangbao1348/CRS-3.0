-- 为 pms_inventory 和 inventory_quota 增加 version 字段（乐观锁）
ALTER TABLE pms_inventory ADD COLUMN IF NOT EXISTS version INT NOT NULL DEFAULT 0;
ALTER TABLE inventory_quota ADD COLUMN IF NOT EXISTS version INT NOT NULL DEFAULT 0;
