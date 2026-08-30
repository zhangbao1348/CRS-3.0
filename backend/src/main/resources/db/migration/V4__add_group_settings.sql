CREATE TABLE IF NOT EXISTS `group_settings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int NOT NULL,
  `group_control_mode` varchar(20) NOT NULL DEFAULT 'strong',
  `hourly_room` varchar(20) NOT NULL DEFAULT 'support',
  `ota_promotion_mode` varchar(50) NOT NULL DEFAULT 'groupRegistration',
  `show_ctrip_price` bit(1) NOT NULL DEFAULT b'0',
  `show_meituan_price` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_settings_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
