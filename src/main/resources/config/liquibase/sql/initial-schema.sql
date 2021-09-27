CREATE TABLE `product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `product_name` VARCHAR(45) NOT NULL,
  `amount_available` INT NOT NULL,
  -- `cost` DECIMAL(21,2) NOT NULL,
  `cost` BIGINT NOT NULL,
  `seller_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`))
COMMENT = 'Stores products infos';

CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(45) NOT NULL,
    `password` VARCHAR(45) NOT NULL,
    `deposit` BIGINT NOT NULL,
    `role` VARCHAR(45) NOT NULL,
PRIMARY KEY (`id`))
COMMENT = 'Stores users infos';

CREATE TABLE `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `customer_id` BIGINT NOT NULL,
    `date` DATETIME DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`))
COMMENT = 'Stores orders infos';

CREATE TABLE `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `product_id` BIGINT NOT NULL,
    `order_id` BIGINT NOT NULL,
    `cost` BIGINT NOT NULL,
PRIMARY KEY (`id`))
COMMENT = 'Stores orders items infos';
