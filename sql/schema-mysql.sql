-- 仿美团外卖系统建表语句
-- 适用数据库：MySQL 8.x
-- 说明：
-- 1. 该脚本覆盖课程设计/毕设版完整项目的核心表
-- 2. 尽量兼容当前 back-end 代码已实现的实体结构
-- 3. 对于当前后端暂未使用的扩展字段，尽量设置为 NULL 或默认值，避免影响现有代码插入

CREATE DATABASE IF NOT EXISTS `meituan`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `meituan`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `notification`;
DROP TABLE IF EXISTS `delivery_trace`;
DROP TABLE IF EXISTS `payment_record`;
DROP TABLE IF EXISTS `order_comment`;
DROP TABLE IF EXISTS `order_item`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `cart_item`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `product_category`;
DROP TABLE IF EXISTS `shop`;
DROP TABLE IF EXISTS `user_address`;
DROP TABLE IF EXISTS `user_account`;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. 用户表
CREATE TABLE `user_account` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(64) NOT NULL COMMENT '登录用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '登录密码（加密后）',
  `display_name` VARCHAR(32) NOT NULL COMMENT '显示名称',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
  `role` VARCHAR(16) NOT NULL COMMENT '角色：USER/MERCHANT/RIDER/ADMIN',
  `status` VARCHAR(16) NOT NULL DEFAULT 'NORMAL' COMMENT '状态：NORMAL/DISABLED',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_account_username` (`username`),
  UNIQUE KEY `uk_user_account_phone` (`phone`),
  KEY `idx_user_account_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户账号表';

-- 2. 用户地址表
CREATE TABLE `user_address` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `contact_name` VARCHAR(32) NOT NULL COMMENT '联系人',
  `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `province` VARCHAR(32) DEFAULT NULL COMMENT '省',
  `city` VARCHAR(32) DEFAULT NULL COMMENT '市',
  `district` VARCHAR(32) DEFAULT NULL COMMENT '区',
  `detail_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
  `address_tag` VARCHAR(16) DEFAULT NULL COMMENT '地址标签：家/公司/学校',
  `longitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '经度',
  `latitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '纬度',
  `default_address` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_address_user_id` (`user_id`),
  CONSTRAINT `fk_user_address_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收货地址表';

-- 3. 店铺表
CREATE TABLE `shop` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `merchant_id` BIGINT NOT NULL COMMENT '商家账号ID',
  `name` VARCHAR(64) NOT NULL COMMENT '店铺名称',
  `logo` VARCHAR(255) DEFAULT NULL COMMENT '店铺logo',
  `announcement` VARCHAR(255) NOT NULL COMMENT '店铺公告',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '店铺地址',
  `delivery_fee` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '配送费',
  `min_order_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '起送价',
  `avg_delivery_time` INT NOT NULL DEFAULT 30 COMMENT '平均配送时长（分钟）',
  `score` DECIMAL(3, 2) NOT NULL DEFAULT 5.00 COMMENT '评分',
  `monthly_sales` INT NOT NULL DEFAULT 0 COMMENT '月售',
  `open` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否营业中',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_merchant_id` (`merchant_id`),
  KEY `idx_shop_open` (`open`),
  CONSTRAINT `fk_shop_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店铺表';

-- 4. 商品分类表
CREATE TABLE `product_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
  `name` VARCHAR(32) NOT NULL COMMENT '分类名',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_category_shop_id` (`shop_id`),
  KEY `idx_product_category_sort_order` (`sort_order`),
  CONSTRAINT `fk_product_category_shop` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 5. 商品表
CREATE TABLE `product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `name` VARCHAR(64) NOT NULL COMMENT '商品名',
  `description` VARCHAR(255) NOT NULL COMMENT '商品描述',
  `price` DECIMAL(10, 2) NOT NULL COMMENT '现价',
  `origin_price` DECIMAL(10, 2) DEFAULT NULL COMMENT '原价',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
  `monthly_sales` INT NOT NULL DEFAULT 0 COMMENT '月售',
  `image_url` VARCHAR(255) DEFAULT NULL COMMENT '商品图片',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否上架',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_shop_id` (`shop_id`),
  KEY `idx_product_category_id` (`category_id`),
  KEY `idx_product_enabled` (`enabled`),
  CONSTRAINT `fk_product_shop` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `product_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 6. 购物车表
CREATE TABLE `cart_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `quantity` INT NOT NULL COMMENT '数量',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cart_item_user_product` (`user_id`, `product_id`),
  KEY `idx_cart_item_user_id` (`user_id`),
  KEY `idx_cart_item_product_id` (`product_id`),
  CONSTRAINT `fk_cart_item_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_cart_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车项表';

