--liquibase formatted sql

--changeset anisimov:1875213 splitStatements:false runOnChange:false 3.sql
--comment Добавление execution type в client join

alter table client_bet_join
    add column execution_type varchar not null default 'BSC';

update client_bet_join set execution_type = 'FREE' where bet_id > 100000000;