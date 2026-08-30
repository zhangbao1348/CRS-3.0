-- CRS Flyway V1 baseline：由 2026-08-25 已验证的 schema-only 备份生成。
-- 现有非空数据库通过 baseline-on-migrate 只登记 V1；仅空数据库执行本文件。
-- MySQL dump 10.13  Distrib 9.6.0, for macos26.3 (arm64)
--
-- Host: 127.0.0.1    Database: CRS
-- ------------------------------------------------------
-- Server version	9.6.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `api_logs`
--

DROP TABLE IF EXISTS `api_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `api_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `reservation_id` int DEFAULT NULL COMMENT '关联预订ID',
  `request_body` text COMMENT '入参JSON',
  `response_body` text COMMENT '出参JSON',
  `error_message` text COMMENT '失败原因',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_api_logs_reservation_id` (`reservation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='接口日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `archives`
--

DROP TABLE IF EXISTS `archives`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `archives` (
  `id` int NOT NULL AUTO_INCREMENT,
  `archive_id` varchar(50) DEFAULT NULL COMMENT 'æ¡£æ¡ˆID',
  `tenant_id` int DEFAULT NULL,
  `name` varchar(100) NOT NULL COMMENT '档案名称',
  `type` varchar(50) NOT NULL COMMENT '档案类型：公司/个人',
  `company_name` varchar(200) DEFAULT NULL COMMENT '公司名称',
  `company_tax_number` varchar(50) DEFAULT NULL COMMENT '公司税号',
  `member_number` varchar(50) DEFAULT NULL COMMENT '会员号',
  `member_level` varchar(50) DEFAULT NULL COMMENT '会员等级',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `contact_email` varchar(100) DEFAULT NULL COMMENT '联系邮箱',
  `address` text COMMENT '地址',
  `description` text COMMENT '描述',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态',
  `group_id` int DEFAULT NULL COMMENT '集团ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `booking_code` varchar(50) DEFAULT NULL,
  `rate_codes` text,
  PRIMARY KEY (`id`),
  KEY `idx_archives_group_id` (`group_id`),
  KEY `idx_archives_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `base_prices`
--

DROP TABLE IF EXISTS `base_prices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_prices` (
  `base_price` double NOT NULL,
  `date` date NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `price` double NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `status` enum('active','inactive') COLLATE utf8mb4_unicode_ci NOT NULL,
  `rate_type_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `room_type_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_hotel_date` (`tenant_id`,`hotel_code`,`date`),
  KEY `FKt21v4h57vy0d0kgh84jplnxme` (`hotel_code`),
  CONSTRAINT `FKt21v4h57vy0d0kgh84jplnxme` FOREIGN KEY (`hotel_code`) REFERENCES `hotels` (`hotel_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `booking_control_logs`
--

DROP TABLE IF EXISTS `booking_control_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_control_logs` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` int NOT NULL COMMENT '租户ID',
  `hotel_code` varchar(50) NOT NULL COMMENT '酒店CODE',
  `dimension_type` varchar(20) NOT NULL COMMENT '维度类型',
  `dimension_code` varchar(50) NOT NULL DEFAULT '' COMMENT '维度值',
  `operator_name` varchar(100) NOT NULL COMMENT '操作人',
  `operation_type` varchar(20) NOT NULL COMMENT '操作类型：single/batch',
  `operation_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `detail` text COMMENT '操作明细JSON',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_hotel` (`tenant_id`,`hotel_code`),
  KEY `idx_dimension` (`dimension_type`,`dimension_code`),
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预订控制操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `booking_controls`
--

DROP TABLE IF EXISTS `booking_controls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_controls` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` int NOT NULL COMMENT '租户ID',
  `hotel_code` varchar(50) NOT NULL COMMENT '酒店CODE',
  `dimension_type` varchar(20) NOT NULL COMMENT '维度类型：hotel/rate/channel/rate_category/market',
  `dimension_code` varchar(50) NOT NULL DEFAULT '' COMMENT '维度值：酒店维度为空，其他为对应CODE',
  `control_date` date NOT NULL COMMENT '日期',
  `cancellation_rule` varchar(20) DEFAULT 'free' COMMENT '取消规则：free/timed/non_refundable',
  `advance_booking_days` int DEFAULT '0' COMMENT '提前预订天数',
  `min_stay` int DEFAULT '1' COMMENT '最小连住天数',
  `max_stay` int DEFAULT '30' COMMENT '最大连住天数',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_booking_control` (`tenant_id`,`hotel_code`,`dimension_type`,`dimension_code`,`control_date`),
  KEY `idx_tenant_hotel` (`tenant_id`,`hotel_code`),
  KEY `idx_dimension` (`dimension_type`,`dimension_code`),
  KEY `idx_control_date` (`control_date`)
) ENGINE=InnoDB AUTO_INCREMENT=314 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预订控制表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cancellation_policies`
--

DROP TABLE IF EXISTS `cancellation_policies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cancellation_policies` (
  `cancellation_days` int DEFAULT NULL,
  `group_id` int DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `is_default` int DEFAULT '0',
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `cancellation_time` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cancellation_fee_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_jtc13rwe1ieg8b01t69puleyv` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `channel_codes`
--

DROP TABLE IF EXISTS `channel_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `channel_codes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `level` int NOT NULL DEFAULT '1',
  `parent_id` int DEFAULT NULL,
  `tenant_id` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `status` enum('active','inactive') COLLATE utf8mb4_unicode_ci NOT NULL,
  `parent_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_default` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `channel_hotel_mappings`
--

DROP TABLE IF EXISTS `channel_hotel_mappings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `channel_hotel_mappings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `channel_id` int NOT NULL COMMENT '渠道ID',
  `channel_code` varchar(50) DEFAULT NULL,
  `channel_name` varchar(50) DEFAULT NULL COMMENT '渠道名称',
  `hotel_id` int NOT NULL COMMENT '酒店ID',
  `hotel_name` varchar(100) DEFAULT NULL COMMENT '酒店名称',
  `hotel_code` varchar(50) DEFAULT NULL COMMENT '酒店CODE',
  `channel_hotel_code` varchar(100) NOT NULL COMMENT '渠道酒店CODE',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态：active/inactive',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_channel_hotel` (`channel_id`,`hotel_id`),
  KEY `idx_channel_hotel_mappings_channel_id` (`channel_id`),
  KEY `idx_channel_hotel_mappings_hotel_id` (`hotel_id`),
  KEY `idx_tenant_channel_hotel` (`tenant_id`,`channel_code`,`hotel_code`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='渠道酒店映射表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `channel_publish_records`
--

DROP TABLE IF EXISTS `channel_publish_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `channel_publish_records` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` int NOT NULL COMMENT '租户ID',
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '酒店代码',
  `channel_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '渠道代码',
  `rate_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房价码',
  `room_type_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房型代码',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'published' COMMENT '状态：published-已发布，unpublished-未发布',
  `published_at` timestamp NULL DEFAULT NULL COMMENT '发布时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_publish_record` (`tenant_id`,`hotel_code`,`channel_code`,`rate_code`,`room_type_code`),
  KEY `idx_tenant_hotel_channel` (`tenant_id`,`hotel_code`,`channel_code`),
  KEY `idx_rate_room` (`rate_code`,`room_type_code`),
  KEY `idx_tenant_publish_record` (`tenant_id`,`hotel_code`,`channel_code`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='渠道发布记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `channel_rate_code_mappings`
--

DROP TABLE IF EXISTS `channel_rate_code_mappings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `channel_rate_code_mappings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `channel_id` int NOT NULL COMMENT '渠道ID',
  `channel_name` varchar(50) DEFAULT NULL COMMENT '渠道名称',
  `hotel_id` int NOT NULL COMMENT '酒店ID',
  `hotel_name` varchar(100) DEFAULT NULL COMMENT '酒店名称',
  `rate_code_id` int NOT NULL COMMENT '房价码ID',
  `rate_code_name` varchar(100) DEFAULT NULL COMMENT '房价码名称',
  `rate_code` varchar(50) DEFAULT NULL COMMENT '房价码',
  `channel_rate_code` varchar(100) NOT NULL COMMENT '渠道房价码',
  `channel_rate_name` varchar(100) DEFAULT NULL COMMENT '渠道房价名称',
  `markup` decimal(5,2) DEFAULT '0.00' COMMENT '加价率%',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态：active/inactive',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `channel_code` varchar(50) DEFAULT NULL COMMENT '渠道CODE',
  `hotel_code` varchar(50) DEFAULT NULL COMMENT '酒店CODE',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_channel_hotel_rate` (`channel_id`,`hotel_id`,`rate_code_id`),
  KEY `idx_channel_rate_code_mappings_channel_id` (`channel_id`),
  KEY `idx_channel_rate_code_mappings_hotel_id` (`hotel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='渠道房价映射表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `channel_room_type_mappings`
--

DROP TABLE IF EXISTS `channel_room_type_mappings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `channel_room_type_mappings` (
  `channel_id` int NOT NULL,
  `hotel_id` int NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `room_type_id` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `channel_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `room_type_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `channel_room_type_code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `channel_room_type_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hotel_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `room_type_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `channel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '渠道CODE',
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '酒店CODE',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dictionary_items`
--

DROP TABLE IF EXISTS `dictionary_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dictionary_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int NOT NULL COMMENT '租户ID',
  `type_code` varchar(50) NOT NULL COMMENT '字典类型编码',
  `item_code` varchar(50) NOT NULL COMMENT '字典项编码',
  `item_name` varchar(100) NOT NULL COMMENT '字典项名称',
  `item_value` varchar(100) DEFAULT NULL COMMENT '字典项值',
  `description` text COMMENT '描述',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认',
  `status` enum('active','inactive') NOT NULL,
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dictionary_item` (`tenant_id`,`type_code`,`item_code`),
  KEY `idx_dictionary_item_query` (`tenant_id`,`type_code`,`status`,`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dictionary_types`
--

DROP TABLE IF EXISTS `dictionary_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dictionary_types` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int NOT NULL COMMENT '租户ID',
  `type_code` varchar(50) NOT NULL COMMENT '字典类型编码',
  `type_name` varchar(100) NOT NULL COMMENT '字典类型名称',
  `description` text COMMENT '描述',
  `status` enum('active','inactive') NOT NULL,
  `built_in` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否内置',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dictionary_type` (`tenant_id`,`type_code`),
  KEY `idx_dictionary_type_status` (`tenant_id`,`status`),
  KEY `idx_dictionary_type_sort` (`tenant_id`,`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_cancellation_policies`
--

DROP TABLE IF EXISTS `group_cancellation_policies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_cancellation_policies` (
  `group_id` int NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `policy_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `policy_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `policy_details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_dxhfx041db8379ksb45w5t0i7` (`policy_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_facilities`
--

DROP TABLE IF EXISTS `group_facilities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_facilities` (
  `available` bit(1) NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `facility_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `scope` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'hotel' COMMENT '适用范围：hotel（酒店设施）/ room_type（房型设施）',
  `facility_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `facility_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_p8g37xpm5s8tvyd8a4dlo2t5o` (`facility_code`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_guarantee_policies`
--

DROP TABLE IF EXISTS `group_guarantee_policies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_guarantee_policies` (
  `group_id` int NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `policy_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `policy_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `policy_details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_auhdwdb2u361hn9n1swrk6uaw` (`policy_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_rate_codes`
--

DROP TABLE IF EXISTS `group_rate_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_rate_codes` (
  `advance_booking_max` int DEFAULT NULL,
  `advance_booking_min` int DEFAULT NULL,
  `allow_points` bit(1) DEFAULT NULL,
  `discount` double DEFAULT NULL,
  `group_id` int NOT NULL,
  `tenant_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `market_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `minimum_stay_max` int DEFAULT NULL,
  `minimum_stay_min` int DEFAULT NULL,
  `parent_rate_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `points_value` double DEFAULT NULL,
  `source_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `booking_end_time` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `booking_start_time` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `checkin_end_time` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `checkin_start_time` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `coupon_rule` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `derivative_level` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `points_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `promotion_rule` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rate_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rounding` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cancellation_rule` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guarantee_rule` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rate_category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rate_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rate_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `applicable_room_types` json DEFAULT NULL,
  `company_membership` json DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `packages` json DEFAULT NULL,
  `personal_membership` json DEFAULT NULL,
  `group_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_px5ul4taa2m1fy58seeush69a` (`rate_code`),
  UNIQUE KEY `uk_tenant_rate_code` (`group_id`,`rate_code`),
  UNIQUE KEY `UKre9giqw17pa4wab4veg50axlo` (`group_id`,`rate_code`),
  KEY `idx_group_rate_codes_tenant_code` (`tenant_code`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_room_type_hotel`
--

DROP TABLE IF EXISTS `group_room_type_hotel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_room_type_hotel` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_room_type_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_id` int DEFAULT NULL,
  `allocated` tinyint(1) DEFAULT '1' COMMENT '是否已分配',
  `room_info_editable` tinyint(1) DEFAULT '0' COMMENT '房型信息是否可编辑',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_grth_hotel_code` (`hotel_code`),
  KEY `idx_grth_grt_code` (`group_room_type_code`)
) ENGINE=InnoDB AUTO_INCREMENT=461 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='集团房型-酒店关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_room_types`
--

DROP TABLE IF EXISTS `group_room_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_room_types` (
  `group_id` int NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `max_occupancy` int DEFAULT NULL,
  `room_type_category_id` int DEFAULT NULL,
  `sort_order` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `room_type_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `room_type_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `tenant_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户CODE',
  `room_type_category_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '房型大类CODE',
  `group_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_t6cb8642hy4n6nms19y5q249m` (`room_type_code`),
  UNIQUE KEY `UK_flwqcmqp685ohg124l1woqxe2` (`room_type_code`),
  KEY `FKjh5et5ufodygiv34cywaxg9f0` (`room_type_category_id`),
  KEY `idx_grt_tenant_code` (`tenant_code`),
  CONSTRAINT `FKjh5et5ufodygiv34cywaxg9f0` FOREIGN KEY (`room_type_category_id`) REFERENCES `room_type_categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=485 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guarantee_policies`
--

DROP TABLE IF EXISTS `guarantee_policies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `guarantee_policies` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '政策名称',
  `code` varchar(50) NOT NULL COMMENT '政策代码',
  `type` varchar(50) NOT NULL COMMENT '担保类型：无担保/信用卡/预付/公司/第三方/特殊',
  `guarantee_sub_type` varchar(50) DEFAULT NULL COMMENT '担保子类型：一律担保/超时担保（仅信用卡）',
  `guarantee_amount` varchar(50) DEFAULT NULL COMMENT '担保金额：首晚/全额（仅信用卡）',
  `latest_arrival_time` varchar(10) DEFAULT NULL COMMENT '最晚到店时间（仅超时担保）',
  `description` text COMMENT '描述',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态',
  `group_id` int DEFAULT NULL COMMENT '集团ID',
  `tenant_id` int DEFAULT NULL,
  `is_default` int DEFAULT '0',
  `card_type` varchar(50) DEFAULT NULL,
  `latest_check_in_time` varchar(10) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  UNIQUE KEY `uk_guarantee_tenant_code` (`tenant_id`,`code`),
  KEY `idx_guarantee_policies_group_id` (`group_id`),
  KEY `idx_guarantee_policies_status` (`status`),
  KEY `idx_guarantee_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='担保政策表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hotel_facilities`
--

DROP TABLE IF EXISTS `hotel_facilities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_facilities` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` int DEFAULT NULL,
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '酒店编码',
  `facility_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设施类型',
  `facility_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设施名称',
  `facility_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设施编码',
  `available` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_facility_code` (`facility_code`),
  KEY `idx_hotel_code` (`hotel_code`)
) ENGINE=InnoDB AUTO_INCREMENT=192 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒店设施表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hotel_images`
--

DROP TABLE IF EXISTS `hotel_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_images` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '酒店编码',
  `image_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片类型：logo-店图, external-外观图片, restaurant-餐厅图片, lobby-大堂图片',
  `image_path` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片存储路径',
  `image_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片名称',
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片描述',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_image_type` (`image_type`),
  KEY `idx_hotel_code` (`hotel_code`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒店图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hotel_price_logs`
--

DROP TABLE IF EXISTS `hotel_price_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_price_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int NOT NULL COMMENT '租户ID',
  `hotel_code` varchar(50) NOT NULL COMMENT '酒店CODE',
  `rate_code` varchar(50) NOT NULL COMMENT '房价码CODE',
  `operator_name` varchar(100) NOT NULL COMMENT '操作人姓名',
  `operation_type` varchar(20) NOT NULL COMMENT '操作类型：create/update/delete/batch_update/batch_delete',
  `operation_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `start_date` date DEFAULT NULL COMMENT '起始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `detail` text COMMENT '操作明细JSON，格式：[{roomTypeCode, roomTypeName, dates:[], oldPrice, newPrice}]',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_hotel` (`tenant_id`,`hotel_code`),
  KEY `idx_rate_code` (`rate_code`),
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='酒店价格操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hotel_prices`
--

DROP TABLE IF EXISTS `hotel_prices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_prices` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int NOT NULL COMMENT '租户ID',
  `hotel_code` varchar(50) NOT NULL COMMENT '酒店CODE',
  `rate_code` varchar(50) NOT NULL COMMENT '房价码CODE',
  `room_type_code` varchar(50) NOT NULL COMMENT '房型CODE',
  `price_date` date NOT NULL COMMENT '日期',
  `price_with_tax` decimal(10,2) DEFAULT NULL COMMENT '含税价格',
  `price_without_tax` decimal(10,2) DEFAULT NULL COMMENT '不含税价格',
  `status` varchar(20) NOT NULL DEFAULT 'active' COMMENT '状态：active/inactive',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_price` (`tenant_id`,`hotel_code`,`rate_code`,`room_type_code`,`price_date`),
  KEY `idx_tenant_hotel` (`tenant_id`,`hotel_code`),
  KEY `idx_hotel_date` (`hotel_code`,`price_date`),
  KEY `idx_rate_code` (`rate_code`),
  KEY `idx_room_type` (`room_type_code`),
  KEY `idx_tenant_hotel_date` (`tenant_id`,`hotel_code`,`price_date`)
) ENGINE=InnoDB AUTO_INCREMENT=933 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='酒店价格表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hotel_rate_code_allocations`
--

DROP TABLE IF EXISTS `hotel_rate_code_allocations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_rate_code_allocations` (
  `allocated` bit(1) NOT NULL,
  `basic_info_editable` bit(1) NOT NULL,
  `booking_limit_editable` bit(1) NOT NULL,
  `guarantee_rule_editable` bit(1) NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `price_info_editable` bit(1) NOT NULL,
  `promotion_editable` bit(1) NOT NULL,
  `tenant_id` int NOT NULL,
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rate_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=160 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hotel_room_types`
--

DROP TABLE IF EXISTS `hotel_room_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_room_types` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_type_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房型代码',
  `room_type_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房型名称',
  `english_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '英文名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `max_occupancy` int DEFAULT '2' COMMENT '最大入住人数',
  `max_adults` int DEFAULT NULL COMMENT '最大成人数',
  `max_children` int DEFAULT NULL COMMENT '最大儿童数',
  `total_rooms` int DEFAULT NULL COMMENT '房间数',
  `room_quantity` int DEFAULT NULL COMMENT '房型数量',
  `area` decimal(38,2) DEFAULT NULL,
  `floor` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '楼层',
  `window_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '窗型',
  `bed_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '床型',
  `image` text COLLATE utf8mb4_unicode_ci COMMENT '图片',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '酒店CODE',
  `tenant_id` int DEFAULT NULL,
  `group_room_type_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '集团房型CODE',
  `room_type_category_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '房型大类CODE',
  PRIMARY KEY (`id`),
  KEY `idx_room_type_code` (`room_type_code`),
  KEY `idx_status` (`status`),
  KEY `idx_hrt_hotel_code` (`hotel_code`),
  KEY `FKawu4yr4fl1jnr7hqhohjyi9x6` (`group_room_type_code`),
  CONSTRAINT `FKawu4yr4fl1jnr7hqhohjyi9x6` FOREIGN KEY (`group_room_type_code`) REFERENCES `group_room_types` (`room_type_code`),
  CONSTRAINT `FKbyjo2dypgqjqyr98nlh03wgb3` FOREIGN KEY (`hotel_code`) REFERENCES `hotels` (`hotel_code`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒店房型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hotels`
--

DROP TABLE IF EXISTS `hotels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotels` (
  `id` int NOT NULL AUTO_INCREMENT,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `tenant_id` int DEFAULT NULL,
  `total_rooms` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `star_rating` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `support_multi_price` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `support_person_price_diff` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `support_room_type_price_diff` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `allow_create_rate_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `allow_create_room_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `city` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `province` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `chinese_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `english_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `introduction` text COLLATE utf8mb4_unicode_ci,
  `multi_price_options` text COLLATE utf8mb4_unicode_ci,
  `status` enum('active','inactive') COLLATE utf8mb4_unicode_ci NOT NULL,
  `minimum_price` decimal(10,2) DEFAULT NULL COMMENT '酒店最低价格（含税）',
  `region` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '酒店所在区域',
  `tax_rate_codes` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'ç»‘å®šçš„é›†å›¢ç¨ŽçŽ‡ä»£ç åˆ—è¡¨(é€—å·åˆ†éš”)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_l5sej0h6own7pnqy1i9e3it9y` (`hotel_code`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `allocated_rooms` int NOT NULL,
  `available_rooms` int NOT NULL,
  `channel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `date` date NOT NULL,
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `rate_plan_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `room_type_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `status` enum('active','inactive') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_hotel_date` (`tenant_id`,`hotel_code`,`date`),
  KEY `FK3tjob40fvcl7eufk3tywo1snu` (`hotel_code`),
  CONSTRAINT `FK3tjob40fvcl7eufk3tywo1snu` FOREIGN KEY (`hotel_code`) REFERENCES `hotels` (`hotel_code`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventory_quota`
--

DROP TABLE IF EXISTS `inventory_quota`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_quota` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int NOT NULL,
  `hotel_code` varchar(50) NOT NULL,
  `dimension_type` varchar(30) NOT NULL COMMENT 'rate/channel/market/channel_room_type/rate_category',
  `dimension_code` varchar(100) NOT NULL DEFAULT '',
  `quota_date` date NOT NULL,
  `quota_limit` int DEFAULT NULL COMMENT '库存限制（NULL表示未设置）',
  `sold_count` int NOT NULL DEFAULT '0' COMMENT '已售数量',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inventory_quota` (`tenant_id`,`hotel_code`,`dimension_type`,`dimension_code`,`quota_date`),
  KEY `idx_tenant_hotel` (`tenant_id`,`hotel_code`),
  KEY `idx_dimension` (`dimension_type`,`dimension_code`),
  KEY `idx_quota_date` (`quota_date`)
) ENGINE=InnoDB AUTO_INCREMENT=214 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房量控制表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventory_quota_logs`
--

DROP TABLE IF EXISTS `inventory_quota_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_quota_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int NOT NULL,
  `hotel_code` varchar(50) NOT NULL,
  `dimension_type` varchar(30) NOT NULL,
  `dimension_code` varchar(100) NOT NULL DEFAULT '',
  `operator_name` varchar(100) NOT NULL,
  `operation_type` varchar(20) NOT NULL,
  `operation_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `detail` text,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_hotel` (`tenant_id`,`hotel_code`),
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房量控制操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `market_code_categories`
--

DROP TABLE IF EXISTS `market_code_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `market_code_categories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `market_codes`
--

DROP TABLE IF EXISTS `market_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `market_codes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `level` int NOT NULL DEFAULT '1',
  `parent_id` int DEFAULT NULL,
  `tenant_id` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `parent_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `menus`
--

DROP TABLE IF EXISTS `menus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menus` (
  `id` int NOT NULL AUTO_INCREMENT,
  `parent_id` int DEFAULT NULL,
  `sort_order` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `menu_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `system_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `menu_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `parent_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `icon` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `menu_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `permission` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `component` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `path` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `remark` text COLLATE utf8mb4_unicode_ci,
  `status` enum('active','inactive') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_gnlmiibemu8hv3gmiryrr7p0d` (`menu_code`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `operation_logs`
--

DROP TABLE IF EXISTS `operation_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `time` datetime(6) NOT NULL,
  `action` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `operator` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `details` text COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `overbooking`
--

DROP TABLE IF EXISTS `overbooking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `overbooking` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int NOT NULL,
  `hotel_code` varchar(50) NOT NULL,
  `dimension_type` varchar(20) NOT NULL COMMENT 'hotel/room_type',
  `dimension_code` varchar(50) NOT NULL DEFAULT '' COMMENT '酒店级为空，房型级为房型CODE',
  `overbook_date` date NOT NULL,
  `overbook_count` int NOT NULL DEFAULT '0' COMMENT '超预订数量',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_overbooking` (`tenant_id`,`hotel_code`,`dimension_type`,`dimension_code`,`overbook_date`),
  UNIQUE KEY `UKuj7n6k476ap0m83g3e826k89` (`tenant_id`,`hotel_code`,`dimension_type`,`dimension_code`,`overbook_date`),
  KEY `idx_tenant_hotel` (`tenant_id`,`hotel_code`),
  KEY `idx_overbook_date` (`overbook_date`)
) ENGINE=InnoDB AUTO_INCREMENT=237 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='超预订管理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `overbooking_logs`
--

DROP TABLE IF EXISTS `overbooking_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `overbooking_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int NOT NULL,
  `hotel_code` varchar(50) NOT NULL,
  `dimension_type` varchar(20) NOT NULL,
  `dimension_code` varchar(50) NOT NULL DEFAULT '',
  `operator_name` varchar(100) NOT NULL,
  `operation_type` varchar(20) NOT NULL COMMENT 'single/batch',
  `operation_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `detail` text,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_hotel` (`tenant_id`,`hotel_code`),
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='超预订操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `package_daily_prices`
--

DROP TABLE IF EXISTS `package_daily_prices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `package_daily_prices` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int NOT NULL COMMENT '租户ID',
  `hotel_code` varchar(50) NOT NULL COMMENT '酒店CODE',
  `package_code` varchar(50) NOT NULL COMMENT '包价CODE',
  `price_date` date NOT NULL COMMENT '价格日期',
  `sale_price` decimal(10,2) NOT NULL COMMENT '当日价格',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_package_daily_price` (`tenant_id`,`hotel_code`,`package_code`,`price_date`),
  KEY `idx_package_daily_price_query` (`tenant_id`,`hotel_code`,`package_code`,`price_date`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='酒店包价每日价格表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `packages`
--

DROP TABLE IF EXISTS `packages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `packages` (
  `fixed_price` double DEFAULT NULL,
  `fixed_quantity` int DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `tax_included` bit(1) NOT NULL,
  `tenant_id` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `price_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `quantity_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `frequency` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `status` enum('active','inactive') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_74enlscq9byi1coqeype4ehip` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `person_diff_systems`
--

DROP TABLE IF EXISTS `person_diff_systems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `person_diff_systems` (
  `hotel_id` int NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `status` enum('active','inactive') COLLATE utf8mb4_unicode_ci NOT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhw3ngurvot8fvpdxstv06ni95` (`hotel_id`),
  KEY `FK9ldllvbvwu49asg06cs661pvm` (`hotel_code`),
  CONSTRAINT `FK9ldllvbvwu49asg06cs661pvm` FOREIGN KEY (`hotel_code`) REFERENCES `hotels` (`hotel_code`),
  CONSTRAINT `FKhw3ngurvot8fvpdxstv06ni95` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pms_inventory`
--

DROP TABLE IF EXISTS `pms_inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_inventory` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int NOT NULL,
  `hotel_code` varchar(50) NOT NULL,
  `room_type_code` varchar(50) NOT NULL,
  `inventory_date` date NOT NULL,
  `physical_rooms` int NOT NULL DEFAULT '0',
  `available_rooms` int NOT NULL DEFAULT '0',
  `maintenance_rooms` int NOT NULL DEFAULT '0',
  `overbook_count` int NOT NULL DEFAULT '0',
  `version` int NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pms_inventory` (`tenant_id`,`hotel_code`,`room_type_code`,`inventory_date`)
) ENGINE=InnoDB AUTO_INCREMENT=2088 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pms_sync_logs`
--

DROP TABLE IF EXISTS `pms_sync_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_sync_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `detail` text COLLATE utf8mb4_unicode_ci,
  `error_message` text COLLATE utf8mb4_unicode_ci,
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sync_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sync_time` datetime(6) NOT NULL,
  `sync_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tenant_id` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rate_plan_packages`
--

DROP TABLE IF EXISTS `rate_plan_packages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rate_plan_packages` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `package_id` int NOT NULL,
  `rate_plan_id` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `rate_plan_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '价格计划CODE',
  `package_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '包价CODE',
  PRIMARY KEY (`id`),
  KEY `FK9s6yh7cc4mnjmr85lxlm0p1f8` (`package_id`),
  KEY `FK2baph366tn1xw8sfx0yr2fq8w` (`rate_plan_id`),
  CONSTRAINT `FK2baph366tn1xw8sfx0yr2fq8w` FOREIGN KEY (`rate_plan_id`) REFERENCES `rate_plans` (`id`),
  CONSTRAINT `FK9s6yh7cc4mnjmr85lxlm0p1f8` FOREIGN KEY (`package_id`) REFERENCES `packages` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rate_plans`
--

DROP TABLE IF EXISTS `rate_plans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rate_plans` (
  `advance_booking_max` int DEFAULT NULL,
  `advance_booking_min` int DEFAULT NULL,
  `allow_points` bit(1) DEFAULT NULL,
  `discount` double DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `minimum_stay_max` int DEFAULT NULL,
  `minimum_stay_min` int DEFAULT NULL,
  `points_value` double DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `booking_end_time` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `booking_start_time` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `checkin_end_time` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `checkin_start_time` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `coupon_rule` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `derivative_level` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `points_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `promotion_rule` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rate_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rounding` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cancellation_rule` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guarantee_rule` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rate_category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rate_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rate_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `applicable_room_types` json DEFAULT NULL,
  `company_membership` json DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `packages` json DEFAULT NULL,
  `personal_membership` json DEFAULT NULL,
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '酒店CODE',
  `source_group_rate_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '源集团价格码CODE',
  `market_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '市场码CODE',
  `source_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源码CODE',
  `parent_rate_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '父价格码CODE',
  `room_type_diff_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `person_diff_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rate_types`
--

DROP TABLE IF EXISTS `rate_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rate_types` (
  `id` int NOT NULL AUTO_INCREMENT,
  `sort_order` int DEFAULT NULL,
  `tenant_id` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `status` enum('active','inactive') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `report_daily_reservation_summary`
--

DROP TABLE IF EXISTS `report_daily_reservation_summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report_daily_reservation_summary` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int NOT NULL,
  `report_date` date NOT NULL,
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `channel_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `market_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rate_category_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rate_plan_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `room_type_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `reservation_status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'confirmed',
  `order_count` int NOT NULL DEFAULT '0',
  `room_nights` int NOT NULL DEFAULT '0',
  `total_revenue` decimal(12,2) NOT NULL DEFAULT '0.00',
  `points_redeemed` int NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_daily_summary` (`tenant_id`,`report_date`,`hotel_code`,`channel_code`,`market_code`,`rate_category_code`,`rate_plan_code`,`room_type_code`,`reservation_status`),
  KEY `idx_summary_base` (`tenant_id`,`report_date`,`hotel_code`),
  KEY `idx_summary_channel` (`channel_code`),
  KEY `idx_summary_market` (`market_code`),
  KEY `idx_summary_category` (`rate_category_code`),
  KEY `idx_summary_rate_plan` (`rate_plan_code`),
  KEY `idx_summary_status` (`reservation_status`)
) ENGINE=InnoDB AUTO_INCREMENT=504215 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `adult_count` int NOT NULL,
  `channel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '渠道编码',
  `channel_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '渠道名称（快照）',
  `check_in_date` date NOT NULL,
  `check_out_date` date NOT NULL,
  `nights` int DEFAULT NULL COMMENT '入住晚数',
  `child_count` int DEFAULT NULL,
  `contact_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系人手机号',
  `contact_email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系人邮箱',
  `member_no` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员编号',
  `member_level` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员等级',
  `original_price` decimal(12,2) DEFAULT NULL COMMENT '订单原价',
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '酒店编码',
  `hotel_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '酒店名称（快照）',
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL COMMENT '租户ID',
  `market_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '市场编码',
  `rate_plan_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '价格计划编码',
  `rate_plan_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '价格计划名称（快照）',
  `room_count` int NOT NULL,
  `room_type_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '房型编码',
  `room_type_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '房型名称（快照）',
  `source_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源编码',
  `total_price` decimal(12,2) NOT NULL DEFAULT '0.00',
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `currency` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `payment_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `reservation_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `payment_deadline` datetime(6) DEFAULT NULL,
  `created_by` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `guarantee_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guarantee_info` text COLLATE utf8mb4_unicode_ci COMMENT '担保信息JSON',
  `cancellation_policy_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '取消政策编码',
  `cancellation_policy_desc` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '取消政策描述',
  `guarantee_policy_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '担保政策编码',
  `guarantee_policy_desc` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '担保政策描述',
  `guest_phone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `modified_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cancelled_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '取消操作人',
  `cancelled_at` timestamp NULL DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '取消原因',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `reservation_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `channel_order_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '渠道订单号',
  `pms_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'PMS单号',
  `guest_email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guest_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `notes` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guest_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '客人备注',
  `hotel_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '门店备注',
  `is_manual` tinyint(1) DEFAULT '0' COMMENT '是否人工干预',
  `manual_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '人工干预原因',
  `commission_rate` decimal(5,4) DEFAULT NULL COMMENT '佣金比例',
  `commission_amount` decimal(12,2) DEFAULT NULL COMMENT '佣金金额',
  `order_source` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'channel' COMMENT '订单来源',
  `special_request` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `credit_card_info` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('active','cancelled','completed') COLLATE utf8mb4_unicode_ci NOT NULL,
  `booking_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_b16r178jxqqmyfum9swkjtri9` (`reservation_code`),
  KEY `idx_reservation_check_in_date` (`tenant_id`,`check_in_date`),
  KEY `idx_reservation_status` (`tenant_id`,`reservation_status`),
  KEY `idx_reservation_created_at` (`tenant_id`,`created_at`),
  KEY `idx_tenant_hotel` (`tenant_id`,`hotel_code`),
  KEY `FK7gy0cy5adtq2x4dtv525q6sp0` (`hotel_code`),
  CONSTRAINT `FK7gy0cy5adtq2x4dtv525q6sp0` FOREIGN KEY (`hotel_code`) REFERENCES `hotels` (`hotel_code`)
) ENGINE=InnoDB AUTO_INCREMENT=260090 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation_daily_price`
--

DROP TABLE IF EXISTS `reservation_daily_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation_daily_price` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `reservation_id` int NOT NULL COMMENT '关联订单ID',
  `price_date` date NOT NULL COMMENT '价格日期',
  `original_price` decimal(12,2) DEFAULT NULL COMMENT '原价（折扣前）',
  `actual_price` decimal(12,2) NOT NULL COMMENT '实际价格（折扣后含税）',
  `tax_amount` decimal(12,2) DEFAULT NULL COMMENT '税费金额',
  `service_charge` decimal(12,2) DEFAULT NULL COMMENT '服务费',
  `breakfast_included` tinyint(1) DEFAULT '0' COMMENT '是否含早餐',
  `breakfast_count` int DEFAULT '0' COMMENT '早餐份数',
  `packages_json` text COMMENT '包价信息JSON',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_reservation_date` (`reservation_id`,`price_date`),
  KEY `idx_reservation_id` (`reservation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30057 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单每日价格明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation_daily_price_taxes`
--

DROP TABLE IF EXISTS `reservation_daily_price_taxes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation_daily_price_taxes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `reservation_daily_price_id` int NOT NULL COMMENT 'æ¯æ—¥ä»·æ ¼æ˜Žç»†ID',
  `tax_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ç¨ŽçŽ‡CODE',
  `tax_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ç¨ŽçŽ‡åç§°',
  `rate_amount` decimal(10,4) NOT NULL COMMENT 'ç¨ŽçŽ‡(%)',
  `calculated_amount` decimal(12,2) NOT NULL COMMENT 'è¯¥é¡¹ç¨Žè´¹é‡‘é¢',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_res_daily_price` (`reservation_daily_price_id`),
  CONSTRAINT `fk_res_daily_price` FOREIGN KEY (`reservation_daily_price_id`) REFERENCES `reservation_daily_price` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='è®¢å•æ¯æ—¥ä»·æ ¼ç¨Žè´¹ç»†è¡¨';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation_guest`
--

DROP TABLE IF EXISTS `reservation_guest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation_guest` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `reservation_id` int NOT NULL COMMENT '关联订单ID',
  `guest_type` varchar(20) DEFAULT 'guest' COMMENT '客人类型：contact/guest',
  `name` varchar(100) NOT NULL COMMENT '姓名',
  `phone` varchar(50) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `id_type` varchar(30) DEFAULT NULL COMMENT '证件类型',
  `id_number` varchar(100) DEFAULT NULL COMMENT '证件号码',
  `member_no` varchar(50) DEFAULT NULL COMMENT '会员编号',
  `member_level` varchar(30) DEFAULT NULL COMMENT '会员等级',
  `room_number` varchar(20) DEFAULT NULL COMMENT '房间号',
  `pms_account` varchar(100) DEFAULT NULL COMMENT 'PMS账号',
  `pms_status` varchar(30) DEFAULT NULL COMMENT 'PMS状态',
  `room_index` int DEFAULT '0',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_reservation_id` (`reservation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18886 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单入住人信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation_history`
--

DROP TABLE IF EXISTS `reservation_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `reservation_id` int NOT NULL COMMENT '预订ID',
  `action` varchar(50) DEFAULT NULL COMMENT '操作类型',
  `content` varchar(200) NOT NULL COMMENT '操作内容',
  `result` varchar(20) NOT NULL COMMENT '结果：成功/失败',
  `operator` varchar(50) NOT NULL COMMENT '操作人',
  `operator_type` varchar(20) DEFAULT 'system' COMMENT '操作人类型',
  `detail` text COMMENT '操作详情JSON',
  `operation_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `log_id` int DEFAULT NULL COMMENT '接口日志ID',
  PRIMARY KEY (`id`),
  KEY `idx_reservation_history_reservation_id` (`reservation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=151 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预订操作历史表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation_payment`
--

DROP TABLE IF EXISTS `reservation_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation_payment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `reservation_id` int NOT NULL COMMENT '关联订单ID',
  `payment_method` varchar(30) NOT NULL COMMENT '支付方式',
  `payment_type` varchar(20) DEFAULT 'payment' COMMENT '支付类型：payment/refund',
  `payment_amount` decimal(12,2) NOT NULL COMMENT '支付金额',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '第三方支付流水号',
  `credit_card_last4` varchar(4) DEFAULT NULL COMMENT '信用卡尾号4位',
  `credit_card_expiry` varchar(10) DEFAULT NULL COMMENT '信用卡有效期',
  `status` varchar(20) DEFAULT 'pending' COMMENT '支付状态：pending/success/failed',
  `paid_at` timestamp NULL DEFAULT NULL COMMENT '支付成功时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_reservation_id` (`reservation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单支付记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation_promotion`
--

DROP TABLE IF EXISTS `reservation_promotion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation_promotion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `reservation_id` int NOT NULL COMMENT '关联订单ID',
  `promotion_name` varchar(200) NOT NULL COMMENT '优惠名称',
  `discount_type` varchar(30) NOT NULL COMMENT '折扣类型',
  `discount_value` decimal(12,2) DEFAULT NULL COMMENT '折扣值',
  `discount_amount` decimal(12,2) NOT NULL COMMENT '实际优惠金额',
  `promotion_code` varchar(50) DEFAULT NULL COMMENT '优惠券码',
  `provider` varchar(30) DEFAULT NULL COMMENT '优惠承担方',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_reservation_id` (`reservation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单促销优惠表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_menus`
--

DROP TABLE IF EXISTS `role_menus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_menus` (
  `id` int NOT NULL AUTO_INCREMENT,
  `menu_id` int NOT NULL,
  `role_id` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=119 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `data_scope` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `status` enum('active','inactive') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `room_status`
--

DROP TABLE IF EXISTS `room_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int NOT NULL,
  `hotel_code` varchar(50) NOT NULL,
  `dimension_type` varchar(30) NOT NULL COMMENT 'hotel/room_type/rate/channel/channel_room_type/market/rate_category',
  `dimension_code` varchar(100) NOT NULL DEFAULT '',
  `status_date` date NOT NULL,
  `is_open` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0=关, 1=开',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_status` (`tenant_id`,`hotel_code`,`dimension_type`,`dimension_code`,`status_date`),
  UNIQUE KEY `UKglfwoixhqeva0s00l69l5ik43` (`tenant_id`,`hotel_code`,`dimension_type`,`dimension_code`,`status_date`),
  KEY `idx_tenant_hotel` (`tenant_id`,`hotel_code`),
  KEY `idx_dimension` (`dimension_type`,`dimension_code`),
  KEY `idx_status_date` (`status_date`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房态管理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `room_status_logs`
--

DROP TABLE IF EXISTS `room_status_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_status_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int NOT NULL,
  `hotel_code` varchar(50) NOT NULL,
  `dimension_type` varchar(30) NOT NULL,
  `dimension_code` varchar(100) NOT NULL DEFAULT '',
  `operator_name` varchar(100) NOT NULL,
  `operation_type` varchar(20) NOT NULL COMMENT 'single/batch',
  `operation_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `detail` text COMMENT '操作明细JSON',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_hotel` (`tenant_id`,`hotel_code`),
  KEY `idx_dimension` (`dimension_type`,`dimension_code`),
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房态操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `room_type_categories`
--

DROP TABLE IF EXISTS `room_type_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_type_categories` (
  `group_id` int NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `sort_order` int DEFAULT NULL,
  `tenant_id` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `group_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `room_type_diff_systems`
--

DROP TABLE IF EXISTS `room_type_diff_systems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_type_diff_systems` (
  `hotel_id` int NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfqbep4flr086chbvk3di8nrg3` (`hotel_id`),
  KEY `FK3yvyoh4a49cgkrdhfi3tgk73x` (`hotel_code`),
  CONSTRAINT `FK3yvyoh4a49cgkrdhfi3tgk73x` FOREIGN KEY (`hotel_code`) REFERENCES `hotels` (`hotel_code`),
  CONSTRAINT `FKfqbep4flr086chbvk3di8nrg3` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `room_type_diffs`
--

DROP TABLE IF EXISTS `room_type_diffs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_type_diffs` (
  `end_date` date DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `room_type_id` int NOT NULL,
  `start_date` date NOT NULL,
  `system_id` int NOT NULL,
  `value` double NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `weekdays` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `system_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `room_type_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn84oaee79vy3smei8cws8yvcl` (`room_type_id`),
  KEY `FKghag0u16k0l3mmovjl094lb7b` (`system_id`),
  CONSTRAINT `FKghag0u16k0l3mmovjl094lb7b` FOREIGN KEY (`system_id`) REFERENCES `room_type_diff_systems` (`id`),
  CONSTRAINT `FKn84oaee79vy3smei8cws8yvcl` FOREIGN KEY (`room_type_id`) REFERENCES `room_types` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `room_type_facilities`
--

DROP TABLE IF EXISTS `room_type_facilities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_type_facilities` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` int DEFAULT NULL,
  `room_type_id` int NOT NULL COMMENT '房型ID',
  `hotel_id` int NOT NULL COMMENT '酒店ID',
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '酒店编码',
  `room_type_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '房型编码',
  `facility_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设施类型',
  `facility_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设施名称',
  `facility_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设施编码',
  `available` tinyint(1) DEFAULT '1' COMMENT '是否可用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_room_type_id` (`room_type_id`),
  KEY `idx_hotel_id` (`hotel_id`),
  KEY `idx_hotel_code` (`hotel_code`),
  KEY `idx_facility_type` (`facility_type`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房型设施表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `room_types`
--

DROP TABLE IF EXISTS `room_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_types` (
  `group_room_type_id` int DEFAULT NULL,
  `hotel_id` int NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `group_room_type_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hotel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8o8nd8dlqke36euaoe37e2r32` (`group_room_type_id`),
  KEY `FK42cc0t2sr43om89u1loqh7arj` (`hotel_id`),
  KEY `idx_tenant_code` (`tenant_id`,`code`),
  KEY `FKkrs8tdlwppm0vm377tpk5ietc` (`group_room_type_code`),
  KEY `FKe5eyr07eq75cfy3wsn9wwgfyp` (`hotel_code`),
  CONSTRAINT `FK42cc0t2sr43om89u1loqh7arj` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`),
  CONSTRAINT `FK8o8nd8dlqke36euaoe37e2r32` FOREIGN KEY (`group_room_type_id`) REFERENCES `group_room_types` (`id`),
  CONSTRAINT `FKe5eyr07eq75cfy3wsn9wwgfyp` FOREIGN KEY (`hotel_code`) REFERENCES `hotels` (`hotel_code`),
  CONSTRAINT `FKkrs8tdlwppm0vm377tpk5ietc` FOREIGN KEY (`group_room_type_code`) REFERENCES `group_room_types` (`room_type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `source_codes`
--

DROP TABLE IF EXISTS `source_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `source_codes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `level` int NOT NULL DEFAULT '1',
  `parent_id` int DEFAULT NULL,
  `tenant_id` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `parent_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_trace_logs`
--

DROP TABLE IF EXISTS `system_trace_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_trace_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `decision_snapshot` mediumtext COLLATE utf8mb4_unicode_ci,
  `error_class` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `error_line` int DEFAULT NULL,
  `error_method` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `error_stack` text COLLATE utf8mb4_unicode_ci,
  `operation_name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reference_code` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `related_prd_link` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `source_type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `trace_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`),
  KEY `idx_reference_code` (`reference_code`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=3279 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tax_settings`
--

DROP TABLE IF EXISTS `tax_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tax_settings` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` int NOT NULL COMMENT '租户ID',
  `tax_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '税率编码',
  `legal_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '税费法定全称',
  `rate_amount` decimal(10,4) DEFAULT NULL COMMENT '税率/定额标准',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_tax_code` (`tenant_id`,`tax_code`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_tax_code` (`tax_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='税和服务费设置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tenant_channels`
--

DROP TABLE IF EXISTS `tenant_channels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tenant_channels` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` int NOT NULL COMMENT '租户ID（等同于集团ID）',
  `channel_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '渠道名称（如：携程、美团、飞猪）',
  `channel_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '渠道代码（如：CTRIP、MEITUAN、FLIGGY）',
  `connected` tinyint(1) DEFAULT '0' COMMENT '是否已对接：0-未对接，1-已对接',
  `logo_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '渠道LOGO地址',
  `switch_channel` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通道（如：德比debi、畅联changlian）',
  `access_key` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '对接key',
  `access_secret` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '对接秘钥',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `price_rounding` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'keep' COMMENT '价格取整方式：keep/ceil/floor',
  `prepaid_commission_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'percentage' COMMENT '预付佣金类型：percentage/fixed',
  `prepaid_commission_value` decimal(10,2) DEFAULT NULL COMMENT '预付佣金数值',
  `postpaid_commission_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'percentage' COMMENT '现付佣金类型：percentage/fixed',
  `postpaid_commission_value` decimal(10,2) DEFAULT NULL COMMENT '现付佣金数值',
  `prepaid_order_requires_payment` tinyint(1) NOT NULL DEFAULT '1' COMMENT '预付订单是否需要支付',
  `cancel_order_checks_cancellation_rule` tinyint(1) NOT NULL DEFAULT '1' COMMENT '取消订单是否校验取消规则',
  `cancel_failure_requires_manual_intervention` tinyint(1) NOT NULL DEFAULT '1' COMMENT '取消失败时是否需要人工介入',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_channel` (`tenant_id`,`channel_code`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_channel_code` (`channel_code`),
  KEY `idx_connected` (`connected`),
  KEY `idx_status` (`status`),
  KEY `idx_tenant_channel_code` (`tenant_id`,`channel_code`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户可对接渠道表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tenants`
--

DROP TABLE IF EXISTS `tenants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tenants` (
  `expire_date` date DEFAULT NULL,
  `hotel_count` int DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_r2h4deuvsyct6en1d7av037c7` (`tenant_code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_id` int NOT NULL,
  `tenant_id` int DEFAULT NULL,
  `user_id` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenant_id` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `last_login_time` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_login_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('active','inactive') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'CRS'
--

--
-- Dumping routines for database 'CRS'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-08-25 19:45:47
