-- Phase 3: Add CODE association fields to hotel management related tables

-- hotels
ALTER TABLE hotels ADD COLUMN IF NOT EXISTS tenant_code VARCHAR(50) COMMENT '租户CODE';

-- hotel_facilities
ALTER TABLE hotel_facilities ADD COLUMN IF NOT EXISTS hotel_code VARCHAR(50) COMMENT '酒店CODE';

-- hotel_images
ALTER TABLE hotel_images ADD COLUMN IF NOT EXISTS hotel_code VARCHAR(50) COMMENT '酒店CODE';

-- base_prices
ALTER TABLE base_prices ADD COLUMN IF NOT EXISTS hotel_code VARCHAR(50) COMMENT '酒店CODE';

-- room_types
ALTER TABLE room_types ADD COLUMN IF NOT EXISTS hotel_code VARCHAR(50) COMMENT '酒店CODE';

-- room_type_diff_systems
ALTER TABLE room_type_diff_systems ADD COLUMN IF NOT EXISTS hotel_code VARCHAR(50) COMMENT '酒店CODE';

-- person_diff_systems
ALTER TABLE person_diff_systems ADD COLUMN IF NOT EXISTS hotel_code VARCHAR(50) COMMENT '酒店CODE';

-- room_type_diffs
ALTER TABLE room_type_diffs ADD COLUMN IF NOT EXISTS room_type_code VARCHAR(50) COMMENT '房型CODE';

-- Add indexes
CREATE INDEX IF NOT EXISTS idx_hotels_tenant_code ON hotels(tenant_code);
CREATE INDEX IF NOT EXISTS idx_hf_hotel_code ON hotel_facilities(hotel_code);
CREATE INDEX IF NOT EXISTS idx_hi_hotel_code ON hotel_images(hotel_code);
CREATE INDEX IF NOT EXISTS idx_bp_hotel_code ON base_prices(hotel_code);
CREATE INDEX IF NOT EXISTS idx_rt_hotel_code ON room_types(hotel_code);
CREATE INDEX IF NOT EXISTS idx_rtds_hotel_code ON room_type_diff_systems(hotel_code);
CREATE INDEX IF NOT EXISTS idx_pds_hotel_code ON person_diff_systems(hotel_code);
