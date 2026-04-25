-- Phase 2: Add CODE association fields to group room type related tables

-- group_room_types
ALTER TABLE group_room_types ADD COLUMN IF NOT EXISTS tenant_code VARCHAR(50) COMMENT '租户CODE';
ALTER TABLE group_room_types ADD COLUMN IF NOT EXISTS room_type_category_code VARCHAR(50) COMMENT '房型大类CODE';

-- group_room_type_hotel
ALTER TABLE group_room_type_hotel ADD COLUMN IF NOT EXISTS group_room_type_code VARCHAR(50) COMMENT '集团房型CODE';
ALTER TABLE group_room_type_hotel ADD COLUMN IF NOT EXISTS hotel_code VARCHAR(50) COMMENT '酒店CODE';

-- hotel_room_types
ALTER TABLE hotel_room_types ADD COLUMN IF NOT EXISTS hotel_code VARCHAR(50) COMMENT '酒店CODE';
ALTER TABLE hotel_room_types ADD COLUMN IF NOT EXISTS group_room_type_code VARCHAR(50) COMMENT '集团房型CODE';
ALTER TABLE hotel_room_types ADD COLUMN IF NOT EXISTS room_type_category_code VARCHAR(50) COMMENT '房型大类CODE';

-- hotel_room_type_allocations
ALTER TABLE hotel_room_type_allocations ADD COLUMN IF NOT EXISTS hotel_code VARCHAR(50) COMMENT '酒店CODE';
ALTER TABLE hotel_room_type_allocations ADD COLUMN IF NOT EXISTS room_type_code VARCHAR(50) COMMENT '房型CODE';

-- Add indexes
CREATE INDEX IF NOT EXISTS idx_grt_tenant_code ON group_room_types(tenant_code);
CREATE INDEX IF NOT EXISTS idx_grth_hotel_code ON group_room_type_hotel(hotel_code);
CREATE INDEX IF NOT EXISTS idx_grth_grt_code ON group_room_type_hotel(group_room_type_code);
CREATE INDEX IF NOT EXISTS idx_hrt_hotel_code ON hotel_room_types(hotel_code);
CREATE INDEX IF NOT EXISTS idx_hrta_hotel_code ON hotel_room_type_allocations(hotel_code);
