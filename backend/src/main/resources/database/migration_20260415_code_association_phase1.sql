-- Phase 1: Add CODE association fields to group_rate_codes and rate_plans
-- This migration adds CODE-based fields alongside existing ID-based fields for backward compatibility.
-- Existing ID fields are NOT removed.

-- group_rate_codes: add CODE fields
ALTER TABLE group_rate_codes ADD COLUMN IF NOT EXISTS tenant_code VARCHAR(50) COMMENT '租户CODE';
ALTER TABLE group_rate_codes ADD COLUMN IF NOT EXISTS market_code VARCHAR(50) COMMENT '市场码CODE';
ALTER TABLE group_rate_codes ADD COLUMN IF NOT EXISTS source_code VARCHAR(50) COMMENT '来源码CODE';
ALTER TABLE group_rate_codes ADD COLUMN IF NOT EXISTS parent_rate_code VARCHAR(50) COMMENT '父级房价码CODE';

-- rate_plans: add CODE fields
ALTER TABLE rate_plans ADD COLUMN IF NOT EXISTS hotel_code VARCHAR(50) COMMENT '酒店CODE';
ALTER TABLE rate_plans ADD COLUMN IF NOT EXISTS source_group_rate_code VARCHAR(50) COMMENT '来源集团房价码CODE';
ALTER TABLE rate_plans ADD COLUMN IF NOT EXISTS market_code VARCHAR(50) COMMENT '市场码CODE';
ALTER TABLE rate_plans ADD COLUMN IF NOT EXISTS source_code VARCHAR(50) COMMENT '来源码CODE';
ALTER TABLE rate_plans ADD COLUMN IF NOT EXISTS parent_rate_code VARCHAR(50) COMMENT '父级房价码CODE';

-- Add indexes
CREATE INDEX IF NOT EXISTS idx_group_rate_codes_tenant_code ON group_rate_codes(tenant_code);
CREATE INDEX IF NOT EXISTS idx_group_rate_codes_market_code ON group_rate_codes(market_code);
CREATE INDEX IF NOT EXISTS idx_rate_plans_hotel_code ON rate_plans(hotel_code);
CREATE INDEX IF NOT EXISTS idx_rate_plans_source_group_rate_code ON rate_plans(source_group_rate_code);
