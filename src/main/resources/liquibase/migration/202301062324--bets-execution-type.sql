--liquibase formatted sql

--changeset anisimov:1875210 splitStatements:false runOnChange:false 3.sql
--comment Добавление execution type в bets

alter table binary_p2p_bet
    add column execution_type varchar not null default 'BSC';

alter table custom_p2p_bet
    add column execution_type varchar not null default 'BSC';

alter table auction_p2p_bet
    add column execution_type varchar not null default 'BSC';

alter table jackpot_p2p_bet
    add column execution_type varchar not null default 'BSC';