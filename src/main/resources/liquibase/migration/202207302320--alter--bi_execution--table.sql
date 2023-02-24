--liquibase formatted sql

--changeset anisimov:1875189 splitStatements:false runOnChange:false 3.sql

--comment Снятие not null в филде bet_id bi_execution

alter table bi_execution
    alter column bet_id drop not null;
