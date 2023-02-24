--liquibase formatted sql

--changeset anisimov:1875185 splitStatements:false runOnChange:false 2.sql

--comment Создание таблицы для BinaryP2PBet

CREATE TABLE IF NOT EXISTS binary_p2p_bet
(
    id                   BIGINT primary key,
    left_amount          NUMERIC        NOT NULL,
    right_amount         NUMERIC        NOT NULL,
    locked_value         VARCHAR(32),
    final_value          VARCHAR(32),
    side_won             BOOLEAN,
    lock_date            TIMESTAMP      NOT NULL,
    expiration_date      TIMESTAMP      NOT NULL,
    created_tx_hash      VARCHAR(100)   NOT NULL,
    created_block_number numeric(19, 2) NOT NULL,
    closed_tx_hash       VARCHAR(100),
    closed_block_number  numeric(19, 2),
    event_base_id        UUID,
    created_date         TIMESTAMP      NOT NULL,
    modified_date        TIMESTAMP      NOT NULL
)