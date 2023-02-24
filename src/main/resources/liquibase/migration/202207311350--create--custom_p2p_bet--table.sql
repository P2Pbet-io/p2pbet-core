--liquibase formatted sql

--changeset anisimov:1875190 splitStatements:false runOnChange:false 2.sql

--comment Создание таблицы для BinaryP2PBet

CREATE TABLE IF NOT EXISTS custom_p2p_bet
(
    id                   BIGINT primary key,
    status               varchar(20)    NOT NULL,
    lock_date            TIMESTAMP      NOT NULL,
    expiration_date      TIMESTAMP      NOT NULL,
    event_base_id        UUID,
    target_value         VARCHAR(32)    NOT NULL,
    target_side          BOOLEAN        NOT NULL,
    coefficient          numeric        NOT NULL,
    creator              VARCHAR(50)    NOT NULL,
    final_value          VARCHAR(32),
    target_side_won      BOOLEAN,
    created_tx_hash      VARCHAR(100)   NOT NULL,
    created_block_number numeric(19, 2) NOT NULL,
    closed_tx_hash       VARCHAR(100),
    closed_block_number  numeric(19, 2),
    created_date         TIMESTAMP      NOT NULL,
    modified_date        TIMESTAMP      NOT NULL,
    error_message        varchar
)