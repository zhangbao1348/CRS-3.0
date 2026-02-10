-- 更新packages表结构，添加包价相关字段
ALTER TABLE packages
ADD COLUMN `type` VARCHAR(50) NOT NULL AFTER `status`,
ADD COLUMN `quantity_type` VARCHAR(20) NOT NULL AFTER `type`,
ADD COLUMN `fixed_quantity` INT DEFAULT NULL AFTER `quantity_type`,
ADD COLUMN `frequency` VARCHAR(50) NOT NULL AFTER `fixed_quantity`,
ADD COLUMN `price_type` VARCHAR(20) NOT NULL AFTER `frequency`,
ADD COLUMN `fixed_price` DOUBLE DEFAULT NULL AFTER `price_type`,
ADD COLUMN `tax_included` BOOLEAN NOT NULL DEFAULT FALSE AFTER `fixed_price`;