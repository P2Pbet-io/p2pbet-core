--liquibase formatted sql

--changeset anisimov:1875212 splitStatements:false runOnChange:false 3.sql
--comment Добавление execution type в bets

ALTER TABLE binary_join DROP CONSTRAINT fk_binary_join_binary_bet;
ALTER TABLE binary_p2p_bet DROP CONSTRAINT binary_p2p_bet_pkey;
ALTER TABLE custom_join DROP CONSTRAINT fk_custom_join_custom_bet;
ALTER TABLE custom_matching DROP CONSTRAINT fk_custom_matching_custom_bet;
ALTER TABLE custom_p2p_bet DROP CONSTRAINT custom_p2p_bet_pkey;
ALTER TABLE auction_join DROP CONSTRAINT fk_auction_join_auction_bet;
ALTER TABLE auction_p2p_bet DROP CONSTRAINT auction_p2p_bet_pkey;
ALTER TABLE jackpot_join DROP CONSTRAINT fk_jackpot_join_jackpot_bet;
ALTER TABLE jackpot_p2p_bet DROP CONSTRAINT jackpot_p2p_bet_pkey;

ALTER TABLE binary_p2p_bet RENAME COLUMN id TO bet_id;
ALTER TABLE custom_p2p_bet RENAME COLUMN id TO bet_id;
ALTER TABLE auction_p2p_bet RENAME COLUMN id TO bet_id;
ALTER TABLE jackpot_p2p_bet RENAME COLUMN id TO bet_id;

ALTER TABLE binary_p2p_bet ADD COLUMN id UUID;
ALTER TABLE custom_p2p_bet ADD COLUMN id UUID;
ALTER TABLE auction_p2p_bet ADD COLUMN id UUID;
ALTER TABLE jackpot_p2p_bet ADD COLUMN id UUID;

ALTER TABLE binary_p2p_bet ADD PRIMARY KEY (id);
ALTER TABLE custom_p2p_bet ADD PRIMARY KEY (id);
ALTER TABLE auction_p2p_bet ADD PRIMARY KEY (id);
ALTER TABLE jackpot_p2p_bet ADD PRIMARY KEY (id);
