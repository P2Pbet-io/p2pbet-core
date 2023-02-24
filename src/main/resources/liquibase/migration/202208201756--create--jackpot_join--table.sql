--liquibase formatted sql

--changeset anisimov:1875200 splitStatements:false runOnChange:false 3.sql
--comment Добавление хэшей операций в JackpotJoin

CREATE TABLE IF NOT EXISTS jackpot_join
(
    id                 UUID primary key,
    status             varchar(20)  NOT NULL,
    client             VARCHAR(50)  NOT NULL,
    jackpot_bet_id     BIGINT       NOT NULL,
    join_id            BIGINT       NOT NULL,
    join_id_client_ref BIGINT       NOT NULL,
    target_value       VARCHAR(32)  NOT NULL,
    use_alter_fee      BOOLEAN      NOT NULL,
    join_hash          VARCHAR(100) NOT NULL,
    cancel_hash        VARCHAR(100),
    refund_hash        VARCHAR(100),
    prize_taken_hash   VARCHAR(100),
    created_date       TIMESTAMP    NOT NULL,
    modified_date      TIMESTAMP    NOT NULL,
    CONSTRAINT fk_jackpot_join_jackpot_bet FOREIGN KEY (jackpot_bet_id) REFERENCES jackpot_p2p_bet (id)
)
