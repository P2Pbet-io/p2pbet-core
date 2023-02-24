drop table if exists event_type;


--changeset anisimov:1875187 splitStatements:false runOnChange:false 3.sql

--comment Добавление статуса в BinaryP2PBet

alter table binary_p2p_bet
    add column status        varchar(20) not null DEFAULT 'CREATED',
    add column error_message varchar;
