--liquibase formatted sql

--changeset anisimov:1875196 splitStatements:false runOnChange:false 3.sql

--comment Добавление таблицы BinaryJoinEntity

CREATE TABLE IF NOT EXISTS binary_join
(
    id                 UUID primary key,
    status             varchar(20)  NOT NULL,
    client             VARCHAR(50)  NOT NULL,
    binary_bet_id      BIGINT       NOT NULL,
    join_id            BIGINT       NOT NULL,
    join_id_client_ref BIGINT       NOT NULL,
    side               BOOLEAN      NOT NULL,
    use_alter_fee      BOOLEAN      NOT NULL,
    join_amount        NUMERIC      NOT NULL,
    created_date       TIMESTAMP    NOT NULL,
    modified_date      TIMESTAMP    NOT NULL,
    join_hash          VARCHAR(100) NOT NULL,
    cancel_hash        VARCHAR(100),
    refund_hash        VARCHAR(100),
    prize_taken_hash   VARCHAR(100),
    CONSTRAINT fk_binary_join_binary_bet FOREIGN KEY (binary_bet_id) REFERENCES binary_p2p_bet (id)
);
