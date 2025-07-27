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
-- Dumping data for table `books`
--

LOCK TABLES `books` WRITE;
/*!40000 ALTER TABLE `books` DISABLE KEYS */;
INSERT INTO `books` VALUES (1,'Paulo Coelho','NXB Hà Nội','Bìa mềm','2020-08-10',228,'Tiếng Việt','Tiểu thuyết',NULL,0,NULL),(2,'Dale Carnegie','NXB Tổng hợp TPHCM','Bìa mềm','2022-01-01',320,'Tiếng Việt','Self-help',NULL,0,NULL),(3,'J.K. Rowling','NXB Trẻ','Bìa cứng','2017-01-01',435,'Tiếng Việt','Fantasy',NULL,0,NULL),(4,'Yuval Noah Harari','NXB Tri Thức','Bìa mềm','2018-05-15',512,'Tiếng Việt','Lịch sử',NULL,0,NULL),(5,'Fyodor Dostoevsky','NXB Văn Học','Bìa cứng','2019-11-20',650,'Tiếng Việt','Kinh điển',NULL,0,NULL),(6,'Vũ Trọng Phụng','NXB Hội Nhà Văn','Bìa mềm','2021-03-10',250,'Tiếng Việt','Văn học Việt Nam',NULL,0,NULL),(7,'F. Scott Fitzgerald','Penguin Books','Paperback','2004-09-30',180,'English','Classic',NULL,0,NULL),(8,'George Orwell','NXB Dân Trí','Bìa mềm','2020-01-01',328,'Tiếng Việt','Dystopian',NULL,0,NULL),(9,'J.D. Salinger','NXB Văn Học','Bìa mềm','2018-09-01',277,'Tiếng Việt','Kinh điển',NULL,0,NULL),(10,'James Clear','NXB Lao Động','Bìa mềm','2021-06-10',320,'Tiếng Việt','Self-help',NULL,0,NULL),(11,'Tô Hoài','NXB Kim Đồng','Bìa cứng','2019-01-01',156,'Tiếng Việt','Thiếu nhi',NULL,0,NULL),(12,'J.R.R. Tolkien','Mariner Books','Paperback','2005-09-15',1178,'English','Fantasy',NULL,0,NULL),(13,'BS. Trí Đoàn & Mẹ Ong Bông','NXB Phụ Nữ','Bìa mềm','2016-01-01',240,'Tiếng Việt','Nuôi dạy con',NULL,0,NULL),(14,'Haruki Murakami','NXB Hội Nhà Văn','Bìa mềm','2016-07-25',480,'Tiếng Việt','Văn học Nhật Bản',NULL,0,NULL),(15,'Daniel Kahneman','Farrar, Straus and Giroux','Paperback','2013-04-02',499,'English','Psychology',NULL,0,NULL),(16,'Gabriel Garcia Marquez','NXB Văn Học','Bìa mềm','2018-01-01',471,'Tiếng Việt','Kinh điển',NULL,0,NULL),(17,'Yuval Noah Harari','NXB Tri Thức','Bìa mềm','2019-01-01',448,'Tiếng Việt','Khoa học',NULL,0,NULL),(18,'Margaret Mitchell','Scribner','Paperback','1996-09-01',1037,'English','Classic',NULL,0,NULL),(19,'Mario Puzo','NXB Văn Học','Bìa mềm','2017-01-01',544,'Tiếng Việt','Kinh điển',NULL,0,NULL),(20,'Harper Lee','NXB Văn Học','Bìa mềm','2018-01-01',324,'Tiếng Việt','Kinh điển',NULL,0,NULL);
/*!40000 ALTER TABLE `books` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `cdlps`
--

LOCK TABLES `cdlps` WRITE;
/*!40000 ALTER TABLE `cdlps` DISABLE KEYS */;
INSERT INTO `cdlps` VALUES (21,'Mỹ Tâm','MT Entertainment','Track 1, Track 2...',NULL,NULL),(22,'Taylor Swift','Big Machine Records','Welcome to New York...',NULL,NULL),(23,'Adele','XL Recordings','Hello, Send My Love...',NULL,NULL),(24,'OneRepublic','Interscope Records','Run, Someday, Savior...',NULL,NULL),(25,'Sơn Tùng M-TP','M-TP Entertainment','Cơn Mưa Ngang Qua...',NULL,NULL),(26,'Hà Anh Tuấn','Viet Vision','Tháng Mấy Em Nhớ Anh?...',NULL,NULL),(27,'The Beatles','Apple Records','Side A, Side B',NULL,NULL),(28,'Pink Floyd','Harvest Records','Speak to Me, Breathe...',NULL,NULL),(29,'Queen','EMI Records','Bohemian Rhapsody...',NULL,NULL),(30,'Ed Sheeran','Asylum Records','Shape of You, Castle on the Hill...',NULL,NULL);
/*!40000 ALTER TABLE `cdlps` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `delivery_info`
--

LOCK TABLES `delivery_info` WRITE;
/*!40000 ALTER TABLE `delivery_info` DISABLE KEYS */;
INSERT INTO `delivery_info` VALUES (1,'Nguyễn Văn A','0905111222','123 Đường Lê Lợi, Quận 1','TP. Hồ Chí Minh','Giao hàng trong giờ hành chính',2),(2,'Trần Thị B','0905333444','456 Phố Bà Triệu, Quận Hai Bà Trưng','Hà Nội','Gọi trước khi giao 30 phút',3),(3,'Test User','0123456789','123 Test Street','Ho Chi Minh City','',1),(4,'Test User','0123456789','123 Test Street','Ho Chi Minh City','',1),(5,'Test User 2','0987654321','456 Test Street','Hanoi','',1),(6,'John Doe','0987654321','456 Another Street','Hanoi','',1),(7,'Guest User','0000000000','Unknown Address','Unknown Province','',1),(8,'Guest User','0000000000','Unknown Address','Unknown Province','',1),(9,'Test User','0123456789','123 Test Street','Ho Chi Minh','',1),(10,'Test User','0123456789','123 Test Street','Ho Chi Minh','',1),(11,'Guest User','0000000000','Unknown Address','Unknown Province','',1),(12,'Guest User','0000000000','Unknown Address','Unknown Province','',1),(13,'Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội','',1),(14,'Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội','',1),(15,'Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội','',1),(16,'Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội','',1),(17,'Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội','',1),(18,'Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội','',1),(19,'Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội','',1),(20,'Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội','',1),(21,'Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội','',1),(22,'Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội','',1),(23,'Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội','',1),(24,'Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội','',1),(25,'Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội','',1),(26,'Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội','',1),(27,'Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội','',1);
/*!40000 ALTER TABLE `delivery_info` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `dvds`
--

LOCK TABLES `dvds` WRITE;
/*!40000 ALTER TABLE `dvds` DISABLE KEYS */;
INSERT INTO `dvds` VALUES (31,'DVD','Trấn Thành',20800,'Tran Thanh Town','Tiếng Việt','Tiếng Anh','2021-06-15',NULL,NULL),(32,'DVD','Victor Vũ',15700,'Galaxy M&E','Tiếng Việt','Tiếng Anh','2020-03-20',NULL,NULL),(33,'Blu-ray','Bong Joon Ho',21200,'CJ Entertainment','Korean','English, Vietnamese','2019-10-11',NULL,NULL),(34,'DVD','Francis Ford Coppola',25500,'Paramount Pictures','English','English, Spanish','1972-03-24',NULL,NULL),(35,'DVD','Quentin Tarantino',23400,'Miramax','English','English, French','1994-10-14',NULL,NULL),(36,'Blu-ray 4K','Anthony & Joe Russo',30100,'Marvel Studios','English','Multiple','2019-08-13',NULL,NULL),(37,'Blu-ray','Christopher Nolan',22800,'Warner Bros.','English','English, Vietnamese','2010-12-07',NULL,NULL),(38,'DVD','Hayao Miyazaki',20500,'Studio Ghibli','Japanese','English, Vietnamese','2002-07-20',NULL,NULL),(39,'DVD','Lê Văn Kiệt',13800,'Studio68','Tiếng Việt','English','2019-05-22',NULL,NULL),(40,'Blu-ray','Makoto Shinkai',14600,'CoMix Wave Films','Japanese','English, Vietnamese','2017-07-26',NULL,NULL);
/*!40000 ALTER TABLE `dvds` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `orderitem`
--

LOCK TABLES `orderitem` WRITE;
/*!40000 ALTER TABLE `orderitem` DISABLE KEYS */;
/*!40000 ALTER TABLE `orderitem` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (4,NULL,NULL,1,50000.00,368000.00,'2025-06-30 19:56:41',_binary '\0','test@example.com','Test User','0123456789','123 Test Street','Ha Noi',318000.00),(5,1,'AUTO_1751313400_807',1,30000.00,150000.00,'2025-07-01 02:56:40',_binary '','system@auto.com','System Auto Order','0000000000','System Generated Address','System Province',120000.00),(6,NULL,NULL,1,50000.00,368000.00,'2025-06-30 20:38:56',_binary '\0','test@email.com','Nguyen Van A','0123456789','123 Test Street','Ha Noi',318000.00),(7,NULL,NULL,2,70000.00,250000.00,'2025-06-30 20:48:59',_binary '\0','test2@email.com','Test Order 2','0987654321','456 Another Street','Ho Chi Minh',180000.00),(8,NULL,NULL,1,30000.00,129000.00,'2025-06-30 21:14:52',_binary '\0','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',99000.00),(9,NULL,NULL,1,30000.00,1030000.00,'2025-06-30 21:19:51',_binary '\0','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',1000000.00),(10,NULL,NULL,1,30000.00,250000.00,'2025-06-30 21:23:55',_binary '\0','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',220000.00),(11,NULL,NULL,1,30000.00,200000.00,'2025-06-30 21:36:09',_binary '\0','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',170000.00),(12,NULL,NULL,1,50000.00,1050000.00,'2025-07-01 01:15:53',_binary '\0','testfix@email.com','Test Fix Order','0123456789','Test Address','Ha Noi',1000000.00),(13,NULL,NULL,1,50000.00,269000.00,'2025-07-01 02:19:47',_binary '\0','testfe@email.com','Test User FE','0123456789','Test Address FE','Ha Noi',219000.00),(17,NULL,'4',1,30000.00,150000.00,'2025-07-06 12:59:46',_binary '','test@example.com','Test Customer','0123456789','123 Test Street','TP.HCM',120000.00),(18,NULL,NULL,1,30000.00,880000.00,'2025-07-06 13:05:07',_binary '\0','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',850000.00),(19,NULL,NULL,1,30000.00,129000.00,'2025-07-06 13:08:31',_binary '\0','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',99000.00),(20,NULL,'5',1,30000.00,150000.00,'2025-07-06 13:16:36',_binary '','test@example.com','Test Customer','0123456789','123 Test Street','TP.HCM',120000.00),(21,NULL,'9',1,30000.00,980000.00,'2025-07-06 13:18:02',_binary '','test@example.com','Test User','0123456789','123 Test Street','TP.HCM',950000.00),(22,NULL,NULL,1,30000.00,100000.00,'2025-07-06 13:22:52',_binary '\0','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',70000.00),(23,NULL,'8',1,30000.00,150000.00,'2025-07-06 13:31:39',_binary '','test@example.com','Test Customer','0123456789','123 Test Street','TP.HCM',120000.00),(24,NULL,'10',1,30000.00,340000.00,'2025-07-06 13:37:52',_binary '','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',310000.00),(25,NULL,NULL,1,30000.00,280000.00,'2025-07-06 13:44:26',_binary '\0','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',250000.00),(26,NULL,'11',1,30000.00,210000.00,'2025-07-06 13:50:33',_binary '','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',180000.00),(27,NULL,'12',1,30000.00,480000.00,'2025-07-06 14:03:21',_binary '','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',450000.00),(28,NULL,'13',1,30000.00,480000.00,'2025-07-06 14:06:21',_binary '','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',450000.00),(29,NULL,'14',1,22000.00,182000.00,'2025-07-06 14:13:30',_binary '','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',160000.00),(30,NULL,'15',1,22000.00,472000.00,'2025-07-06 14:21:39',_binary '','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',450000.00),(31,NULL,'16',1,22000.00,202000.00,'2025-07-06 14:23:19',_binary '','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',180000.00),(32,NULL,'17',1,22000.00,202000.00,'2025-07-06 14:28:39',_binary '','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',180000.00),(33,NULL,'18',1,22000.00,1022000.00,'2025-07-06 14:31:47',_binary '','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',1000000.00),(34,NULL,'19',1,22000.00,872000.00,'2025-07-06 14:34:42',_binary '','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',850000.00),(35,4,'20',1,30000.00,155000.00,'2025-07-06 14:43:15',_binary '','pika@example.com','pika','','','',125000.00),(36,4,'21',1,30000.00,195000.00,'2025-07-06 14:50:30',_binary '','pika@example.com','pika','','','',165000.00),(37,4,'22',1,30000.00,280000.00,'2025-07-06 15:05:41',_binary '','nguyenvana@email.com','Nguyễn Văn A','0905111222','123 Đường Lê Lợi, Quận 1','TP. Hồ Chí Minh',250000.00),(38,NULL,'23',1,22000.00,192000.00,'2025-07-06 15:07:12',_binary '','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',170000.00),(39,NULL,'25',1,22000.00,121000.00,'2025-07-06 15:21:35',_binary '','vungoclam001@gmail.com','Vũ Ngọc Lâm','0984674643','Quận Đống Đa','Hà Nội',99000.00),(40,NULL,'26',2,30000.00,180000.00,'2025-07-06 21:52:19',_binary '','a@a.a','aaa','0986654323','Đống Đa, Hà Nội','Hà Nội',150000.00),(41,NULL,NULL,1,22000.00,312000.00,'2025-07-06 21:55:59',_binary '\0','a@a.a','aaa','0986654323','Đống Đa, Hà Nội','Hà Nội',290000.00),(42,NULL,'27',2,30000.00,530000.00,'2025-07-06 22:14:39',_binary '','a@a.a','aaa','0986654323','Đống Đa, Hà Nội','Hà Nội',500000.00),(43,NULL,'28',2,30000.00,1680000.00,'2025-07-06 23:01:39',_binary '','vungoclam001@gmail.com','Vũ Ngọc Lâm','0986654323','Đống Đa, Hà Nội','Hà Nội',1650000.00),(44,NULL,NULL,1,22000.00,1022000.00,'2025-07-06 23:06:56',_binary '\0','vungoclam001@gmail.com','Vũ Ngọc Lâm','0986654323','Đống Đa, Hà Nội','Hà Nội',1000000.00),(45,NULL,NULL,1,22000.00,922000.00,'2025-07-06 23:07:39',_binary '\0','vungoclam001@gmail.com','Vũ Ngọc Lâm','0986654323','Đống Đa, Hà Nội','Hà Nội',900000.00),(46,NULL,'29',1,22000.00,922000.00,'2025-07-06 23:07:59',_binary '','vungoclam001@gmail.com','Vũ Ngọc Lâm','0986654323','Đống Đa, Hà Nội','Hà Nội',900000.00);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Nhà Giả Kim',120000,'Book','https://picsum.photos/200?random=1',144,'2023-01-10',20.5,0.3,1),(2,'Đắc Nhân Tâm',99000,'Book','https://picsum.photos/200?random=2',192,'2023-02-15',20.5,0.4,1),(3,'Harry Potter và Hòn Đá Phù Thủy',180000,'Book','https://picsum.photos/200?random=3',117,'2023-03-01',21,0.5,1),(4,'Sapiens: Lược Sử Loài Người',250000,'Book','https://picsum.photos/200?random=4',76,'2023-03-20',23,0.7,1),(5,'Tội Ác và Hình Phạt',165000,'Book','https://picsum.photos/200?random=5',60,'2023-04-11',20.5,0.6,1),(6,'Số Đỏ',85000,'Book','https://picsum.photos/200?random=6',180,'2023-05-02',19,0.3,1),(7,'The Great Gatsby',130000,'Book','https://picsum.photos/200?random=7',90,'2023-05-15',20,0.35,1),(8,'1984',140000,'Book','https://picsum.photos/200?random=8',110,'2023-06-01',19.8,0.3,1),(9,'Bắt Trẻ Đồng Xanh',115000,'Book','https://picsum.photos/200?random=9',75,'2023-06-12',20.5,0.3,1),(10,'Atomic Habits',155000,'Book','https://picsum.photos/200?random=10',250,'2023-07-01',22,0.45,1),(11,'Dế Mèn Phiêu Lưu Ký',70000,'Book','https://picsum.photos/200?random=11',299,'2023-07-15',20.5,0.4,1),(12,'The Lord of the Rings',450000,'Book','https://picsum.photos/200?random=12',47,'2023-08-01',23.4,1.2,1),(13,'Để Con Được Ốm',95000,'Book','https://picsum.photos/200?random=13',130,'2023-08-10',21,0.3,1),(14,'Rừng Na Uy',145000,'Book','https://picsum.photos/200?random=14',85,'2023-08-22',20.5,0.5,1),(15,'Thinking, Fast and Slow',210000,'Book','https://picsum.photos/200?random=15',99,'2023-09-01',23,0.6,1),(16,'Trăm Năm Cô Đơn',170000,'Book','https://picsum.photos/200?random=16',69,'2023-09-15',21,0.55,1),(17,'Lược Sử Tương Lai',240000,'Book','https://picsum.photos/200?random=17',75,'2023-09-28',23,0.65,1),(18,'Gone with the Wind',220000,'Book','https://picsum.photos/200?random=18',40,'2023-10-05',22,1,1),(19,'Bố Già',125000,'Book','https://picsum.photos/200?random=19',64,'2023-10-18',21,0.5,1),(20,'Giết Con Chim Nhại',135000,'Book','https://picsum.photos/200?random=20',88,'2023-11-01',20.5,0.4,1),(21,'CD Album \"Tâm 9\"',250000,'CD','https://picsum.photos/200?random=21',99,'2023-03-10',12.5,0.1,1),(22,'CD Album \"1989\"',320000,'CD','https://picsum.photos/200?random=22',70,'2023-04-15',12.5,0.1,1),(23,'CD Album \"25\"',300000,'CD','https://picsum.photos/200?random=23',80,'2023-05-20',12.5,0.1,1),(24,'CD Album \"Human\"',280000,'CD','https://picsum.photos/200?random=24',60,'2023-06-25',12.5,0.1,1),(25,'CD Album \"M-TP\"',290000,'CD','https://picsum.photos/200?random=25',148,'2023-07-30',12.5,0.1,1),(26,'CD Album \"Romance\"',275000,'CD','https://picsum.photos/200?random=26',55,'2023-08-14',12.5,0.1,1),(27,'LP \"Abbey Road\"',850000,'LP','https://picsum.photos/200?random=27',38,'2023-09-05',31.5,0.4,1),(28,'LP \"The Dark Side of the Moon\"',950000,'LP','https://picsum.photos/200?random=28',29,'2023-10-10',31.5,0.4,1),(29,'LP \"A Night at the Opera\"',900000,'LP','https://picsum.photos/200?random=29',33,'2023-11-12',31.5,0.4,1),(30,'CD Album \"Divide\"',310000,'CD','https://picsum.photos/200?random=30',84,'2023-11-20',12.5,0.1,1),(31,'DVD Phim \"Bố Già\"',150000,'DVD','https://picsum.photos/200?random=31',98,'2023-04-05',19,0.15,1),(32,'DVD Phim \"Mắt Biếc\"',140000,'DVD','https://picsum.photos/200?random=32',120,'2023-05-10',19,0.15,1),(33,'DVD Phim \"Parasite\"',180000,'DVD','https://picsum.photos/200?random=33',79,'2023-06-15',19,0.15,1),(34,'DVD Phim \"The Godfather\"',200000,'DVD','https://picsum.photos/200?random=34',60,'2023-07-20',19,0.15,1),(35,'DVD Phim \"Pulp Fiction\"',170000,'DVD','https://picsum.photos/200?random=35',69,'2023-08-25',19,0.15,1),(36,'DVD Phim \"Avengers: Endgame\"',220000,'DVD','https://picsum.photos/200?random=36',149,'2023-09-30',19,0.18,1),(37,'DVD Phim \"Inception\"',190000,'DVD','https://picsum.photos/200?random=37',95,'2023-10-15',19,0.16,1),(38,'DVD Phim \"Spirited Away\"',160000,'DVD','https://picsum.photos/200?random=38',109,'2023-11-05',19,0.15,1),(39,'DVD Phim \"Hai Phượng\"',130000,'DVD','https://picsum.photos/200?random=39',85,'2023-11-18',19,0.15,1),(40,'DVD Phim \"Your Name\"',165000,'DVD','https://picsum.photos/200?random=40',104,'2023-12-01',19,0.15,1),(41,'aaa',1000000,'Book','',0,NULL,0,0,NULL);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `shippingmethod`
--

LOCK TABLES `shippingmethod` WRITE;
/*!40000 ALTER TABLE `shippingmethod` DISABLE KEYS */;
INSERT INTO `shippingmethod` VALUES (1,'Standard',0,22000),(2,'Express 2H',1,30000);
/*!40000 ALTER TABLE `shippingmethod` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `transactioninformation`
--

LOCK TABLES `transactioninformation` WRITE;
/*!40000 ALTER TABLE `transactioninformation` DISABLE KEYS */;
INSERT INTO `transactioninformation` VALUES (1,NULL,121000,'SUCCESS','2025-06-30 19:06:10','Thanh toan don hang 1751310338016','VNPay','NCB','VNP15048618','00','15048618','1751310338016'),(2,NULL,142000,'SUCCESS','2025-06-30 19:30:54','Thanh toan don hang 1751311673584','VNPay','NCB','VNP15048649','00','15048649','1751311673584'),(3,NULL,121000,'SUCCESS','2025-06-30 19:32:18','Thanh toan don hang 1751311913024','VNPay','NCB','VNP15048651','00','15048651','1751311913024'),(4,17,150000,'SUCCESS','2025-07-06 12:59:46','Test payment','VNPAY_TEST',NULL,NULL,'00','TEST_1751806786419','ORDER_17'),(5,20,150000,'SUCCESS','2025-07-06 13:16:36','Test payment','VNPAY_TEST',NULL,NULL,'00','TEST_1751807795643','ORDER_20'),(6,NULL,92000,'SUCCESS','2025-07-06 13:23:22','Thanh toan don hang 22','VNPay','NCB','VNP15058960','00','15058960','22'),(7,NULL,980000,'SUCCESS','2025-07-06 13:30:09','Thanh toan don hang 21','VNPay','NCB','VNP123456','00','TEST123456','21'),(8,23,150000,'SUCCESS','2025-07-06 13:31:39','Test payment','VNPAY_TEST',NULL,NULL,'00','TEST_1751808699440','ORDER_23'),(9,21,980000,'SUCCESS','2025-07-06 13:33:30','Thanh toan don hang 21','VNPay','NCB','VNP123456','00','TEST123456','21'),(10,24,332000,'SUCCESS','2025-07-06 13:38:21','Thanh toan don hang 24','VNPay','NCB','VNP15058985','00','15058985','24'),(11,26,202000,'SUCCESS','2025-07-06 13:51:13','Thanh toan don hang 26','VNPay','NCB','VNP15059007','00','15059007','26'),(12,27,472000,'SUCCESS','2025-07-06 14:03:57','Thanh toan don hang 27','VNPay','NCB','VNP15059030','00','15059030','27'),(13,28,472000,'SUCCESS','2025-07-06 14:06:54','Thanh toan don hang 28','VNPay','NCB','VNP15059038','00','15059038','28'),(14,29,182000,'SUCCESS','2025-07-06 14:13:56','Thanh toan don hang 29','VNPay','NCB','VNP15059052','00','15059052','29'),(15,30,472000,'SUCCESS','2025-07-06 14:22:05','Thanh toan don hang 30','VNPay','NCB','VNP15059063','00','15059063','30'),(16,31,202000,'SUCCESS','2025-07-06 14:23:43','Thanh toan don hang 31','VNPay','NCB','VNP15059067','00','15059067','31'),(17,32,202000,'SUCCESS','2025-07-06 14:29:02','Thanh toan don hang 32','VNPay','NCB','VNP15059079','00','15059079','32'),(18,33,1022000,'SUCCESS','2025-07-06 14:32:07','Thanh toan don hang 33','VNPay','NCB','VNP15059086','00','15059086','33'),(19,34,872000,'SUCCESS','2025-07-06 14:35:04','Thanh toan don hang 34','VNPay','NCB','VNP15059093','00','15059093','34'),(20,35,147000,'SUCCESS','2025-07-06 14:43:36','Thanh toan don hang 35','VNPay','NCB','VNP15059107','00','15059107','35'),(21,36,187000,'SUCCESS','2025-07-06 14:52:14','Thanh toan don hang 36','VNPay','NCB','VNP15059123','00','15059123','36'),(22,37,272000,'SUCCESS','2025-07-06 15:06:07','Thanh toan don hang 37','VNPay','NCB','VNP15059146','00','15059146','37'),(23,38,192000,'SUCCESS','2025-07-06 15:07:35','Thanh toan don hang 38','VNPay','NCB','VNP15059150','00','15059150','38'),(24,39,121000,'SUCCESS','2025-07-06 15:22:00','Thanh toan don hang 39','VNPay','NCB','VNP15059174','00','15059174','39'),(25,39,121000,'SUCCESS','2025-07-06 15:22:12','Thanh toan don hang 39','VNPay','NCB','VNP15059174','00','15059174','39'),(26,40,180000,'SUCCESS','2025-07-06 21:52:43','Thanh toan don hang 40','VNPay','NCB','VNP15059359','00','15059359','40'),(27,42,530000,'SUCCESS','2025-07-06 22:15:05','Thanh toan don hang 42','VNPay','NCB','VNP15059361','00','15059361','42'),(28,43,1680000,'SUCCESS','2025-07-06 23:02:14','Thanh toan don hang 43','VNPay','NCB','VNP15059366','00','15059366','43'),(29,46,922000,'SUCCESS','2025-07-06 23:08:22','Thanh toan don hang 46','VNPay','NCB','VNP15059368','00','15059368','46');
/*!40000 ALTER TABLE `transactioninformation` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `trigger_debug_log`
--

LOCK TABLES `trigger_debug_log` WRITE;
/*!40000 ALTER TABLE `trigger_debug_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `trigger_debug_log` ENABLE KEYS */;
UNLOCK TABLES;

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

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin@aims.com','admin','$2a$10$NKuYiBdq6B/mup6brLwFkOZ4PGYmW5IZ.PmQvEe2UhYH/JSn8hdNe'),(2,'nguyenvana','nguyenvana@email.com','customer','hashed_customer_password_1'),(3,'tranthib','tranthib@email.com','customer','hashed_customer_password_2'),(4,'pika','pika@example.com','user','$2a$10$NKuYiBdq6B/mup6brLwFkOZ4PGYmW5IZ.PmQvEe2UhYH/JSn8hdNe'),(5,'testuser2','test2@example.com','customer','$2a$10$RSnQi6FG0XvikfT92TTNHeZg1n3vC6R8slswc7Px0t1lx6ezZiZU2'),(6,'aaaa','student001@example.com','customer','$2a$10$mewG2GH52wwmwPQsQS6VCeFbKmqZv2tZckfIMgnc3xCpb4H.KrRbC'),(7,'test','test@example.com','user','$2a$10$a3QJ7Vvw02nJOKMgsH9g1eQpXWI6QqE8dbc71TNx6reqN/WYieO3m');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-07  6:51:51
