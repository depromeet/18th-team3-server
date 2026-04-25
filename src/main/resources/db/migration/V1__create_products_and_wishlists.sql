CREATE TABLE products (
    id BIGINT NOT NULL AUTO_INCREMENT,
    source_url VARCHAR(2048) NOT NULL,
    name VARCHAR(512) NULL,
    regular_price INT NULL,
    discounted_price INT NULL,
    currency VARCHAR(8) NULL,
    image_url VARCHAR(2048) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_products_source_url (source_url(512))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE wishlists (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    snapshot_regular_price INT NULL,
    snapshot_discounted_price INT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_wishlists_user_product (user_id, product_id),
    KEY idx_wishlists_user_id (user_id),
    CONSTRAINT fk_wishlists_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
