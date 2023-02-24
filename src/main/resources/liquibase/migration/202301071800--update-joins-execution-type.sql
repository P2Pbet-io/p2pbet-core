--liquibase formatted sql

--changeset anisimov:1875215 splitStatements:false runOnChange:false 3.sql
--comment Обновление joins, добавление execution_type


ALTER TABLE binary_join ADD COLUMN execution_type varchar;
ALTER TABLE custom_join ADD COLUMN execution_type varchar;
ALTER TABLE auction_join ADD COLUMN execution_type varchar;
ALTER TABLE jackpot_join ADD COLUMN execution_type varchar;

update binary_join set execution_type = binary_p2p_bet.execution_type
from binary_p2p_bet
where binary_p2p_bet.bet_id = binary_join.binary_bet_id_temp;

update custom_join set execution_type = custom_p2p_bet.execution_type
from custom_p2p_bet
where custom_p2p_bet.bet_id = custom_join.custom_bet_id_temp;

update auction_join set execution_type = auction_p2p_bet.execution_type
from auction_p2p_bet
where auction_p2p_bet.bet_id = auction_join.auction_bet_id_temp;

update jackpot_join set execution_type = jackpot_p2p_bet.execution_type
from jackpot_p2p_bet
where jackpot_p2p_bet.bet_id = jackpot_join.jackpot_bet_id_temp;
