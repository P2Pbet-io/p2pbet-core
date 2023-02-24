--liquibase formatted sql

--changeset anisimov:1875206 splitStatements:false runOnChange:false 3.sql
--comment Добавление таблицы ClientBetTransactionEntity

CREATE TABLE IF NOT EXISTS client_bet_transaction
(
    id                 UUID primary key,
    join_external_id   UUID,
    client_bet_join_id UUID        NOT NULL,
    transaction_type   VARCHAR(40) NOT NULL,
    log_data           JSON        NOT NULL,
    created_date       TIMESTAMP   NOT NULL,
    CONSTRAINT fk_client_transaction_client_join FOREIGN KEY (client_bet_join_id) REFERENCES client_bet_join (id)
)
