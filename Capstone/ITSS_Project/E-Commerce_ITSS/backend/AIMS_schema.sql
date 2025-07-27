-- MySQL dump 10.13  Distrib 9.3.0, for Win64 (x86_64)
--
-- Host: localhost    Database: aims_database
-- ------------------------------------------------------
-- Server version	9.3.0

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
-- Table structure for table `books`
--

DROP TABLE IF EXISTS `books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cdlps`
--

DROP TABLE IF EXISTS `cdlps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cdlps` (
  `product_id` int NOT NULL,
  `artist` varchar(255) DEFAULT NULL,
  `recordLabel` varchar(255) DEFAULT NULL,
  `tracklist` varchar(255) DEFAULT NULL,
  `record_label` varchar(255) DEFAULT NULL,
  `track_list` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  CONSTRAINT `fk_cdlps_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `delivery_info`
--

DROP TABLE IF EXISTS `delivery_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dvds`
--

DROP TABLE IF EXISTS `dvds`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `orderitem`
--

DROP TABLE IF EXISTS `orderitem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `transaction_id` varchar(20) DEFAULT NULL,
  `method_id` int NOT NULL,
  `shipping_fees` decimal(12,2) NOT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `is_payment` bit(1) NOT NULL,
  `customer_email` varchar(100) NOT NULL,
  `customer_full_name` varchar(100) NOT NULL,
  `customer_phone` varchar(20) NOT NULL,
  `delivery_address` text NOT NULL,
  `delivery_province` varchar(50) NOT NULL,
  `subtotal_amount` decimal(12,2) NOT NULL,
  PRIMARY KEY (`order_id`),
  KEY `fk_orders_user` (`user_id`),
  KEY `fk_orders_shippingmethod` (`method_id`),
  KEY `fk_orders_transaction` (`transaction_id`),
  CONSTRAINT `fk_orders_shippingmethod` FOREIGN KEY (`method_id`) REFERENCES `shippingmethod` (`method_id`),
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shippingmethod`
--

DROP TABLE IF EXISTS `shippingmethod`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shippingmethod` (
  `method_id` int NOT NULL AUTO_INCREMENT,
  `method_name` varchar(255) DEFAULT NULL,
  `is_rush` tinyint(1) DEFAULT NULL,
  `shipping_fees` double DEFAULT NULL,
  PRIMARY KEY (`method_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transactioninformation`
--

DROP TABLE IF EXISTS `transactioninformation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `trigger_debug_log`
--

DROP TABLE IF EXISTS `trigger_debug_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trigger_debug_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `table_name` varchar(255) NOT NULL,
  `operation` varchar(20) NOT NULL,
  `old_values` text,
  `new_values` text,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `trigger_name` varchar(255) DEFAULT NULL,
  `message` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-07  6:51:26
