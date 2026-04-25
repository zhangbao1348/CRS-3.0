-- Phase 4+5: Add CODE association fields to inventory, reservation, channel mapping tables

-- inventory
ALTER TABLE inventory ADD COLUMN IF NOT EXISTS hotel_code VARCHAR(50) COMMENT '酒店CODE';
ALTER TABLE inventory ADD COLUMN IF NOT EXISTS rate_plan_code VARCHAR(50) COMMENT '价格计划CODE';
ALTER TABLE inventory ADD COLUMN IF NOT EXISTS room_type_code VARCHAR(50) COMMENT '房型CODE';
ALTER TABLE inventory ADD COLUMN IF NOT EXISTS channel_code VARCHAR(50) COMMENT '渠道CODE';

-- reservation
ALTER TABLE reservation ADD COLUMN IF NOT EXISTS hotel_code VARCHAR(50) COMMENT '酒店CODE';
ALTER TABLE reservation ADD COLUMN IF NOT EXISTS rate_plan_code VARCHAR(50) COMMENT '价格计划CODE';
ALTER TABLE reservation ADD COLUMN IF NOT EXISTS room_type_code VARCHAR(50) COMMENT '房型CODE';
ALTER TABLE reservation ADD COLUMN IF NOT EXISTS channel_code VARCHAR(50) COMMENT '渠道CODE';
ALTER TABLE reservation ADD COLUMN IF NOT EXISTS market_code VARCHAR(50) COMMENT '市场码CODE';
ALTER TABLE reservation ADD COLUMN IF NOT EXISTS source_code VARCHAR(50) COMMENT '来源码CODE';

-- channel_hotel_mappings
ALTER TABLE channel_hotel_mappings ADD COLUMN IF NOT EXISTS channel_code VARCHAR(50) COMMENT '渠道CODE';

-- channel_room_type_mappings
ALTER TABLE channel_room_type_mappings ADD COLUMN IF NOT EXISTS channel_code VARCHAR(50) COMMENT '渠道CODE';
ALTER TABLE channel_room_type_mappings ADD COLUMN IF NOT EXISTS hotel_code VARCHAR(50) COMMENT '酒店CODE';

-- channel_rate_code_mappings
ALTER TABLE channel_rate_code_mappings ADD COLUMN IF NOT EXISTS channel_code VARCHAR(50) COMMENT '渠道CODE';
ALTER TABLE channel_rate_code_mappings ADD COLUMN IF NOT EXISTS hotel_code VARCHAR(50) COMMENT '酒店CODE';

-- rate_plan_packages
ALTER TABLE rate_plan_packages ADD COLUMN IF NOT EXISTS rate_plan_code VARCHAR(50) COMMENT '价格计划CODE';
ALTER TABLE rate_plan_packages ADD COLUMN IF NOT EXISTS package_code VARCHAR(50) COMMENT '包价CODE';

-- Add indexes
CREATE INDEX IF NOT EXISTS idx_inv_hotel_code ON inventory(hotel_code);
CREATE INDEX IF NOT EXISTS idx_inv_room_type_code ON inventory(room_type_code);
CREATE INDEX IF NOT EXISTS idx_res_hotel_code ON reservation(hotel_code);
CREATE INDEX IF NOT EXISTS idx_res_channel_code ON reservation(channel_code);
CREATE INDEX IF NOT EXISTS idx_chm_channel_code ON channel_hotel_mappings(channel_code);
CREATE INDEX IF NOT EXISTS idx_crtm_channel_code ON channel_room_type_mappings(channel_code);
CREATE INDEX IF NOT EXISTS idx_crcm_channel_code ON channel_rate_code_mappings(channel_code);
