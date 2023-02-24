--liquibase formatted sql

--changeset anisimov:1875188 splitStatements:false runOnChange:false 3.sql

--comment Добавление таблицы запрос в BlockchainIntegration - bi_execution

CREATE TABLE IF NOT EXISTS bi_execution
(
    id               UUID primary key,
    bet_id           BIGINT      NOT NULL,
    execution_status VARCHAR(32) NOT NULL,
    execution_action VARCHAR(32) NOT NULL,
    transaction_hash VARCHAR(100),
    error_message    VARCHAR,
    created_date     TIMESTAMP   NOT NULL,
    modified_date    TIMESTAMP   NOT NULL
)