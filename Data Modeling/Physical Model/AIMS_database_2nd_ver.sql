CREATE TABLE `books` (
  `product_id` int NOT NULL,
  `author` varchar(255) DEFAULT NULL,
  `publisher` varchar(255) DEFAULT NULL,
  `coverType` varchar(255) DEFAULT NULL,
  `publicationDate` date DEFAULT NULL,
  `pagesNumber` int DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `genre` varchar(255) DEFAULT NULL,
  `cover_type` varchar(255) DEFAULT NULL,
  `pages_number` int NOT NULL,
  `publication_date` date DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  CONSTRAINT `fk_books_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
);

CREATE TABLE `cdlps` (
  `product_id` int NOT NULL,
  `artist` varchar(255) DEFAULT NULL,
  `recordLabel` varchar(255) DEFAULT NULL,
  `tracklist` varchar(255) DEFAULT NULL,
  `record_label` varchar(255) DEFAULT NULL,
  `track_list` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  CONSTRAINT `fk_cdlps_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
);

CREATE TABLE `delivery_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `province` varchar(255) DEFAULT NULL,
  `instruction` varchar(255) DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_delivery_user` (`user_id`),
  CONSTRAINT `fk_delivery_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);

CREATE TABLE `dvds` (
  `product_id` int NOT NULL,
  `discType` varchar(255) DEFAULT NULL,
  `director` varchar(255) DEFAULT NULL,
  `runtime` int DEFAULT NULL,
  `studio` varchar(255) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `subtitles` varchar(255) DEFAULT NULL,
  `releaseDate` date DEFAULT NULL,
  `disc_type` varchar(255) DEFAULT NULL,
  `release_date` date DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  CONSTRAINT `fk_dvds_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
);

CREATE TABLE `orderitem` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `price` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_orderitem_order` (`order_id`),
  KEY `fk_orderitem_product` (`product_id`),
  CONSTRAINT `fk_orderitem_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `fk_orderitem_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
);

CREATE TABLE `orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `delivery_id` int DEFAULT NULL,
  `transaction_id` varchar(20) DEFAULT NULL,
  `method_id` int NOT NULL,
  `shipping_fees` decimal(12,2) NOT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `is_payment` bit(1) NOT NULL,
  `total_fees` decimal(12,2) DEFAULT NULL,
  `customer_email` varchar(100) NOT NULL,
  `customer_full_name` varchar(100) NOT NULL,
  `customer_phone` varchar(20) NOT NULL,
  `delivery_address` text NOT NULL,
  `delivery_province` varchar(50) NOT NULL,
  `shipping_option` enum('standard','express_2h') DEFAULT 'standard',
  `subtotal_amount` decimal(12,2) NOT NULL,
  PRIMARY KEY (`order_id`),
  KEY `fk_orders_user` (`user_id`),
  KEY `fk_orders_delivery` (`delivery_id`),
  KEY `fk_orders_shippingmethod` (`method_id`),
  KEY `fk_orders_transaction` (`transaction_id`),
  CONSTRAINT `fk_orders_delivery` FOREIGN KEY (`delivery_id`) REFERENCES `delivery_info` (`id`),
  CONSTRAINT `fk_orders_shippingmethod` FOREIGN KEY (`method_id`) REFERENCES `shippingmethod` (`method_id`),
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);

CREATE TABLE `orders_backup` (
  `order_id` int NOT NULL DEFAULT '0',
  `user_id` int DEFAULT NULL,
  `delivery_id` int DEFAULT NULL,
  `transaction_id` varchar(20) DEFAULT NULL,
  `method_id` int NOT NULL,
  `shipping_fees` decimal(12,2) NOT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `is_payment` bit(1) NOT NULL DEFAULT b'0',
  `total_fees` decimal(12,2) DEFAULT NULL,
  `customer_email` varchar(100) NOT NULL,
  `customer_full_name` varchar(100) NOT NULL,
  `customer_phone` varchar(20) NOT NULL,
  `delivery_address` text NOT NULL,
  `delivery_province` varchar(50) NOT NULL,
  `shipping_option` enum('standard','express_2h') DEFAULT 'standard',
  `subtotal_amount` decimal(12,2) NOT NULL
);

CREATE TABLE `products` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(350) DEFAULT NULL COMMENT 'Tên sản phẩm',
  `price` double NOT NULL,
  `category` varchar(20) DEFAULT NULL,
  `imageURL` varchar(300) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `entry_date` date DEFAULT NULL,
  `dimension` double DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_products_user` (`user_id`),
  CONSTRAINT `fk_products_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `products_chk_1` CHECK ((`price` >= 0)),
  CONSTRAINT `products_chk_2` CHECK ((`quantity` >= 0))
);

CREATE TABLE `shippingmethod` (
  `method_id` int NOT NULL AUTO_INCREMENT,
  `method_name` varchar(255) DEFAULT NULL,
  `is_rush` tinyint(1) DEFAULT NULL,
  `shipping_fees` double DEFAULT NULL,
  PRIMARY KEY (`method_id`)
);

CREATE TABLE `transactioninformation` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int DEFAULT NULL,
  `total_fee` double DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `transaction_time` datetime DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `vnp_bank_code` varchar(255) DEFAULT NULL,
  `vnp_bank_tran_no` varchar(255) DEFAULT NULL,
  `vnp_response_code` varchar(255) DEFAULT NULL,
  `vnp_transaction_no` varchar(255) DEFAULT NULL,
  `order_reference` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `fk_transInfo_orders` (`order_id`),
  CONSTRAINT `fk_transInfo_orders` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
);

CREATE TABLE `trigger_debug_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `trigger_name` varchar(100) DEFAULT NULL,
  `action_type` varchar(50) DEFAULT NULL,
  `orderitem_id` int DEFAULT NULL,
  `old_order_id` int DEFAULT NULL,
  `new_order_id` int DEFAULT NULL,
  `message` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_trigger_name` (`trigger_name`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_action_type` (`action_type`)
);

CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
);
