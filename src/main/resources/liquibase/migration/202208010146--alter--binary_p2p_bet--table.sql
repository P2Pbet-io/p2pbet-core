--liquibase formatted sql

--changeset anisimov:1875194 splitStatements:false runOnChange:false 3.sql

--comment Добавление периода в BinaryP2PBet

alter table binary_p2p_bet
    add column period BIGINT NOT NULL DEFAULT 0;