--liquibase formatted sql

--changeset anisimov:1875201 splitStatements:false runOnChange:false 3.sql
--comment Добавление периода в BinaryP2PBet

alter table custom_p2p_bet
    add column hidden BOOLEAN NOT NULL DEFAULT false;