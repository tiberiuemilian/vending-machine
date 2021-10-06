CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(45) NOT NULL,
    `password` VARCHAR(100) NOT NULL,
    `role` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`id`))
COMMENT = 'Stores users infos';

CREATE TABLE `deposit` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `denomination` BIGINT NOT NULL,
    `nrOfCoins` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_deposit_user_id` (`user_id`),
    CONSTRAINT `fk_deposit_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`))
COMMENT = 'A User deposit of coins for a particular denomination';

CREATE TABLE `product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `product_name` VARCHAR(45) NOT NULL,
  `amount_available` INT NOT NULL,
  `cost` BIGINT NOT NULL,
  `seller_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_product_user_id` (`seller_id`),
  CONSTRAINT `fk_product_user_id` FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`))
COMMENT = 'Stores products infos';
