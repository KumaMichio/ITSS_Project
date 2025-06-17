

CREATE TABLE products(
  id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(350) COMMENT 'Tên sản phẩm',
  price DOUBLE NOT NULL CHECK (price >= 0),
  category VARCHAR(20),
  imageURL VARCHAR(300),
  quantity INT CHECK (quantity >= 0),
  entry_date DATE,
  dimension DOUBLE,
  `weight` DOUBLE
);

CREATE TABLE shippingmethod (
  method_id INT PRIMARY KEY AUTO_INCREMENT,
  method_name VARCHAR(255),
  is_rush BOOLEAN,
  shipping_fees DOUBLE
);

CREATE TABLE deliveryinformation (
  delivery_id INT PRIMARY KEY,
  shipping_method_id INT,
  recipient_name VARCHAR(255),
  email VARCHAR(255),
  phone VARCHAR(15),
  address VARCHAR(255),
  province VARCHAR(255),
  shipping_fee DOUBLE,
  instruction VARCHAR(255),
  FOREIGN KEY (shipping_method_id) REFERENCES shippingmethod(method_id)
);

CREATE TABLE transactioninformation (
  transaction_id INT PRIMARY KEY,
  order_id INT,
  total_fee DOUBLE,
  status VARCHAR(255),
  transaction_time DATETIME,
  content VARCHAR(255)
);

CREATE TABLE books(
  product_id INT PRIMARY KEY,
  FOREIGN KEY (product_id) REFERENCES products (id),
  author VARCHAR(255),
  publisher VARCHAR(255),
  coverType VARCHAR(255),
  publicationDate DATE,
  pagesNumber INT,
  `language` VARCHAR(255),
  genre VARCHAR(255)
);

CREATE TABLE cdlps(
  product_id INT PRIMARY KEY,
  FOREIGN KEY (product_id) REFERENCES products (id),
  artist VARCHAR(255),
  recordLabel VARCHAR(255),
  tracklist VARCHAR(255)
);

CREATE TABLE dvds(
  product_id INT PRIMARY KEY,
  FOREIGN KEY (product_id) REFERENCES products (id),
  discType VARCHAR(255),
  director VARCHAR(255),
  runtime TIME,
  studio VARCHAR(255),
  `language` VARCHAR(255),
  subtitles VARCHAR(255),
  releaseDate DATE
);

CREATE TABLE users(
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255),
  email VARCHAR(255)
);

CREATE TABLE orders(
  order_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  delivery_id INT,
  transaction_id INT,
  shipping_fees DOUBLE,
  total_amount DOUBLE,
  created_at DATETIME,
  VAT INT,
  totalFees DOUBLE,
  FOREIGN KEY (user_id) REFERENCES users (user_id),
  FOREIGN KEY (delivery_id) REFERENCES deliveryinformation (delivery_id),
  FOREIGN KEY (transaction_id) REFERENCES transactioninformation (transaction_id)
);

CREATE TABLE orderitem (
  order_item_id INT PRIMARY KEY AUTO_INCREMENT,
  order_id INT,
  product_id INT,
  quantity INT,
  unit_price DOUBLE,
  FOREIGN KEY (order_id) REFERENCES orders(order_id),
  FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE `orderitem_order` (
  `orderitem_order_id` int,
  `order_order_id` int,
  PRIMARY KEY (`orderitem_order_id`, `order_order_id`)
);

ALTER TABLE `orderitem_order` ADD FOREIGN KEY (`orderitem_order_id`) REFERENCES `orderitem` (`order_id`);

ALTER TABLE `orderitem_order` ADD FOREIGN KEY (`order_order_id`) REFERENCES `orders` (`order_id`);
