--liquibase formatted sql

--changeset fabrikanec:1875183 splitStatements:false runOnChange:false 2.sql

--comment Создание таблицы для BaseEvent

CREATE TABLE IF NOT EXISTS base_event
(
    id              uuid primary key,
    event_type_name uuid      NOT NULL,
    created_date    TIMESTAMP NOT NULL
)