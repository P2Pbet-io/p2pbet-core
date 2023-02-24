--liquibase formatted sql

--changeset anisimov:1875195 splitStatements:false runOnChange:false 3.sql

--comment Добавление use alter fee в AuctionJoin

alter table auction_join
    add column use_alter_fee BOOLEAN NOT NULL DEFAULT FALSE;