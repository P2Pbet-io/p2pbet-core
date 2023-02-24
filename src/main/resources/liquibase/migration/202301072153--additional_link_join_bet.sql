--liquibase formatted sql

--changeset anisimov:1875215 splitStatements:false runOnChange:false 3.sql
--comment Нативные айди в джойны

ALTER TABLE binary_join RENAME COLUMN binary_bet_id TO binary_bet_id_ref;
ALTER TABLE custom_join RENAME COLUMN custom_bet_id TO custom_bet_id_ref;
ALTER TABLE custom_matching RENAME COLUMN custom_bet_id TO custom_bet_id_ref;
ALTER TABLE auction_join RENAME COLUMN auction_bet_id TO auction_bet_id_ref;
ALTER TABLE jackpot_join RENAME COLUMN jackpot_bet_id TO jackpot_bet_id_ref;

ALTER TABLE binary_join RENAME COLUMN binary_bet_id_temp TO binary_bet_id;
ALTER TABLE custom_join RENAME COLUMN custom_bet_id_temp TO custom_bet_id;
ALTER TABLE custom_matching RENAME COLUMN custom_bet_id_temp TO custom_bet_id;
ALTER TABLE auction_join RENAME COLUMN auction_bet_id_temp TO auction_bet_id;
ALTER TABLE jackpot_join RENAME COLUMN jackpot_bet_id_temp TO jackpot_bet_id;