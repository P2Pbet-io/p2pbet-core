--liquibase formatted sql

--changeset anisimov:1875204 splitStatements:false runOnChange:false 3.sql
--comment Добавление src_url в BaseEvent

alter table base_event
    add column src_url text NOT NULL DEFAULT '';