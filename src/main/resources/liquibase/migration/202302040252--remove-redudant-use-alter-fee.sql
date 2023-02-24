--liquibase formatted sql

--changeset anisimov:1875216 splitStatements:false runOnChange:false 3.sql
--comment Удаление use_alter_fee, после апдейта ск

alter table auction_join drop column use_alter_fee;
alter table binary_join drop column use_alter_fee;
alter table custom_join drop column use_alter_fee;
alter table jackpot_join drop column use_alter_fee;