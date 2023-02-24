--liquibase formatted sql

--changeset anisimov:1875203 splitStatements:false runOnChange:false 3.sql
--comment Добавление totalPool в CustomMatching

alter table custom_matching
    add column total_pool NUMERIC NOT NULL DEFAULT 0;