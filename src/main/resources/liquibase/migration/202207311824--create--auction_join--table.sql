--liquibase formatted sql

--changeset anisimov:1875192 splitStatements:false runOnChange:false 2.sql

--comment Создание таблицы для AuctionJoin

CREATE TABLE IF NOT EXISTS auction_join
(
    id                 UUID primary key,
    status             varchar(20) NOT NULL,
    client             VARCHAR(50) NOT NULL,
    auction_bet_id     BIGINT      NOT NULL,
    join_id            BIGINT      NOT NULL,
    join_id_client_ref BIGINT      NOT NULL,
    target_value       VARCHAR(32) NOT NULL,
    created_date       TIMESTAMP   NOT NULL,
    modified_date      TIMESTAMP   NOT NULL,
    CONSTRAINT fk_auction_join_auction_bet FOREIGN KEY (auction_bet_id) REFERENCES auction_p2p_bet (id)
)