-- Laptopshop database bootstrap script
-- Usage:
--   mysql -u root -p < database/laptopshop.sql

CREATE DATABASE IF NOT EXISTS laptopshop
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE laptopshop;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS roles (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  PRIMARY KEY (id),
  UNIQUE KEY uk_roles_name (name)
);

CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  full_name VARCHAR(255) NOT NULL,
  address VARCHAR(255),
  phone VARCHAR(255),
  avatar VARCHAR(255),
  role_id BIGINT,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email),
  KEY idx_users_role_id (role_id),
  CONSTRAINT fk_users_role
    FOREIGN KEY (role_id) REFERENCES roles(id)
    ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS products (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  price DOUBLE NOT NULL,
  image VARCHAR(255),
  detail_desc MEDIUMTEXT NOT NULL,
  short_desc VARCHAR(255) NOT NULL,
  quantity BIGINT NOT NULL,
  sold BIGINT NOT NULL DEFAULT 0,
  factory VARCHAR(255),
  target VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS carts (
  id BIGINT NOT NULL AUTO_INCREMENT,
  sum INT NOT NULL DEFAULT 0,
  user_id BIGINT,
  PRIMARY KEY (id),
  UNIQUE KEY uk_carts_user_id (user_id),
  CONSTRAINT fk_carts_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS cart_detail (
  id BIGINT NOT NULL AUTO_INCREMENT,
  quantity BIGINT NOT NULL,
  price DOUBLE NOT NULL,
  cart_id BIGINT,
  product_id BIGINT,
  PRIMARY KEY (id),
  KEY idx_cart_detail_cart_id (cart_id),
  KEY idx_cart_detail_product_id (product_id),
  CONSTRAINT fk_cart_detail_cart
    FOREIGN KEY (cart_id) REFERENCES carts(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_cart_detail_product
    FOREIGN KEY (product_id) REFERENCES products(id)
    ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS orders (
  id BIGINT NOT NULL AUTO_INCREMENT,
  total_price DOUBLE NOT NULL,
  receiver_name VARCHAR(255),
  receiver_address VARCHAR(255),
  receiver_phone VARCHAR(255),
  status VARCHAR(255),
  user_id BIGINT,
  PRIMARY KEY (id),
  KEY idx_orders_user_id (user_id),
  CONSTRAINT fk_orders_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS order_detail (
  id BIGINT NOT NULL AUTO_INCREMENT,
  quantity BIGINT NOT NULL,
  price DOUBLE NOT NULL,
  order_id BIGINT,
  product_id BIGINT,
  PRIMARY KEY (id),
  KEY idx_order_detail_order_id (order_id),
  KEY idx_order_detail_product_id (product_id),
  CONSTRAINT fk_order_detail_order
    FOREIGN KEY (order_id) REFERENCES orders(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_order_detail_product
    FOREIGN KEY (product_id) REFERENCES products(id)
    ON UPDATE CASCADE ON DELETE SET NULL
);

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO roles (name, description)
VALUES
  ('ADMIN', 'Administrator'),
  ('USER', 'End user')
ON DUPLICATE KEY UPDATE description = VALUES(description);

INSERT INTO products (name, price, image, detail_desc, short_desc, quantity, sold, factory, target)
VALUES
  ('Dell Inspiron 15', 14990000, '1711078452562-dell-01.png', 'Core i5, 16GB RAM, 512GB SSD.', 'Laptop office and study', 10, 0, 'DELL', 'SINHVIEN-OFFICE'),
  ('ASUS TUF Gaming', 22990000, '1711078092373-asus-01.png', 'Ryzen 7, 16GB RAM, RTX 4050.', 'Gaming laptop', 8, 0, 'ASUS', 'GAMING'),
  ('MacBook Air M2', 26990000, '1711079954090-apple-01.png', 'Apple M2, 16GB RAM, 512GB SSD.', 'Thin and light laptop', 6, 0, 'APPLE', 'MOBILE-CREATIVE')
ON DUPLICATE KEY UPDATE
  price = VALUES(price),
  quantity = VALUES(quantity),
  sold = VALUES(sold);
