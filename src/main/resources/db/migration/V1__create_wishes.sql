CREATE TABLE wishes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    guest_id BINARY(16) NOT NULL,
    source_url VARCHAR(2048) NOT NULL,
    name VARCHAR(512) NULL,
    image_url VARCHAR(2048) NULL,
    regular_price INT NULL,
    discounted_price INT NULL,
    currency VARCHAR(8) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_wishes_guest_source (guest_id, source_url(512)),
    KEY idx_wishes_guest_id (guest_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
