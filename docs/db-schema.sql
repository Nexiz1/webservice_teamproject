-- =============================================
-- 온라인 서점 데이터베이스 스키마
-- =============================================

CREATE TABLE `book` (
	`book_id`	INT	NOT NULL,
	`title`	VARCHAR(255)	NOT NULL,
	`author`	VARCHAR(255)	NOT NULL,
	`publisher`	VARCHAR(255)	NOT NULL,
	`summary`	TEXT	NOT NULL,
	`isbn`	VARCHAR(20)	NOT NULL,
	`price`	INT	NOT NULL,
	`publication_date`	DATE	NOT NULL,
	`created_at`	DATETIME	NOT NULL,
	`updated_at`	DATETIME	NULL
);

CREATE TABLE `favorite` (
	`favorite_id`	INT	NOT NULL,
	`book_id`	INT	NOT NULL,
	`user_id`	INT	NOT NULL
);

CREATE TABLE `library` (
	`library_id`	INT	NOT NULL,
	`user_id`	INT	NOT NULL,
	`book_id`	INT	NOT NULL
);

CREATE TABLE `cart` (
	`cart_id`	INT	NOT NULL,
	`created_at`	DATETIME	NOT NULL,
	`user_id`	INT	NOT NULL
);

CREATE TABLE `review` (
	`review_id`	INT	NOT NULL,
	`rating`	INT	NOT NULL,
	`content`	VARCHAR(255)	NULL,
	`created_at`	DATETIME	NOT NULL,
	`updated_at`	DATETIME	NULL,
	`book_id`	INT	NOT NULL,
	`user_id`	INT	NOT NULL
);

CREATE TABLE `order_item` (
	`order_item_id`	INT	NOT NULL,
	`quantity`	INT	NOT NULL,
	`price`	INT	NOT NULL,
	`order_id`	INT	NOT NULL,
	`book_id`	INT	NOT NULL
);

CREATE TABLE `comment` (
	`comment_id`	INT	NOT NULL,
	`content`	VARCHAR(255)	NOT NULL,
	`book_id`	INT	NOT NULL,
	`user_id`	INT	NOT NULL,
	`review_id`	INT	NOT NULL,
	`created_at`	DATETIME	NOT NULL,
	`updated_at`	DATETIME	NULL
);

CREATE TABLE `cart_item` (
	`cart_item_id`	INT	NOT NULL,
	`quantity`	INT	NOT NULL,
	`cart_id`	INT	NOT NULL,
	`book_id`	INT	NOT NULL
);

CREATE TABLE `user` (
	`user_id`	INT	NOT NULL,
	`user_name`	VARCHAR(255)	NOT NULL,
	`email`	VARCHAR(255)	NOT NULL,
	`birthday`	DATE	NOT NULL,
	`gender`	CHAR(1)	NOT NULL,
	`phone`	VARCHAR(20)	NOT NULL,
	`role`	ENUM('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SALER')	NOT NULL,
	`password`	VARCHAR(255)	NOT NULL,
	`created_at`	DATETIME	NOT NULL,
	`updated_at`	DATETIME	NULL,
	`address`	VARCHAR(255)	NOT NULL
);

CREATE TABLE `order` (
	`order_id`	INT	NOT NULL,
	`total_amount`	Integer(20)	NOT NULL,
	`status`	ENUM('SHIPPED')	NOT NULL,
	`created_at`	DATETIME	NOT NULL,
	`updated_at`	DATETIME	NULL,
	`user_id`	Integer(11)	NOT NULL
);

-- =============================================
-- Primary Key Constraints
-- =============================================

ALTER TABLE `book` ADD CONSTRAINT `PK_BOOK` PRIMARY KEY (
	`book_id`
);

ALTER TABLE `favorite` ADD CONSTRAINT `PK_FAVORITE` PRIMARY KEY (
	`favorite_id`
);

ALTER TABLE `library` ADD CONSTRAINT `PK_LIBRARY` PRIMARY KEY (
	`library_id`
);

ALTER TABLE `cart` ADD CONSTRAINT `PK_CART` PRIMARY KEY (
	`cart_id`
);

ALTER TABLE `review` ADD CONSTRAINT `PK_REVIEW` PRIMARY KEY (
	`review_id`
);

