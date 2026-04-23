/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80043
 Source Host           : localhost:3306
 Source Schema         : baise_star_anise

 Target Server Type    : MySQL
 Target Server Version : 80043
 File Encoding         : 65001

 Date: 23/04/2026 18:25:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for app_user
-- ----------------------------
DROP TABLE IF EXISTS `app_user`;
CREATE TABLE `app_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `openid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信小程序OpenID',
  `unionid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信UnionID',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名/手机号',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '加密密码',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `role_type` tinyint NOT NULL DEFAULT 1 COMMENT '角色类型 1:C端消费者 2:B端采购商 3:入驻商家 4:平台管理员',
  `real_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '账号状态 0:禁用 1:正常',
  `extra_json` json NULL COMMENT '扩展字段',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0:未删 1:已删',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_openid`(`openid`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_user
-- ----------------------------
INSERT INTO `app_user` VALUES (1, NULL, NULL, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '八角果园管理员', NULL, 4, '百色果农', '13800138000', 1, NULL, '2026-04-14 10:44:47', '2026-04-14 10:44:47', 0);
INSERT INTO `app_user` VALUES (2, 'oXxxxxxTestOpenIdxxx', NULL, NULL, NULL, '测试买家', NULL, 1, NULL, '13900139000', 1, NULL, '2026-04-14 10:44:47', '2026-04-14 10:44:47', 0);

-- ----------------------------
-- Table structure for biz_address
-- ----------------------------
DROP TABLE IF EXISTS `biz_address`;
CREATE TABLE `biz_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `real_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `phone` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人电话',
  `province` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '省份',
  `city` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '城市',
  `district` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区县',
  `detail_address` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认 0:否 1:是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0:未删 1:已删',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收货地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_address
-- ----------------------------
INSERT INTO `biz_address` VALUES (1, 2, '张三', '13900139000', '广东省', '深圳市', '南山区', '科技园XX大厦1001', 1, '2026-04-14 10:44:47', '2026-04-14 10:44:47', 0);

