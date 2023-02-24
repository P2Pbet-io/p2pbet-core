--liquibase formatted sql

--changeset anisimov:1875193 splitStatements:false runOnChange:false 3.sql

--comment Добавление хэшей операций в AuctionJoin

alter table auction_join
    add column join_hash VARCHAR(100) NOT NULL DEFAULT '';

alter table auction_join
    add column cancel_hash VARCHAR(100);

alter table auction_join
    add column refund_hash VARCHAR(100);

alter table auction_join
    add column prize_taken_hash VARCHAR(100);
