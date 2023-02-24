--liquibase formatted sql

--changeset anisimov:1875198 splitStatements:false runOnChange:false 3.sql

--comment Добавление таблицы CustomMatchingEntity

CREATE TABLE IF NOT EXISTS custom_matching
(
    id                  UUID primary key,
    custom_bet_id       BIGINT    NOT NULL,
    left_last_id        BIGINT    NOT NULL,
    left_size           BIGINT    NOT NULL,
    right_last_id       BIGINT    NOT NULL,
    right_size          BIGINT    NOT NULL,
    left_free_amount    NUMERIC   NOT NULL,
    left_locked_amount  NUMERIC   NOT NULL,
    right_free_amount   NUMERIC   NOT NULL,
    right_locked_amount NUMERIC   NOT NULL,
    created_date        TIMESTAMP NOT NULL,
    modified_date       TIMESTAMP NOT NULL,
    CONSTRAINT fk_custom_matching_custom_bet FOREIGN KEY (custom_bet_id) REFERENCES custom_p2p_bet (id)
);
