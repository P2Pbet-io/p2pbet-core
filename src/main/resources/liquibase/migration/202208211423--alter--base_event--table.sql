--liquibase formatted sql

--changeset anisimov:1875202 splitStatements:false runOnChange:false 3.sql
--comment Добавление fullName в BaseEvent

alter table base_event
    add column full_name varchar(200) NOT NULL DEFAULT '';