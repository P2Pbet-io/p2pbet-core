--liquibase formatted sql

--changeset anisimov:1875211 splitStatements:false runOnChange:false 3.sql
--comment Добавление execution type в bets

update auction_p2p_bet set execution_type = 'FREE' where free_mode = true;

alter table auction_p2p_bet drop column free_mode;