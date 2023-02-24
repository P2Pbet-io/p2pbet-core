--liquibase formatted sql

--changeset anisimov:1875208 splitStatements:false runOnChange:false 3.sql
--comment Добавление total_join_count в CustomMatching

alter table custom_matching
    add column total_join_count BIGINT NOT NULL DEFAULT 0;