ALTER TABLE `order_item` ADD CONSTRAINT `PK_ORDER_ITEM` PRIMARY KEY (
	`order_item_id`
);

ALTER TABLE `comment` ADD CONSTRAINT `PK_COMMENT` PRIMARY KEY (
	`comment_id`
);

ALTER TABLE `cart_item` ADD CONSTRAINT `PK_CART_ITEM` PRIMARY KEY (
	`cart_item_id`
);

ALTER TABLE `user` ADD CONSTRAINT `PK_USER` PRIMARY KEY (
	`user_id`
);

ALTER TABLE `order` ADD CONSTRAINT `PK_ORDER` PRIMARY KEY (
	`order_id`
);

-- =============================================
-- Foreign Key Constraints
-- =============================================

ALTER TABLE `favorite` ADD CONSTRAINT `FK_favorite_book`
  FOREIGN KEY (`book_id`) REFERENCES `book`(`book_id`) ON DELETE CASCADE;
ALTER TABLE `favorite` ADD CONSTRAINT `FK_favorite_user`
  FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE;

ALTER TABLE `library` ADD CONSTRAINT `FK_library_user`
  FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE;
ALTER TABLE `library` ADD CONSTRAINT `FK_library_book`
  FOREIGN KEY (`book_id`) REFERENCES `book`(`book_id`) ON DELETE CASCADE;

ALTER TABLE `cart` ADD CONSTRAINT `FK_cart_user`
  FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE;

ALTER TABLE `review` ADD CONSTRAINT `FK_review_book`
  FOREIGN KEY (`book_id`) REFERENCES `book`(`book_id`) ON DELETE CASCADE;
ALTER TABLE `review` ADD CONSTRAINT `FK_review_user`
  FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE;

ALTER TABLE `order_item` ADD CONSTRAINT `FK_order_item_order`
  FOREIGN KEY (`order_id`) REFERENCES `order`(`order_id`) ON DELETE CASCADE;
ALTER TABLE `order_item` ADD CONSTRAINT `FK_order_item_book`
  FOREIGN KEY (`book_id`) REFERENCES `book`(`book_id`) ON DELETE CASCADE;

ALTER TABLE `comment` ADD CONSTRAINT `FK_comment_book`
  FOREIGN KEY (`book_id`) REFERENCES `book`(`book_id`) ON DELETE CASCADE;
ALTER TABLE `comment` ADD CONSTRAINT `FK_comment_user`
  FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE;
ALTER TABLE `comment` ADD CONSTRAINT `FK_comment_review`
  FOREIGN KEY (`review_id`) REFERENCES `review`(`review_id`) ON DELETE CASCADE;

ALTER TABLE `cart_item` ADD CONSTRAINT `FK_cart_item_cart`
  FOREIGN KEY (`cart_id`) REFERENCES `cart`(`cart_id`) ON DELETE CASCADE;
ALTER TABLE `cart_item` ADD CONSTRAINT `FK_cart_item_book`
  FOREIGN KEY (`book_id`) REFERENCES `book`(`book_id`) ON DELETE CASCADE;

ALTER TABLE `order` ADD CONSTRAINT `FK_order_user`
  FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE;

-- =============================================
-- Indexes for search/join performance
-- =============================================

CREATE INDEX `IDX_book_title` ON `book`(`title`);
CREATE INDEX `IDX_book_author` ON `book`(`author`);
CREATE INDEX `IDX_book_publisher` ON `book`(`publisher`);
CREATE INDEX `IDX_book_isbn` ON `book`(`isbn`);

CREATE INDEX `IDX_user_email` ON `user`(`email`);
CREATE INDEX `IDX_user_name` ON `user`(`user_name`);

CREATE INDEX `IDX_review_book` ON `review`(`book_id`);
CREATE INDEX `IDX_review_user` ON `review`(`user_id`);

CREATE INDEX `IDX_order_user` ON `order`(`user_id`);
CREATE INDEX `IDX_order_status` ON `order`(`status`);

CREATE INDEX `IDX_favorite_user` ON `favorite`(`user_id`);
CREATE INDEX `IDX_favorite_book` ON `favorite`(`book_id`);

CREATE INDEX `IDX_cart_item_cart` ON `cart_item`(`cart_id`);
CREATE INDEX `IDX_cart_item_book` ON `cart_item`(`book_id`);
