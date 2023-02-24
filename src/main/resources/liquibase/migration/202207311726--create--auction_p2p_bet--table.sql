--liquibase formatted sql

--changeset anisimov:1875191 splitStatements:false runOnChange:false 2.sql

--comment Создание таблицы для AuctionP2PBet

CREATE TABLE IF NOT EXISTS auction_p2p_bet
(
    id                   BIGINT primary key,
    status               varchar(20)    NOT NULL,
    request_amount       NUMERIC        NOT NULL,
    lock_date            TIMESTAMP      NOT NULL,
    expiration_date      TIMESTAMP      NOT NULL,
    event_base_id        UUID,
    final_value          VARCHAR(32),
    join_id_won          BIGINT,
    created_tx_hash      VARCHAR(100)   NOT NULL,
    created_block_number numeric(19, 2) NOT NULL,
    closed_tx_hash       VARCHAR(100),
    closed_block_number  numeric(19, 2),
    created_date         TIMESTAMP      NOT NULL,
    modified_date        TIMESTAMP      NOT NULL,
    error_message        varchar
)