--liquibase formatted sql

--changeset anisimov:1875197 splitStatements:false runOnChange:false 3.sql

--comment Добавление таблицы CustomJoinEntity

CREATE TABLE IF NOT EXISTS custom_join
(
    id                 UUID primary key,
    status             varchar(20)  NOT NULL,
    client             VARCHAR(50)  NOT NULL,
    custom_bet_id      BIGINT       NOT NULL,
    join_id            BIGINT       NOT NULL,
    join_id_client_ref BIGINT       NOT NULL,
    side               BOOLEAN      NOT NULL,
    use_alter_fee      BOOLEAN      NOT NULL,
    join_amount        NUMERIC      NOT NULL,
    free_amount        NUMERIC      NOT NULL,
    locked_amount      NUMERIC      NOT NULL,
    created_date       TIMESTAMP    NOT NULL,
    modified_date      TIMESTAMP    NOT NULL,
    join_hash          VARCHAR(100) NOT NULL,
    cancel_hash        VARCHAR(100),
    refund_hash        VARCHAR(100),
    prize_taken_hash   VARCHAR(100),
    CONSTRAINT fk_custom_join_custom_bet FOREIGN KEY (custom_bet_id) REFERENCES custom_p2p_bet (id)
);
