-- This SQL script creates a database schema for an e-commerce application - AIMS
-- It includes tables for users, products, orders, order items, shipping methods, delivery information, and transaction information.
CREATE TABLE products(
  id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(350) COMMENT 'Tên sản phẩm',
  price DOUBLE NOT NULL CHECK (price >= 0),
  category VARCHAR(20),
  imageURL VARCHAR(300),
  quantity INT CHECK (quantity >= 0),
  entry_date DATE,
  dimension DOUBLE,
  `weight` DOUBLE,
  user_id INT,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);



CREATE TABLE shippingmethod (
  method_id INT PRIMARY KEY AUTO_INCREMENT,
  method_name VARCHAR(255),
  is_rush BOOLEAN,
  shipping_fees DOUBLE
);

CREATE TABLE delivery_info (
    id INT AUTO_INCREMENT PRIMARY KEY,             
    `name` VARCHAR(255) NOT NULL,                    
    phone VARCHAR(15) NOT NULL,                    
    `address` VARCHAR(500) NOT NULL,                 
    province VARCHAR(100),                         
    instruction VARCHAR(500),                      
    user_id INT NOT NULL,                          
    FOREIGN KEY (user_id) REFERENCES users(id)    
)

CREATE TABLE transactioninformation (
  transaction_id INT PRIMARY KEY,
  order_id INT,
  total_fee DOUBLE,
  `status` VARCHAR(255),
  transaction_time DATETIME,
  content VARCHAR(255),
  payment_method VARCHAR(255),
  FOREIGN KEY (order_id) REFERENCES orders (order_id)
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
  email VARCHAR(255),
  `role` VARCHAR(100),
  `password` text
);

insert into product (title, price, category, image_url, quantity, entry_date, dimension, weight, seller_id)
values('Dewewew',156000, 'Book', 'dddddđd', 123,'2024-06-08', 12, 1, 2);

CREATE TABLE orders(
  order_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  delivery_id INT,
  transaction_id INT,
  method_id INT,
  shipping_fees DOUBLE,
  total_amount DOUBLE,
  created_at DATETIME,
  VAT INT,
  totalFees DOUBLE,
  FOREIGN KEY (user_id) REFERENCES users (user_id),
  FOREIGN KEY (delivery_id) REFERENCES delivery_info (id),
  FOREIGN KEY (transaction_id) REFERENCES transactioninformation (transaction_id),
  FOREIGN KEY (method_id) REFERENCES shippingmethod (method_id)
);

CREATE TABLE orderitem (
  id INT PRIMARY KEY AUTO_INCREMENT,
  order_id INT,
  product_id INT,
  quantity INT,
  price DOUBLE,
  FOREIGN KEY (order_id) REFERENCES orders(order_id),
  FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Insert initial data into shippingmethod table
INSERT INTO shippingmethod(`name`, `shipping_fees`) VALUES ('Regular', 25000);
INSERT INTO shippingmethod(`name`, ` shipping_fees`) VALUES ('Rush', 0);