-- 7. 订单主表
CREATE TABLE `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
  `user_id` BIGINT NOT NULL COMMENT '下单用户ID',
  `shop_id` BIGINT NOT NULL COMMENT '店铺ID',
  `rider_id` BIGINT DEFAULT NULL COMMENT '骑手ID',
  `status` VARCHAR(32) NOT NULL COMMENT '订单状态',
  `goods_amount` DECIMAL(10, 2) NOT NULL COMMENT '商品总额',
  `delivery_fee` DECIMAL(10, 2) NOT NULL COMMENT '配送费',
  `discount_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
  `pay_amount` DECIMAL(10, 2) NOT NULL COMMENT '实付金额',
  `contact_name` VARCHAR(32) NOT NULL COMMENT '联系人快照',
  `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系电话快照',
  `address_snapshot` VARCHAR(255) NOT NULL COMMENT '收货地址快照',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `commented` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已评价',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `accepted_at` DATETIME DEFAULT NULL COMMENT '商家接单时间',
  `preparing_at` DATETIME DEFAULT NULL COMMENT '开始制作时间',
  `ready_at` DATETIME DEFAULT NULL COMMENT '待配送时间',
  `pickup_at` DATETIME DEFAULT NULL COMMENT '骑手取餐时间',
  `delivering_at` DATETIME DEFAULT NULL COMMENT '配送中时间',
  `completed_at` DATETIME DEFAULT NULL COMMENT '完成时间',
  `cancelled_at` DATETIME DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` VARCHAR(255) DEFAULT NULL COMMENT '取消原因',
  `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '拒单原因',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_orders_order_no` (`order_no`),
  KEY `idx_orders_user_id` (`user_id`),
  KEY `idx_orders_shop_id` (`shop_id`),
  KEY `idx_orders_rider_id` (`rider_id`),
  KEY `idx_orders_status` (`status`),
  KEY `idx_orders_created_at` (`created_at`),
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_orders_shop` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`),
  CONSTRAINT `fk_orders_rider` FOREIGN KEY (`rider_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单主表';

-- 8. 订单明细表
CREATE TABLE `order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID快照',
  `product_name` VARCHAR(64) NOT NULL COMMENT '商品名快照',
  `product_price` DECIMAL(10, 2) NOT NULL COMMENT '商品单价快照',
  `quantity` INT NOT NULL COMMENT '购买数量',
  `amount` DECIMAL(10, 2) NOT NULL COMMENT '小计金额',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_item_order_id` (`order_id`),
  KEY `idx_order_item_product_id` (`product_id`),
  CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

-- 9. 订单评价表
CREATE TABLE `order_comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `user_id` BIGINT NOT NULL COMMENT '评价用户ID',
  `rating` INT NOT NULL COMMENT '评分 1-5',
  `content` VARCHAR(255) NOT NULL COMMENT '评价内容',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_comment_order_id` (`order_id`),
  KEY `idx_order_comment_user_id` (`user_id`),
  CONSTRAINT `fk_order_comment_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `fk_order_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单评价表';

-- 10. 支付记录表
CREATE TABLE `payment_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `pay_no` VARCHAR(64) NOT NULL COMMENT '支付流水号',
  `pay_type` VARCHAR(16) NOT NULL DEFAULT 'MOCK' COMMENT '支付方式：MOCK/ALIPAY/WX',
  `pay_amount` DECIMAL(10, 2) NOT NULL COMMENT '支付金额',
  `pay_status` VARCHAR(16) NOT NULL DEFAULT 'SUCCESS' COMMENT '支付状态',
  `paid_at` DATETIME DEFAULT NULL COMMENT '支付时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_record_pay_no` (`pay_no`),
  KEY `idx_payment_record_order_id` (`order_id`),
  CONSTRAINT `fk_payment_record_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- 11. 配送轨迹表
CREATE TABLE `delivery_trace` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `rider_id` BIGINT DEFAULT NULL COMMENT '骑手ID',
  `trace_status` VARCHAR(32) NOT NULL COMMENT '轨迹状态',
  `trace_desc` VARCHAR(255) NOT NULL COMMENT '轨迹描述',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_delivery_trace_order_id` (`order_id`),
  KEY `idx_delivery_trace_rider_id` (`rider_id`),
  CONSTRAINT `fk_delivery_trace_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `fk_delivery_trace_rider` FOREIGN KEY (`rider_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='配送轨迹表';

-- 12. 通知消息表
CREATE TABLE `notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `receiver_id` BIGINT NOT NULL COMMENT '接收人ID',
  `receiver_role` VARCHAR(16) NOT NULL COMMENT '接收人角色',
  `title` VARCHAR(64) NOT NULL COMMENT '标题',
  `content` VARCHAR(255) NOT NULL COMMENT '内容',
  `biz_type` VARCHAR(32) DEFAULT NULL COMMENT '业务类型，如 ORDER',
  `biz_id` BIGINT DEFAULT NULL COMMENT '业务ID',
  `read_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_notification_receiver_id` (`receiver_id`),
  KEY `idx_notification_read_flag` (`read_flag`),
  CONSTRAINT `fk_notification_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内通知表';

SET FOREIGN_KEY_CHECKS = 1;

-- 可选：初始化演示账号
-- 密码建议由后端使用 BCrypt 生成后再插入，这里只给字段示意，不直接写死明文数据。

