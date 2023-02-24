--liquibase formatted sql

--changeset anisimov:1875205 splitStatements:false runOnChange:false 3.sql
--comment Добавление таблицы ClientBetJoinEntity

CREATE TABLE IF NOT EXISTS client_bet_join
(
    id                  UUID primary key,
    bet_id              BIGINT      NOT NULL,
    bet_type            varchar(20) NOT NULL,
    client_address      VARCHAR(50) NOT NULL,
    total_join_amount   NUMERIC     NOT NULL,
    join_status         varchar(20) NOT NULL,
    expected_won_amount NUMERIC,
    amount_taken        NUMERIC,
    created_date        TIMESTAMP   NOT NULL,
    modified_date       TIMESTAMP   NOT NULL
)
