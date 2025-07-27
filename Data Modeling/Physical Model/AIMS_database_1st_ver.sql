CREATE TABLE `Product` (
  `productId` int PRIMARY KEY,
  `title` varchar(100),
  `price` double,
  `category` varchar(20),
  `imageURL` varchar(50),
  `quantity` int,
  `entry_date` date,
  `dimension` double,
  `weight` double
);

CREATE TABLE `Books` (
  `productId` int PRIMARY KEY,
  `author` varchar(255),
  `publisher` varchar(255),
  `coverType` varchar(255),
  `publicationDate` date,
  `pagesNumber` int,
  `language` varchar(255),
  `genre` varchar(255)
);

CREATE TABLE `CDLPs` (
  `productId` int PRIMARY KEY,
  `artist` varchar(255),
  `recordLabel` varchar(255),
  `tracklist` varchar(255)
);

CREATE TABLE `DVDs` (
  `productId` int PRIMARY KEY,
  `discType` varchar(255),
  `director` varchar(255),
  `runtime` time,
  `studio` varchar(255),
  `language` varchar(255),
  `subtitles` varchar(255),
  `releaseDate` date
);

CREATE TABLE `User` (
  `user_id` int PRIMARY KEY,
  `username` varchar(255),
  `email` varchar(255)
);

CREATE TABLE `Order` (
  `order_id` int PRIMARY KEY,
  `user_id` int,
  `delivery_id` int,
  `shipping_fees` double,
  `total_amount` double,
  `created_at` datetime,
  `VAT` int,
  `totalFees` double,
  `transactionId` int
);

CREATE TABLE `shippingMethod` (
  `method_id` int PRIMARY KEY,
  `method_name` varchar(255),
  `is_rush` boolean,
  `shipping_fees` double
);

CREATE TABLE `OrderItem` (
  `order_item_id` int PRIMARY KEY,
  `order_id` int,
  `product_id` int,
  `quantity` int,
  `unit_price` double
);

CREATE TABLE `DeliveryInformation` (
  `delivery_id` int PRIMARY KEY,
  `shipping_method_id` int,
  `recipient_name` varchar(255),
  `email` varchar(255),
  `phone` varchar(15),
  `address` varchar(255),
  `province` varchar(255),
  `shipping_fee` double,
  `instruction` varchar(255)
);

CREATE TABLE `TransactionInformation` (
  `transaction_id` int PRIMARY KEY,
  `order_id` int,
  `total_fee` double,
  `status` varchar(255),
  `transaction_time` datetime,
  `content` varchar(255)
);

ALTER TABLE `Books` ADD FOREIGN KEY (`productId`) REFERENCES `Product` (`productId`);

ALTER TABLE `CDLPs` ADD FOREIGN KEY (`productId`) REFERENCES `Product` (`productId`);

ALTER TABLE `DVDs` ADD FOREIGN KEY (`productId`) REFERENCES `Product` (`productId`);

ALTER TABLE `Order` ADD FOREIGN KEY (`user_id`) REFERENCES `User` (`user_id`);

ALTER TABLE `DeliveryInformation` ADD FOREIGN KEY (`delivery_id`) REFERENCES `Order` (`delivery_id`);

ALTER TABLE `shippingMethod` ADD FOREIGN KEY (`method_id`) REFERENCES `DeliveryInformation` (`shipping_method_id`);

ALTER TABLE `Product` ADD FOREIGN KEY (`productId`) REFERENCES `OrderItem` (`product_id`);

CREATE TABLE `OrderItem_Order` (
  `OrderItem_order_id` int,
  `Order_order_id` int,
  PRIMARY KEY (`OrderItem_order_id`, `Order_order_id`)
);

ALTER TABLE `OrderItem_Order` ADD FOREIGN KEY (`OrderItem_order_id`) REFERENCES `OrderItem` (`order_id`);

ALTER TABLE `OrderItem_Order` ADD FOREIGN KEY (`Order_order_id`) REFERENCES `Order` (`order_id`);


ALTER TABLE `TransactionInformation` ADD FOREIGN KEY (`transaction_id`) REFERENCES `Order` (`transactionId`);
