/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : hotel_booking

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 07/06/2026 14:43:39
*/

CREATE DATABASE IF NOT EXISTS `hotel_booking` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `hotel_booking`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_chat_record
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_record`;
CREATE TABLE `ai_chat_record`  (
  `id` bigint NOT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  `request_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求内容',
  `response_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '响应内容',
  `cost_ms` int NULL DEFAULT NULL COMMENT '耗时（毫秒）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI对话记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_chat_record
-- ----------------------------
INSERT INTO `ai_chat_record` VALUES (1, 1, '帮我找一下上海的外滩酒店', '为您推荐外滩雅致酒店，距离外滩很近。', 1200, '2026-06-05 11:32:54', 0);
INSERT INTO `ai_chat_record` VALUES (2, 1, '有没有便宜一点的？', '朝阳精选酒店性价比很高，基础房型仅需466元。', 800, '2026-06-05 11:32:54', 0);
INSERT INTO `ai_chat_record` VALUES (3, 2, '推荐几个亲子酒店', '西湖奢华酒店的家庭套房非常适合亲子出行，两室一厅空间宽敞。', 1500, '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for bed_type
-- ----------------------------
DROP TABLE IF EXISTS `bed_type`;
CREATE TABLE `bed_type`  (
  `id` bigint NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '床型名称',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '床型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bed_type
-- ----------------------------
INSERT INTO `bed_type` VALUES (1, '大床', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `bed_type` VALUES (2, '双床', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `bed_type` VALUES (3, '单人床', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `bed_type` VALUES (4, '上下铺', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `bed_type` VALUES (5, '圆床', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for breakfast_type
-- ----------------------------
DROP TABLE IF EXISTS `breakfast_type`;
CREATE TABLE `breakfast_type`  (
  `id` bigint NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '早餐类型名称',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '早餐类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of breakfast_type
-- ----------------------------
INSERT INTO `breakfast_type` VALUES (1, '含双早', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `breakfast_type` VALUES (2, '含单早', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `breakfast_type` VALUES (3, '不含早餐', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `breakfast_type` VALUES (4, '可选早餐', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for checkin_record
-- ----------------------------
DROP TABLE IF EXISTS `checkin_record`;
CREATE TABLE `checkin_record`  (
  `id` bigint NOT NULL,
  `order_id` bigint NOT NULL,
  `guest_room_id` bigint NULL DEFAULT NULL COMMENT '入住客房ID',
  `guest_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '入住人姓名',
  `guest_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `id_card` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `checkin_time` datetime NULL DEFAULT NULL COMMENT '实际入住时间',
  `checkout_time` datetime NULL DEFAULT NULL COMMENT '实际离店时间',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态 0-待入住 1-已入住 2-已离店 3-异常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '入住记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of checkin_record
-- ----------------------------
INSERT INTO `checkin_record` VALUES (1, 1, 10001, 'Demo User', '13800138000', NULL, NULL, NULL, 2, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `checkin_record` VALUES (2, 2, 10003, 'Test User', '13900139000', NULL, NULL, NULL, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `checkin_record` VALUES (3, 6, 10009, 'Charlie', '13722223333', NULL, NULL, NULL, 2, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for city
-- ----------------------------
DROP TABLE IF EXISTS `city`;
CREATE TABLE `city`  (
  `id` bigint NOT NULL,
  `country_id` bigint NOT NULL COMMENT '所属国家',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市名称',
  `longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '纬度',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '城市表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of city
