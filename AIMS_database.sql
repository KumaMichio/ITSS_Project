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
  user_id INT
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
  user_id INT NOT NULL
);

CREATE TABLE transactioninformation (
  transaction_id INT PRIMARY KEY,
  order_id INT,
  total_fee DOUBLE,
  `status` VARCHAR(255),
  transaction_time DATETIME,
  content VARCHAR(255),
  payment_method VARCHAR(255)
);

CREATE TABLE books(
  product_id INT PRIMARY KEY,
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
  artist VARCHAR(255),
  recordLabel VARCHAR(255),
  tracklist VARCHAR(255)
);

CREATE TABLE dvds(
  product_id INT PRIMARY KEY,
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
  `password` TEXT
);

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
  totalFees DOUBLE
);

CREATE TABLE orderitem (
  id INT PRIMARY KEY AUTO_INCREMENT,
  order_id INT,
  product_id INT,
  quantity INT,
  price DOUBLE
);

ALTER TABLE products ADD CONSTRAINT fk_products_user FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE books ADD CONSTRAINT fk_books_product FOREIGN KEY (product_id) REFERENCES products(id);

ALTER TABLE cdlps ADD CONSTRAINT fk_cdlps_product FOREIGN KEY (product_id) REFERENCES products(id);

ALTER TABLE dvds ADD CONSTRAINT fk_dvds_product FOREIGN KEY (product_id) REFERENCES products(id);

ALTER TABLE delivery_info ADD CONSTRAINT fk_delivery_user FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE orders
ADD CONSTRAINT fk_orders_user
FOREIGN KEY (user_id) REFERENCES users(user_id),
ADD CONSTRAINT fk_orders_delivery
FOREIGN KEY (delivery_id) REFERENCES delivery_info(id),
ADD CONSTRAINT fk_orders_transaction
FOREIGN KEY (transaction_id) REFERENCES transactioninformation(transaction_id),
ADD CONSTRAINT fk_orders_shippingmethod
FOREIGN KEY (method_id) REFERENCES shippingmethod(method_id);

ALTER TABLE orderitem
ADD CONSTRAINT fk_orderitem_order
FOREIGN KEY (order_id) REFERENCES orders(order_id),
ADD CONSTRAINT fk_orderitem_product
FOREIGN KEY (product_id) REFERENCES products(id);

ALTER TABLE transactioninformation
ADD CONSTRAINT fk_transInfo_orders
FOREIGN KEY (order_id) REFERENCES orders(order_id);

INSERT INTO users (user_id, username, email, role, password)
VALUES (2, 'demo_user', 'demo@example.com', 'seller', '123456');


INSERT INTO products (title, price, category, imageURL, quantity, entry_date, dimension, `weight`, user_id)
VALUES('Dewewew',156000, 'Book', 'dddddđd', 123,'2024-06-08', 12, 1, 2);

-- Insert initial data into shippingmethod table
INSERT INTO shippingmethod(`method_name`, `shipping_fees`) VALUES ('Regular', 25000);
INSERT INTO shippingmethod(`method_name`, `shipping_fees`) VALUES ('Rush', 0);
