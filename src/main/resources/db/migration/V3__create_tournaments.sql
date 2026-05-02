CREATE TABLE tournament
(
    id                        BIGINT       NOT NULL AUTO_INCREMENT,
    user_id                   BINARY(16) NOT NULL,
    name                      VARCHAR(255) NOT NULL,
    round                     INT          NOT NULL,
    final_winner_wish_item_id BIGINT NULL,
    status                    VARCHAR(50)  NOT NULL DEFAULT 'IN_PROGRESS',
    created_at                DATETIME(6) NOT NULL,
    updated_at                DATETIME(6) NOT NULL,
    deleted_at                DATETIME(6) NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tournament_wish_item
(
    tournament_id BIGINT NOT NULL,
    wish_item_id  BIGINT NOT NULL,
    position      INT    NOT NULL,
    PRIMARY KEY (tournament_id, position),
    CONSTRAINT fk_twi_tournament FOREIGN KEY (tournament_id) REFERENCES tournament (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tournament_history
(
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    tournament_id       BIGINT NOT NULL,
    current_round       INT    NOT NULL,
    first_wish_item_id  BIGINT NOT NULL,
    second_wish_item_id BIGINT NOT NULL,
    winner_wish_item_id BIGINT NOT NULL,
    created_at          DATETIME(6) NOT NULL,
    updated_at          DATETIME(6) NOT NULL,
    deleted_at          DATETIME(6) NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
