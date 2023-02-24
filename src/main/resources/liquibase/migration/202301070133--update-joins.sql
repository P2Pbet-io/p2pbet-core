--liquibase formatted sql

--changeset anisimov:1875214 splitStatements:false runOnChange:false 3.sql
--comment Обновление joins


ALTER TABLE binary_join RENAME COLUMN binary_bet_id TO binary_bet_id_temp;
ALTER TABLE custom_join RENAME COLUMN custom_bet_id TO custom_bet_id_temp;
ALTER TABLE custom_matching RENAME COLUMN custom_bet_id TO custom_bet_id_temp;
ALTER TABLE auction_join RENAME COLUMN auction_bet_id TO auction_bet_id_temp;
ALTER TABLE jackpot_join RENAME COLUMN jackpot_bet_id TO jackpot_bet_id_temp;

ALTER TABLE binary_join ADD COLUMN binary_bet_id uuid;
ALTER TABLE custom_join ADD COLUMN custom_bet_id uuid;
ALTER TABLE custom_matching ADD COLUMN custom_bet_id uuid;
ALTER TABLE auction_join ADD COLUMN auction_bet_id uuid;
ALTER TABLE jackpot_join ADD COLUMN jackpot_bet_id uuid;

update binary_join set binary_bet_id = binary_p2p_bet.id
from binary_p2p_bet
where binary_p2p_bet.bet_id = binary_join.binary_bet_id_temp;

update custom_join set custom_bet_id = custom_p2p_bet.id
from custom_p2p_bet
where custom_p2p_bet.bet_id = custom_join.custom_bet_id_temp;

update custom_matching set custom_bet_id = custom_p2p_bet.id
from custom_p2p_bet
where custom_p2p_bet.bet_id = custom_matching.custom_bet_id_temp;

update auction_join set auction_bet_id = auction_p2p_bet.id
from auction_p2p_bet
where auction_p2p_bet.bet_id = auction_join.auction_bet_id_temp;

update jackpot_join set jackpot_bet_id = jackpot_p2p_bet.id
from jackpot_p2p_bet
where jackpot_p2p_bet.bet_id = jackpot_join.jackpot_bet_id_temp;