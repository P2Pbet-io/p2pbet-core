drop table if exists event_type;


--changeset fabrikanec:1875186 splitStatements:false runOnChange:false 3.sql

--comment рефакторинг base_event

alter table base_event
    drop column event_type_name,
    add column symbol varchar(200),
    add column name varchar(200),
    add column type varchar(200);