-- ----------------------------
-- Table structure for biz_after_sale
-- ----------------------------
DROP TABLE IF EXISTS `biz_after_sale`;
CREATE TABLE `biz_after_sale`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `after_sale_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '售后单号',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` tinyint NOT NULL COMMENT '售后类型 1:仅退款 2:退货退款 3:坏果赔付',
  `reason` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '售后原因',
  `evidence_images` json NULL COMMENT '凭证图片',
  `refund_amount` decimal(10, 2) NOT NULL COMMENT '退款金额',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0:待处理 1:同意 2:拒绝 3:完成',
  `handle_remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0:未删 1:已删',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_after_sale_no`(`after_sale_no`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '售后申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_after_sale
-- ----------------------------

-- ----------------------------
-- Table structure for biz_agent
-- ----------------------------
DROP TABLE IF EXISTS `biz_agent`;
CREATE TABLE `biz_agent`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '代理用户ID',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '上级代理ID',
  `level` tinyint NOT NULL DEFAULT 1 COMMENT '代理等级',
  `commission_rate` decimal(5, 2) NOT NULL DEFAULT 0.00 COMMENT '佣金比例(%)',
  `total_income` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '累计收益',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 0:禁用 1:正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0:未删 1:已删',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '分销代理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_agent
-- ----------------------------

-- ----------------------------
-- Table structure for biz_category
-- ----------------------------
DROP TABLE IF EXISTS `biz_category`;
CREATE TABLE `biz_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父分类ID 0=一级',
  `category_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `category_icon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类图标',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序值',
  `extra_json` json NULL COMMENT '扩展字段',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0:未删 1:已删',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_category
-- ----------------------------
INSERT INTO `biz_category` VALUES (1, 0, '八角干货', '/upload/category/anise.png', 1, NULL, '2026-04-14 10:44:47', '2026-04-14 10:44:47', 0);

-- ----------------------------
-- Table structure for biz_finance_log
-- ----------------------------
DROP TABLE IF EXISTS `biz_finance_log`;
CREATE TABLE `biz_finance_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `log_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '流水编号',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `merchant_id` bigint NULL DEFAULT NULL COMMENT '商家ID',
  `type` tinyint NOT NULL COMMENT '流水类型 1:收入 2:退款 3:提现 4:佣金',
  `amount` decimal(10, 2) NOT NULL COMMENT '变动金额',
  `balance` decimal(10, 2) NOT NULL COMMENT '变动后余额',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID',
  `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0:未删 1:已删',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_log_no`(`log_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '财务流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_finance_log
-- ----------------------------
INSERT INTO `biz_finance_log` VALUES (1, 'FN202604140001', 2, 1, 1, 45.00, 45.00, 1, '订单BX202604140001收入', '2026-04-14 10:44:47', '2026-04-14 10:44:47', 0);

-- ----------------------------
-- Table structure for biz_merchant
-- ----------------------------
DROP TABLE IF EXISTS `biz_merchant`;
CREATE TABLE `biz_merchant`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '关联管理员用户ID',
  `merchant_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '果园/商家名称',
  `contact_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系人',
  `contact_phone` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `origin_place` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '产地地址',
  `license_image` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业执照URL',
  `food_license_image` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '食品经营许可证URL',
  `audit_status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态 0:待审核 1:通过 2:拒绝',
  `audit_remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注',
  `commission_rate` decimal(5, 2) NOT NULL DEFAULT 3.00 COMMENT '平台抽成比例(%)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 0:禁用 1:正常',
  `extra_json` json NULL COMMENT '扩展字段',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0:未删 1:已删',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商家/农户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_merchant
-- ----------------------------
INSERT INTO `biz_merchant` VALUES (1, 1, '百色田阳自家八角种植园', '李先生', '13800138000', '广西百色市田阳区', '/upload/license/demo.jpg', '/upload/food_license/demo.jpg', 1, NULL, 0.00, 1, NULL, '2026-04-14 10:44:47', '2026-04-14 10:44:47', 0);

-- ----------------------------
-- Table structure for biz_order
-- ----------------------------
DROP TABLE IF EXISTS `biz_order`;
CREATE TABLE `biz_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `order_type` tinyint NOT NULL DEFAULT 1 COMMENT '订单类型 1:零售 2:批发',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
  `freight_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '运费',
  `discount_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
  `pay_type` tinyint NULL DEFAULT NULL COMMENT '支付方式 1:微信支付 2:线下转账',
  `pay_status` tinyint NOT NULL DEFAULT 0 COMMENT '支付状态 0:待支付 1:已支付 2:已退款',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `delivery_status` tinyint NOT NULL DEFAULT 0 COMMENT '发货状态 0:待发货 1:已发货 2:已收货',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime NULL DEFAULT NULL COMMENT '收货时间',
  `address_id` bigint NOT NULL COMMENT '收货地址ID',
  `address_snapshot` json NOT NULL COMMENT '地址快照',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单备注',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '订单状态 0:取消 1:待支付 2:待发货 3:已发货 4:完成 5:售后中',
  `extra_json` json NULL COMMENT '扩展字段',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0:未删 1:已删',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_order_no`(`order_no`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_merchant_id`(`merchant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_order
-- ----------------------------
INSERT INTO `biz_order` VALUES (1, 'BX202604140001', 2, 1, 1, 45.00, 45.00, 0.00, 0.00, 1, 1, '2026-04-14 10:44:47', 0, NULL, NULL, 1, '{\"city\": \"深圳市\", \"phone\": \"13900139000\", \"district\": \"南山区\", \"province\": \"广东省\", \"real_name\": \"张三\", \"detail_address\": \"科技园XX大厦1001\"}', '尽快发货', 2, NULL, '2026-04-14 10:44:47', '2026-04-14 10:44:47', 0);

-- ----------------------------
-- Table structure for biz_order_item
-- ----------------------------
DROP TABLE IF EXISTS `biz_order_item`;
CREATE TABLE `biz_order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `sku_id` bigint NOT NULL COMMENT '规格ID',
  `product_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称快照',
  `spec_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规格名称快照',
  `main_image` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品主图快照',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `quantity` int NOT NULL COMMENT '购买数量',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '小计金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0:未删 1:已删',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_order_item
-- ----------------------------
INSERT INTO `biz_order_item` VALUES (1, 1, 1, 2, '百色无硫大红八角', '500g/袋', '/upload/product/anise_main.jpg', 45.00, 1, 45.00, '2026-04-14 10:44:47', '2026-04-14 10:44:47', 0);

-- ----------------------------
-- Table structure for biz_product
-- ----------------------------
DROP TABLE IF EXISTS `biz_product`;
CREATE TABLE `biz_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `product_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `product_title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品卖点',
  `main_image` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品主图',
  `image_list` json NOT NULL COMMENT '轮播图列表',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品详情',
  `origin_place` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '产地',
  `is_sulfur_free` tinyint NOT NULL DEFAULT 1 COMMENT '是否无硫 0:否 1:是',
  `drying_level` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '干度',
  `planting_process` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '种植工艺',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 0:下架 1:上架',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序值',
  `extra_json` json NULL COMMENT '扩展字段',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0:未删 1:已删',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_merchant_id`(`merchant_id`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_product
-- ----------------------------
INSERT INTO `biz_product` VALUES (1, 1, 1, '百色无硫大红八角', '自家果园足干无硫 香味浓郁', '/upload/product/anise_main.jpg', '[\"/upload/product/anise_1.jpg\", \"/upload/product/anise_2.jpg\", \"/upload/product/anise_3.jpg\"]', '<p>产地：广西百色田阳</p><p>工艺：自然晾晒，无硫烘干</p><p>等级：足干大红八角</p>', '广西百色市田阳区', 1, '足干9.5成', '传统种植 自然晾晒', 1, 0, NULL, '2026-04-14 10:44:47', '2026-04-14 10:44:47', 0);

-- ----------------------------
-- Table structure for biz_product_sku
-- ----------------------------
DROP TABLE IF EXISTS `biz_product_sku`;
CREATE TABLE `biz_product_sku`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `spec_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规格名称',
  `spec_values` json NOT NULL COMMENT '规格属性',
  `price` decimal(10, 2) NOT NULL COMMENT '销售价',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '划线原价',
  `wholesale_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '批发价',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存',
  `sales` int NOT NULL DEFAULT 0 COMMENT '销量',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0:未删 1:已删',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_id`(`product_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品规格库存表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_product_sku
-- ----------------------------
INSERT INTO `biz_product_sku` VALUES (1, 1, '50g/袋', '{\"包装\": \"食品袋\", \"重量\": \"50g\"}', 9.90, 15.00, NULL, 1000, 0, 1, '2026-04-14 10:44:47', '2026-04-14 10:44:47', 0);
INSERT INTO `biz_product_sku` VALUES (2, 1, '500g/袋', '{\"包装\": \"食品袋\", \"重量\": \"500g\"}', 45.00, 59.00, 39.00, 500, 0, 1, '2026-04-14 10:44:47', '2026-04-14 10:44:47', 0);
INSERT INTO `biz_product_sku` VALUES (3, 1, '10kg/箱', '{\"包装\": \"纸箱\", \"重量\": \"10kg\"}', 780.00, 880.00, 720.00, 100, 0, 1, '2026-04-14 10:44:47', '2026-04-14 10:44:47', 0);

-- ----------------------------
-- Table structure for sys_btn
-- ----------------------------
DROP TABLE IF EXISTS `sys_btn`;
CREATE TABLE `sys_btn`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `btn_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '按钮名称',
  `btn_key` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '菜单下的按钮权限通用key',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_btn
-- ----------------------------
INSERT INTO `sys_btn` VALUES (1, '删除', 'del', '删除', 1, '2023-12-04 21:31:14');
INSERT INTO `sys_btn` VALUES (2, '添加', 'add', '添加', 1, '2023-12-04 21:31:21');
INSERT INTO `sys_btn` VALUES (3, '编辑', 'edit', '编辑', 1, '2023-12-04 21:31:28');
INSERT INTO `sys_btn` VALUES (4, '查看', 'view', '查看', 1, '2023-12-04 22:25:20');

-- ----------------------------
-- Table structure for sys_login_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_info`;
CREATE TABLE `sys_login_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `login_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '登录账号',
  `ipaddr` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '登录IP地址',
  `location` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '登录地点',
  `browser` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '浏览器类型',
  `os` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '操作系统',
  `status` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '提示消息',
  `create_time` datetime NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_login_info
-- ----------------------------
INSERT INTO `sys_login_info` VALUES (2, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2025-11-28 17:16:24');
INSERT INTO `sys_login_info` VALUES (4, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-14 10:55:57');
INSERT INTO `sys_login_info` VALUES (5, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-14 13:38:16');
INSERT INTO `sys_login_info` VALUES (6, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-14 14:26:38');
INSERT INTO `sys_login_info` VALUES (7, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-14 14:34:47');
INSERT INTO `sys_login_info` VALUES (8, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-14 14:38:06');
INSERT INTO `sys_login_info` VALUES (9, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-14 14:51:04');
INSERT INTO `sys_login_info` VALUES (10, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-14 14:55:13');
INSERT INTO `sys_login_info` VALUES (11, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '退出登录', '2026-04-14 14:57:26');
INSERT INTO `sys_login_info` VALUES (12, 'aaaaa', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '1', '账号或密码错误', '2026-04-14 14:57:35');
INSERT INTO `sys_login_info` VALUES (13, 'aaaaa', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-14 14:57:39');
INSERT INTO `sys_login_info` VALUES (14, 'aaaaa', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '退出登录', '2026-04-14 14:57:48');
INSERT INTO `sys_login_info` VALUES (15, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-16 15:55:40');
INSERT INTO `sys_login_info` VALUES (16, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-21 13:38:03');
INSERT INTO `sys_login_info` VALUES (17, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-21 13:39:43');
INSERT INTO `sys_login_info` VALUES (18, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-21 13:40:47');
INSERT INTO `sys_login_info` VALUES (19, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-21 13:41:49');
INSERT INTO `sys_login_info` VALUES (20, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-21 13:42:51');
INSERT INTO `sys_login_info` VALUES (21, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-21 14:02:40');
INSERT INTO `sys_login_info` VALUES (22, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-23 10:07:27');
INSERT INTO `sys_login_info` VALUES (23, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '1', '验证码错误 | 密码错误，还剩5次尝试机会', '2026-04-23 13:56:35');
INSERT INTO `sys_login_info` VALUES (24, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '1', '验证码错误 | 密码错误，还剩5次尝试机会', '2026-04-23 13:56:36');
INSERT INTO `sys_login_info` VALUES (25, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-23 13:56:50');
INSERT INTO `sys_login_info` VALUES (26, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '退出登录', '2026-04-23 13:58:05');
INSERT INTO `sys_login_info` VALUES (27, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-23 13:58:10');
INSERT INTO `sys_login_info` VALUES (28, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '退出登录', '2026-04-23 13:59:37');
INSERT INTO `sys_login_info` VALUES (29, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-23 13:59:49');
INSERT INTO `sys_login_info` VALUES (30, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-23 14:01:21');
INSERT INTO `sys_login_info` VALUES (31, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '1', '验证码错误 | 密码错误，还剩5次尝试机会', '2026-04-23 14:01:43');
INSERT INTO `sys_login_info` VALUES (32, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-23 14:02:11');
INSERT INTO `sys_login_info` VALUES (33, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-23 14:03:05');
INSERT INTO `sys_login_info` VALUES (34, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-23 14:05:25');
INSERT INTO `sys_login_info` VALUES (35, 'test', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-23 14:07:41');
INSERT INTO `sys_login_info` VALUES (36, 'test', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '退出登录', '2026-04-23 14:08:13');
INSERT INTO `sys_login_info` VALUES (37, 'test', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-23 14:08:24');
INSERT INTO `sys_login_info` VALUES (38, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-23 15:41:27');
INSERT INTO `sys_login_info` VALUES (39, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-23 16:26:18');
INSERT INTO `sys_login_info` VALUES (40, 'admin', '127.0.0.1', '内网IP', 'MSEdge', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2026-04-23 17:30:27');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父级ID',
  `menu_type` int NULL DEFAULT 1 COMMENT '0=目录，1=菜单，2-按钮',
  `menu_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '菜单名称',
  `menu_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '菜单地址',
  `permit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '菜单许可Key',
  `sort_num` int NULL DEFAULT 1 COMMENT '排序',
  `icon` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '菜单icon',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, 0, '系统管理', '', NULL, 0, 'fa fa-gears', 1, '2023-12-01 21:31:35');
INSERT INTO `sys_menu` VALUES (2, 0, 0, '日志', 'test', NULL, 1, 'fa fa-hand-peace-o', 1, '2023-12-02 22:20:39');
INSERT INTO `sys_menu` VALUES (3, 1, 1, '菜单列表', 'system/menu/page', 'system:menu:list', 1, 'fa fa-circle-o', 1, '2023-12-01 21:32:57');
INSERT INTO `sys_menu` VALUES (4, 1, 1, '公用按钮', 'system/btn/page', 'system:btn:list', 1, NULL, 1, '2023-12-04 21:30:52');
INSERT INTO `sys_menu` VALUES (5, 1, 1, '用户列表', 'system/user/page', 'system:user:list', 2, 'fa fa-circle-o', NULL, NULL);
INSERT INTO `sys_menu` VALUES (6, 1, 1, '角色列表', 'system/role/page', 'system:role:list', 3, 'fa fa-circle-o', 1, '2023-12-02 22:24:05');
INSERT INTO `sys_menu` VALUES (7, 3, 2, 'del', NULL, 'system:menu:list:del', 1, NULL, 1, '2023-12-04 21:40:11');
INSERT INTO `sys_menu` VALUES (8, 3, 2, 'add', NULL, 'system:menu:list:add', 1, NULL, 1, '2023-12-04 21:40:11');
INSERT INTO `sys_menu` VALUES (9, 3, 2, 'edit', NULL, 'system:menu:list:edit', 1, NULL, 1, '2023-12-04 21:40:11');
INSERT INTO `sys_menu` VALUES (10, 3, 2, 'view', NULL, 'system:menu:list:view', 1, NULL, 1, '2023-12-04 22:25:31');
INSERT INTO `sys_menu` VALUES (11, 5, 2, '权限赋予', NULL, 'system:user:list:grant', 1, NULL, 7, '2023-12-04 23:10:20');
INSERT INTO `sys_menu` VALUES (12, 5, 2, 'del', NULL, 'system:user:list:del', 1, NULL, 1, '2023-12-04 22:49:41');
INSERT INTO `sys_menu` VALUES (13, 5, 2, 'add', NULL, 'system:user:list:add', 1, NULL, 1, '2023-12-04 22:49:41');
INSERT INTO `sys_menu` VALUES (14, 5, 2, 'edit', NULL, 'system:user:list:edit', 1, NULL, 1, '2023-12-04 22:49:42');
INSERT INTO `sys_menu` VALUES (15, 5, 2, 'view', NULL, 'system:user:list:view', 1, NULL, 1, '2023-12-04 22:49:42');
INSERT INTO `sys_menu` VALUES (16, 2, 1, '操作日志', 'system/sysOperLog/page', 'system:sysOperLog:list', 1, NULL, 1, '2025-11-28 16:48:34');
INSERT INTO `sys_menu` VALUES (17, 2, 1, '登录日志', 'system/sysLoginInfo/page', 'system:sysLoginInfo:list', 1, NULL, 1, '2025-11-28 16:49:17');
INSERT INTO `sys_menu` VALUES (18, 6, 2, 'del', NULL, 'system:role:list:del', 1, NULL, 1, '2023-12-04 22:49:53');
INSERT INTO `sys_menu` VALUES (19, 6, 2, 'add', NULL, 'system:role:list:add', 1, NULL, 1, '2023-12-04 22:49:53');
INSERT INTO `sys_menu` VALUES (20, 6, 2, 'edit', NULL, 'system:role:list:edit', 1, NULL, 1, '2023-12-04 22:49:53');
INSERT INTO `sys_menu` VALUES (21, 6, 2, 'view', NULL, 'system:role:list:view', 1, NULL, 1, '2023-12-04 22:49:53');
INSERT INTO `sys_menu` VALUES (22, 4, 2, 'del', NULL, 'system:btn:list:del', 1, NULL, 1, '2023-12-04 22:57:53');
INSERT INTO `sys_menu` VALUES (23, 4, 2, 'add', NULL, 'system:btn:list:add', 1, NULL, 1, '2023-12-04 22:57:53');
INSERT INTO `sys_menu` VALUES (24, 4, 2, 'edit', NULL, 'system:btn:list:edit', 1, NULL, 1, '2023-12-04 22:57:53');
INSERT INTO `sys_menu` VALUES (25, 4, 2, 'view', NULL, 'system:btn:list:view', 1, NULL, 1, '2023-12-04 22:57:53');
INSERT INTO `sys_menu` VALUES (26, 16, 2, '删除', NULL, 'system:sysOperLog:list:del', 1, NULL, 1, '2025-11-28 16:48:38');
INSERT INTO `sys_menu` VALUES (27, 16, 2, '添加', NULL, 'system:sysOperLog:list:add', 1, NULL, 1, '2025-11-28 16:48:38');
INSERT INTO `sys_menu` VALUES (28, 16, 2, '编辑', NULL, 'system:sysOperLog:list:edit', 1, NULL, 1, '2025-11-28 16:48:38');
INSERT INTO `sys_menu` VALUES (29, 16, 2, '查看', NULL, 'system:sysOperLog:list:view', 1, NULL, 1, '2025-11-28 16:48:38');
INSERT INTO `sys_menu` VALUES (30, 17, 2, '删除', NULL, 'system:sysLoginInfo:list:del', 1, NULL, 1, '2025-11-28 16:49:17');
INSERT INTO `sys_menu` VALUES (31, 17, 2, '添加', NULL, 'system:sysLoginInfo:list:add', 1, NULL, 1, '2025-11-28 16:49:17');
INSERT INTO `sys_menu` VALUES (32, 17, 2, '编辑', NULL, 'system:sysLoginInfo:list:edit', 1, NULL, 1, '2025-11-28 16:49:17');
INSERT INTO `sys_menu` VALUES (33, 17, 2, '查看', NULL, 'system:sysLoginInfo:list:view', 1, NULL, 1, '2025-11-28 16:49:17');
INSERT INTO `sys_menu` VALUES (37, 0, 0, '业务', NULL, NULL, 1, 'fa fa-list', 1, '2026-04-23 16:29:41');
INSERT INTO `sys_menu` VALUES (38, 37, 1, '用户管理', 'business/appUser/page', 'business:appUser:list', 1, NULL, 1, '2026-04-23 16:31:23');
INSERT INTO `sys_menu` VALUES (39, 38, 2, '删除', NULL, 'business:appUser:list:del', 1, NULL, 1, '2026-04-23 16:31:23');
INSERT INTO `sys_menu` VALUES (40, 38, 2, '添加', NULL, 'business:appUser:list:add', 1, NULL, 1, '2026-04-23 16:31:23');
INSERT INTO `sys_menu` VALUES (41, 38, 2, '编辑', NULL, 'business:appUser:list:edit', 1, NULL, 1, '2026-04-23 16:31:23');
INSERT INTO `sys_menu` VALUES (42, 38, 2, '查看', NULL, 'business:appUser:list:view', 1, NULL, 1, '2026-04-23 16:31:23');
INSERT INTO `sys_menu` VALUES (43, 37, 1, '收货地址', 'business/bizAddress/page', 'business:bizAddress:list', 1, NULL, 1, '2026-04-23 16:32:50');
INSERT INTO `sys_menu` VALUES (44, 43, 2, '删除', NULL, 'business:bizAddress:list:del', 1, NULL, 1, '2026-04-23 16:32:50');
INSERT INTO `sys_menu` VALUES (45, 43, 2, '添加', NULL, 'business:bizAddress:list:add', 1, NULL, 1, '2026-04-23 16:32:50');
INSERT INTO `sys_menu` VALUES (46, 43, 2, '编辑', NULL, 'business:bizAddress:list:edit', 1, NULL, 1, '2026-04-23 16:32:50');
INSERT INTO `sys_menu` VALUES (47, 43, 2, '查看', NULL, 'business:bizAddress:list:view', 1, NULL, 1, '2026-04-23 16:32:50');
INSERT INTO `sys_menu` VALUES (48, 37, 1, '商品分类', 'business/bizCategory/page', 'business:bizCategory:list', 1, NULL, 1, '2026-04-23 16:33:34');
INSERT INTO `sys_menu` VALUES (49, 48, 2, '删除', NULL, 'business:bizCategory:list:del', 1, NULL, 1, '2026-04-23 16:33:34');
INSERT INTO `sys_menu` VALUES (50, 48, 2, '添加', NULL, 'business:bizCategory:list:add', 1, NULL, 1, '2026-04-23 16:33:34');
INSERT INTO `sys_menu` VALUES (51, 48, 2, '编辑', NULL, 'business:bizCategory:list:edit', 1, NULL, 1, '2026-04-23 16:33:34');
INSERT INTO `sys_menu` VALUES (52, 48, 2, '查看', NULL, 'business:bizCategory:list:view', 1, NULL, 1, '2026-04-23 16:33:34');
INSERT INTO `sys_menu` VALUES (53, 37, 1, '商家列表', 'business/bizMerchant/page', 'business:bizMerchant:list', 1, NULL, 1, '2026-04-23 17:21:05');
INSERT INTO `sys_menu` VALUES (54, 53, 2, '删除', NULL, 'business:bizMerchant:list:del', 1, NULL, 1, '2026-04-23 17:21:05');
INSERT INTO `sys_menu` VALUES (55, 53, 2, '添加', NULL, 'business:bizMerchant:list:add', 1, NULL, 1, '2026-04-23 17:21:05');
INSERT INTO `sys_menu` VALUES (56, 53, 2, '编辑', NULL, 'business:bizMerchant:list:edit', 1, NULL, 1, '2026-04-23 17:21:05');
INSERT INTO `sys_menu` VALUES (57, 53, 2, '查看', NULL, 'business:bizMerchant:list:view', 1, NULL, 1, '2026-04-23 17:21:05');
INSERT INTO `sys_menu` VALUES (58, 37, 1, '产品管理', 'business/bizProduct/page', 'business:bizProduct:list', 1, NULL, 1, '2026-04-23 17:21:57');
INSERT INTO `sys_menu` VALUES (59, 58, 2, '删除', NULL, 'business:bizProduct:list:del', 1, NULL, 1, '2026-04-23 17:21:57');
INSERT INTO `sys_menu` VALUES (60, 58, 2, '添加', NULL, 'business:bizProduct:list:add', 1, NULL, 1, '2026-04-23 17:21:57');
INSERT INTO `sys_menu` VALUES (61, 58, 2, '编辑', NULL, 'business:bizProduct:list:edit', 1, NULL, 1, '2026-04-23 17:21:57');
INSERT INTO `sys_menu` VALUES (62, 58, 2, '查看', NULL, 'business:bizProduct:list:view', 1, NULL, 1, '2026-04-23 17:21:57');
INSERT INTO `sys_menu` VALUES (63, 37, 1, '产品规格', 'business/bizProductSku/page', 'business:bizProductSku:list', 1, NULL, 1, '2026-04-23 17:22:35');
INSERT INTO `sys_menu` VALUES (64, 63, 2, '删除', NULL, 'business:bizProductSku:list:del', 1, NULL, 1, '2026-04-23 17:22:35');
INSERT INTO `sys_menu` VALUES (65, 63, 2, '添加', NULL, 'business:bizProductSku:list:add', 1, NULL, 1, '2026-04-23 17:22:35');
INSERT INTO `sys_menu` VALUES (66, 63, 2, '编辑', NULL, 'business:bizProductSku:list:edit', 1, NULL, 1, '2026-04-23 17:22:35');
INSERT INTO `sys_menu` VALUES (67, 63, 2, '查看', NULL, 'business:bizProductSku:list:view', 1, NULL, 1, '2026-04-23 17:22:35');
INSERT INTO `sys_menu` VALUES (68, 37, 1, '订单管理', 'business/bizOrder/page', 'business:bizOrder:list', 1, NULL, 1, '2026-04-23 17:23:43');
INSERT INTO `sys_menu` VALUES (69, 68, 2, '删除', NULL, 'business:bizOrder:list:del', 1, NULL, 1, '2026-04-23 17:23:43');
INSERT INTO `sys_menu` VALUES (70, 68, 2, '添加', NULL, 'business:bizOrder:list:add', 1, NULL, 1, '2026-04-23 17:23:43');
INSERT INTO `sys_menu` VALUES (71, 68, 2, '编辑', NULL, 'business:bizOrder:list:edit', 1, NULL, 1, '2026-04-23 17:23:43');
INSERT INTO `sys_menu` VALUES (72, 68, 2, '查看', NULL, 'business:bizOrder:list:view', 1, NULL, 1, '2026-04-23 17:23:43');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '返回参数',
  `status` int NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint NULL DEFAULT 0 COMMENT '消耗时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_oper_log_bt`(`business_type`) USING BTREE,
  INDEX `idx_sys_oper_log_s`(`status`) USING BTREE,
  INDEX `idx_sys_oper_log_ot`(`oper_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '操作日志记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (1, '用户管理', 2, 'com.lrs.core.system.controller.SysUserController.edit()', 'POST', 1, 'admin', '', '/system/user/edit', '10.1.160.196', '内网IP', '{\"avatar\":\"/show/20260414/1776135395766.png\",\"createBy\":1,\"createTime\":\"2023-12-02 22:20:07\",\"id\":7,\"nickName\":\"test\",\"status\":1,\"updateBy\":1,\"updateTime\":\"2026-04-14 10:56:38\",\"username\":\"test\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-14 10:56:39', 77);
INSERT INTO `sys_oper_log` VALUES (2, '用户管理', 2, 'com.lrs.core.system.controller.SysUserController.edit()', 'POST', 1, 'admin', '', '/system/user/edit', '10.1.160.196', '内网IP', '{\"avatar\":\"/show/20260414/1776135395766.png\",\"createBy\":1,\"createTime\":\"2023-12-02 22:20:07\",\"id\":7,\"nickName\":\"test\",\"status\":1,\"updateBy\":1,\"updateTime\":\"2026-04-14 14:27:45\",\"username\":\"test\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-14 14:27:45', 80);
INSERT INTO `sys_oper_log` VALUES (3, '用户管理', 2, 'com.lrs.core.system.controller.SysUserController.edit()', 'POST', 1, 'admin', '', '/system/user/edit', '10.1.160.196', '内网IP', '{\"avatar\":\"/show/20260414/1776148072877.png\",\"createBy\":1,\"createTime\":\"2023-12-02 22:20:07\",\"id\":7,\"nickName\":\"test\",\"status\":1,\"updateBy\":1,\"updateTime\":\"2026-04-14 14:27:54\",\"username\":\"test\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-14 14:27:54', 34);
INSERT INTO `sys_oper_log` VALUES (4, '用户管理', 1, 'com.lrs.core.system.controller.SysUserController.add()', 'POST', 1, 'admin', '', '/system/user/add', '10.1.160.196', '内网IP', '{\"createBy\":1,\"createTime\":\"2026-04-14 14:56:40\",\"id\":11,\"nickName\":\"aaa\",\"salt\":\"58d5ee7836654e38a868b536ecc7c28c\",\"username\":\"aaa\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-14 14:56:40', 80);
INSERT INTO `sys_oper_log` VALUES (5, '用户管理', 2, 'com.lrs.core.system.controller.SysUserController.edit()', 'POST', 1, 'admin', '', '/system/user/edit', '10.1.160.196', '内网IP', '{\"createBy\":1,\"createTime\":\"2026-04-14 14:56:40\",\"id\":11,\"nickName\":\"aaaaa\",\"status\":1,\"updateBy\":1,\"updateTime\":\"2026-04-14 14:57:02\",\"username\":\"aaaaa\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-14 14:57:02', 30);
INSERT INTO `sys_oper_log` VALUES (6, '角色管理', 1, 'com.lrs.core.system.controller.SysRoleController.add()', 'POST', 1, 'admin', '', '/system/role/add', '10.1.160.196', '内网IP', '{\"createBy\":1,\"createTime\":\"2026-04-14 14:57:15\",\"id\":3,\"menuIdList\":[2,16,26,27,28,29,17,30,31,32,33],\"remark\":\"test\",\"roleName\":\"test\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-14 14:57:16', 58);
INSERT INTO `sys_oper_log` VALUES (7, '角色管理', 1, 'com.lrs.core.system.controller.SysRoleController.add()', 'POST', 1, 'admin', '', '/system/role/add', '10.1.160.196', '内网IP', '{\"createBy\":1,\"createTime\":\"2026-04-16 15:55:52\",\"id\":4,\"menuIdList\":[1,3,7,8,9,10,4,22,23,24,25,5,11,12,13,14,15,6,18,19,20,21,2,16,26,27,28,29,17,30,31,32,33],\"remark\":\"test\",\"roleName\":\"test\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-16 15:55:52', 130);
INSERT INTO `sys_oper_log` VALUES (8, '角色管理', 3, 'com.lrs.core.system.controller.SysRoleController.del()', 'GET', 1, 'admin', '', '/system/role/del', '10.1.160.196', '内网IP', '{\"id\":\"4\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-16 15:56:10', 34);
INSERT INTO `sys_oper_log` VALUES (9, '菜单管理', 1, 'com.lrs.core.system.controller.SysMenuController.add()', 'POST', 1, 'admin', '', '/system/menu/add', '10.1.160.196', '内网IP', '{\"addBtn\":false,\"child\":[],\"createBy\":1,\"createTime\":\"2026-04-16 15:56:28\",\"hasPermit\":false,\"icon\":\"ttt\",\"id\":34,\"menuName\":\"ts\",\"menuType\":0}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-16 15:56:29', 38);
INSERT INTO `sys_oper_log` VALUES (10, '菜单管理', 3, 'com.lrs.core.system.controller.SysMenuController.del()', 'GET', 1, 'admin', '', '/system/menu/del', '10.1.160.196', '内网IP', '{\"id\":\"34\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-16 15:56:41', 32);
INSERT INTO `sys_oper_log` VALUES (11, '菜单管理', 1, 'com.lrs.core.system.controller.SysMenuController.add()', 'POST', 1, 'admin', '', '/system/menu/add', '10.1.160.196', '内网IP', '{\"addBtn\":false,\"child\":[],\"createBy\":1,\"createTime\":\"2026-04-21 14:02:59\",\"hasPermit\":false,\"id\":35,\"menuName\":\"test\",\"menuType\":0}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-21 14:02:59', 96);
INSERT INTO `sys_oper_log` VALUES (12, '菜单管理', 1, 'com.lrs.core.system.controller.SysMenuController.add()', 'POST', 1, 'admin', '', '/system/menu/add', '10.1.160.196', '内网IP', '{\"addBtn\":false,\"child\":[],\"createBy\":1,\"createTime\":\"2026-04-21 14:04:14\",\"hasPermit\":false,\"id\":36,\"menuName\":\"test\",\"menuType\":0}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-21 14:04:22', 18413);
INSERT INTO `sys_oper_log` VALUES (13, '菜单管理', 3, 'com.lrs.core.system.controller.SysMenuController.del()', 'GET', 1, 'admin', '', '/system/menu/del', '10.1.160.196', '内网IP', '{\"id\":\"36\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-21 14:04:28', 37);
INSERT INTO `sys_oper_log` VALUES (14, '菜单管理', 3, 'com.lrs.core.system.controller.SysMenuController.del()', 'GET', 1, 'admin', '', '/system/menu/del', '10.1.160.196', '内网IP', '{\"id\":\"35\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-21 14:04:31', 36);
INSERT INTO `sys_oper_log` VALUES (15, '用户管理', 2, 'com.lrs.core.system.controller.SysUserController.edit()', 'POST', 1, 'admin', '', '/system/user/edit', '10.1.160.196', '内网IP', '{\"avatar\":\"/show/20260423/1776932812430.jpg\",\"createTime\":\"2023-12-02 22:08:10\",\"id\":1,\"loginDate\":\"2026-04-23 16:26:18\",\"loginIp\":\"10.1.160.196\",\"nickName\":\"超级管理员 \",\"status\":1,\"updateBy\":1,\"updateTime\":\"2026-04-23 16:26:54\",\"username\":\"admin\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-23 16:26:55', 41);
INSERT INTO `sys_oper_log` VALUES (16, '菜单管理', 1, 'com.lrs.core.system.controller.SysMenuController.add()', 'POST', 1, 'admin', '', '/system/menu/add', '10.1.160.196', '内网IP', '{\"addBtn\":false,\"child\":[],\"createBy\":1,\"createTime\":\"2026-04-23 16:29:40\",\"hasPermit\":false,\"icon\":\"fa fa-list\",\"id\":37,\"menuName\":\"业务\",\"menuType\":0}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-23 16:29:41', 33);
INSERT INTO `sys_oper_log` VALUES (17, '菜单管理', 1, 'com.lrs.core.system.controller.SysMenuController.add()', 'POST', 1, 'admin', '', '/system/menu/add', '10.1.160.196', '内网IP', '{\"addBtn\":true,\"child\":[],\"createBy\":1,\"createTime\":\"2026-04-23 16:31:23\",\"hasPermit\":false,\"id\":38,\"menuName\":\"用户管理\",\"menuType\":1,\"menuUrl\":\"business/appUser/page\",\"parentId\":37,\"permit\":\"business:appUser:list\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-23 16:31:23', 76);
INSERT INTO `sys_oper_log` VALUES (18, '菜单管理', 1, 'com.lrs.core.system.controller.SysMenuController.add()', 'POST', 1, 'admin', '', '/system/menu/add', '10.1.160.196', '内网IP', '{\"addBtn\":true,\"child\":[],\"createBy\":1,\"createTime\":\"2026-04-23 16:32:50\",\"hasPermit\":false,\"id\":43,\"menuName\":\"收货地址管理\",\"menuType\":1,\"menuUrl\":\"business/bizAddress/page\",\"parentId\":37,\"permit\":\"business:bizAddress:list\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-23 16:32:50', 66);
INSERT INTO `sys_oper_log` VALUES (19, '菜单管理', 1, 'com.lrs.core.system.controller.SysMenuController.add()', 'POST', 1, 'admin', '', '/system/menu/add', '10.1.160.196', '内网IP', '{\"addBtn\":true,\"child\":[],\"createBy\":1,\"createTime\":\"2026-04-23 16:33:33\",\"hasPermit\":false,\"id\":48,\"menuName\":\"商品分类\",\"menuType\":1,\"menuUrl\":\"business/bizCategory/page\",\"parentId\":37,\"permit\":\"business:bizCategory:list\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-23 16:33:34', 45);
INSERT INTO `sys_oper_log` VALUES (20, '菜单管理', 1, 'com.lrs.core.system.controller.SysMenuController.add()', 'POST', 1, 'admin', '', '/system/menu/add', '10.1.160.196', '内网IP', '{\"addBtn\":true,\"child\":[],\"createBy\":1,\"createTime\":\"2026-04-23 17:21:05\",\"hasPermit\":false,\"id\":53,\"menuName\":\"商家列表\",\"menuType\":1,\"menuUrl\":\"business/bizMerchant/page\",\"parentId\":37,\"permit\":\"business:bizMerchant:list\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-23 17:21:05', 77);
INSERT INTO `sys_oper_log` VALUES (21, '菜单管理', 1, 'com.lrs.core.system.controller.SysMenuController.add()', 'POST', 1, 'admin', '', '/system/menu/add', '10.1.160.196', '内网IP', '{\"addBtn\":true,\"child\":[],\"createBy\":1,\"createTime\":\"2026-04-23 17:21:56\",\"hasPermit\":false,\"id\":58,\"menuName\":\"产品管理\",\"menuType\":1,\"menuUrl\":\"business/bizProduct/page\",\"parentId\":37,\"permit\":\"business:bizProduct:list\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-23 17:21:57', 83);
INSERT INTO `sys_oper_log` VALUES (22, '菜单管理', 1, 'com.lrs.core.system.controller.SysMenuController.add()', 'POST', 1, 'admin', '', '/system/menu/add', '10.1.160.196', '内网IP', '{\"addBtn\":true,\"child\":[],\"createBy\":1,\"createTime\":\"2026-04-23 17:22:35\",\"hasPermit\":false,\"id\":63,\"menuName\":\"产品规格\",\"menuType\":1,\"menuUrl\":\"business/bizProductSku/page\",\"parentId\":37,\"permit\":\"business:bizProductSku:list\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-23 17:22:35', 68);
INSERT INTO `sys_oper_log` VALUES (23, '菜单管理', 1, 'com.lrs.core.system.controller.SysMenuController.add()', 'POST', 1, 'admin', '', '/system/menu/add', '10.1.160.196', '内网IP', '{\"addBtn\":true,\"child\":[],\"createBy\":1,\"createTime\":\"2026-04-23 17:23:43\",\"hasPermit\":false,\"id\":68,\"menuName\":\"订单管理\",\"menuType\":1,\"menuUrl\":\"business/bizOrder/page\",\"parentId\":37,\"permit\":\"business:bizOrder:list\"}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-23 17:23:43', 65);
INSERT INTO `sys_oper_log` VALUES (24, '菜单管理', 2, 'com.lrs.core.system.controller.SysMenuController.edit()', 'POST', 1, 'admin', '', '/system/menu/edit', '10.1.160.196', '内网IP', '{\"addBtn\":false,\"child\":[],\"createBy\":1,\"createTime\":\"2026-04-23 16:32:50\",\"hasPermit\":false,\"id\":43,\"menuName\":\"收货地址\",\"menuType\":1,\"menuUrl\":\"business/bizAddress/page\",\"parentId\":37,\"permit\":\"business:bizAddress:list\",\"sortNum\":1}', '{\"code\":200,\"data\":true,\"msg\":\"操作成功\"}', 0, '', '2026-04-23 17:49:00', 174);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '角色名称',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '角色备注',
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', '超级管理员', 1, '2025-11-28 17:16:42');
INSERT INTO `sys_role` VALUES (2, '管理员', NULL, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role` VALUES (3, 'test', 'test', 1, '2026-04-14 14:57:16');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `menu_id` bigint NULL DEFAULT NULL,
  `role_id` bigint NULL DEFAULT NULL,
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (2, 3, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (3, 7, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (4, 8, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (5, 9, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (6, 10, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (7, 4, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (8, 22, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (9, 23, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (10, 24, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (11, 25, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (12, 5, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (13, 11, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (14, 12, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (15, 13, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (16, 14, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (17, 15, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (18, 6, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (19, 18, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (20, 19, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (21, 20, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (22, 21, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (23, 2, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (24, 16, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (25, 26, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (26, 27, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (27, 28, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (28, 29, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (29, 17, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (30, 30, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (31, 31, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (32, 32, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (33, 33, 1, 1, '2025-11-28 17:16:45');
INSERT INTO `sys_role_menu` VALUES (34, 1, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (35, 3, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (36, 7, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (37, 8, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (38, 9, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (39, 10, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (40, 4, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (41, 22, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (42, 23, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (43, 24, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (44, 25, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (45, 5, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (46, 11, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (47, 12, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (48, 13, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (49, 14, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (50, 15, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (51, 6, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (52, 18, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (53, 19, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (54, 20, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (55, 21, 2, 1, '2025-11-28 17:16:56');
INSERT INTO `sys_role_menu` VALUES (56, 2, 3, 1, '2026-04-14 14:57:16');
INSERT INTO `sys_role_menu` VALUES (57, 16, 3, 1, '2026-04-14 14:57:16');
INSERT INTO `sys_role_menu` VALUES (58, 26, 3, 1, '2026-04-14 14:57:16');
INSERT INTO `sys_role_menu` VALUES (59, 27, 3, 1, '2026-04-14 14:57:16');
INSERT INTO `sys_role_menu` VALUES (60, 28, 3, 1, '2026-04-14 14:57:16');
INSERT INTO `sys_role_menu` VALUES (61, 29, 3, 1, '2026-04-14 14:57:16');
INSERT INTO `sys_role_menu` VALUES (62, 17, 3, 1, '2026-04-14 14:57:16');
INSERT INTO `sys_role_menu` VALUES (63, 30, 3, 1, '2026-04-14 14:57:16');
INSERT INTO `sys_role_menu` VALUES (64, 31, 3, 1, '2026-04-14 14:57:16');
INSERT INTO `sys_role_menu` VALUES (65, 32, 3, 1, '2026-04-14 14:57:16');
INSERT INTO `sys_role_menu` VALUES (66, 33, 3, 1, '2026-04-14 14:57:16');
INSERT INTO `sys_role_menu` VALUES (67, 1, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (68, 3, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (69, 7, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (70, 8, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (71, 9, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (72, 10, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (73, 4, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (74, 22, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (75, 23, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (76, 24, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (77, 25, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (78, 5, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (79, 11, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (80, 12, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (81, 13, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (82, 14, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (83, 15, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (84, 6, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (85, 18, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (86, 19, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (87, 20, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (88, 21, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (89, 2, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (90, 16, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (91, 26, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (92, 27, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (93, 28, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (94, 29, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (95, 17, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (96, 30, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (97, 31, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (98, 32, 4, 1, '2026-04-16 15:55:52');
INSERT INTO `sys_role_menu` VALUES (99, 33, 4, 1, '2026-04-16 15:55:52');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nick_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '昵称',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '密码',
  `salt` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '密码随机盐',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户头像',
  `status` int NULL DEFAULT 1 COMMENT '用户状态：1-正常，2-已锁定',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `login_ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '最后登录IP',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, '超级管理员 ', 'admin', '4a5a3422434efd5f7f432cf844df07f5', '49d11245f5c8c8736b6ad62876075c78', '/show/20260423/1776932812430.jpg', 1, 1, '2026-04-23 17:30:27', '10.1.160.196', '2026-04-23 17:30:27', NULL, '2023-12-02 22:08:10');
INSERT INTO `sys_user` VALUES (7, 'test', 'test', '49d11245f5c8c8736b6ad62876075c78', '653ccf53a4cd47029a308ac2a541ba00', '/show/20260414/1776148072877.png', 1, 7, '2026-04-23 14:08:24', '10.1.160.196', '2026-04-23 14:08:24', 1, '2023-12-02 22:20:07');
INSERT INTO `sys_user` VALUES (9, 'abc', 'abc', 'a8fbcd5503bc21d347a8dc4956f15827', 'a0ce1b00ec224c9c8b384fdfaf865967', '/show/20231209/1702097065890.png', 1, 1, NULL, NULL, '2023-12-09 12:44:28', 1, '2023-12-09 12:33:43');
INSERT INTO `sys_user` VALUES (11, 'aaaaa', 'aaaaa', 'e34d8eaf1604bbffdc88540ee1e43c53', '58d5ee7836654e38a868b536ecc7c28c', NULL, 1, 11, '2026-04-14 14:57:39', '10.1.160.196', '2026-04-14 14:57:39', 1, '2026-04-14 14:56:40');

-- ----------------------------
-- Table structure for sys_user_online
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_online`;
CREATE TABLE `sys_user_online`  (
  `sessionId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户会话id',
  `login_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '登录账号',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '部门名称',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '操作系统',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '在线状态on_line在线off_line离线',
  `start_timestamp` datetime NULL DEFAULT NULL COMMENT 'session创建时间',
  `last_access_time` datetime NULL DEFAULT NULL COMMENT 'session最后访问时间',
  `expire_time` int NULL DEFAULT 0 COMMENT '超时时间，单位为分钟',
  PRIMARY KEY (`sessionId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '在线用户记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_online
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL,
  `role_id` int NULL DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1, '2025-11-28 17:17:38');
INSERT INTO `sys_user_role` VALUES (4, 7, 2, '2026-04-14 14:27:36');
INSERT INTO `sys_user_role` VALUES (6, 11, 3, '2026-04-14 14:57:23');

SET FOREIGN_KEY_CHECKS = 1;
