--liquibase formatted sql

--changeset anisimov:1875209 splitStatements:false runOnChange:false 3.sql
--comment Добавление bet_schedule для шедула через бд

CREATE TABLE IF NOT EXISTS bet_schedule
(
    id                 uuid primary key,
    bet_type           varchar NOT NULL,
    bet_execution_type varchar NOT NULL,
    cron               varchar NOT NULL,
    description        varchar NOT NULL,
    archive            bool    NOT NULL,
    request_amount     numeric NOT NULL,
    lock_period        bigint  NOT NULL,
    expiration_period  bigint  NOT NULL,
    event_id           uuid    NOT NULL
)