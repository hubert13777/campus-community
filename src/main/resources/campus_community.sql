/*
 Navicat Premium Data Transfer

 Source Server         : 腾讯云-hubert
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : 42.192.198.61:3306
 Source Schema         : campus_community

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 14/10/2022 15:31:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for Comment
-- ----------------------------
DROP TABLE IF EXISTS `Comment`;
CREATE TABLE `Comment`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NOT NULL,
  `entity_type` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '1表示帖子，2表示评论',
  `entity_id` int(0) NULL DEFAULT NULL,
  `target_id` int(0) NULL DEFAULT NULL,
  `content` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `status` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态，0正常，1拉黑',
  `create_time` datetime(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for DiscussPost
-- ----------------------------
DROP TABLE IF EXISTS `DiscussPost`;
CREATE TABLE `DiscussPost`  (
  `postId` int(0) NOT NULL AUTO_INCREMENT,
  `userId` int(0) NOT NULL,
  `title` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `content` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `type` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '0普通，1置顶',
  `status` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '0正常，1精华，2拉黑',
  `createTime` datetime(0) NULL DEFAULT NULL,
  `likeCount` int(0) NULL DEFAULT NULL COMMENT '点赞数量',
  `commentCount` int(0) NULL DEFAULT 0 COMMENT '评论数',
  `score` double NULL DEFAULT NULL COMMENT '帖子评分',
  PRIMARY KEY (`postId`) USING BTREE,
  INDEX `userId_foreignKey`(`userId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for LoginTicket
-- ----------------------------
DROP TABLE IF EXISTS `LoginTicket`;
CREATE TABLE `LoginTicket`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `userId` int(0) NOT NULL,
  `ticket` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `status` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '0有效，1无效',
  `expired` timestamp(0) NOT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_ticket`(`ticket`(20)) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for Message
-- ----------------------------
DROP TABLE IF EXISTS `Message`;
CREATE TABLE `Message`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `from_id` int(0) NOT NULL,
  `to_id` int(0) NOT NULL,
  `conversation_id` varchar(22) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `content` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `status` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_conversation`(`conversation_id`(5)) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for User
-- ----------------------------
DROP TABLE IF EXISTS `User`;
CREATE TABLE `User`  (
  `userId` int(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '新用户' COMMENT '用户名，最多20字',
  `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码，经过加密的结果',
  `salt` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '盐值，辅助加密',
  `email` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `code` varchar(6) CHARACTER SET utf8 COLLATE utf8_esperanto_ci NULL DEFAULT NULL COMMENT '验证码，用于邮箱校验',
  `type` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '0普通用户，1管理员，2版主',
  `status` varchar(1) CHARACTER SET utf8 COLLATE utf8_esperanto_ci NULL DEFAULT NULL COMMENT '0未激活，1正常，2封禁',
  `headImageUrl` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像图片地址',
  `createTime` datetime(0) NULL DEFAULT NULL COMMENT '注册时间',
  PRIMARY KEY (`userId`) USING BTREE,
  INDEX `index_username`(`username`(6)) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_esperanto_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of User
-- ----------------------------
INSERT INTO `User` VALUES (0, '系统', '1c259756b902b02601eacc9d647ea56a', '36c50', NULL, NULL, '1', '1', 'http://images.nowcoder.com/head/110t.png', '2022-10-14 12:21:22');
INSERT INTO `User` VALUES (1, 'hubert', '57a9ea2aace7f5f17198fd6c62f179d7', '6ff6e', '1207820441@qq.com', '5bf662', '0', '1', 'http://localhost:8081/community/user/header/d9cc7bf4b63b449a9eb6ed2149581afb.jpeg', '2022-09-11 20:50:36');
INSERT INTO `User` VALUES (2, '慎独', '8d0ee984b762e83d557d56326f9e0744', '30962', 'hubert137@163.com', '85517a', '0', '1', 'http://localhost:8081/community/user/header/3644e393da42454a9e2293820a603bec.jpg', '2022-09-22 16:35:11');

SET FOREIGN_KEY_CHECKS = 1;