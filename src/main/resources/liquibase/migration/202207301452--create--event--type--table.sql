--liquibase formatted sql

--changeset fabrikanec:1875184 splitStatements:false runOnChange:false 3.sql

--comment Создание таблицы для EventType

CREATE TABLE IF NOT EXISTS event_type
(
    id   uuid primary key,
    name VARCHAR(200) NOT NULL
)