-- ----------------------------
INSERT INTO `city` VALUES (1, 1, '上海', 121.473700, 31.230400, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `city` VALUES (2, 1, '北京', 116.407400, 39.904200, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `city` VALUES (3, 1, '杭州', 120.155100, 30.274100, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `city` VALUES (4, 1, '广州', 113.264400, 23.129100, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `city` VALUES (5, 1, '深圳', 114.057900, 22.543100, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `city` VALUES (6, 1, '成都', 104.066800, 30.572800, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for country
-- ----------------------------
DROP TABLE IF EXISTS `country`;
CREATE TABLE `country`  (
  `id` bigint NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '国家名称',
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '国家代码',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '国家表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of country
-- ----------------------------
INSERT INTO `country` VALUES (1, '中国', 'CN', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `country` VALUES (2, '日本', 'JP', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `country` VALUES (3, '泰国', 'TH', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for facility
-- ----------------------------
DROP TABLE IF EXISTS `facility`;
CREATE TABLE `facility`  (
  `id` bigint NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设施名称',
  `icon` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类（酒店设施/房间设施）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '设施字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of facility
-- ----------------------------
INSERT INTO `facility` VALUES (1, '免费Wi-Fi', 'wifi', '酒店设施', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `facility` VALUES (2, '停车场', 'parking', '酒店设施', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `facility` VALUES (3, '含早餐', 'breakfast', '酒店设施', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `facility` VALUES (4, '健身房', 'gym', '酒店设施', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `facility` VALUES (5, '游泳池', 'pool', '酒店设施', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `facility` VALUES (6, 'SPA', 'spa', '酒店设施', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `facility` VALUES (7, '会议室', 'meeting', '酒店设施', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `facility` VALUES (8, '洗衣服务', 'laundry', '酒店设施', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `facility` VALUES (9, '家庭房', 'family', '酒店设施', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `facility` VALUES (10, '无障碍设施', 'accessible', '酒店设施', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `hotel_id` bigint NOT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_hotel`(`user_id` ASC, `hotel_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_hotel`(`hotel_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite` VALUES (2, 1, 103, '2026-06-05 11:32:54');
INSERT INTO `favorite` VALUES (3, 2, 101, '2026-06-05 11:32:54');
INSERT INTO `favorite` VALUES (4, 1, 104, '2026-06-05 11:32:54');
INSERT INTO `favorite` VALUES (5, 1, 105, '2026-06-05 11:32:54');
INSERT INTO `favorite` VALUES (6, 2, 107, '2026-06-05 11:32:54');

-- ----------------------------
-- Table structure for guest_room
-- ----------------------------
DROP TABLE IF EXISTS `guest_room`;
CREATE TABLE `guest_room`  (
  `id` bigint NOT NULL,
  `hotel_id` bigint NOT NULL,
  `room_type_id` bigint NOT NULL COMMENT '所属房型',
  `room_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房号',
  `floor` int NULL DEFAULT NULL COMMENT '楼层',
  `area` decimal(6, 2) NULL DEFAULT NULL COMMENT '面积',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态 0-空闲 1-已预订 2-已入住 3-维修 4-停用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_hotel_room_type`(`hotel_id` ASC, `room_type_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '具体客房表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of guest_room
-- ----------------------------
INSERT INTO `guest_room` VALUES (10001, 101, 1001, '801', 8, 35.00, 0, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `guest_room` VALUES (10002, 101, 1001, '802', 8, 35.00, 1, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `guest_room` VALUES (10003, 101, 1002, '803', 8, 38.00, 2, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `guest_room` VALUES (10004, 101, 1002, '804', 8, 38.00, 0, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `guest_room` VALUES (10005, 102, 1003, '501', 5, 28.00, 0, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `guest_room` VALUES (10006, 102, 1003, '502', 5, 28.00, 0, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `guest_room` VALUES (10007, 102, 1004, '505', 5, 32.00, 1, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `guest_room` VALUES (10008, 103, 1005, '1001', 10, 40.00, 2, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `guest_room` VALUES (10009, 103, 1006, '1011', 10, 65.00, 0, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for hotel
-- ----------------------------
DROP TABLE IF EXISTS `hotel`;
CREATE TABLE `hotel`  (
  `id` bigint NOT NULL,
  `name_cn` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '中文名称',
  `name_en` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '英文名称',
  `city_id` bigint NOT NULL COMMENT '所属城市',
  `address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '纬度',
  `star_level` tinyint NULL DEFAULT NULL COMMENT '星级 1-5',
  `brand` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '品牌',
  `score` decimal(3, 1) NULL DEFAULT 0.0 COMMENT '综合评分',
  `review_count` int NULL DEFAULT 0 COMMENT '评论数量',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '酒店描述',
  `checkin_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '14:00' COMMENT '入住时间',
  `checkout_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '12:00' COMMENT '离店时间',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0-下架 1-上架 2-审核中 3-停用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_city`(`city_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_location`(`longitude` ASC, `latitude` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '酒店表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hotel
-- ----------------------------
INSERT INTO `hotel` VALUES (101, '外滩雅致酒店', 'Bund Elegant Hotel', 1, '上海市黄浦区中山东一路88号', 121.490300, 31.241700, 5, 'ELEGANT', 9.1, 1298, '紧邻外滩与地铁站，商务休闲两相宜。', '14:00', '12:00', '021-55556666', 1, '2026-06-05 11:32:54', '2026-06-06 16:34:37', 0);
INSERT INTO `hotel` VALUES (102, '朝阳精选酒店', 'Chaoyang Select Hotel', 2, '北京市朝阳区建国路99号', 116.480500, 39.908700, 4, 'SELECT', 8.6, 854, '交通便利，周边商圈丰富。', '14:00', '12:00', '010-66668888', 1, '2026-06-05 11:32:54', '2026-06-06 16:35:04', 0);
INSERT INTO `hotel` VALUES (103, '西湖奢华酒店', 'West Lake Luxe Hotel', 3, '杭州市西湖区北山街66号', 120.150400, 30.255100, 5, 'LUXE', 9.4, 2103, '步行可达西湖景区。', '15:00', '12:00', '0571-77778888', 1, '2026-06-05 11:32:54', '2026-06-06 16:35:19', 0);
INSERT INTO `hotel` VALUES (104, '广州天河假日酒店', 'Guangzhou Tianhe Holiday', 4, '广州市天河区天河路208号', 113.328900, 23.137600, 4, 'HOLIDAY', 8.8, 1560, '位于天河CBD核心，购物出行方便。', '14:00', '12:00', '020-88889999', 1, '2026-06-05 11:32:54', '2026-06-06 16:35:34', 0);
INSERT INTO `hotel` VALUES (105, '深圳湾海景酒店', 'Shenzhen Bay Seaview Hotel', 5, '深圳市南山区滨海大道3001号', 113.943300, 22.521700, 5, 'SEAVIEW', 9.2, 980, '一线海景，毗邻深圳湾公园。', '14:00', '12:00', '0755-33334444', 1, '2026-06-05 11:32:54', '2026-06-06 16:35:48', 0);
INSERT INTO `hotel` VALUES (106, '成都宽窄巷子精品酒店', 'Chengdu Kuanzhai Boutique', 6, '成都市青羊区宽窄巷子12号', 104.055600, 30.666700, 4, 'BOUTIQUE', 8.9, 1120, '坐落于历史文化街区，体验地道成都生活。', '14:00', '12:00', '028-44445555', 1, '2026-06-05 11:32:54', '2026-06-06 16:35:51', 1);
INSERT INTO `hotel` VALUES (107, '上海浦东丽思卡尔顿酒店', 'The Ritz-Carlton Shanghai, Pudong', 1, '上海市浦东新区陆家嘴世纪大道8号', 121.501800, 31.237300, 5, 'RITZ-CARLTON', 9.6, 3200, '地标性奢华酒店，俯瞰黄浦江。', '15:00', '12:00', '021-20201111', 1, '2026-06-05 11:32:54', '2026-06-06 16:36:05', 0);
INSERT INTO `hotel` VALUES (108, '北京王府井希尔顿酒店', 'Hilton Beijing Wangfujing', 2, '北京市东城区王府井大街8号', 116.411400, 39.914200, 5, 'HILTON', 9.3, 2450, '位于王府井步行街，购物美食触手可及。', '14:00', '12:00', '010-58128888', 1, '2026-06-05 11:32:54', '2026-06-06 16:36:18', 0);

-- ----------------------------
-- Table structure for hotel_facility
-- ----------------------------
DROP TABLE IF EXISTS `hotel_facility`;
CREATE TABLE `hotel_facility`  (
  `id` bigint NOT NULL,
  `hotel_id` bigint NOT NULL,
  `facility_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_hotel_facility`(`hotel_id` ASC, `facility_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '酒店设施关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hotel_facility
-- ----------------------------
INSERT INTO `hotel_facility` VALUES (1, 101, 1);
INSERT INTO `hotel_facility` VALUES (4, 101, 2);
INSERT INTO `hotel_facility` VALUES (2, 101, 3);
INSERT INTO `hotel_facility` VALUES (3, 101, 4);
INSERT INTO `hotel_facility` VALUES (5, 102, 1);
INSERT INTO `hotel_facility` VALUES (6, 102, 7);
INSERT INTO `hotel_facility` VALUES (7, 102, 8);
INSERT INTO `hotel_facility` VALUES (8, 103, 1);
INSERT INTO `hotel_facility` VALUES (9, 103, 5);
INSERT INTO `hotel_facility` VALUES (10, 103, 6);
INSERT INTO `hotel_facility` VALUES (11, 103, 9);
INSERT INTO `hotel_facility` VALUES (12, 104, 1);
INSERT INTO `hotel_facility` VALUES (13, 104, 3);
INSERT INTO `hotel_facility` VALUES (14, 104, 7);
INSERT INTO `hotel_facility` VALUES (15, 105, 1);
INSERT INTO `hotel_facility` VALUES (18, 105, 2);
INSERT INTO `hotel_facility` VALUES (16, 105, 5);
INSERT INTO `hotel_facility` VALUES (17, 105, 6);
INSERT INTO `hotel_facility` VALUES (19, 106, 1);
INSERT INTO `hotel_facility` VALUES (20, 106, 9);
INSERT INTO `hotel_facility` VALUES (21, 107, 1);
INSERT INTO `hotel_facility` VALUES (26, 107, 2);
INSERT INTO `hotel_facility` VALUES (22, 107, 3);
INSERT INTO `hotel_facility` VALUES (23, 107, 4);
INSERT INTO `hotel_facility` VALUES (24, 107, 5);
INSERT INTO `hotel_facility` VALUES (25, 107, 6);
INSERT INTO `hotel_facility` VALUES (27, 108, 1);
INSERT INTO `hotel_facility` VALUES (28, 108, 3);
INSERT INTO `hotel_facility` VALUES (29, 108, 4);
INSERT INTO `hotel_facility` VALUES (30, 108, 7);

-- ----------------------------
-- Table structure for hotel_image
-- ----------------------------
DROP TABLE IF EXISTS `hotel_image`;
CREATE TABLE `hotel_image`  (
  `id` bigint NOT NULL,
  `hotel_id` bigint NOT NULL,
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `type` tinyint NULL DEFAULT 1 COMMENT '类型 1-主图 2-外观 3-大堂 4-房间 5-餐厅 6-设施',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_hotel`(`hotel_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '酒店图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hotel_image
-- ----------------------------
INSERT INTO `hotel_image` VALUES (1, 101, 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=80', 1, 0, '2026-06-05 11:32:54', 0);
INSERT INTO `hotel_image` VALUES (2, 101, 'https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?auto=format&fit=crop&w=1200&q=80', 2, 1, '2026-06-05 11:32:54', 0);
INSERT INTO `hotel_image` VALUES (3, 102, 'https://images.unsplash.com/photo-1445019980597-93fa8acb246c?auto=format&fit=crop&w=1200&q=80', 1, 0, '2026-06-05 11:32:54', 0);
INSERT INTO `hotel_image` VALUES (4, 102, 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?auto=format&fit=crop&w=1200&q=80', 2, 1, '2026-06-05 11:32:54', 0);
INSERT INTO `hotel_image` VALUES (5, 103, 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=1200&q=80', 1, 0, '2026-06-05 11:32:54', 0);
INSERT INTO `hotel_image` VALUES (6, 103, 'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1200&q=80', 2, 1, '2026-06-05 11:32:54', 0);
INSERT INTO `hotel_image` VALUES (7, 104, 'https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=1200&q=80', 1, 0, '2026-06-05 11:32:54', 0);
INSERT INTO `hotel_image` VALUES (8, 105, 'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=1200&q=80', 1, 0, '2026-06-05 11:32:54', 0);
INSERT INTO `hotel_image` VALUES (9, 106, 'https://images.unsplash.com/photo-1563911302283-d2bc129e7c1f?auto=format&fit=crop&w=1200&q=80', 1, 0, '2026-06-05 11:32:54', 0);
INSERT INTO `hotel_image` VALUES (10, 107, 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=1200&q=80', 1, 0, '2026-06-05 11:32:54', 0);
INSERT INTO `hotel_image` VALUES (11, 108, 'https://images.unsplash.com/photo-1564501049412-61c2a3083791?auto=format&fit=crop&w=1200&q=80', 1, 0, '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for hotel_order
-- ----------------------------
DROP TABLE IF EXISTS `hotel_order`;
CREATE TABLE `hotel_order`  (
  `id` bigint NOT NULL,
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `hotel_id` bigint NOT NULL COMMENT '酒店ID',
  `room_type_id` bigint NOT NULL COMMENT '房型ID',
  `checkin_date` date NOT NULL COMMENT '入住日期',
  `checkout_date` date NOT NULL COMMENT '离店日期',
  `nights` int NOT NULL COMMENT '入住晚数',
  `room_count` int NOT NULL DEFAULT 1 COMMENT '房间数',
  `guest_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '入住人姓名',
  `guest_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '入住人手机号',
  `special_request` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '特殊要求',
  `original_amount` decimal(10, 2) NOT NULL COMMENT '原价',
  `discount_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
  `pay_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态 0-待支付 1-已支付 2-已确认 3-已取消 4-已入住 5-已完成 6-退款申请中 7-已退款 8-退款拒绝',
  `pay_deadline` datetime NULL DEFAULT NULL COMMENT '支付截止时间',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取消原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_hotel`(`hotel_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_pay_deadline`(`pay_deadline` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hotel_order
-- ----------------------------
INSERT INTO `hotel_order` VALUES (1, 'ORD2023101501', 1, 101, 1001, '2026-06-05', '2026-06-06', 1, 1, 'Demo User', '13800138000', NULL, 688.00, 0.00, 650.00, 5, NULL, NULL, NULL, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `hotel_order` VALUES (2, 'ORD2023101502', 2, 101, 1002, '2026-06-05', '2026-06-07', 2, 1, 'Test User', '13900139000', NULL, 1576.00, 0.00, 1500.00, 4, NULL, NULL, NULL, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `hotel_order` VALUES (3, 'ORD2023101503', 1, 102, 1003, '2026-06-10', '2026-06-12', 2, 1, 'Demo User', '13800138000', NULL, 932.00, 0.00, 900.00, 0, NULL, NULL, NULL, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `hotel_order` VALUES (4, 'ORD2023101504', 2, 103, 1005, '2026-06-06', '2026-06-07', 1, 1, 'Alice', '13900001111', NULL, 899.00, 0.00, 850.00, 2, NULL, NULL, NULL, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `hotel_order` VALUES (5, 'ORD2023101505', 1, 104, 1007, '2026-06-15', '2026-06-17', 2, 2, 'Bob', '13811112222', NULL, 2232.00, 0.00, 2200.00, 6, NULL, NULL, NULL, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `hotel_order` VALUES (6, 'ORD2023101506', 2, 105, 1009, '2026-06-02', '2026-06-04', 2, 1, 'Charlie', '13722223333', NULL, 2176.00, 0.00, 2100.00, 5, NULL, NULL, NULL, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for payment_record
-- ----------------------------
DROP TABLE IF EXISTS `payment_record`;
CREATE TABLE `payment_record`  (
  `id` bigint NOT NULL,
  `payment_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付流水号',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `trade_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付宝交易号',
  `amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `status` tinyint NULL DEFAULT 0 COMMENT '支付状态 0-待支付 1-成功 2-失败 3-已退款',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付成功时间',
  `callback_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '回调内容摘要',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_payment_no`(`payment_no` ASC) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment_record
-- ----------------------------
INSERT INTO `payment_record` VALUES (1, 'PAY2023101501', 1, 'ORD2023101501', NULL, 650.00, 1, NULL, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `payment_record` VALUES (2, 'PAY2023101502', 2, 'ORD2023101502', NULL, 1500.00, 1, NULL, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `payment_record` VALUES (3, 'PAY2023101504', 4, 'ORD2023101504', NULL, 850.00, 1, NULL, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `payment_record` VALUES (4, 'PAY2023101505', 5, 'ORD2023101505', NULL, 2200.00, 1, NULL, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `payment_record` VALUES (5, 'PAY2023101506', 6, 'ORD2023101506', NULL, 2100.00, 1, NULL, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for refund_record
-- ----------------------------
DROP TABLE IF EXISTS `refund_record`;
CREATE TABLE `refund_record`  (
  `id` bigint NOT NULL,
  `refund_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '退款编号',
  `order_id` bigint NOT NULL,
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `refund_amount` decimal(10, 2) NOT NULL COMMENT '退款金额',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '退款原因',
  `audit_status` tinyint NULL DEFAULT 0 COMMENT '审核状态 0-待审核 1-通过 2-拒绝',
  `auditor_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime NULL DEFAULT NULL,
  `alipay_refund_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付宝退款流水',
  `refund_time` datetime NULL DEFAULT NULL COMMENT '退款成功时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_refund_no`(`refund_no` ASC) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '退款记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of refund_record
-- ----------------------------
INSERT INTO `refund_record` VALUES (1, 'REF2023101501', 5, 'ORD2023101505', 2200.00, '行程变更', 0, NULL, NULL, NULL, NULL, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for review
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review`  (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `hotel_id` bigint NOT NULL,
  `order_id` bigint NOT NULL,
  `overall_score` decimal(3, 1) NOT NULL COMMENT '综合评分',
  `location_score` decimal(3, 1) NULL DEFAULT NULL COMMENT '位置评分',
  `hygiene_score` decimal(3, 1) NULL DEFAULT NULL COMMENT '卫生评分',
  `service_score` decimal(3, 1) NULL DEFAULT NULL COMMENT '服务评分',
  `facility_score` decimal(3, 1) NULL DEFAULT NULL COMMENT '设施评分',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '文字评价',
  `images` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '评价图片URL，逗号分隔',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0-隐藏 1-正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_hotel`(`hotel_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of review
-- ----------------------------
INSERT INTO `review` VALUES (1, 1, 101, 1, 5.0, 5.0, 5.0, 5.0, 5.0, '酒店环境非常好，前台小哥哥服务热情！强烈推荐外滩景观房。', NULL, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `review` VALUES (2, 2, 102, 2, 4.0, 4.0, 4.0, 3.0, 4.0, '整体还行，就是早餐种类不够多，希望能改进。', NULL, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `review` VALUES (3, 2, 105, 6, 4.8, 5.0, 5.0, 4.5, 4.5, '海景非常无敌！晚上吹海风很舒服。', NULL, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `review` VALUES (4, 1, 108, 7, 4.5, 5.0, 4.5, 4.0, 4.5, '王府井逛街很方便，老牌五星级服务有保障。', NULL, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `review` VALUES (5, 2, 107, 8, 5.0, 5.0, 5.0, 5.0, 5.0, '极致奢华，管家服务让人宾至如归！', NULL, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for review_reply
-- ----------------------------
DROP TABLE IF EXISTS `review_reply`;
CREATE TABLE `review_reply`  (
  `id` bigint NOT NULL,
  `review_id` bigint NOT NULL,
  `admin_id` bigint NOT NULL COMMENT '回复管理员',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '回复内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_review`(`review_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '酒店评价回复表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of review_reply
-- ----------------------------
INSERT INTO `review_reply` VALUES (1, 1, 1, '感谢您的五星好评，期待您再次光临！', '2026-06-05 11:32:54', 0);
INSERT INTO `review_reply` VALUES (2, 2, 1, '非常抱歉给您带来不好的体验，我们会立即向餐饮部反馈并丰富早餐种类。', '2026-06-05 11:32:54', 0);
INSERT INTO `review_reply` VALUES (2062893097005006850, 3, 1, '111', '2026-06-05 21:43:36', 0);

-- ----------------------------
-- Table structure for room_category
-- ----------------------------
DROP TABLE IF EXISTS `room_category`;
CREATE TABLE `room_category`  (
  `id` bigint NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `sort` int NULL DEFAULT 0,
  `status` tinyint NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '房间分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room_category
-- ----------------------------
INSERT INTO `room_category` VALUES (1, '大床房', 1, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_category` VALUES (2, '双床房', 2, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_category` VALUES (3, '套房', 3, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_category` VALUES (4, '家庭房', 4, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for room_inventory_calendar
-- ----------------------------
DROP TABLE IF EXISTS `room_inventory_calendar`;
CREATE TABLE `room_inventory_calendar`  (
  `id` bigint NOT NULL,
  `room_type_id` bigint NOT NULL,
  `date` date NOT NULL COMMENT '日期',
  `total_inventory` int NOT NULL DEFAULT 0 COMMENT '总库存',
  `used_inventory` int NOT NULL DEFAULT 0 COMMENT '已用库存',
  `locked_inventory` int NOT NULL DEFAULT 0 COMMENT '锁定库存（待支付）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_room_date`(`room_type_id` ASC, `date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '房型日期库存表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room_inventory_calendar
-- ----------------------------
INSERT INTO `room_inventory_calendar` VALUES (1, 1001, '2026-06-05', 10, 2, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_inventory_calendar` VALUES (2, 1001, '2026-06-06', 10, 0, 0, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_inventory_calendar` VALUES (3, 1002, '2026-06-05', 5, 1, 0, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_inventory_calendar` VALUES (4, 1003, '2026-06-05', 20, 5, 2, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_inventory_calendar` VALUES (5, 1004, '2026-06-05', 15, 8, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_inventory_calendar` VALUES (6, 1005, '2026-06-05', 8, 4, 0, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for room_price_calendar
-- ----------------------------
DROP TABLE IF EXISTS `room_price_calendar`;
CREATE TABLE `room_price_calendar`  (
  `id` bigint NOT NULL,
  `room_type_id` bigint NOT NULL,
  `date` date NOT NULL COMMENT '日期',
  `price` decimal(10, 2) NOT NULL COMMENT '当日价格',
  `member_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '会员价',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_room_date`(`room_type_id` ASC, `date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '房型日期价格表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room_price_calendar
-- ----------------------------
INSERT INTO `room_price_calendar` VALUES (1, 1001, '2026-06-05', 688.00, 650.00, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_price_calendar` VALUES (2, 1001, '2026-06-06', 688.00, 650.00, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_price_calendar` VALUES (3, 1002, '2026-06-05', 788.00, 750.00, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_price_calendar` VALUES (4, 1003, '2026-06-05', 466.00, 450.00, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_price_calendar` VALUES (5, 1004, '2026-06-05', 528.00, 500.00, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_price_calendar` VALUES (6, 1005, '2026-06-05', 899.00, 850.00, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for room_type
-- ----------------------------
DROP TABLE IF EXISTS `room_type`;
CREATE TABLE `room_type`  (
  `id` bigint NOT NULL,
  `hotel_id` bigint NOT NULL COMMENT '所属酒店',
  `category_id` bigint NULL DEFAULT NULL COMMENT '房间分类',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房型名称',
  `bed_type_id` bigint NULL DEFAULT NULL COMMENT '床型',
  `breakfast_type_id` bigint NULL DEFAULT NULL COMMENT '早餐类型',
  `area` decimal(6, 2) NULL DEFAULT NULL COMMENT '面积（平方米）',
  `max_adults` int NULL DEFAULT 2 COMMENT '最大成人数',
  `max_children` int NULL DEFAULT 1 COMMENT '最大儿童数',
  `cancel_policy` tinyint NULL DEFAULT 1 COMMENT '取消政策 0-不可取消 1-免费取消 2-限时取消',
  `cancel_deadline_hours` int NULL DEFAULT 24 COMMENT '免费取消截止小时数',
  `base_price` decimal(10, 2) NOT NULL COMMENT '基础价格',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '房型描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0-下架 1-上架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_hotel`(`hotel_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '房型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room_type
-- ----------------------------
INSERT INTO `room_type` VALUES (1001, 101, 1, '豪华大床房', 1, 1, 35.00, 2, 1, 1, 24, 688.00, '宽敞舒适，配备高档床品', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_type` VALUES (1002, 101, 2, '行政双床房', 2, 1, 38.00, 2, 1, 1, 24, 788.00, '商务首选，独立办公区', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_type` VALUES (1003, 102, 1, '标准大床房', 1, 3, 28.00, 2, 1, 0, 0, 466.00, '经济实惠，功能齐全', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_type` VALUES (1004, 102, 2, '商务双床房', 2, 4, 32.00, 2, 1, 1, 24, 528.00, '含可选早餐，灵活便捷', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_type` VALUES (1005, 103, 1, '湖景大床房', 1, 1, 40.00, 2, 1, 1, 24, 899.00, '落地窗外即是西湖美景', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_type` VALUES (1006, 103, 3, '家庭套房', 5, 1, 65.00, 2, 2, 1, 24, 1188.00, '两室一厅，亲子出行首选', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_type` VALUES (1007, 104, 1, '高级大床房', 1, 2, 30.00, 2, 1, 1, 24, 558.00, '现代简约设计', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_type` VALUES (1008, 104, 2, '豪华双床房', 2, 1, 35.00, 2, 1, 1, 24, 628.00, '空间宽敞，床品舒适', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_type` VALUES (1009, 105, 1, '海景大床房', 1, 1, 42.00, 2, 1, 1, 24, 1088.00, '180度无敌海景', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_type` VALUES (1010, 105, 3, '海景套房', 5, 1, 80.00, 2, 2, 1, 24, 1888.00, '至尊体验，独立客厅', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_type` VALUES (1011, 106, 1, '庭院大床房', 1, 3, 25.00, 2, 0, 1, 24, 398.00, '传统川西庭院风格', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_type` VALUES (1012, 106, 2, '禅意双床房', 2, 4, 28.00, 2, 1, 1, 24, 458.00, '静谧空间，修身养性', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_type` VALUES (1013, 107, 1, '外滩景观房', 1, 1, 50.00, 2, 1, 1, 24, 2288.00, '直面外滩万国建筑群', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_type` VALUES (1014, 107, 3, '丽思卡尔顿套房', 5, 1, 120.00, 2, 2, 1, 24, 5888.00, '极致奢华，管家服务', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_type` VALUES (1015, 108, 1, '行政大床房', 1, 1, 45.00, 2, 1, 1, 24, 1688.00, '尊享行政酒廊礼遇', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `room_type` VALUES (1016, 108, 2, '豪华双床房', 2, 1, 45.00, 2, 1, 1, 24, 1588.00, '经典希尔顿品质', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for room_type_image
-- ----------------------------
DROP TABLE IF EXISTS `room_type_image`;
CREATE TABLE `room_type_image`  (
  `id` bigint NOT NULL,
  `room_type_id` bigint NOT NULL,
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort` int NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_room_type`(`room_type_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '房型图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room_type_image
-- ----------------------------

-- ----------------------------
-- Table structure for sys_admin
-- ----------------------------
DROP TABLE IF EXISTS `sys_admin`;
CREATE TABLE `sys_admin`  (
  `id` bigint NOT NULL COMMENT '管理员ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录账号',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密存储）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `hotel_id` bigint NULL DEFAULT NULL COMMENT '所属酒店ID（酒店管理员必填）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_admin
-- ----------------------------
INSERT INTO `sys_admin` VALUES (1, 'admin', 'admin123', '超级管理员', NULL, NULL, NULL, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` bigint NOT NULL,
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父权限ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码',
  `type` tinyint NULL DEFAULT 1 COMMENT '类型 1-菜单 2-按钮 3-接口',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由路径',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, 0, '系统管理', 'sys:manage', 1, '/system', 'setting', 99, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `sys_permission` VALUES (2, 1, '用户管理', 'sys:user:list', 1, '/system/user', 'user', 1, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `sys_permission` VALUES (3, 1, '角色管理', 'sys:role:list', 1, '/system/role', 'team', 2, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `sys_permission` VALUES (4, 0, '酒店管理', 'hotel:manage', 1, '/hotel', 'home', 10, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `sys_permission` VALUES (5, 4, '酒店列表', 'hotel:list', 1, '/hotel/list', 'list', 1, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `sys_permission` VALUES (6, 4, '房型管理', 'hotel:room_type', 1, '/hotel/room_type', 'appstore', 2, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL,
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `status` tinyint NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '系统管理员', 'ROLE_SYS_ADMIN', '系统最高权限管理员', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `sys_role` VALUES (2, '酒店管理员', 'ROLE_HOTEL_ADMIN', '酒店业务管理员', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `sys_role` VALUES (3, '普通用户', 'ROLE_USER', '注册用户', 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_permission`(`role_id` ASC, `permission_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (1, 1, 1);
INSERT INTO `sys_role_permission` VALUES (2, 1, 2);
INSERT INTO `sys_role_permission` VALUES (3, 1, 3);
INSERT INTO `sys_role_permission` VALUES (4, 1, 4);
INSERT INTO `sys_role_permission` VALUES (5, 1, 5);
INSERT INTO `sys_role_permission` VALUES (6, 1, 6);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL COMMENT '用户ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码（加密存储）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
  `points` int NULL DEFAULT 0 COMMENT '积分',
  `level_id` bigint NULL DEFAULT NULL COMMENT '会员等级ID',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '普通用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, '13800138000', 'demo@hotel.com', '123456', 'Demo User', '/uploads/43b8defc-b8ff-4928-aaea-756d752a014d.png', 0, 331, 1, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `sys_user` VALUES (2, '13900139000', 'test@hotel.com', '123456', 'Test User', NULL, 1, 500, 1, 1, '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL COMMENT '用户/管理员ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `user_type` tinyint NULL DEFAULT 1 COMMENT '用户类型 1-普通用户 2-管理员',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC, `user_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1, 2);
INSERT INTO `sys_user_role` VALUES (2, 1, 3, 1);
INSERT INTO `sys_user_role` VALUES (3, 2, 3, 1);

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`  (
  `id` bigint NOT NULL,
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '配置值',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_config
-- ----------------------------
INSERT INTO `system_config` VALUES (1, 'system_name', '酒店辅助订购系统', '系统名称', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `system_config` VALUES (2, 'order_timeout', '30', '订单支付超时时间(分钟)', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `system_config` VALUES (3, 'points_rate', '10', '每消费10元积1分', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `system_config` VALUES (4, 'contact_email', 'support@hotel-booking.com', '客服邮箱', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for user_level
-- ----------------------------
DROP TABLE IF EXISTS `user_level`;
CREATE TABLE `user_level`  (
  `id` bigint NOT NULL,
  `level_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '等级名称',
  `min_points` int NOT NULL COMMENT '最低积分',
  `max_points` int NOT NULL COMMENT '最高积分',
  `discount_rate` decimal(4, 2) NOT NULL COMMENT '折扣比例（如0.95表示95折）',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户等级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_level
-- ----------------------------
INSERT INTO `user_level` VALUES (1, '普通会员', 0, 999, 1.00, '无额外优惠', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `user_level` VALUES (2, '银卡会员', 1000, 2999, 0.99, '订单99折', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `user_level` VALUES (3, '金卡会员', 3000, 7999, 0.97, '订单97折', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);
INSERT INTO `user_level` VALUES (4, '铂金会员', 8000, 999999, 0.95, '订单95折', '2026-06-05 11:32:54', '2026-06-05 11:32:54', 0);

-- ----------------------------
-- Table structure for user_points_log
-- ----------------------------
DROP TABLE IF EXISTS `user_points_log`;
CREATE TABLE `user_points_log`  (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `points` int NOT NULL COMMENT '积分变动值（正加负减）',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型（order/register/review/refund）',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联ID（订单ID等）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户积分流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_points_log
-- ----------------------------
INSERT INTO `user_points_log` VALUES (1, 1, 100, 'register', '新用户注册奖励', NULL, '2026-06-05 11:32:54');
INSERT INTO `user_points_log` VALUES (2, 1, 20, 'order', '订单完成积分奖励', 1, '2026-06-05 11:32:54');
INSERT INTO `user_points_log` VALUES (3, 2, 500, 'order', '大额订单完成积分奖励', 6, '2026-06-05 11:32:54');
INSERT INTO `user_points_log` VALUES (4, 1, -10, 'exchange', '积分商城兑换抵扣', NULL, '2026-06-05 11:32:54');
INSERT INTO `user_points_log` VALUES (2063178536102281218, 1, 111, 'admin_recharge', '111', NULL, '2026-06-06 16:37:50');
INSERT INTO `user_points_log` VALUES (2063178561951776769, 1, 100, 'admin_recharge', '', NULL, '2026-06-06 16:37:56');

SET FOREIGN_KEY_CHECKS = 1;
