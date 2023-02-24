--liquibase formatted sql

--changeset anisimov:1875207 splitStatements:false runOnChange:false 3.sql
--comment Добавление free_mode в AuctionBet

alter table auction_p2p_bet
    add column free_mode BOOLEAN NOT NULL DEFAULT false;