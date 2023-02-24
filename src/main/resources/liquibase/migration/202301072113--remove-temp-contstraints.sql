--liquibase formatted sql

--changeset anisimov:1875215 splitStatements:false runOnChange:false 3.sql
--comment Удаление нот налл констрейнтов у редьюдант temp

ALTER TABLE binary_join ALTER COLUMN binary_bet_id_temp drop not null;
ALTER TABLE custom_join ALTER COLUMN custom_bet_id_temp drop not null;
ALTER TABLE auction_join ALTER COLUMN auction_bet_id_temp drop not null;
ALTER TABLE jackpot_join ALTER COLUMN jackpot_bet_id_temp drop not null;
ALTER TABLE custom_matching ALTER COLUMN custom_bet_id_temp drop not null;
